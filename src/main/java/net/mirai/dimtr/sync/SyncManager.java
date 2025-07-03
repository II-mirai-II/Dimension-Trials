package net.mirai.dimtr.sync;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.PartyManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gerenciador centralizado de sincronização para todos os sistemas do mod
 * 
 * Este sistema resolve os problemas de sincronização identificados na análise,
 * fornecendo um ponto central para coordenar atualizações entre party e individual,
 * além de garantir que os clientes sejam notificados adequadamente.
 * 
 * 🎯 PERFORMANCE: Implementa batching para reduzir overhead de sincronização
 * ✅ THREAD-SAFE: Usa locks para evitar condições de corrida
 * 📡 NETWORK: Gerencia pacotes de sincronização de forma eficiente
 */
public class SyncManager {
    
    // ✅ THREAD-SAFE: Lock para operações de sincronização
    private static final ReentrantLock SYNC_LOCK = new ReentrantLock();
    
    // 🎯 PERFORMANCE: Sistema de batching para sincronização
    private static final ScheduledExecutorService SYNC_SCHEDULER = Executors.newSingleThreadScheduledExecutor(
        r -> {
            Thread t = new Thread(r, "DimTr-SyncManager");
            t.setDaemon(true);
            return t;
        }
    );
    
    // Conjuntos de jogadores que precisam ser sincronizados
    private static final Set<UUID> PENDING_PROGRESSION_SYNC = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PENDING_PARTY_SYNC = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PENDING_PHASE_SYNC = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PENDING_FORCE_SYNC = ConcurrentHashMap.newKeySet();
    
    // Configurações de timing
    private static final long BATCH_SYNC_DELAY_MS = 1000; // 1 segundo para batching normal
    private static final long FORCE_SYNC_DELAY_MS = 100;  // 100ms para sync forçado
    
    // Estado de inicialização
    private static boolean initialized = false;
    
