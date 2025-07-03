package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.integration.ExternalModIntegration;
import net.mirai.dimtr.sync.SyncManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coordenador especializado para processamento de bosses de mods externos
 * 
 * Este coordenador foi extraído do ProgressionCoordinator como parte da refatoração
 * arquitetural para modularizar responsabilidades e melhorar a manutenibilidade.
 * 
 * ✅ RESPONSABILIDADES:
 * - Processar morte de bosses externos (Cataclysm, Mowzie's Mobs, etc.)
 * - Verificar completion de fases baseado em bosses externos
 * - Coordenar entre sistemas de party e individual
 * - Aplicar sincronização imediata para eventos críticos
 * 
 * 🎯 INTEGRAÇÃO: Trabalha junto com ExternalModIntegration para detectar bosses
 * 📡 SINCRONIZAÇÃO: Usa SyncManager para atualizações imediatas
 */
public class ExternalBossCoordinator {
    
    // ✅ THREAD-SAFETY: Lock para operações críticas
    private static final ReentrantLock PROCESSING_LOCK = new ReentrantLock();
    
    /**
     * Processar morte de boss externo com coordenação entre party e individual
     * 
     * @param playerId ID do jogador que matou o boss
     * @param bossEntityId ID da entidade do boss (ex: "cataclysm:ignis")
     * @param phase Fase à qual o boss pertence (1, 2, ou 3)
     * @param serverLevel Nível do servidor
     * @return true se foi processado com sucesso
     */
    public static boolean processExternalBossKill(UUID playerId, String bossEntityId, int phase, ServerLevel serverLevel) {
        if (!DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("🔒 Integração com mods externos desabilitada - ignorando boss {}", bossEntityId);
            }
            return false;
        }
        
        PROCESSING_LOCK.lock();
        try {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            String objectiveKey = bossEntityId.replace(":", "_");
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.debug("🔍 [ExternalBossCoordinator] Processando boss externo - ID: {}, Key: {}, Fase: {}, Jogador: {}", 
                    bossEntityId, objectiveKey, phase, playerId);
            }
            
            // 🎯 PRIMEIRO: Tentar processar para party
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null) {
                    return processPartyExternalBoss(party, playerId, bossEntityId, objectiveKey, phase, serverLevel, progressionManager);
                }
            }
            
            // 🎯 SEGUNDO: Processar individualmente se não foi processado por party
            return processIndividualExternalBoss(playerId, bossEntityId, objectiveKey, phase, progressionManager, serverLevel);
            
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
    
    /**
     * Processar boss externo para party
     */
    private static boolean processPartyExternalBoss(PartyData party, UUID playerId, String bossEntityId, 
                                                  String objectiveKey, int phase, ServerLevel serverLevel, 
                                                  ProgressionManager progressionManager) {
        
        // Marcar no sistema de custom objectives da party
        party.setSharedCustomObjectiveComplete("external_bosses", objectiveKey, true);
        
        // Também marcar no progresso individual para backup/restauração
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        playerData.setCustomObjectiveComplete("external_bosses", objectiveKey, true);
        
        // Marcar como modificado para garantir persistência
        progressionManager.setDirty();
        
        // Enviar notificação e verificar conclusão da fase
        notifyPartyMembersOfBossKill(party, bossEntityId, serverLevel);
        checkPhaseCompletionWithExternalBosses(party, phase, serverLevel);
        
        // Sincronizar party com TODOS os membros da party
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                progressionManager.sendToClient(member); // Enviar dados de progressão para o cliente
                PartyManager.get(serverLevel).sendPartyToClient(member); // Enviar dados da party para o cliente
            }
        }
        
        // Log para depuração
        DimTrMod.LOGGER.info("🎯 Boss externo {} derrotado por party {} - Fase {} - Marcado em objetivos externos",
                bossEntityId, party.getPartyId(), phase);
        return true;
    }
    
    /**
     * Processar boss externo para jogador individual
     */
    private static boolean processIndividualExternalBoss(UUID playerId, String bossEntityId, String objectiveKey, 
                                                       int phase, ProgressionManager progressionManager, ServerLevel serverLevel) {
        
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        
        // Marcar o boss como derrotado no mapa de objetivos personalizados
        playerData.setCustomObjectiveComplete("external_bosses", objectiveKey, true);
        
        // Verificar se a fase está completa (incluindo bosses externos)
        checkPhaseCompletionWithExternalBossesIndividual(playerData, phase);
        
        // Forçar salvamento dos dados
        progressionManager.setDirty();
        
        // Enviar imediatamente para o cliente
        ServerPlayer serverPlayer = serverLevel.getServer().getPlayerList().getPlayer(playerId);
        if (serverPlayer != null) {
            progressionManager.sendToClient(serverPlayer);
        }
        
        DimTrMod.LOGGER.info("🎉 Boss externo {} derrotado por jogador individual {} - Fase {} atualizada", 
                bossEntityId, playerId, phase);
        return true;
    }
    
    /**
     * Verificar se uma fase está completa considerando bosses externos (party)
     */
    private static void checkPhaseCompletionWithExternalBosses(PartyData party, int phase, ServerLevel serverLevel) {
        switch (phase) {
            case 1:
                if (isPhase1CompleteWithExternalBosses(party)) {
                    party.setPhase1SharedCompleted(true);
                    notifyPartyPhaseCompletion(party, 1, serverLevel);
                    DimTrMod.LOGGER.info("🎯 Fase 1 completa para party {} (incluindo bosses externos)", party.getLeaderId());
                }
                break;
            case 2:
                if (isPhase2CompleteWithExternalBosses(party)) {
                    party.setPhase2SharedCompleted(true);
                    notifyPartyPhaseCompletion(party, 2, serverLevel);
                    DimTrMod.LOGGER.info("🎯 Fase 2 completa para party {} (incluindo bosses externos)", party.getLeaderId());
                }
                break;
            case 3:
                if (isPhase3CompleteWithExternalBosses(party)) {
                    // Aqui podemos adicionar lógica para Phase 3 se necessário
                    notifyPartyPhaseCompletion(party, 3, serverLevel);
                    DimTrMod.LOGGER.info("🎯 Fase 3 (bosses externos) completa para party {}", party.getLeaderId());
                }
                break;
        }
    }
    
    /**
     * Verificar se uma fase está completa considerando bosses externos (individual)
     */
    private static void checkPhaseCompletionWithExternalBossesIndividual(PlayerProgressionData playerData, int phase) {
        switch (phase) {
            case 1:
                if (isPhase1CompleteWithExternalBossesIndividual(playerData)) {
                    playerData.phase1Completed = true;
                    DimTrMod.LOGGER.info("🎯 Fase 1 completa para jogador {} (incluindo bosses externos)", playerData.getPlayerId());
                }
                break;
            case 2:
                if (isPhase2CompleteWithExternalBossesIndividual(playerData)) {
                    playerData.phase2Completed = true;
                    DimTrMod.LOGGER.info("🎯 Fase 2 completa para jogador {} (incluindo bosses externos)", playerData.getPlayerId());
                }
                break;
            case 3:
                // Phase 3 logic se necessário no futuro
                break;
        }
    }
    
    /**
     * Verificar se Fase 1 está completa incluindo bosses externos (party)
     */
    private static boolean isPhase1CompleteWithExternalBosses(PartyData party) {
        // Verificar objetivos padrão da Fase 1
        boolean standardObjectivesComplete = party.isSharedElderGuardianKilled() && 
                                           party.isSharedRaidWon() && 
                                           party.isSharedTrialVaultAdvancementEarned();
        
        if (!standardObjectivesComplete) {
            return false;
        }
        
        // Verificar bosses externos da Fase 1 se habilitados
        if (DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            var phase1Bosses = ExternalModIntegration.getBossesForPhase(1);
            for (var boss : phase1Bosses) {
                if (boss.required && !party.isSharedCustomObjectiveComplete("external_bosses", boss.entityId.replace(":", "_"))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Verificar se Fase 2 está completa incluindo bosses externos (party)
     */
    private static boolean isPhase2CompleteWithExternalBosses(PartyData party) {
        // Verificar objetivos padrão da Fase 2
        boolean standardObjectivesComplete = party.isSharedWitherKilled() && party.isSharedWardenKilled();
        
        if (!standardObjectivesComplete) {
            return false;
        }
        
        // Verificar bosses externos da Fase 2 se habilitados
        if (DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            var phase2Bosses = ExternalModIntegration.getBossesForPhase(2);
            for (var boss : phase2Bosses) {
                if (boss.required && !party.isSharedCustomObjectiveComplete("external_bosses", boss.entityId.replace(":", "_"))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Verificar se Fase 3 está completa incluindo bosses externos (party)
     */
    private static boolean isPhase3CompleteWithExternalBosses(PartyData party) {
        if (!DimTrConfig.SERVER.enableExternalModIntegration.get() || !DimTrConfig.SERVER.createPhase3ForEndBosses.get()) {
            return false;
        }
        
        var phase3Bosses = ExternalModIntegration.getBossesForPhase(3);
        for (var boss : phase3Bosses) {
            if (boss.required && !party.isSharedCustomObjectiveComplete("external_bosses", boss.entityId.replace(":", "_"))) {
                return false;
            }
        }
        
        return !phase3Bosses.isEmpty(); // Só completa se existem bosses e todos foram derrotados
    }
    
    /**
     * Verificar se Fase 1 está completa incluindo bosses externos (individual)
     */
    private static boolean isPhase1CompleteWithExternalBossesIndividual(PlayerProgressionData playerData) {
        // Verificar objetivos padrão da Fase 1
        boolean standardObjectivesComplete = playerData.elderGuardianKilled && 
                                           playerData.raidWon && 
                                           playerData.trialVaultAdvancementEarned;
        
        if (!standardObjectivesComplete) {
            return false;
        }
        
        // Verificar bosses externos da Fase 1 se habilitados
        if (DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            var phase1Bosses = ExternalModIntegration.getBossesForPhase(1);
            for (var boss : phase1Bosses) {
                if (boss.required && !playerData.isCustomObjectiveComplete("external_bosses", boss.entityId.replace(":", "_"))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Verificar se Fase 2 está completa incluindo bosses externos (individual)
     */
    private static boolean isPhase2CompleteWithExternalBossesIndividual(PlayerProgressionData playerData) {
        // Verificar objetivos padrão da Fase 2
        boolean standardObjectivesComplete = playerData.witherKilled && playerData.wardenKilled;
        
        if (!standardObjectivesComplete) {
            return false;
        }
        
        // Verificar bosses externos da Fase 2 se habilitados
        if (DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            var phase2Bosses = ExternalModIntegration.getBossesForPhase(2);
            for (var boss : phase2Bosses) {
                if (boss.required && !playerData.isCustomObjectiveComplete("external_bosses", boss.entityId.replace(":", "_"))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Notificar membros da party sobre morte de boss
     */
    private static void notifyPartyMembersOfBossKill(PartyData party, String bossEntityId, ServerLevel serverLevel) {
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                // TODO: Implementar sistema de notificação de bosses
                // Por enquanto, usar log
                DimTrMod.LOGGER.info("📢 Notificando membro {} da party sobre boss {} derrotado", 
                    member.getGameProfile().getName(), bossEntityId);
            }
        }
    }
    
    /**
     * Notificar party sobre completion de fase
     */
    private static void notifyPartyPhaseCompletion(PartyData party, int phase, ServerLevel serverLevel) {
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                // TODO: Implementar sistema de notificação de fases
                // Por enquanto, usar log
                DimTrMod.LOGGER.info("🎉 Notificando membro {} da party sobre conclusão da Fase {}", 
                    member.getGameProfile().getName(), phase);
            }
        }
    }
    
    /**
     * Obter status de completion de boss externo para um jogador
     * 
     * @param playerId ID do jogador
     * @param bossEntityId ID da entidade do boss
     * @param serverLevel Nível do servidor
     * @return true se o boss foi derrotado
     */
    public static boolean isExternalBossComplete(UUID playerId, String bossEntityId, ServerLevel serverLevel) {
        PROCESSING_LOCK.lock();
        try {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            String objectiveKey = bossEntityId.replace(":", "_");
            
            // Verificar primeiro no sistema de party se o jogador estiver em uma
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null && party.isSharedCustomObjectiveComplete("external_bosses", objectiveKey)) {
                    return true;
                }
            }
            
            // Verificar no progresso individual
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            return playerData.isCustomObjectiveComplete("external_bosses", objectiveKey);
            
        } finally {
            PROCESSING_LOCK.unlock();
        }
    }
}
