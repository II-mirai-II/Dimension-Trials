package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.util.Constants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Coordenador especializado para progressão de parties
 * Extraído do ProgressionCoordinator monolítico para melhor separação de responsabilidades
 */
public class PartyProgressionCoordinator {
    
    private static final Object PROCESSING_LOCK = new Object();
    
    /**
     * Processar mob kill para party
     */
    public static boolean processPartyMobKill(UUID playerId, String mobType, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            PartyManager partyManager = PartyManager.get(serverLevel);
            
            if (!partyManager.isPlayerInParty(playerId)) {
                return false; // Não está em party
            }
            
            PartyData party = partyManager.getPlayerParty(playerId);
            if (party == null) {
                return false;
            }
            
            // Incrementar kill compartilhado
            boolean wasIncremented = party.incrementSharedMobKill(mobType);
            if (!wasIncremented) {
                return false; // Mob type não é válido ou já no máximo
            }
            
            partyManager.setDirty();
            
            // 🔧 CORREÇÃO CRÍTICA: Sincronizar TODOS os membros da party imediatamente
            syncAllPartyMembers(party, serverLevel);
            
            DimTrMod.LOGGER.debug("✅ Party mob kill processed and synced: {} killed {} for party {} (all {} members synced)", 
                playerId, mobType, party.getLeaderId(), party.getMembers().size());
            
            // Verificar se alguma fase foi completada
            checkPhaseCompletionForParty(party, serverLevel);
            
            return true;
        }
    }
    
    /**
     * Processar objetivo especial para party
     */
    public static boolean processPartySpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            PartyManager partyManager = PartyManager.get(serverLevel);
            
            if (!partyManager.isPlayerInParty(playerId)) {
                return false; // Não está em party
            }
            
            PartyData party = partyManager.getPlayerParty(playerId);
            if (party == null) {
                return false;
            }
            
            // Marcar objetivo como completo para toda a party
            boolean wasNewlyCompleted = false;
            
            switch (objectiveType) {
                case Constants.OBJECTIVE_TYPE_ELDER_GUARDIAN -> {
                    if (!party.isSharedElderGuardianKilled()) {
                        party.setSharedElderGuardianKilled(true);
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_RAID -> {
                    if (!party.isSharedRaidWon()) {
                        party.setSharedRaidWon(true);
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_TRIAL_VAULT -> {
                    if (!party.isSharedTrialVaultAdvancementEarned()) {
                        party.setSharedTrialVaultAdvancementEarned(true);
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_VOLUNTARY_EXILE -> {
                    if (!party.isSharedVoluntaireExileAdvancementEarned()) {
                        party.setSharedVoluntaireExileAdvancementEarned(true);
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_WITHER -> {
                    if (!party.isSharedWitherKilled()) {
                        party.setSharedWitherKilled(true);
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_WARDEN -> {
                    if (!party.isSharedWardenKilled()) {
                        party.setSharedWardenKilled(true);
                        wasNewlyCompleted = true;
                    }
                }
                default -> {
                    DimTrMod.LOGGER.warn("Unknown objective type for party: {}", objectiveType);
                    return false;
                }
            }
            
            if (wasNewlyCompleted) {
                partyManager.setDirty();
                
                // 🔧 CORREÇÃO CRÍTICA: Sincronizar TODOS os membros da party imediatamente
                syncAllPartyMembers(party, serverLevel);
                
                DimTrMod.LOGGER.info("✅ Party objective completed and synced: {} for party {} (all {} members synced)", 
                    objectiveType, party.getLeaderId(), party.getMembers().size());
                
                // Verificar se alguma fase foi completada
                checkPhaseCompletionForParty(party, serverLevel);
                
                return true;
            }
            
            return false; // Já estava completo
        }
    }
    
    /**
     * Verificar e completar fases para party
     */
    private static void checkPhaseCompletionForParty(PartyData party, ServerLevel serverLevel) {
        // Verificar Phase 1
        if (!party.isPhase1SharedCompleted() && isPhase1CompleteForParty(party)) {
            party.setPhase1SharedCompleted(true);
            DimTrMod.LOGGER.info("🎉 Phase 1 completed for party {}", party.getLeaderId());
            
            // 🎆 NOVO: Lançar fogos de artifício para todos os membros da party
            launchPartyFireworks(party, 1, serverLevel);
            
            // Notificar todos os membros
            notifyPartyMembers(party, "🎊 Phase 1 Complete! Nether Access Unlocked! 🎊", serverLevel);
        }
        
        // Verificar Phase 2
        if (!party.isPhase2SharedCompleted() && isPhase2CompleteForParty(party)) {
            party.setPhase2SharedCompleted(true);
            DimTrMod.LOGGER.info("🎉 Phase 2 completed for party {}", party.getLeaderId());
            
            // 🎆 NOVO: Lançar fogos de artifício para todos os membros da party
            launchPartyFireworks(party, 2, serverLevel);
            
            // Notificar todos os membros
            notifyPartyMembers(party, "🌟 Phase 2 Complete! The End Access Unlocked! 🌟", serverLevel);
        }
    }
    
    /**
     * 🎆 Lançar fogos de artifício para toda a party
     */
    private static void launchPartyFireworks(PartyData party, int phaseNumber, ServerLevel serverLevel) {
        java.util.List<ServerPlayer> onlineMembers = new java.util.ArrayList<>();
        
        // Coletar membros online
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null && member.level() != null) {
                onlineMembers.add(member);
            }
        }
        
        if (!onlineMembers.isEmpty()) {
            // Usar o novo sistema de celebração de party
            net.mirai.dimtr.util.NotificationHelper.launchPartyCelebrationFireworks(onlineMembers, phaseNumber);
            
            DimTrMod.LOGGER.info("🎆 Launched celebration fireworks for {} party members (Phase {})", 
                onlineMembers.size(), phaseNumber);
        }
    }
    
    /**
     * Verificar se Phase 1 está completa para party
     */
    private static boolean isPhase1CompleteForParty(PartyData party) {
        // Verificar objetivos especiais
        boolean specialObjectivesComplete = 
            party.isSharedElderGuardianKilled() &&
            party.isSharedRaidWon() &&
            party.isSharedTrialVaultAdvancementEarned();
        
        // Verificar se Voluntary Exile é necessário
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqVoluntaryExile.get()) {
            specialObjectivesComplete = specialObjectivesComplete && 
                party.isSharedVoluntaireExileAdvancementEarned();
        }
        
        // NOVO: Verificar bosses externos se a integração estiver habilitada
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            // Obter bosses externos da fase 1
            java.util.List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> phase1Bosses = 
                net.mirai.dimtr.integration.ExternalModIntegration.getBossesForPhase(1);
            
            // Verificar cada boss se é requerido
            for (net.mirai.dimtr.integration.ExternalModIntegration.BossInfo boss : phase1Bosses) {
                if (boss.required) {
                    String bossKey = boss.entityId.replace(":", "_");
                    if (!party.isSharedCustomObjectiveComplete("external_bosses", bossKey)) {
                        specialObjectivesComplete = false;
                        break;
                    }
                }
            }
        }
        
        // Verificar mob kills se habilitado
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableMobKillsPhase1.get()) {
            // Implementar verificação de mob kills para party
            // TODO: Adicionar lógica de verificação de mob kills
        }
        
        return specialObjectivesComplete;
    }
    
    /**
     * Verificar se Phase 2 está completa para party
     */
    private static boolean isPhase2CompleteForParty(PartyData party) {
        // Verificar objetivos especiais da Phase 2
        boolean specialObjectivesComplete = 
            party.isSharedWitherKilled() && 
            party.isSharedWardenKilled();
        
        // NOVO: Verificar bosses externos se a integração estiver habilitada
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            // Obter bosses externos da fase 2
            java.util.List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> phase2Bosses = 
                net.mirai.dimtr.integration.ExternalModIntegration.getBossesForPhase(2);
            
            // Verificar cada boss se é requerido
            for (net.mirai.dimtr.integration.ExternalModIntegration.BossInfo boss : phase2Bosses) {
                if (boss.required) {
                    String bossKey = boss.entityId.replace(":", "_");
                    if (!party.isSharedCustomObjectiveComplete("external_bosses", bossKey)) {
                        specialObjectivesComplete = false;
                        break;
                    }
                }
            }
        }
        
        // Verificar mob kills se habilitado
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableMobKillsPhase2.get()) {
            // TODO: Adicionar lógica de verificação de mob kills da Phase 2
        }
        
        return specialObjectivesComplete;
    }
    
    /**
     * Notificar todos os membros da party
     */
    private static void notifyPartyMembers(PartyData party, String message, ServerLevel serverLevel) {
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                net.mirai.dimtr.util.I18nHelper.sendMessage(member, "party.phase.complete", message);
            }
        }
    }
    
    /**
     * Transferir progresso individual para party quando jogador entra
     */
    public static void transferIndividualToParty(UUID playerId, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            PartyData party = partyManager.getPlayerParty(playerId);
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            
            if (party == null || playerData == null) {
                return;
            }
            
            // Transferir objetivos especiais
            if (playerData.elderGuardianKilled && !party.isSharedElderGuardianKilled()) {
                party.setSharedElderGuardianKilled(true);
            }
            if (playerData.raidWon && !party.isSharedRaidWon()) {
                party.setSharedRaidWon(true);
            }
            if (playerData.trialVaultAdvancementEarned && !party.isSharedTrialVaultAdvancementEarned()) {
                party.setSharedTrialVaultAdvancementEarned(true);
            }
            if (playerData.voluntaireExileAdvancementEarned && !party.isSharedVoluntaireExileAdvancementEarned()) {
                party.setSharedVoluntaireExileAdvancementEarned(true);
            }
            if (playerData.witherKilled && !party.isSharedWitherKilled()) {
                party.setSharedWitherKilled(true);
            }
            if (playerData.wardenKilled && !party.isSharedWardenKilled()) {
                party.setSharedWardenKilled(true);
            }
            
            // Transferir mob kills (usar o maior valor)
            // TODO: Implementar transferência de mob kills quando métodos estiverem disponíveis
            // for (var entry : playerData.getMobKills().entrySet()) {
            //     String mobType = entry.getKey();
            //     int playerKills = entry.getValue();
            //     int partyKills = party.getSharedMobKills().getOrDefault(mobType, 0);
            //     
            //     if (playerKills > partyKills) {
            //         party.setSharedMobKill(mobType, playerKills);
            //     }
            // }
            
            partyManager.setDirty();
            
            DimTrMod.LOGGER.info("Transferred individual progress to party for player {}", playerId);
        }
    }
    
    /**
     * Transferir progresso da party para individual quando jogador sai
     */
    public static void transferPartyToIndividual(UUID playerId, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            // Buscar dados da party antes da saída (deve ser chamado antes de remover da party)
            PartyManager partyManager = PartyManager.get(serverLevel);
            PartyData party = partyManager.getPlayerParty(playerId);
            
            if (party == null) {
                return;
            }
            
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            
            // Transferir objetivos especiais (manter progresso)
            if (party.isSharedElderGuardianKilled()) {
                playerData.elderGuardianKilled = true;
            }
            if (party.isSharedRaidWon()) {
                playerData.raidWon = true;
            }
            if (party.isSharedTrialVaultAdvancementEarned()) {
                playerData.trialVaultAdvancementEarned = true;
            }
            if (party.isSharedVoluntaireExileAdvancementEarned()) {
                playerData.voluntaireExileAdvancementEarned = true;
            }
            if (party.isSharedWitherKilled()) {
                playerData.witherKilled = true;
            }
            if (party.isSharedWardenKilled()) {
                playerData.wardenKilled = true;
            }
            
            // Transferir mob kills (usar o maior valor)
            // TODO: Implementar transferência de mob kills quando métodos estiverem disponíveis
            // for (var entry : party.getSharedMobKills().entrySet()) {
            //     String mobType = entry.getKey();
            //     int partyKills = entry.getValue();
            //     int playerKills = playerData.getMobKills().getOrDefault(mobType, 0);
            //     
            //     if (partyKills > playerKills) {
            //         playerData.setMobKills(mobType, partyKills);
            //     }
            // }
            
            // Transferir status de fases
            if (party.isPhase1SharedCompleted()) {
                playerData.phase1Completed = true;
            }
            if (party.isPhase2SharedCompleted()) {
                playerData.phase2Completed = true;
            }
            
            progressionManager.setDirty();
            
            DimTrMod.LOGGER.info("Transferred party progress to individual for player {}", playerId);
        }
    }
    
    /**
     * Processar morte de mob em party
     */
    public static boolean processMobKill(UUID playerId, String mobType, PartyData partyData, ServerLevel serverLevel) {
        // O método existente espera apenas playerId, mobType, serverLevel
        // e obtém a party internamente, então vamos usar ele diretamente
        return processPartyMobKill(playerId, mobType, serverLevel);
    }
    
    /**
     * Processar objetivo especial em party
     */
    public static boolean processSpecialObjective(UUID playerId, String objectiveType, PartyData partyData, ServerLevel serverLevel) {
        // O método existente espera apenas playerId, objectiveType, serverLevel
        // e obtém a party internamente, então vamos usar ele diretamente
        return processPartySpecialObjective(playerId, objectiveType, serverLevel);
    }
    
    /**
     * Verificar se jogador pode acessar dimensão (party)
     */
    public static boolean canPlayerAccessDimension(UUID playerId, String dimension, PartyData partyData, ServerLevel serverLevel) {
        return switch (dimension) {
            case "nether" -> partyData.canAccessNether();
            case "end" -> partyData.canAccessEnd();
            default -> false;
        };
    }
    
    /**
     * 🔧 CORREÇÃO CRÍTICA: Sincronizar TODOS os membros de uma party imediatamente
     * Este método garante que quando qualquer membro da party mata um mob ou completa
     * um objetivo, TODOS os membros recebem a atualização instantaneamente.
     * 
     * ✅ CORREÇÃO: Agora envia dados DE PROGRESSÃO DA PARTY, não dados individuais
     */
    private static void syncAllPartyMembers(PartyData party, ServerLevel serverLevel) {
        if (party == null || party.getMembers().isEmpty()) {
            return;
        }
        
        PartyManager partyManager = PartyManager.get(serverLevel);
        
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                // ✅ CORREÇÃO CRÍTICA: Enviar dados de PROGRESSÃO DA PARTY
                partyManager.sendPartyProgressionToClient(member);
                
                // Sincronizar dados de party (membros, etc.)
                partyManager.sendPartyToClient(member);
                
                DimTrMod.LOGGER.debug("📡 Synced party member with PARTY progression: {} ({})", 
                    memberId, member.getGameProfile().getName());
            }
        }
        
        DimTrMod.LOGGER.debug("✅ Synced all {} party members with PARTY progression data", party.getMembers().size());
    }
}
