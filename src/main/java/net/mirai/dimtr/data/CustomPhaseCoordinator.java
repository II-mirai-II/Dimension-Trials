package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.sync.SyncManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coordenador especializado para processamento de fases customizadas
 * 
 * Este coordenador foi extraído do ProgressionCoordinator como parte da refatoração
 * arquitetural para modularizar responsabilidades e melhorar a manutenibilidade.
 * 
 * ✅ RESPONSABILIDADES:
 * - Processar objetivos de fases customizadas
 * - Processar kills de mobs customizados
 * - Verificar completion de fases customizadas
 * - Coordenar entre sistemas de party e individual
 * - Aplicar multiplicadores de requisitos para parties
 * 
 * 🎯 INTEGRAÇÃO: Trabalha com sistema de Custom Requirements
 * 📡 SINCRONIZAÇÃO: Usa SyncManager para atualizações em lote
 */
public class CustomPhaseCoordinator {
    
    // ✅ THREAD-SAFETY: Lock para operações críticas
    private static final ReentrantLock PROCESSING_LOCK = new ReentrantLock();
    
    /**
     * Processar objetivo customizado com coordenação entre party e individual
     * 
     * @param playerId ID do jogador que alcançou o objetivo
     * @param phaseId ID da fase customizada
     * @param objectiveId ID do objetivo
     * @param serverLevel Nível do servidor
     * @return true se foi processado com sucesso
     */
    public static boolean processCustomObjective(UUID playerId, String phaseId, String objectiveId, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("🎯 [CustomPhaseCoordinator] Processando objetivo customizado - Fase: {}, Objetivo: {}, Jogador: {}", 
                    phaseId, objectiveId, playerId);
            }
            
