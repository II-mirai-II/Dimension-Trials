package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Coordenador central para sincronização entre sistemas de Party e Individual
 * 
 * CORREÇÃO: Este sistema resolve o problema de double-counting e 
 * garante que apenas um sistema processa cada objetivo/kill por vez.
 */
public class ProgressionCoordinator {
    
    /**
     * Processar morte de mob com coordenação entre sistemas
     * 
     * @param playerId ID do jogador que matou o mob
     * @param mobType Tipo do mob morto
     * @param serverLevel Nível do servidor
     * @return true se foi processado com sucesso
     */
    public static boolean processMobKill(UUID playerId, String mobType, ServerLevel serverLevel) {
        if (!DimTrConfig.SERVER.enablePartySystem.get()) {
            // Sistema de party desabilitado - usar apenas individual
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            return progressionManager.incrementMobKill(playerId, mobType);
        }
        
        // Sistema de party habilitado - verificar se jogador está em party
        PartyManager partyManager = PartyManager.get(serverLevel);
        PartyData playerParty = partyManager.getPlayerParty(playerId);
        
        if (playerParty != null && playerParty.getMembers().size() > 1) {
            // Jogador está em party com outros membros - usar sistema de party
            boolean processedByParty = partyManager.processPartyMobKill(playerId, mobType);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.info("🎯 Mob kill processed by PARTY system: {} -> {}", 
                    mobType, processedByParty ? "SUCCESS" : "FAILED");
            }
            
            return processedByParty;
        } else {
            // Jogador sozinho ou não está em party - usar sistema individual
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            boolean processedIndividually = progressionManager.incrementMobKill(playerId, mobType);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.info("🎯 Mob kill processed by INDIVIDUAL system: {} -> {}", 
                    mobType, processedIndividually ? "SUCCESS" : "FAILED");
            }
            
            return processedIndividually;
        }
    }
    
    /**
     * Processar objetivo especial com coordenação entre sistemas
     * 
     * @param playerId ID do jogador que alcançou o objetivo
     * @param objectiveType Tipo do objetivo ("elder_guardian", "wither", "warden")
     * @param serverLevel Nível do servidor
     * @return true se foi processado com sucesso
     */
    public static boolean processSpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel) {
        if (!DimTrConfig.SERVER.enablePartySystem.get()) {
            // Sistema de party desabilitado - usar apenas individual
            return processIndividualObjective(playerId, objectiveType, serverLevel);
        }
        
        // Sistema de party habilitado - verificar se jogador está em party
        PartyManager partyManager = PartyManager.get(serverLevel);
        PartyData playerParty = partyManager.getPlayerParty(playerId);
        
        if (playerParty != null && playerParty.getMembers().size() > 1) {
            // Jogador está em party com outros membros - usar sistema de party
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, objectiveType);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.info("🎯 Special objective processed by PARTY system: {} -> {}", 
                    objectiveType, processedByParty ? "SUCCESS" : "FAILED");
            }
            
            return processedByParty;
        } else {
            // Jogador sozinho ou não está em party - usar sistema individual
            boolean processedIndividually = processIndividualObjective(playerId, objectiveType, serverLevel);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.info("🎯 Special objective processed by INDIVIDUAL system: {} -> {}", 
                    objectiveType, processedIndividually ? "SUCCESS" : "FAILED");
            }
            
            return processedIndividually;
        }
    }
    
    /**
     * Processar objetivo individual
     */
    private static boolean processIndividualObjective(UUID playerId, String objectiveType, ServerLevel serverLevel) {
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        
        return switch (objectiveType) {
            case "elder_guardian" -> progressionManager.updateElderGuardianKilled(playerId);
            case "wither" -> progressionManager.updateWitherKilled(playerId);
            case "warden" -> progressionManager.updateWardenKilled(playerId);
            case "raid" -> progressionManager.updateRaidWon(playerId);
            case "trial_vault" -> progressionManager.updateTrialVaultAdvancementEarned(playerId);
            case "voluntary_exile" -> progressionManager.updateVoluntaireExileAdvancementEarned(playerId);
            default -> {
                DimTrMod.LOGGER.warn("⚠️ Unknown special objective type: {}", objectiveType);
                yield false;
            }
        };
    }
    
    /**
     * Verificar se um jogador pode acessar uma dimensão
     * Considera tanto progressão individual quanto de party
     */
    public static boolean canPlayerAccessDimension(UUID playerId, String dimension, ServerLevel serverLevel) {
        if (!DimTrConfig.SERVER.enablePartySystem.get()) {
            // Sistema de party desabilitado - verificar apenas individual
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            return switch (dimension) {
                case "nether" -> progressionManager.canPlayerAccessNether(playerId);
                case "end" -> progressionManager.canPlayerAccessEnd(playerId);
                default -> false;
            };
        }
        
        // Sistema de party habilitado - verificar ambos os sistemas
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PartyManager partyManager = PartyManager.get(serverLevel);
        
        PartyData playerParty = partyManager.getPlayerParty(playerId);
        
        // Verificar acesso individual
        boolean individualAccess = switch (dimension) {
            case "nether" -> progressionManager.canPlayerAccessNether(playerId);
            case "end" -> progressionManager.canPlayerAccessEnd(playerId);
            default -> false;
        };
        
        // Se jogador está em party, verificar também acesso da party
        if (playerParty != null) {
            boolean partyAccess = switch (dimension) {
                case "nether" -> playerParty.isPhase1SharedCompleted();
                case "end" -> playerParty.isPhase2SharedCompleted();
                default -> false;
            };
            
            // Jogador pode acessar se QUALQUER um dos sistemas permitir
            return individualAccess || partyAccess;
        }
        
        return individualAccess;
    }
}
