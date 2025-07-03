package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.sync.SyncManager;
import net.mirai.dimtr.util.ConfigCache;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coordenador central REFATORADO para sincronização entre sistemas de Party e Individual
 * 
 * ✅ REFATORAÇÃO COMPLETA: Este coordenador agora delega responsabilidades específicas
 * para coordenadores especializados, mantendo apenas a lógica de roteamento central.
 * 
 * 🎯 ARQUITETURA MODULAR:
 * - PartyProgressionCoordinator: Lógica específica de parties
 * - IndividualProgressionCoordinator: Lógica específica individual  
 * - ExternalBossCoordinator: Processamento de bosses externos
 * - CustomPhaseCoordinator: Processamento de fases customizadas
 * - SyncManager: Sincronização centralizada
 * 
 * 🔄 COMPATIBILIDADE: Mantém interface pública para não quebrar chamadas existentes
 */
public class ProgressionCoordinator {
    
    // ✅ THREAD-SAFETY: Lock para operações críticas
    private static final ReentrantLock PROCESSING_LOCK = new ReentrantLock();
    
    /**
     * Processar morte de mob com coordenação entre sistemas
     * REFATORADO: Agora delega para coordenadores especializados
     */
    public static boolean processMobKill(UUID playerId, String mobType, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            if (!ConfigCache.isPartySystemEnabled()) {
                // Sistema de party desabilitado - usar coordenador individual
                return IndividualProgressionCoordinator.processMobKill(playerId, mobType, serverLevel);
            }
            
            // Sistema de party habilitado - verificar se jogador está em party
            PartyManager partyManager = PartyManager.get(serverLevel);
            PartyData playerParty = partyManager.getPlayerParty(playerId);
            
            if (playerParty != null) {
                // Jogador está em party - usar coordenador de party
                return PartyProgressionCoordinator.processMobKill(playerId, mobType, playerParty, serverLevel);
            } else {
                // Jogador não está em party - usar coordenador individual
                return IndividualProgressionCoordinator.processMobKill(playerId, mobType, serverLevel);
            }
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Processar objetivo especial com coordenação entre sistemas
     * REFATORADO: Agora delega para coordenadores especializados
     */
    public static boolean processSpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            if (!DimTrConfig.SERVER.enablePartySystem.get()) {
                // Sistema de party desabilitado - usar coordenador individual
                return IndividualProgressionCoordinator.processSpecialObjective(playerId, objectiveType, serverLevel);
            }
            
            // Sistema de party habilitado - verificar se jogador está em party
            PartyManager partyManager = PartyManager.get(serverLevel);
            PartyData playerParty = partyManager.getPlayerParty(playerId);
            
            if (playerParty != null && playerParty.getMembers().size() > 1) {
                // Jogador está em party com outros membros - usar coordenador de party
                return PartyProgressionCoordinator.processSpecialObjective(playerId, objectiveType, playerParty, serverLevel);
            } else {
                // Jogador sozinho ou não está em party - usar coordenador individual
                return IndividualProgressionCoordinator.processSpecialObjective(playerId, objectiveType, serverLevel);
            }
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Verificar se um jogador pode acessar uma dimensão
     * REFATORADO: Agora delega para coordenadores especializados
     */
    public static boolean canPlayerAccessDimension(UUID playerId, String dimension, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            if (!ConfigCache.isPartySystemEnabled()) {
                // Sistema de party desabilitado - verificar apenas individual
                return IndividualProgressionCoordinator.canPlayerAccessDimension(playerId, dimension, serverLevel);
            }
            
            // Sistema de party habilitado - verificar qual usar
            PartyManager partyManager = PartyManager.get(serverLevel);
            PartyData playerParty = partyManager.getPlayerParty(playerId);
            
            if (playerParty != null && playerParty.getMembers().size() > 1) {
                // Jogador está em party com outros membros - usar coordenador de party
                return PartyProgressionCoordinator.canPlayerAccessDimension(playerId, dimension, playerParty, serverLevel);
            } else {
                // Jogador sozinho ou não está em party - usar coordenador individual
                return IndividualProgressionCoordinator.canPlayerAccessDimension(playerId, dimension, serverLevel);
            }
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Processar objetivo customizado - DELEGADO para CustomPhaseCoordinator
     */
    public static boolean processCustomObjective(UUID playerId, String phaseId, String objectiveId, ServerLevel serverLevel) {
        return CustomPhaseCoordinator.processCustomObjective(playerId, phaseId, objectiveId, serverLevel);
    }
    
    /**
     * Processar kill de mob customizado - DELEGADO para CustomPhaseCoordinator
     */
    public static boolean processCustomMobKill(UUID playerId, String phaseId, String mobType, ServerLevel serverLevel) {
        return CustomPhaseCoordinator.processCustomMobKill(playerId, phaseId, mobType, serverLevel);
    }
    
    /**
     * Verificar acesso a dimensão customizada - DELEGADO para CustomPhaseCoordinator
     */
    public static boolean canPlayerAccessCustomDimension(UUID playerId, String dimensionString, ServerLevel serverLevel) {
        return CustomPhaseCoordinator.canPlayerAccessCustomDimension(playerId, dimensionString, serverLevel);
    }
    
    /**
     * Processar objetivo de boss externo - DELEGADO para ExternalBossCoordinator
     */
    public static boolean processExternalBossObjective(UUID playerId, String bossEntityId, int phase, ServerLevel serverLevel) {
        return ExternalBossCoordinator.processExternalBossKill(playerId, bossEntityId, phase, serverLevel);
    }
    
    /**
     * Verificar completion de boss externo - DELEGADO para ExternalBossCoordinator
     */
    public static boolean isExternalBossComplete(UUID playerId, String bossEntityId, ServerLevel serverLevel) {
        return ExternalBossCoordinator.isExternalBossComplete(playerId, bossEntityId, serverLevel);
    }
    
    /**
     * Agendar sincronização em lote - DELEGADO para SyncManager
     */
    public static void scheduleBatchSync(UUID playerId) {
        SyncManager.scheduleFullSync(playerId);
    }
    
    /**
     * Forçar sincronização imediata - DELEGADO para SyncManager
     */
    public static void forceSync(UUID playerId) {
        SyncManager.forceSync(playerId);
    }
    
    /**
     * Inicializar todos os coordenadores especializados
     */
    public static void initialize() {
        SyncManager.initialize();
        DimTrMod.LOGGER.info("✅ ProgressionCoordinator e coordenadores especializados inicializados");
    }
    
    /**
     * Finalizar todos os coordenadores especializados
     */
    public static void shutdown() {
        SyncManager.shutdown();
        DimTrMod.LOGGER.info("✅ ProgressionCoordinator e coordenadores especializados finalizados");
    }
}
