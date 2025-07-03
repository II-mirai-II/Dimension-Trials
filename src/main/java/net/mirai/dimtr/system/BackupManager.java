package net.mirai.dimtr.system;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.PlayerProgressionData;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.data.PartyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPOutputStream;

/**
 * Sistema de Backup Automático para Dimension Trials
 * 
 * Este sistema oferece:
 * ✅ Backups automáticos programados
 * ✅ Backups manuais com razão documentada
 * ✅ Restauração de backups específicos
 * ✅ Compressão automática dos arquivos
 * ✅ Rotação de backups antigos
 */
public class BackupManager {
    
    private static final String BACKUP_DIR = "config/dimtr/backups/";
    private static final int MAX_BACKUPS = 30; // Número máximo de backups mantidos
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = LOCK.writeLock();
    
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "DimTr-BackupScheduler");
        t.setDaemon(true);
        return t;
    });
    
    private static boolean initialized = false;
    private static MinecraftServer server;
    
    /**
     * Inicializa o sistema de backup
     */
    public static void initialize(MinecraftServer server) {
        if (initialized) return;
        
        BackupManager.server = server;
        
        // Criar diretório de backups se não existir
        createBackupDirectory();
        
        // Programar backups automáticos (a cada 2 horas)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                createBackup("auto", null);
            } catch (Exception e) {
                DimTrMod.LOGGER.error("Erro durante backup automático: {}", e.getMessage());
            }
        }, 2, 2, TimeUnit.HOURS);
        
        initialized = true;
        DimTrMod.LOGGER.info("✅ Sistema de Backup inicializado");
    }
    
    /**
     * Criar backup com uma razão específica
     */
    public static String createBackup(String reason) {
        return createBackup(reason, null);
    }
    
    /**
     * Criar backup com uma razão específica e jogador relacionado
     */
    public static String createBackup(String reason, UUID playerId) {
        if (!initialized) {
            DimTrMod.LOGGER.warn("Sistema de Backup não inicializado");
            return null;
        }
        
        writeLock.lock();
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String backupId = timestamp + (reason != null ? "_" + reason : "");
            String fileName = backupId + ".dat.gz";
            Path backupPath = Paths.get(BACKUP_DIR, fileName);
            
            // Obter dados a serem salvos
            CompoundTag backupData = createBackupData(reason, playerId);
            
            // Salvar dados comprimidos
            try (GZIPOutputStream outputStream = new GZIPOutputStream(Files.newOutputStream(backupPath))) {
                NbtIo.writeCompressed(backupData, outputStream);
                DimTrMod.LOGGER.info("✅ Backup criado: {} ({})", backupId, reason);
                
                // Remover backups antigos
                pruneOldBackups();
                
                return backupId;
            } catch (IOException e) {
                DimTrMod.LOGGER.error("❌ Erro ao criar backup: {}", e.getMessage());
                return null;
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Restaurar a partir de um backup específico
     */
    public static boolean restoreBackup(String backupId) {
        if (!initialized) {
            DimTrMod.LOGGER.warn("Sistema de Backup não inicializado");
            return false;
        }
        
        writeLock.lock();
        try {
            // Criar backup do estado atual antes de restaurar
            createBackup("pre_restore_" + backupId);
            
            String fileName = backupId + ".dat.gz";
            Path backupPath = Paths.get(BACKUP_DIR, fileName);
            
            if (!Files.exists(backupPath)) {
                // Tentar encontrar arquivo apenas com o ID, sem extensão
                File directory = new File(BACKUP_DIR);
                File[] matchingFiles = directory.listFiles((dir, name) -> name.startsWith(backupId));
                
                if (matchingFiles == null || matchingFiles.length == 0) {
                    DimTrMod.LOGGER.error("❌ Backup não encontrado: {}", backupId);
                    return false;
                }
                
                backupPath = matchingFiles[0].toPath();
            }
            
            try {
                CompoundTag backupData = NbtIo.readCompressed(backupPath, NbtAccounter.create(0));
                
                // Aplicar o backup carregado
                applyBackupData(backupData);
                
                DimTrMod.LOGGER.info("✅ Backup restaurado: {}", backupId);
                return true;
            } catch (IOException e) {
                DimTrMod.LOGGER.error("❌ Erro ao restaurar backup: {}", e.getMessage());
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Listar todos os backups disponíveis
     */
    public static List<BackupInfo> listBackups() {
        readLock.lock();
        try {
            List<BackupInfo> backups = new ArrayList<>();
            File directory = new File(BACKUP_DIR);
            
            if (!directory.exists()) {
                return backups;
            }
            
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat.gz"));
            if (files == null) {
                return backups;
            }
            
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    String backupId = fileName.substring(0, fileName.lastIndexOf(".dat.gz"));
                    
                    CompoundTag metadata = NbtIo.readCompressed(file.toPath(), NbtAccounter.create(0));
                    CompoundTag header = metadata.getCompound("header");
                    
                    String reason = header.getString("reason");
                    long timestamp = header.getLong("timestamp");
                    
                    backups.add(new BackupInfo(backupId, reason, timestamp, file.length()));
                } catch (Exception e) {
                    DimTrMod.LOGGER.warn("Erro ao ler metadata do backup {}: {}", file.getName(), e.getMessage());
                }
            }
            
            // Ordenar por data (mais recente primeiro)
            backups.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
            
            return backups;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Desligar o sistema de backup
     */
    public static void shutdown() {
        if (!initialized) return;
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        
        initialized = false;
        DimTrMod.LOGGER.info("Sistema de Backup finalizado");
    }
    
    // ============================================================================
    // MÉTODOS PRIVADOS
    // ============================================================================
    
    /**
     * Criar o diretório de backups
     */
    private static void createBackupDirectory() {
        File dir = new File(BACKUP_DIR);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                DimTrMod.LOGGER.info("Diretório de backups criado: {}", BACKUP_DIR);
            } else {
                DimTrMod.LOGGER.error("Falha ao criar diretório de backups: {}", BACKUP_DIR);
            }
        }
    }
    
    /**
     * Criar dados do backup
     */
    private static CompoundTag createBackupData(String reason, UUID playerId) {
        CompoundTag root = new CompoundTag();
        
        // Header com metadata
        CompoundTag header = new CompoundTag();
        header.putString("reason", reason != null ? reason : "manual");
        header.putLong("timestamp", System.currentTimeMillis());
        header.putString("version", DimTrMod.MODID + "-" + "1.3.0"); // Versão do mod
        if (playerId != null) {
            header.putUUID("playerId", playerId);
        }
        root.put("header", header);
        
        // Dados de progressão individual
        if (server != null) {
            ServerLevel level = server.overworld();
            ProgressionManager progressionManager = ProgressionManager.get(level);
            PartyManager partyManager = PartyManager.get(level);
            
            if (progressionManager != null) {
                CompoundTag progressionData = progressionManager.serializeForBackup();
                root.put("progression", progressionData);
            }
            
            if (partyManager != null) {
                CompoundTag partyData = partyManager.serializeForBackup();
                root.put("party", partyData);
            }
        }
        
        return root;
    }
    
    /**
     * Aplicar dados do backup
     */
    private static void applyBackupData(CompoundTag backupData) {
        if (server == null) {
            DimTrMod.LOGGER.error("Não é possível restaurar: servidor não disponível");
            return;
        }
        
        ServerLevel level = server.overworld();
        ProgressionManager progressionManager = ProgressionManager.get(level);
        PartyManager partyManager = PartyManager.get(level);
        
        if (progressionManager != null && backupData.contains("progression")) {
            CompoundTag progressionTag = backupData.getCompound("progression");
            progressionManager.deserializeFromBackup(progressionTag);
        }
        
        if (partyManager != null && backupData.contains("party")) {
            CompoundTag partyTag = backupData.getCompound("party");
            partyManager.deserializeFromBackup(partyTag);
        }
        
        // Forçar sincronização para todos os jogadores
        syncAllPlayers();
    }
    
    /**
     * Remover backups antigos quando exceder o limite
     */
    private static void pruneOldBackups() {
        File directory = new File(BACKUP_DIR);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat.gz"));
        
        if (files == null || files.length <= MAX_BACKUPS) {
            return;
        }
        
        // Ordenar por data de modificação (mais antigo primeiro)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        
        // Remover os mais antigos até ficar dentro do limite
        int toDelete = files.length - MAX_BACKUPS;
        for (int i = 0; i < toDelete; i++) {
            if (files[i].delete()) {
                DimTrMod.LOGGER.debug("Backup antigo removido: {}", files[i].getName());
            } else {
                DimTrMod.LOGGER.warn("Não foi possível remover backup antigo: {}", files[i].getName());
            }
        }
    }
    
    /**
     * Sincronizar todos os jogadores online após restauração
     */
    private static void syncAllPlayers() {
        if (server == null) return;
        
        server.execute(() -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                // Enviar dados atualizados para o cliente
                // Utilizar seu sistema de sincronização existente
                DimTrMod.LOGGER.debug("Sincronizando dados para jogador após restauração: {}", player.getGameProfile().getName());
            });
        });
    }
    
    /**
     * Classe para representar informações sobre um backup
     */
    public static class BackupInfo {
        public final String id;
        public final String reason;
        public final long timestamp;
        public final long size;
        
        public BackupInfo(String id, String reason, long timestamp, long size) {
            this.id = id;
            this.reason = reason;
            this.timestamp = timestamp;
            this.size = size;
        }
        
        @Override
        public String toString() {
            Date date = new Date(timestamp);
            return String.format("%s - %s (%.2f MB)", id, reason, size / (1024.0 * 1024.0));
        }
    }
}