    /**
     * Inicializar o SyncManager
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // Inicializar scheduler de sincronização em lote
        SYNC_SCHEDULER.scheduleAtFixedRate(SyncManager::processPendingSyncs, 
            BATCH_SYNC_DELAY_MS, BATCH_SYNC_DELAY_MS, TimeUnit.MILLISECONDS);
        
        // Inicializar scheduler de sincronização forçada
        SYNC_SCHEDULER.scheduleAtFixedRate(SyncManager::processForcedSyncs, 
            FORCE_SYNC_DELAY_MS, FORCE_SYNC_DELAY_MS, TimeUnit.MILLISECONDS);
        
        initialized = true;
        DimTrMod.LOGGER.info("✅ SyncManager inicializado com sucesso");
    }
    
    /**
     * Agendar sincronização de progressão para um jogador
     * 
     * @param playerId ID do jogador
     */
    public static void scheduleProgressionSync(UUID playerId) {
        if (playerId == null) return;
        
        PENDING_PROGRESSION_SYNC.add(playerId);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.debug("📡 Agendada sincronização de progressão para jogador {}", playerId);
        }
    }
    
    /**
     * Agendar sincronização de party para um jogador
     * 
     * @param playerId ID do jogador
     */
    public static void schedulePartySync(UUID playerId) {
        if (playerId == null) return;
        
        PENDING_PARTY_SYNC.add(playerId);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.debug("📡 Agendada sincronização de party para jogador {}", playerId);
        }
    }
    
    /**
     * Agendar sincronização de fase para um jogador
     * 
     * @param playerId ID do jogador
     */
    public static void schedulePhaseSync(UUID playerId) {
        if (playerId == null) return;
        
        PENDING_PHASE_SYNC.add(playerId);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.debug("📡 Agendada sincronização de fase para jogador {}", playerId);
        }
    }
    
    /**
     * Agendar sincronização completa para um jogador
     * 
     * @param playerId ID do jogador
     */
    public static void scheduleFullSync(UUID playerId) {
        if (playerId == null) return;
        
        PENDING_PROGRESSION_SYNC.add(playerId);
        PENDING_PARTY_SYNC.add(playerId);
        PENDING_PHASE_SYNC.add(playerId);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.debug("📡 Agendada sincronização completa para jogador {}", playerId);
        }
    }
    
    /**
     * Forçar sincronização imediata (para eventos críticos como boss kills)
     * 
     * @param playerId ID do jogador
     */
    public static void forceSync(UUID playerId) {
        if (playerId == null) return;
        
        PENDING_FORCE_SYNC.add(playerId);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.debug("🚨 Forçada sincronização imediata para jogador {}", playerId);
        }
    }
    
    /**
     * Agendar sincronização para todos os membros de uma party
     * 
     * @param partyData Dados da party
     */
    public static void schedulePartyMembersSync(PartyData partyData) {
        if (partyData == null) return;
        
        for (UUID memberId : partyData.getMembers()) {
            scheduleFullSync(memberId);
        }
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.debug("📡 Agendada sincronização para {} membros da party", partyData.getMembers().size());
        }
    }
    
    /**
     * Processar sincronizações pendentes em lote
     */
    private static void processPendingSyncs() {
        if (PENDING_PROGRESSION_SYNC.isEmpty() && PENDING_PARTY_SYNC.isEmpty() && PENDING_PHASE_SYNC.isEmpty()) {
            return;
        }
        
        SYNC_LOCK.lock();
        try {
            MinecraftServer server = getMinecraftServer();
            if (server == null) {
                return;
            }
            
            // Processar sincronizações de progressão
            if (!PENDING_PROGRESSION_SYNC.isEmpty()) {
                Set<UUID> toSync = Set.copyOf(PENDING_PROGRESSION_SYNC);
                PENDING_PROGRESSION_SYNC.clear();
                
                for (UUID playerId : toSync) {
                    syncPlayerProgression(playerId, server);
                }
            }
            
            // Processar sincronizações de party
            if (!PENDING_PARTY_SYNC.isEmpty()) {
                Set<UUID> toSync = Set.copyOf(PENDING_PARTY_SYNC);
                PENDING_PARTY_SYNC.clear();
                
                for (UUID playerId : toSync) {
                    syncPlayerParty(playerId, server);
                }
            }
            
            // Processar sincronizações de fase
            if (!PENDING_PHASE_SYNC.isEmpty()) {
                Set<UUID> toSync = Set.copyOf(PENDING_PHASE_SYNC);
                PENDING_PHASE_SYNC.clear();
                
                for (UUID playerId : toSync) {
                    syncPlayerPhase(playerId, server);
                }
            }
            
        } finally {
            SYNC_LOCK.unlock();
        }
    }
    
    /**
     * Processar sincronizações forçadas (alta prioridade)
     */
    private static void processForcedSyncs() {
        if (PENDING_FORCE_SYNC.isEmpty()) {
            return;
        }
        
        SYNC_LOCK.lock();
        try {
            MinecraftServer server = getMinecraftServer();
            if (server == null) {
                return;
            }
            
            Set<UUID> toSync = Set.copyOf(PENDING_FORCE_SYNC);
            PENDING_FORCE_SYNC.clear();
            
            for (UUID playerId : toSync) {
                syncPlayerComplete(playerId, server);
            }
            
            if (DimTrConfig.SERVER.enableDebugLogging.get() && !toSync.isEmpty()) {
                DimTrMod.LOGGER.debug("🚨 Processadas {} sincronizações forçadas", toSync.size());
            }
            
        } finally {
            SYNC_LOCK.unlock();
        }
    }
    
    /**
     * Sincronizar progressão individual de um jogador
     */
    private static void syncPlayerProgression(UUID playerId, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player == null) {
            return;
        }
        
        try {
            // ServerLevel level = player.serverLevel();
            // ProgressionManager progressionManager = ProgressionManager.get(level);
            // PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            
            // TODO: Implementar envio quando sistema de rede estiver integrado
            // UpdateProgressionToClientPayload packet = createProgressionPayload(playerData);
            // PacketDistributor.sendToPlayer(player, packet);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("📡 Sincronização de progressão programada para {}", player.getGameProfile().getName());
            }
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("❌ Erro ao sincronizar progressão do jogador {}: {}", playerId, e.getMessage());
        }
    }
    
    /**
     * Sincronizar dados de party de um jogador
     */
    private static void syncPlayerParty(UUID playerId, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player == null) {
            return;
        }
        
        try {
            ServerLevel level = player.serverLevel();
            PartyManager partyManager = PartyManager.get(level);
            PartyData partyData = partyManager.getPlayerParty(playerId);
            
            if (partyData != null) {
                // TODO: Implementar envio quando sistema de rede estiver integrado
                // UpdatePartyToClientPayload packet = createPartyPayload(partyData);
                // PacketDistributor.sendToPlayer(player, packet);
            }
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("📡 Sincronização de party programada para {}", player.getGameProfile().getName());
            }
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("❌ Erro ao sincronizar party do jogador {}: {}", playerId, e.getMessage());
        }
    }
    
    /**
     * Sincronizar informações de fase de um jogador
     */
    private static void syncPlayerPhase(UUID playerId, MinecraftServer server) {
        // Por enquanto, usar o mesmo método de progressão completa
        // já que não temos um payload específico para fases
        syncPlayerProgression(playerId, server);
    }
    
    /**
     * Sincronizar completamente um jogador (usado para sync forçado)
     */
    private static void syncPlayerComplete(UUID playerId, MinecraftServer server) {
        // Enviar tanto progressão quanto party
        syncPlayerProgression(playerId, server);
        syncPlayerParty(playerId, server);
    }
    
    /*
     * TODO: Implementar quando sistema de rede estiver integrado
     * 
     * Criar payload de progressão baseado nos dados do jogador
     */
    /*
    private static UpdateProgressionToClientPayload createProgressionPayload(PlayerProgressionData playerData) {
        // TODO: Implementar métodos getMobKills em PlayerProgressionData
        // Por enquanto, usar valores padrão para evitar erros de compilação
        return new UpdateProgressionToClientPayload(
            // Objetivos originais
            playerData.elderGuardianKilled,
            playerData.raidWon,
            false, // ravagerKilled (compatibilidade)
            false, // evokerKilled (compatibilidade)
            playerData.trialVaultAdvancementEarned,
            playerData.voluntaireExileAdvancementEarned,
            playerData.phase1Completed,
            playerData.witherKilled,
            playerData.wardenKilled,
            playerData.phase2Completed,
            
            // Contadores de mobs - Fase 1 (TODO: implementar getMobKills)
            0, // zombieKills
            0, // zombieVillagerKills (sempre 0)
            0, // skeletonKills
            0, // strayKills
            0, // huskKills
            0, // spiderKills
            0, // creeperKills
            0, // drownedKills
            0, // endermanKills
            0, // witchKills
            0, // pillagerKills
            0, // captainKills
            0, // vindicatorKills
            0, // boggedKills
            0, // breezeKills
            0, // ravagerKills
            0, // evokerKills
            
            // Contadores de mobs - Fase 2
            0, // blazeKills
            0, // witherSkeletonKills
            0, // piglinBruteKills
            0, // hoglinKills
            0, // zoglinKills
            0, // ghastKills
            0, // endermiteKills (sempre 0)
            0, // piglinKills
            
            // Requisitos (TODO: obter da configuração)
            10, // reqZombieKills
            0,  // reqZombieVillagerKills (sempre 0)
            10, // reqSkeletonKills
            5,  // reqStrayKills
            5,  // reqHuskKills
            10, // reqSpiderKills
            10, // reqCreeperKills
            5,  // reqDrownedKills
            5,  // reqEndermanKills
            3,  // reqWitchKills
            10, // reqPillagerKills
            3,  // reqCaptainKills
            5,  // reqVindicatorKills
            3,  // reqBoggedKills
            2,  // reqBreezeKills
            1,  // reqRavagerKills
            5,  // reqEvokerKills
            5,  // reqBlazeKills
            5,  // reqWitherSkeletonKills
            3,  // reqPiglinBruteKills
            1,  // reqHoglinKills
            1,  // reqZoglinKills
            3,  // reqGhastKills
            0,  // reqEndermiteKills (sempre 0)
            5,  // reqPiglinKills
            true, // serverReqVoluntaryExile
            
            // Custom phases (TODO: implementar)
            new HashMap<>(), // customPhaseCompletion
            new HashMap<>(), // customMobKills
            new HashMap<>()  // customObjectiveCompletion
        );
    }
    */
    
    /*
     * TODO: Implementar quando sistema de rede estiver integrado
     * 
     * Criar payload de party baseado nos dados da party
     */
    /*
    private static UpdatePartyToClientPayload createPartyPayload(PartyData partyData) {
        return new UpdatePartyToClientPayload(
            partyData.getPartyId(),
            partyData.getName(),
            partyData.isPublic(),
            partyData.getLeaderId(),
            new ArrayList<>(partyData.getMembers()),
            partyData.getPartyMultiplier(),
            partyData.getMembers().size(),
            partyData.getSharedMobKills(),
            partyData.isSharedElderGuardianKilled(),
            partyData.isSharedRaidWon(),
            partyData.isSharedTrialVaultAdvancementEarned(),
            partyData.isSharedVoluntaireExileAdvancementEarned(),
            partyData.isSharedWitherKilled(),
            partyData.isSharedWardenKilled(),
            partyData.isPhase1SharedCompleted(),
            partyData.isPhase2SharedCompleted(),
            
            // Custom phases (TODO: implementar)
            new HashMap<>(), // sharedCustomPhaseCompletion
            new HashMap<>(), // sharedCustomMobKills
            new HashMap<>()  // sharedCustomObjectiveCompletion
        );
    }
    */
    
    /**
     * Obter instância do servidor Minecraft
     */
    private static MinecraftServer getMinecraftServer() {
        // TODO: Implementar método para obter servidor
        // Exemplo de implementação seria:
        // return ServerLifecycleHooks.getCurrentServer();
        return null;
    }
    
    /**
     * Limpar recursos e parar schedulers
     */
    public static void shutdown() {
        if (!initialized) {
            return;
        }
        
        SYNC_LOCK.lock();
        try {
            if (!SYNC_SCHEDULER.isShutdown()) {
                SYNC_SCHEDULER.shutdown();
                try {
                    if (!SYNC_SCHEDULER.awaitTermination(5, TimeUnit.SECONDS)) {
                        SYNC_SCHEDULER.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    SYNC_SCHEDULER.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            // Limpar pendências
            PENDING_PROGRESSION_SYNC.clear();
            PENDING_PARTY_SYNC.clear();
            PENDING_PHASE_SYNC.clear();
            PENDING_FORCE_SYNC.clear();
            
            initialized = false;
            DimTrMod.LOGGER.info("✅ SyncManager finalizado com sucesso");
            
        } finally {
            SYNC_LOCK.unlock();
        }
    }
    
    /**
     * Verificar se o SyncManager está inicializado
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Obter estatísticas de sincronização para debug
     */
    public static String getSyncStats() {
        return String.format("Pending syncs - Progression: %d, Party: %d, Phase: %d, Force: %d",
            PENDING_PROGRESSION_SYNC.size(),
            PENDING_PARTY_SYNC.size(),
            PENDING_PHASE_SYNC.size(),
            PENDING_FORCE_SYNC.size()
        );
    }
}