            // 🎯 PRIMEIRO: Tentar processar para party
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null) {
                    return processPartyCustomObjective(party, playerId, phaseId, objectiveId, serverLevel, progressionManager);
                }
            }
            
            // 🎯 SEGUNDO: Processar individualmente se não foi processado por party
            return processIndividualCustomObjective(playerId, phaseId, objectiveId, progressionManager);
            
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Processar kill de mob customizado com coordenação entre party e individual
     * 
     * @param playerId ID do jogador que matou o mob
     * @param phaseId ID da fase customizada
     * @param mobType Tipo do mob
     * @param serverLevel Nível do servidor
     * @return true se foi processado com sucesso
     */
    public static boolean processCustomMobKill(UUID playerId, String phaseId, String mobType, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("🎯 [CustomPhaseCoordinator] Processando kill customizado - Fase: {}, Mob: {}, Jogador: {}", 
                    phaseId, mobType, playerId);
            }
            
            // 🎯 PRIMEIRO: Tentar processar para party
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null) {
                    return processPartyCustomMobKill(party, playerId, phaseId, mobType, serverLevel, progressionManager);
                }
            }
            
            // 🎯 SEGUNDO: Processar individualmente se não foi processado por party
            return processIndividualCustomMobKill(playerId, phaseId, mobType, progressionManager);
            
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Verificar se jogador pode acessar dimensão customizada
     * 
     * @param playerId ID do jogador
     * @param dimensionString String da dimensão
     * @param serverLevel Nível do servidor
     * @return true se pode acessar
     */
    public static boolean canPlayerAccessCustomDimension(UUID playerId, String dimensionString, ServerLevel serverLevel) {
        String blockingPhase = net.mirai.dimtr.config.CustomRequirements.findBlockingPhaseForDimension(dimensionString);
        
        if (blockingPhase == null) {
            return true; // Dimensão não é controlada por nenhuma fase customizada
        }
        
        PROCESSING_LOCK.lock();
        try {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            // Verificar se jogador está em party
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null && party.isCustomPhaseComplete(blockingPhase)) {
                    return true; // Party já completou a fase
                }
            }
            
            // Verificar progresso individual
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            return playerData.isCustomPhaseComplete(blockingPhase);
            
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Processar objetivo customizado para party
     */
    private static boolean processPartyCustomObjective(PartyData party, UUID playerId, String phaseId, 
                                                     String objectiveId, ServerLevel serverLevel, 
                                                     ProgressionManager progressionManager) {
        
        if (party.isSharedCustomObjectiveComplete(phaseId, objectiveId)) {
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("⚠️ Objetivo customizado {}/{} já estava completo para party", phaseId, objectiveId);
            }
            return false;
        }
        
        party.setSharedCustomObjectiveComplete(phaseId, objectiveId, true);
        
        // Também marcar no progresso individual (para restauração futura)
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        playerData.setCustomObjectiveComplete(phaseId, objectiveId, true);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.info("✅ [PARTY] Objetivo customizado {}/{} completo para party do jogador {}", 
                phaseId, objectiveId, playerId);
        }
        
        // Verificar se a fase está completa agora
        checkAndCompleteCustomPhase(party, phaseId, serverLevel);
        
        // Sincronizar com todos os membros da party
        for (UUID memberId : party.getMembers()) {
            SyncManager.scheduleFullSync(memberId);
        }
        
        return true;
    }
    
    /**
     * Processar objetivo customizado individualmente
     */
    private static boolean processIndividualCustomObjective(UUID playerId, String phaseId, String objectiveId, 
                                                          ProgressionManager progressionManager) {
        
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        
        if (playerData.isCustomObjectiveComplete(phaseId, objectiveId)) {
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("⚠️ Objetivo customizado {}/{} já estava completo para jogador {}", 
                    phaseId, objectiveId, playerId);
            }
            return false;
        }
        
        playerData.setCustomObjectiveComplete(phaseId, objectiveId, true);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            DimTrMod.LOGGER.info("✅ [INDIVIDUAL] Objetivo customizado {}/{} completo para jogador {}", 
                phaseId, objectiveId, playerId);
        }
        
        // Verificar se a fase está completa agora
        checkAndCompleteCustomPhaseIndividual(playerData, phaseId);
        
        SyncManager.scheduleFullSync(playerId);
        
        return true;
    }
    
    /**
     * Processar kill de mob customizado para party
     */
    private static boolean processPartyCustomMobKill(PartyData party, UUID playerId, String phaseId, 
                                                   String mobType, ServerLevel serverLevel, 
                                                   ProgressionManager progressionManager) {
        
        party.incrementSharedCustomMobKill(phaseId, mobType);
        
        // Também incrementar no progresso individual (para restauração futura)
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        playerData.incrementCustomMobKill(phaseId, mobType);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            int currentCount = party.getSharedCustomMobKills(phaseId, mobType);
            DimTrMod.LOGGER.debug("🔥 [PARTY] Kill customizado {}/{} incrementado para {} (total: {})", 
                phaseId, mobType, playerId, currentCount);
        }
        
        // Verificar se a fase está completa agora
        checkAndCompleteCustomPhase(party, phaseId, serverLevel);
        
        // Sincronizar com todos os membros da party
        for (UUID memberId : party.getMembers()) {
            SyncManager.scheduleProgressionSync(memberId);
        }
        
        return true;
    }
    
    /**
     * Processar kill de mob customizado individualmente
     */
    private static boolean processIndividualCustomMobKill(UUID playerId, String phaseId, String mobType, 
                                                        ProgressionManager progressionManager) {
        
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        playerData.incrementCustomMobKill(phaseId, mobType);
        
        if (DimTrConfig.SERVER.enableDebugLogging.get()) {
            int currentCount = playerData.getCustomMobKills(phaseId, mobType);
            DimTrMod.LOGGER.debug("🔥 [INDIVIDUAL] Kill customizado {}/{} incrementado para {} (total: {})", 
                phaseId, mobType, playerId, currentCount);
        }
        
        // Verificar se a fase está completa agora
        checkAndCompleteCustomPhaseIndividual(playerData, phaseId);
        
        SyncManager.scheduleProgressionSync(playerId);
        
        return true;
    }
    
    /**
     * Verificar e completar fase customizada para party
     */
    private static void checkAndCompleteCustomPhase(PartyData party, String phaseId, ServerLevel serverLevel) {
        if (party.isCustomPhaseComplete(phaseId)) {
            return; // Já completa
        }
        
        var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
        if (customPhase == null) {
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.warn("⚠️ Fase customizada {} não encontrada", phaseId);
            }
            return;
        }
        
        // Verificar todos os requisitos
        boolean allRequirementsMet = true;
        
        // Verificar mob requirements
        if (customPhase.mobRequirements != null) {
            for (var entry : customPhase.mobRequirements.entrySet()) {
                String mobType = entry.getKey();
                int required = party.getAdjustedRequirement(entry.getValue()); // Aplicar multiplicador de party
                int current = party.getSharedCustomMobKills(phaseId, mobType);
                
                if (current < required) {
                    allRequirementsMet = false;
                    if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                        DimTrMod.LOGGER.debug("❌ [PARTY] Fase {} - Mob {} não atende requisito: {}/{}", 
                            phaseId, mobType, current, required);
                    }
                    break;
                }
            }
        }
        
        // Verificar special objectives
        if (allRequirementsMet && customPhase.specialObjectives != null) {
            for (var entry : customPhase.specialObjectives.entrySet()) {
                String objectiveId = entry.getKey();
                var objective = entry.getValue();
                
                if (objective.required && !party.isSharedCustomObjectiveComplete(phaseId, objectiveId)) {
                    allRequirementsMet = false;
                    if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                        DimTrMod.LOGGER.debug("❌ [PARTY] Fase {} - Objetivo {} não completo", phaseId, objectiveId);
                    }
                    break;
                }
            }
        }
        
        // Completar fase se todos os requisitos foram atendidos
        if (allRequirementsMet) {
            party.setCustomPhaseComplete(phaseId, true);
            
            // Notificar todos os membros da party
            notifyPartyPhaseCompletion(party, customPhase.name, phaseId, serverLevel);
            
            DimTrMod.LOGGER.info("🎉 [PARTY] Fase customizada {} completa para party {}", 
                customPhase.name, party.getLeaderId());
        }
    }
    
    /**
     * Verificar e completar fase customizada individual
     */
    private static void checkAndCompleteCustomPhaseIndividual(PlayerProgressionData playerData, String phaseId) {
        if (playerData.isCustomPhaseComplete(phaseId)) {
            return; // Já completa
        }
        
        var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
        if (customPhase == null) {
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.warn("⚠️ Fase customizada {} não encontrada", phaseId);
            }
            return;
        }
        
        // Verificar todos os requisitos
        boolean allRequirementsMet = true;
        
        // Verificar mob requirements
        if (customPhase.mobRequirements != null) {
            for (var entry : customPhase.mobRequirements.entrySet()) {
                String mobType = entry.getKey();
                int required = entry.getValue(); // Sem multiplicador para individual
                int current = playerData.getCustomMobKills(phaseId, mobType);
                
                if (current < required) {
                    allRequirementsMet = false;
                    if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                        DimTrMod.LOGGER.debug("❌ [INDIVIDUAL] Fase {} - Mob {} não atende requisito: {}/{}", 
                            phaseId, mobType, current, required);
                    }
                    break;
                }
            }
        }
        
        // Verificar special objectives
        if (allRequirementsMet && customPhase.specialObjectives != null) {
            for (var entry : customPhase.specialObjectives.entrySet()) {
                String objectiveId = entry.getKey();
                var objective = entry.getValue();
                
                if (objective.required && !playerData.isCustomObjectiveComplete(phaseId, objectiveId)) {
                    allRequirementsMet = false;
                    if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                        DimTrMod.LOGGER.debug("❌ [INDIVIDUAL] Fase {} - Objetivo {} não completo", phaseId, objectiveId);
                    }
                    break;
                }
            }
        }
        
        // Completar fase se todos os requisitos foram atendidos
        if (allRequirementsMet) {
            playerData.setCustomPhaseComplete(phaseId, true);
            
            DimTrMod.LOGGER.info("🎉 [INDIVIDUAL] Fase customizada {} completa para jogador {}", 
                customPhase.name, playerData.getPlayerId());
        }
    }
    
    /**
     * Notificar party sobre completion de fase customizada
     */
    private static void notifyPartyPhaseCompletion(PartyData party, String phaseName, String phaseId, ServerLevel serverLevel) {
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                // TODO: Implementar sistema de notificação customizado
                // Por enquanto, usar log
                DimTrMod.LOGGER.info("🎉 Notificando membro {} da party sobre conclusão da fase customizada {}", 
                    member.getGameProfile().getName(), phaseName);
            }
        }
    }
    
    /**
     * Verificar se uma fase customizada está completa para um jogador
     * 
     * @param playerId ID do jogador
     * @param phaseId ID da fase
     * @param serverLevel Nível do servidor
     * @return true se a fase está completa
     */
    public static boolean isCustomPhaseComplete(UUID playerId, String phaseId, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            // Verificar primeiro no sistema de party se o jogador estiver em uma
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null && party.isCustomPhaseComplete(phaseId)) {
                    return true;
                }
            }
            
            // Verificar no progresso individual
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            return playerData.isCustomPhaseComplete(phaseId);
            
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
}
