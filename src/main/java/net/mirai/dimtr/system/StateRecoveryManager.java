package net.mirai.dimtr.system;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.data.PartyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * Sistema de Recuperação de Estado para Dimension Trials
 * 
 * Este sistema oferece:
 * ✅ Recuperação automática em caso de corrupção
 * ✅ Restauração de estado anterior válido
 * ✅ Criação de snapshots de estado antes de operações críticas
 * ✅ Integração com sistema de backup
 */
public class StateRecoveryManager {

    private static final String RECOVERY_DIR = "config/dimtr/recovery/";
    private static final String LAST_STABLE_STATE = "last_stable_state.dat.gz";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "DimTr-RecoveryScheduler");
        t.setDaemon(true);
        return t;
    });

    private static MinecraftServer server;
    private static boolean initialized = false;

    /**
     * Inicializa o sistema de recuperação
     */
    public static void initialize(MinecraftServer server) {
        if (initialized) return;

        StateRecoveryManager.server = server;
        createRecoveryDirectory();

        // Programar verificações periódicas de estado
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkAndUpdateStableState();
            } catch (Exception e) {
                DimTrMod.LOGGER.error("Erro durante verificação de estado: {}", e.getMessage());
            }
        }, 15, 15, TimeUnit.MINUTES);

        initialized = true;
        DimTrMod.LOGGER.info("✅ Sistema de Recuperação inicializado");
    }

    /**
     * Cria um snapshot do estado atual antes de operações críticas
     * @param operationName Nome da operação (para registro)
     * @return CompletableFuture que completa quando o snapshot terminar
     */
    public static CompletableFuture<Void> createStateSnapshot(String operationName) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (!initialized || server == null) {
            DimTrMod.LOGGER.warn("Sistema de Recovery não inicializado");
            future.complete(null);
            return future;
        }

        // Executar em thread separada para não bloquear
        CompletableFuture.runAsync(() -> {
            try {
                String timestamp = LocalDateTime.now().format(DATE_FORMAT);
                String fileName = "pre_" + operationName + "_" + timestamp + ".dat.gz";
                Path snapshotPath = Paths.get(RECOVERY_DIR, fileName);

                ServerLevel level = server.getLevel(Level.OVERWORLD);
                if (level == null) {
                    DimTrMod.LOGGER.error("Erro ao criar snapshot: overworld não disponível");
                    future.complete(null);
                    return;
                }

                ProgressionManager progressionManager = ProgressionManager.get(level);
                PartyManager partyManager = PartyManager.get(level);

                CompoundTag root = new CompoundTag();
                root.putString("operation", operationName);
                root.putLong("timestamp", System.currentTimeMillis());

                if (progressionManager != null) {
                    CompoundTag progressionData = progressionManager.serializeForBackup();
                    root.put("progression", progressionData);
                }

                if (partyManager != null) {
                    CompoundTag partyData = partyManager.serializeForBackup();
                    root.put("party", partyData);
                }

                NbtIo.writeCompressed(root, Files.newOutputStream(snapshotPath));
                DimTrMod.LOGGER.info("✅ Snapshot de estado criado antes de: {}", operationName);
                future.complete(null);

            } catch (IOException e) {
                DimTrMod.LOGGER.error("❌ Erro ao criar snapshot: {}", e.getMessage());
                future.complete(null);
            }
        });

        return future;
    }

    /**
     * Executar recovery com base na validação
     * @param issues Problemas encontrados pela validação
     * @return true se o recovery foi bem-sucedido
     */
    public static boolean performRecoveryIfNeeded(List<DataValidator.ValidationIssue> issues) {
        if (!initialized || server == null) {
            DimTrMod.LOGGER.warn("Sistema de Recovery não inicializado");
            return false;
        }

        // Verificar se há problemas críticos
        boolean hasCritical = issues.stream()
                .anyMatch(issue -> "CRITICAL".equals(issue.severity));

        // Verificar quantidade de erros
        long errorCount = issues.stream()
                .filter(issue -> "ERROR".equals(issue.severity))
                .count();

        // Determinar se recovery é necessário
        boolean needsRecovery = hasCritical || errorCount >= 3;

        if (!needsRecovery) {
            DimTrMod.LOGGER.info("Validação não indicou necessidade de recovery");
            return true;
        }

        // Logar problemas encontrados
        DimTrMod.LOGGER.warn("⚠️ Problemas críticos detectados, iniciando recovery");
        issues.forEach(issue -> DimTrMod.LOGGER.warn("  - {}", issue));

        // Tentar restaurar último estado estável
        return restoreLastStableState();
    }

    /**
     * Restaurar último estado estável
     */
    public static boolean restoreLastStableState() {
        if (!initialized || server == null) {
            DimTrMod.LOGGER.warn("Sistema de Recovery não inicializado");
            return false;
        }

        Path stablePath = Paths.get(RECOVERY_DIR, LAST_STABLE_STATE);
        if (!Files.exists(stablePath)) {
            DimTrMod.LOGGER.error("❌ Não há estado estável para recuperar");
            return false;
        }

        try {
            // Criar backup do estado atual
            BackupManager.createBackup("pre_recovery");

            // Carregar estado estável usando a assinatura correta do método
            CompoundTag stableState = NbtIo.readCompressed(stablePath, NbtAccounter.create(0));

            // Aplicar estado aos managers
            ServerLevel level = server.getLevel(Level.OVERWORLD);
            if (level == null) {
                DimTrMod.LOGGER.error("❌ Não foi possível acessar o overworld");
                return false;
            }

            // Restaurar dados de progressão
            if (stableState.contains("progression")) {
                ProgressionManager progressionManager = ProgressionManager.get(level);
                CompoundTag progressionTag = stableState.getCompound("progression");
                progressionManager.deserializeFromBackup(progressionTag);
                DimTrMod.LOGGER.info("✅ Dados de progressão restaurados");
            }

            // Restaurar dados de party
            if (stableState.contains("party")) {
                PartyManager partyManager = PartyManager.get(level);
                CompoundTag partyTag = stableState.getCompound("party");
                partyManager.deserializeFromBackup(partyTag);
                DimTrMod.LOGGER.info("✅ Dados de party restaurados");
            }

            // Sincronizar com todos os jogadores
            syncAllPlayers();

            DimTrMod.LOGGER.info("✅ Recovery concluído com sucesso");
            return true;

        } catch (IOException e) {
            DimTrMod.LOGGER.error("❌ Erro ao restaurar estado estável: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verificar e atualizar o estado estável
     */
    private static void checkAndUpdateStableState() {
        if (!initialized || server == null) return;

        // Executar validação
        DataValidator validator = new DataValidator(server);
        List<DataValidator.ValidationIssue> issues = validator.validateAll();

        // Verificar se o estado atual é válido
        boolean isValid = issues.stream()
                .noneMatch(issue -> "CRITICAL".equals(issue.severity) || "ERROR".equals(issue.severity));

        if (!isValid) {
            DimTrMod.LOGGER.warn("⚠️ Estado atual não é válido, não será usado como estado estável");
            return;
        }

        // Estado é válido, salvar como estado estável
        try {
            Path stablePath = Paths.get(RECOVERY_DIR, LAST_STABLE_STATE);

            ServerLevel level = server.getLevel(Level.OVERWORLD);
            if (level == null) {
                DimTrMod.LOGGER.error("Erro ao salvar estado estável: overworld não disponível");
                return;
            }

            ProgressionManager progressionManager = ProgressionManager.get(level);
            PartyManager partyManager = PartyManager.get(level);

            CompoundTag root = new CompoundTag();
            root.putString("stableStateInfo", "Estado válido salvo automaticamente");
            root.putLong("timestamp", System.currentTimeMillis());

            if (progressionManager != null) {
                CompoundTag progressionData = progressionManager.serializeForBackup();
                root.put("progression", progressionData);
            }

            if (partyManager != null) {
                CompoundTag partyData = partyManager.serializeForBackup();
                root.put("party", partyData);
            }

            NbtIo.writeCompressed(root, Files.newOutputStream(stablePath));
            DimTrMod.LOGGER.info("✅ Estado estável atualizado");

        } catch (IOException e) {
            DimTrMod.LOGGER.error("❌ Erro ao salvar estado estável: {}", e.getMessage());
        }
    }

    /**
     * Criar diretório de recovery
     */
    private static void createRecoveryDirectory() {
        File dir = new File(RECOVERY_DIR);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                DimTrMod.LOGGER.info("Diretório de recovery criado: {}", RECOVERY_DIR);
            } else {
                DimTrMod.LOGGER.error("Falha ao criar diretório de recovery: {}", RECOVERY_DIR);
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
                DimTrMod.LOGGER.debug("Sincronizando dados para jogador após recovery: {}", 
                    player.getGameProfile().getName());
                
                // Sincronizar dados de progressão
                ServerLevel level = server.getLevel(Level.OVERWORLD);
                if (level != null) {
                    ProgressionManager progressionManager = ProgressionManager.get(level);
                    if (progressionManager != null) {
                        progressionManager.sendToClient(player);
                    }
                    
                    // Sincronizar dados de party
                    PartyManager partyManager = PartyManager.get(level);
                    if (partyManager != null && partyManager.isPlayerInParty(player.getUUID())) {
                        // Usar o método existente para sincronizar party
                        // (depende da implementação atual)
                    }
                }
            });
        });
    }

    /**
     * Desligar sistema de recovery
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
        DimTrMod.LOGGER.info("Sistema de Recovery finalizado");
    }
}
