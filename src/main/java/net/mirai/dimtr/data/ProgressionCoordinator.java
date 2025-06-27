package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.util.ConfigCache;
import net.mirai.dimtr.util.Constants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Coordenador central para sincronização entre sistemas de Party e Individual
 * 
 * CORREÇÃO: Este sistema resolve o problema de double-counting e 
 * garante que apenas um sistema processa cada objetivo/kill por vez.
 * 
 * 🎯 PERFORMANCE: Implementa batching para reduzir overhead de sincronização
 */
public class ProgressionCoordinator {
    
    // ✅ NOVO: Mutex para evitar condições de corrida
    private static final Object PROCESSING_LOCK = new Object();
    
    // 🎯 PERFORMANCE: Sistema de batching para sincronização
    private static final ScheduledExecutorService SYNC_SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Set<UUID> PENDING_SYNC_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final long SYNC_DELAY_MS = 1000; // 1 segundo de delay para batching
    
    static {
        // Inicializar scheduler de sincronização em lote
        SYNC_SCHEDULER.scheduleAtFixedRate(ProgressionCoordinator::processPendingSyncs, 
            SYNC_DELAY_MS, SYNC_DELAY_MS, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Processar morte de mob com coordenação entre sistemas
     * 
     * @param playerId ID do jogador que matou o mob
     * @param mobType Tipo do mob morto
     * @param serverLevel Nível do servidor
     * @return true se foi processado com sucesso
     */
    public static boolean processMobKill(UUID playerId, String mobType, ServerLevel serverLevel) {
        // ✅ CORREÇÃO: Sincronizar para evitar double counting
        synchronized (PROCESSING_LOCK) {
            if (!ConfigCache.isPartySystemEnabled()) {
                // Sistema de party desabilitado - usar apenas individual
                ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
                return progressionManager.incrementMobKill(playerId, mobType);
            }
            
            // Sistema de party habilitado - verificar se jogador está em party
            PartyManager partyManager = PartyManager.get(serverLevel);
            PartyData playerParty = partyManager.getPlayerParty(playerId);
            
            if (playerParty != null) {
                // Jogador está em party - usar sistema de party (independente do número de membros)
                boolean processedByParty = partyManager.processPartyMobKill(playerId, mobType);
                
                if (ConfigCache.isDebugLoggingEnabled()) {
                    DimTrMod.LOGGER.info(Constants.LOG_MOB_KILL_PARTY, 
                        mobType, processedByParty ? "SUCCESS" : "FAILED");
                }
                
                return processedByParty;
            } else {
                // Jogador não está em party - usar sistema individual
                ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
                boolean processedIndividually = progressionManager.incrementMobKill(playerId, mobType);
                
                if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                    DimTrMod.LOGGER.info(Constants.LOG_MOB_KILL_INDIVIDUAL, 
                        mobType, processedIndividually ? "SUCCESS" : "FAILED");
                }
                
                return processedIndividually;
            }
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
                DimTrMod.LOGGER.info(Constants.LOG_OBJECTIVE_PARTY, 
                    objectiveType, processedByParty ? "SUCCESS" : "FAILED");
            }
            
            return processedByParty;
        } else {
            // Jogador sozinho ou não está em party - usar sistema individual
            boolean processedIndividually = processIndividualObjective(playerId, objectiveType, serverLevel);
            
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.info(Constants.LOG_OBJECTIVE_INDIVIDUAL, 
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
     * CORREÇÃO: Usar sistema de party quando jogador está em party ativa
     */
    public static boolean canPlayerAccessDimension(UUID playerId, String dimension, ServerLevel serverLevel) {
        if (!ConfigCache.isPartySystemEnabled()) {
            // Sistema de party desabilitado - verificar apenas individual
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            return switch (dimension) {
                case "nether" -> progressionManager.canPlayerAccessNether(playerId);
                case "end" -> progressionManager.canPlayerAccessEnd(playerId);
                default -> false;
            };
        }
        
        // Sistema de party habilitado - verificar qual usar
        PartyManager partyManager = PartyManager.get(serverLevel);
        PartyData playerParty = partyManager.getPlayerParty(playerId);
        
        if (playerParty != null && playerParty.getMembers().size() > 1) {
            // Jogador está em party com outros membros - usar verificação de party
            return switch (dimension) {
                case "nether" -> playerParty.canAccessNether();
                case "end" -> playerParty.canAccessEnd();
                default -> false;
            };
        } else {
            // Jogador sozinho ou não está em party - usar verificação individual
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            return switch (dimension) {
                case "nether" -> progressionManager.canPlayerAccessNether(playerId);
                case "end" -> progressionManager.canPlayerAccessEnd(playerId);
                default -> false;
            };
        }
    }
    
    /**
     * 🎯 PERFORMANCE: Agendar sincronização em lote para reduzir overhead
     */
    public static void scheduleBatchSync(UUID playerId) {
        PENDING_SYNC_PLAYERS.add(playerId);
    }
    
    /**
     * 🎯 PERFORMANCE: Processar todas as sincronizações pendentes em lote
     */
    private static void processPendingSyncs() {
        if (PENDING_SYNC_PLAYERS.isEmpty()) {
            return;
        }
        
        Set<UUID> toSync = Set.copyOf(PENDING_SYNC_PLAYERS);
        PENDING_SYNC_PLAYERS.clear();
        
        for (UUID playerId : toSync) {
            ServerPlayer player = getPlayerById(playerId);
            if (player != null) {
                sendUpdatePackets(player);
            }
        }
        
        if (DimTrConfig.SERVER.enableDebugLogging.get() && !toSync.isEmpty()) {
            DimTrMod.LOGGER.debug("🎯 Batched sync completed for {} players", toSync.size());
        }
    }
    
    /**
     * 🎯 PERFORMANCE: Limpar recursos na parada do servidor
     */
    public static void shutdown() {
        if (!SYNC_SCHEDULER.isShutdown()) {
            SYNC_SCHEDULER.shutdown();
        }
    }
    
    /**
     * Obter jogador pelo ID (placeholder para lógica real de obtenção de jogador)
     */
    private static ServerPlayer getPlayerById(UUID playerId) {
        // TODO: Implementar lógica para obter jogador pelo ID
        return null;
    }
    
    /**
     * Enviar pacotes de atualização para o jogador (placeholder para lógica real de envio de pacotes)
     */
    private static void sendUpdatePackets(ServerPlayer player) {
        // TODO: Implementar lógica para enviar pacotes de atualização para o jogador
    }
    
    /**
     * 🎯 NOVO: Verificar se jogador pode acessar dimensão customizada
     */
    public static boolean canPlayerAccessCustomDimension(UUID playerId, String dimensionString, ServerLevel serverLevel) {
        String blockingPhase = net.mirai.dimtr.config.CustomRequirements.findBlockingPhaseForDimension(dimensionString);
        
        if (blockingPhase == null) {
            return true; // Dimensão não é controlada por nenhuma fase customizada
        }
        
        // ✅ COORDENAÇÃO: Verificar party primeiro, depois individual
        synchronized (PROCESSING_LOCK) {
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
            var playerData = progressionManager.getPlayerData(playerId);
            return playerData.isCustomPhaseComplete(blockingPhase);
        }
    }
    
    /**
     * 🎯 NOVO: Processar objetivo customizado com coordenação
     */
    public static boolean processCustomObjective(UUID playerId, String phaseId, String objectiveId, ServerLevel serverLevel) {
        // ✅ COORDENAÇÃO: Verificar party primeiro, depois individual
        synchronized (PROCESSING_LOCK) {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            // 🎯 PRIMEIRO: Tentar processar para party
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null && !party.isSharedCustomObjectiveComplete(phaseId, objectiveId)) {
                    party.setSharedCustomObjectiveComplete(phaseId, objectiveId, true);
                    
                    // Verificar se a fase está completa agora
                    checkAndCompleteCustomPhase(party, phaseId, serverLevel);
                    
                    // Sincronizar com todos os membros da party
                    scheduleBatchSync(playerId);
                    for (UUID memberId : party.getMembers()) {
                        scheduleBatchSync(memberId);
                    }
                    
                    DimTrMod.LOGGER.info(Constants.LOG_PARTY_OBJECTIVE_COMPLETED, 
                            phaseId, objectiveId, playerId);
                    return true;
                }
            }
            
            // 🎯 SEGUNDO: Processar individualmente se não foi processado por party
            var playerData = progressionManager.getPlayerData(playerId);
            if (!playerData.isCustomObjectiveComplete(phaseId, objectiveId)) {
                playerData.setCustomObjectiveComplete(phaseId, objectiveId, true);
                
                // Verificar se a fase está completa agora
                checkAndCompleteCustomPhaseIndividual(playerData, phaseId);
                
                scheduleBatchSync(playerId);
                
                DimTrMod.LOGGER.info(Constants.LOG_INDIVIDUAL_OBJECTIVE_COMPLETED, 
                        phaseId, objectiveId, playerId);
                return true;
            }
            
            return false; // Já estava completo
        }
    }
    
    /**
     * 🎯 NOVO: Processar kill de mob customizado com coordenação
     */
    public static boolean processCustomMobKill(UUID playerId, String phaseId, String mobType, ServerLevel serverLevel) {
        synchronized (PROCESSING_LOCK) {
            PartyManager partyManager = PartyManager.get(serverLevel);
            ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
            
            // 🎯 PRIMEIRO: Tentar processar para party
            if (partyManager.isPlayerInParty(playerId)) {
                PartyData party = partyManager.getPlayerParty(playerId);
                if (party != null) {
                    party.incrementSharedCustomMobKill(phaseId, mobType);
                    
                    // Também incrementar no progresso individual (para restauração futura)
                    var playerData = progressionManager.getPlayerData(playerId);
                    playerData.incrementCustomMobKill(phaseId, mobType);
                    
                    // Verificar se a fase está completa agora
                    checkAndCompleteCustomPhase(party, phaseId, serverLevel);
                    
                    // Sincronizar com todos os membros da party
                    scheduleBatchSync(playerId);
                    for (UUID memberId : party.getMembers()) {
                        scheduleBatchSync(memberId);
                    }
                    
                    return true;
                }
            }
            
            // 🎯 SEGUNDO: Processar individualmente se não foi processado por party
            var playerData = progressionManager.getPlayerData(playerId);
            playerData.incrementCustomMobKill(phaseId, mobType);
            
            // Verificar se a fase está completa agora
            checkAndCompleteCustomPhaseIndividual(playerData, phaseId);
            
            scheduleBatchSync(playerId);
            return true;
        }
    }
    
    /**
     * 🎯 NOVO: Verificar e completar fase customizada para party
     */
    private static void checkAndCompleteCustomPhase(PartyData party, String phaseId, ServerLevel serverLevel) {
        if (party.isCustomPhaseComplete(phaseId)) {
            return; // Já completa
        }
        
        var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
        if (customPhase == null) {
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
                    break;
                }
            }
        }
        
