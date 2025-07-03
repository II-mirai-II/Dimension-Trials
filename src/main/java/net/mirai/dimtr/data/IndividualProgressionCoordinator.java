package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.util.Constants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Coordenador especializado para progressão individual
 * Extraído do ProgressionCoordinator monolítico para melhor separação de responsabilidades
 */
public class IndividualProgressionCoordinator {
    
    private static final Object PROCESSING_LOCK = new Object();
    
    /**
     * Processar mob kill individual
     */
    public static boolean processIndividualMobKill(UUID playerId, String mobType, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            
            // Incrementar kill individual
            boolean wasIncremented = playerData.incrementMobKill(mobType);
            if (!wasIncremented) {
                return false; // Mob type não é válido ou já no máximo
            }
            
            // 🔧 CORREÇÃO CRÍTICA: Marcar como dirty E enviar atualizações para o cliente
            progressionManager.setDirty();
            
            // 🔧 CORREÇÃO CRÍTICA: Enviar atualização imediata para o cliente
            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(playerId);
            if (player != null) {
                progressionManager.sendToClient(player);
            }
            
            DimTrMod.LOGGER.debug("✅ Individual mob kill processed and synced: {} killed {} ({})", 
                playerId, mobType, playerData.getMobKillCount(mobType));
            
            // Verificar se alguma fase foi completada
            checkPhaseCompletionForPlayer(playerData, progressionManager, serverLevel);
            
            return true;
        }
    }
    
    /**
     * Processar objetivo especial individual
     */
    public static boolean processIndividualSpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
            
            boolean wasNewlyCompleted = false;
            
            switch (objectiveType) {
                case Constants.OBJECTIVE_TYPE_ELDER_GUARDIAN -> {
                    if (!playerData.elderGuardianKilled) {
                        playerData.elderGuardianKilled = true;
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_RAID -> {
                    if (!playerData.raidWon) {
                        playerData.raidWon = true;
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_TRIAL_VAULT -> {
                    if (!playerData.trialVaultAdvancementEarned) {
                        playerData.trialVaultAdvancementEarned = true;
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_VOLUNTARY_EXILE -> {
                    if (!playerData.voluntaireExileAdvancementEarned) {
                        playerData.voluntaireExileAdvancementEarned = true;
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_WITHER -> {
                    if (!playerData.witherKilled) {
                        playerData.witherKilled = true;
                        wasNewlyCompleted = true;
                    }
                }
                case Constants.OBJECTIVE_TYPE_WARDEN -> {
                    if (!playerData.wardenKilled) {
                        playerData.wardenKilled = true;
                        wasNewlyCompleted = true;
                    }
                }
                default -> {
                    DimTrMod.LOGGER.warn("Unknown objective type for individual: {}", objectiveType);
                    return false;
                }
            }
            
            if (wasNewlyCompleted) {
                // 🔧 CORREÇÃO CRÍTICA: Marcar como dirty E enviar atualizações para o cliente
                progressionManager.setDirty();
                
                // 🔧 CORREÇÃO CRÍTICA: Enviar atualização imediata para o cliente
                ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(playerId);
                if (player != null) {
                    progressionManager.sendToClient(player);
                }
                
                DimTrMod.LOGGER.info("✅ Individual objective completed and synced: {} for player {}", 
                    objectiveType, playerId);
                
                // Verificar se alguma fase foi completada
                checkPhaseCompletionForPlayer(playerData, progressionManager, serverLevel);
                
                return true;
            }
            
            return false; // Já estava completo
        }
    }
    
    /**
     * Processar morte de mob individual
     */
    public static boolean processMobKill(UUID playerId, String mobType, ServerLevel serverLevel) {
        return processIndividualMobKill(playerId, mobType, serverLevel);
    }
    
    /**
     * Processar objetivo especial individual
     */
    public static boolean processSpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel) {
        return processIndividualSpecialObjective(playerId, objectiveType, serverLevel);
    }
    
    /**
     * Verificar se jogador pode acessar dimensão (individual)
     */
    public static boolean canPlayerAccessDimension(UUID playerId, String dimension, ServerLevel serverLevel) {
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        return switch (dimension) {
            case "nether" -> progressionManager.canPlayerAccessNether(playerId);
            case "end" -> progressionManager.canPlayerAccessEnd(playerId);
            default -> false;
        };
    }
    
    /**
     * Verificar e completar fases para jogador individual
     */
    private static void checkPhaseCompletionForPlayer(PlayerProgressionData playerData, 
                                                     ProgressionManager progressionManager, 
                                                     ServerLevel serverLevel) {
        // Verificar Phase 1
        if (!playerData.phase1Completed && isPhase1CompleteForPlayer(playerData)) {
            playerData.phase1Completed = true;
            progressionManager.setDirty();
            
            DimTrMod.LOGGER.info("Phase 1 completed for individual player {}", playerData.getPlayerId());
            
            // Notificar jogador
            var player = serverLevel.getServer().getPlayerList().getPlayer(playerData.getPlayerId());
            if (player != null) {
                net.mirai.dimtr.util.I18nHelper.sendMessage(player, "progression.phase1.complete");
            }
        }
        
        // Verificar Phase 2
        if (!playerData.phase2Completed && isPhase2CompleteForPlayer(playerData)) {
            playerData.phase2Completed = true;
            progressionManager.setDirty();
            
            DimTrMod.LOGGER.info("Phase 2 completed for individual player {}", playerData.getPlayerId());
            
            // Notificar jogador
            var player = serverLevel.getServer().getPlayerList().getPlayer(playerData.getPlayerId());
            if (player != null) {
                net.mirai.dimtr.util.I18nHelper.sendMessage(player, "progression.phase2.complete");
            }
        }
    }
    
    /**
     * Verificar se Phase 1 está completa para jogador individual
     */
    private static boolean isPhase1CompleteForPlayer(PlayerProgressionData playerData) {
        // Verificar objetivos especiais
        boolean specialObjectivesComplete = 
            playerData.elderGuardianKilled &&
            playerData.raidWon &&
            playerData.trialVaultAdvancementEarned;
        
        // Verificar se Voluntary Exile é necessário
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqVoluntaryExile.get()) {
            specialObjectivesComplete = specialObjectivesComplete && 
                playerData.voluntaireExileAdvancementEarned;
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
                    if (!playerData.isCustomObjectiveComplete("external_bosses", bossKey)) {
                        specialObjectivesComplete = false;
                        break;
                    }
                }
            }
        }
        
        // Verificar mob kills se habilitado
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableMobKillsPhase1.get()) {
            boolean mobKillsComplete = checkPhase1MobKills(playerData);
            return specialObjectivesComplete && mobKillsComplete;
        }
        
        return specialObjectivesComplete;
    }
    
    /**
     * Verificar se Phase 2 está completa para jogador individual
     */
    private static boolean isPhase2CompleteForPlayer(PlayerProgressionData playerData) {
        // Verificar objetivos especiais da Phase 2
        boolean specialObjectivesComplete = 
            playerData.witherKilled && 
            playerData.wardenKilled;
            
        // NOVO: Verificar bosses externos se a integração estiver habilitada
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            // Obter bosses externos da fase 2
            java.util.List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> phase2Bosses = 
                net.mirai.dimtr.integration.ExternalModIntegration.getBossesForPhase(2);
            
            // Verificar cada boss se é requerido
            for (net.mirai.dimtr.integration.ExternalModIntegration.BossInfo boss : phase2Bosses) {
                if (boss.required) {
                    String bossKey = boss.entityId.replace(":", "_");
                    if (!playerData.isCustomObjectiveComplete("external_bosses", bossKey)) {
                        specialObjectivesComplete = false;
                        break;
                    }
                }
            }
        }
        
        // Verificar mob kills se habilitado
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableMobKillsPhase2.get()) {
            boolean mobKillsComplete = checkPhase2MobKills(playerData);
            return specialObjectivesComplete && mobKillsComplete;
        }
        
        return specialObjectivesComplete;
    }
    
    /**
     * Verificar mob kills da Phase 1 para jogador individual
     */
    private static boolean checkPhase1MobKills(PlayerProgressionData playerData) {
        // Verificar kills necessários para Phase 1
        String[] phase1Mobs = {
            "zombie", "skeleton", "stray", "husk", "spider", "creeper", "drowned",
            "enderman", "witch", "pillager", "captain", "vindicator", "bogged", "breeze",
            "ravager", "evoker"
        };
        
        for (String mobType : phase1Mobs) {
            int required = getRequiredKills(mobType, 1);
            if (required > 0) {
                int current = playerData.getMobKillCount(mobType);
                if (current < required) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Verificar mob kills da Phase 2 para jogador individual
     */
    private static boolean checkPhase2MobKills(PlayerProgressionData playerData) {
        // Verificar kills necessários para Phase 2
        String[] phase2Mobs = {
            "blaze", "wither_skeleton", "piglin_brute", "hoglin", "zoglin", "ghast", "piglin",
            // Também mobs do Overworld com requisitos aumentados
            "zombie", "skeleton", "creeper", "spider", "enderman", "witch", "pillager",
            "ravager", "evoker"
        };
        
        for (String mobType : phase2Mobs) {
            int required = getRequiredKills(mobType, 2);
            if (required > 0) {
                int current = playerData.getMobKillCount(mobType);
                if (current < required) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Obter kills necessários para um mob específico e fase
     */
    private static int getRequiredKills(String mobType, int phase) {
        var config = net.mirai.dimtr.config.DimTrConfig.SERVER;
        
        if (phase == 1) {
            return switch (mobType) {
                case "zombie" -> config.reqZombieKills.get();
                case "skeleton" -> config.reqSkeletonKills.get();
                case "stray" -> config.reqStrayKills.get();
                case "husk" -> config.reqHuskKills.get();
                case "spider" -> config.reqSpiderKills.get();
                case "creeper" -> config.reqCreeperKills.get();
                case "drowned" -> config.reqDrownedKills.get();
                case "enderman" -> config.reqEndermanKills.get();
                case "witch" -> config.reqWitchKills.get();
                case "pillager" -> config.reqPillagerKills.get();
                case "captain" -> config.reqCaptainKills.get();
                case "vindicator" -> config.reqVindicatorKills.get();
                case "bogged" -> config.reqBoggedKills.get();
                case "breeze" -> config.reqBreezeKills.get();
                case "ravager" -> config.reqRavagerKills.get();
                case "evoker" -> config.reqEvokerKills.get();
                default -> 0;
            };
        } else if (phase == 2) {
            // Phase 2 - Nether mobs
            int netherRequirement = switch (mobType) {
                case "blaze" -> config.reqBlazeKills.get();
                case "wither_skeleton" -> config.reqWitherSkeletonKills.get();
                case "piglin_brute" -> config.reqPiglinBruteKills.get();
                case "hoglin" -> config.reqHoglinKills.get();
                case "zoglin" -> config.reqZoglinKills.get();
                case "ghast" -> config.reqGhastKills.get();
                case "piglin" -> config.reqPiglinKills.get();
                default -> 0;
            };
            
            if (netherRequirement > 0) {
                return netherRequirement;
            }
            
            // Phase 2 - Overworld mobs com 125% dos requisitos
            int overworldBase = switch (mobType) {
                case "zombie" -> config.reqZombieKills.get();
                case "skeleton" -> config.reqSkeletonKills.get();
                case "creeper" -> config.reqCreeperKills.get();
                case "spider" -> config.reqSpiderKills.get();
                case "enderman" -> config.reqEndermanKills.get();
                case "witch" -> config.reqWitchKills.get();
                case "pillager" -> config.reqPillagerKills.get();
                case "ravager" -> config.reqRavagerKills.get();
                case "evoker" -> config.reqEvokerKills.get();
                default -> 0;
            };
            
            return (int) Math.ceil(overworldBase * 1.25);
        }
        
        return 0;
    }
}