        // Completar fase se todos os requisitos foram atendidos
        if (allRequirementsMet) {
            party.setCustomPhaseComplete(phaseId, true);
            
            // Notificar todos os membros da party
            for (UUID memberId : party.getMembers()) {
                ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
                if (member != null) {
                    net.mirai.dimtr.util.I18nHelper.sendMessage(member, 
                            net.mirai.dimtr.util.I18nHelper.Events.PHASE_COMPLETE, 
                            customPhase.name);
                }
            }
            
            DimTrMod.LOGGER.info(Constants.LOG_PARTY_PHASE_COMPLETED, customPhase.name, phaseId);
        }
    }
    
    /**
     * 🎯 NOVO: Verificar e completar fase customizada individual
     */
    private static void checkAndCompleteCustomPhaseIndividual(PlayerProgressionData playerData, String phaseId) {
        if (playerData.isCustomPhaseComplete(phaseId)) {
            return; // Já completa
        }
        
        var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
        if (customPhase == null) {
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
                    break;
                }
            }
        }
        
        // Completar fase se todos os requisitos foram atendidos
        if (allRequirementsMet) {
            playerData.setCustomPhaseComplete(phaseId, true);
            DimTrMod.LOGGER.info(Constants.LOG_PLAYER_PHASE_COMPLETED, customPhase.name, phaseId);
        }
    }
}
