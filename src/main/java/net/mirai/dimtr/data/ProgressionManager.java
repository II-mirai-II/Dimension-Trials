package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.CustomRequirements;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.mirai.dimtr.util.Constants;
import net.mirai.dimtr.util.ConfigCache;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

/**
 * Gerenciador centralizado para progressão individual de jogadores
 */
public class ProgressionManager extends SavedData {
    private final Map<UUID, PlayerProgressionData> playerProgressions = new ConcurrentHashMap<>();
    private MinecraftServer serverForContext;

    // CORREÇÃO: Usar constante ao invés de valor hardcoded
    public static final double PROXIMITY_RADIUS = Constants.DEFAULT_PROXIMITY_RADIUS;

    public ProgressionManager() {
    }

    public ProgressionManager(CompoundTag tag, HolderLookup.Provider registries) {
        loadData(tag, registries);
    }

    public static ProgressionManager create() {
        return new ProgressionManager();
    }

    public static ProgressionManager load(CompoundTag tag, HolderLookup.Provider registries) {
        return new ProgressionManager(tag, registries);
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag compoundTag, @Nonnull HolderLookup.Provider registries) {
        ListTag playerList = new ListTag();

        for (PlayerProgressionData playerData : playerProgressions.values()) {
            playerList.add(playerData.save(registries));
        }

        compoundTag.put("players", playerList);
        return compoundTag;
    }

    private void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        playerProgressions.clear();

        if (tag.contains("players", Tag.TAG_LIST)) {
            ListTag playerList = tag.getList("players", Tag.TAG_COMPOUND);

            for (Tag playerTag : playerList) {
                if (playerTag instanceof CompoundTag playerCompound) {
                    PlayerProgressionData playerData = PlayerProgressionData.load(playerCompound, registries);
                    playerProgressions.put(playerData.getPlayerId(), playerData);
                }
            }
        }
    }

    public static ProgressionManager get(ServerLevel level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            throw new IllegalStateException("Server is null!");
        }

        ServerLevel overworldLevel = server.getLevel(Level.OVERWORLD);
        if (overworldLevel == null) {
            throw new IllegalStateException("Overworld level not found!");
        }

        ProgressionManager manager = overworldLevel.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ProgressionManager::create, ProgressionManager::load, DataFixTypes.LEVEL),
                Constants.PROGRESSION_DATA_NAME + "_individual"
        );

        if (manager.serverForContext == null) {
            manager.serverForContext = server;
        }

        return manager;
    }

    // Obter dados de progressão de um jogador específico
    public PlayerProgressionData getPlayerData(UUID playerId) {
        return playerProgressions.computeIfAbsent(playerId, PlayerProgressionData::new);
    }

    public PlayerProgressionData getPlayerData(ServerPlayer player) {
        return getPlayerData(player.getUUID());
    }

    // Incrementar kill de mob para um jogador específico
    public boolean incrementMobKill(UUID playerId, String mobType) {
        PlayerProgressionData playerData = getPlayerData(playerId);

        if (playerData.incrementMobKill(mobType)) {
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }

        return false;
    }

    // Verificar e atualizar completude das fases
    private void checkAndUpdatePhaseCompletion(PlayerProgressionData playerData) {
        checkAndCompletePhase1(playerData);
        checkAndCompletePhase2(playerData);
    }

    private void checkAndCompletePhase1(PlayerProgressionData playerData) {
        if (playerData.phase1Completed || !DimTrConfig.SERVER.enablePhase1.get()) {
            return;
        }

        // Verificar objetivos especiais
        boolean elderGuardianMet = !DimTrConfig.SERVER.reqElderGuardian.get() || playerData.elderGuardianKilled;
        boolean raidMet = !DimTrConfig.SERVER.reqRaid.get() || playerData.raidWon;
        boolean trialVaultMet = !DimTrConfig.SERVER.reqTrialVaultAdv.get() || playerData.trialVaultAdvancementEarned;
        boolean voluntaryExileMet = !DimTrConfig.SERVER.reqVoluntaryExile.get() || playerData.voluntaireExileAdvancementEarned;

        // Verificar requisitos de mobs
        boolean mobKillsMet = checkPhase1MobRequirements(playerData);

        if (elderGuardianMet && raidMet && trialVaultMet && voluntaryExileMet && mobKillsMet) {
            playerData.phase1Completed = true;

            // Notificar jogador individualmente
            if (serverForContext != null) {
                ServerPlayer player = serverForContext.getPlayerList().getPlayer(playerData.getPlayerId());
                if (player != null) {
                    player.sendSystemMessage(Component.translatable("message.dimtr.phase1_complete_individual")
                            .withStyle(ChatFormatting.GREEN));
                    
                    // 🎆 NOVO: Lançar fogos de artifício para celebrar
                    net.mirai.dimtr.util.NotificationHelper.launchCelebrationFireworks(player, 1);
                }
            }
        }
    }

    private void checkAndCompletePhase2(PlayerProgressionData playerData) {
        if (playerData.phase2Completed || !DimTrConfig.SERVER.enablePhase2.get() || !playerData.isPhase1EffectivelyComplete()) {
            return;
        }

        boolean witherMet = !DimTrConfig.SERVER.reqWither.get() || playerData.witherKilled;
        boolean wardenMet = !DimTrConfig.SERVER.reqWarden.get() || playerData.wardenKilled;
        boolean mobKillsMet = checkPhase2MobRequirements(playerData);

        if (witherMet && wardenMet && mobKillsMet) {
            playerData.phase2Completed = true;

            // Notificar jogador individualmente
            if (serverForContext != null) {
                ServerPlayer player = serverForContext.getPlayerList().getPlayer(playerData.getPlayerId());
                if (player != null) {
                    player.sendSystemMessage(Component.translatable("message.dimtr.phase2_complete_individual")
                            .withStyle(ChatFormatting.GREEN));
                    
                    // 🎆 NOVO: Lançar fogos de artifício para celebrar
                    net.mirai.dimtr.util.NotificationHelper.launchCelebrationFireworks(player, 2);
                }
            }
        }
    }

    // Implementar verificações de requisitos (similares à versão original)
    private boolean checkPhase1MobRequirements(PlayerProgressionData playerData) {
        if (!ConfigCache.isMobKillsPhase1Enabled()) {
            return true;
        }

        // 🎯 NOVO: Usar requisitos ajustados por party se aplicável
        UUID playerId = playerData.getPlayerId();
        
        return playerData.zombieKills >= getAdjustedRequirement(playerId, "zombie", DimTrConfig.SERVER.reqZombieKills.get()) &&
                playerData.skeletonKills >= getAdjustedRequirement(playerId, "skeleton", DimTrConfig.SERVER.reqSkeletonKills.get()) &&
                playerData.strayKills >= getAdjustedRequirement(playerId, "stray", DimTrConfig.SERVER.reqStrayKills.get()) &&
                playerData.huskKills >= getAdjustedRequirement(playerId, "husk", DimTrConfig.SERVER.reqHuskKills.get()) &&
                playerData.spiderKills >= getAdjustedRequirement(playerId, "spider", DimTrConfig.SERVER.reqSpiderKills.get()) &&
                playerData.creeperKills >= getAdjustedRequirement(playerId, "creeper", DimTrConfig.SERVER.reqCreeperKills.get()) &&
                playerData.drownedKills >= getAdjustedRequirement(playerId, "drowned", DimTrConfig.SERVER.reqDrownedKills.get()) &&
                playerData.endermanKills >= getAdjustedRequirement(playerId, "enderman", DimTrConfig.SERVER.reqEndermanKills.get()) &&
                playerData.witchKills >= getAdjustedRequirement(playerId, "witch", DimTrConfig.SERVER.reqWitchKills.get()) &&
                playerData.pillagerKills >= getAdjustedRequirement(playerId, "pillager", DimTrConfig.SERVER.reqPillagerKills.get()) &&
                playerData.vindicatorKills >= getAdjustedRequirement(playerId, "vindicator", DimTrConfig.SERVER.reqVindicatorKills.get()) &&
                playerData.boggedKills >= getAdjustedRequirement(playerId, "bogged", DimTrConfig.SERVER.reqBoggedKills.get()) &&
                playerData.breezeKills >= getAdjustedRequirement(playerId, "breeze", DimTrConfig.SERVER.reqBreezeKills.get()) &&
                playerData.ravagerKills >= getAdjustedRequirement(playerId, "ravager", DimTrConfig.SERVER.reqRavagerKills.get()) &&
                playerData.evokerKills >= getAdjustedRequirement(playerId, "evoker", DimTrConfig.SERVER.reqEvokerKills.get());
    }

    /**
     * 🎯 NOVO: Obter requisito ajustado por multiplicador de party
     */
    private int getAdjustedRequirement(UUID playerId, String mobType, int baseRequirement) {
        if (serverForContext == null) return baseRequirement;
        
        PartyManager partyManager = PartyManager.get((ServerLevel) serverForContext.overworld());
        return partyManager.getRequiredMobKills(playerId, mobType, baseRequirement);
    }

    private boolean checkPhase2MobRequirements(PlayerProgressionData playerData) {
        if (!DimTrConfig.SERVER.enableMobKillsPhase2.get()) {
            return true;
        }

        // 🎯 NOVO: Usar requisitos ajustados por party se aplicável
        UUID playerId = playerData.getPlayerId();

        // Verificar mobs do Nether
        boolean netherMobsMet = 
                playerData.blazeKills >= getAdjustedRequirement(playerId, "blaze", DimTrConfig.SERVER.reqBlazeKills.get()) &&
                playerData.witherSkeletonKills >= getAdjustedRequirement(playerId, "wither_skeleton", DimTrConfig.SERVER.reqWitherSkeletonKills.get()) &&
                playerData.piglinBruteKills >= getAdjustedRequirement(playerId, "piglin_brute", DimTrConfig.SERVER.reqPiglinBruteKills.get()) &&
                playerData.hoglinKills >= getAdjustedRequirement(playerId, "hoglin", DimTrConfig.SERVER.reqHoglinKills.get()) &&
                playerData.zoglinKills >= getAdjustedRequirement(playerId, "zoglin", DimTrConfig.SERVER.reqZoglinKills.get()) &&
                playerData.ghastKills >= getAdjustedRequirement(playerId, "ghast", DimTrConfig.SERVER.reqGhastKills.get()) &&
                playerData.piglinKills >= getAdjustedRequirement(playerId, "piglin", DimTrConfig.SERVER.reqPiglinKills.get());

        // Verificar mobs do Overworld com 125%
        boolean overworldMobsMet = 
                playerData.zombieKills >= getAdjustedRequirement(playerId, "zombie", getPhase2OverworldRequirement(DimTrConfig.SERVER.reqZombieKills.get())) &&
                playerData.skeletonKills >= getAdjustedRequirement(playerId, "skeleton", getPhase2OverworldRequirement(DimTrConfig.SERVER.reqSkeletonKills.get())) &&
                playerData.creeperKills >= getAdjustedRequirement(playerId, "creeper", getPhase2OverworldRequirement(DimTrConfig.SERVER.reqCreeperKills.get())) &&
                playerData.spiderKills >= getAdjustedRequirement(playerId, "spider", getPhase2OverworldRequirement(DimTrConfig.SERVER.reqSpiderKills.get())) &&
                playerData.endermanKills >= getAdjustedRequirement(playerId, "enderman", getPhase2OverworldRequirement(DimTrConfig.SERVER.reqEndermanKills.get()));

        return netherMobsMet && overworldMobsMet;
    }

    private int getPhase2OverworldRequirement(int originalRequirement) {
        return (int) Math.ceil(originalRequirement * Constants.DEFAULT_PHASE2_OVERWORLD_MULTIPLIER);
    }

    // Calcular multiplicador médio para jogadores próximos
    public double calculateAverageMultiplierNearPosition(double x, double y, double z, ServerLevel level) {
        List<ServerPlayer> nearbyPlayers = getNearbyPlayers(x, y, z, level);

        if (nearbyPlayers.isEmpty()) {
            return 1.0; // Sem players próximos = multiplicador padrão (sem bônus)
        }

        double totalMultiplier = 0.0;
        int validPlayers = 0;

        // 🎯 NOVO: Integração com sistema de party
        PartyManager partyManager = PartyManager.get(level);

        for (ServerPlayer player : nearbyPlayers) {
            PlayerProgressionData playerData = getPlayerData(player);
            
            // 🎯 MUDANÇA CRÍTICA: Só aplicar multiplicador se o player DEVERIA ter ele
            double playerMultiplier = getValidatedProgressionMultiplier(playerData);
            
            // 🎯 NOVO: Log detalhado para debug
            if (DimTrConfig.SERVER.enableDebugLogging.get()) {
                DimTrMod.LOGGER.info("Player {} near mob at ({}, {}, {}) - Phase1: {}, Phase2: {}, Multiplier: {}",
                    player.getName().getString(),
                    (int)x, (int)y, (int)z,
                    playerData.isPhase1EffectivelyComplete(),
                    playerData.isPhase2EffectivelyComplete(),
                    String.format("%.2f", playerMultiplier)
                );
            }
            
            // 🎯 NOVO: Verificar se jogador está em party e aplicar multiplicador adicional
            if (partyManager.isPlayerInParty(player.getUUID())) {
                PartyData party = partyManager.getPlayerParty(player.getUUID());
                if (party != null && party.getMemberCount() > 1) {
                    // Aplicar bônus de party baseado no número de membros próximos
                    int nearbyPartyMembers = 0;
                    for (UUID memberId : party.getMembers()) {
                        ServerPlayer member = serverForContext != null ? 
                            serverForContext.getPlayerList().getPlayer(memberId) : null;
                        if (member != null) {
                            double memberDistance = member.distanceToSqr(x, y, z);
                            if (memberDistance <= PROXIMITY_RADIUS * PROXIMITY_RADIUS) {
                                nearbyPartyMembers++;
                            }
                        }
                    }
                    
                    // 🎯 MUDANÇA: Só aplicar bônus de party se o multiplicador base for > 1.0
                    if (nearbyPartyMembers > 1 && playerMultiplier > 1.0) {
                        double partyBonus = 1.0 + (nearbyPartyMembers - 1) * DimTrConfig.SERVER.partyProgressionMultiplier.get();
                        playerMultiplier *= partyBonus;
                    }
                }
            }
            
            totalMultiplier += playerMultiplier;
            validPlayers++;
        }

        if (validPlayers == 0) {
            return 1.0;
        }

        return totalMultiplier / validPlayers;
    }

    /**
     * 🎯 NOVO: Obter multiplicador validado baseado na progressão real do player
     * Só retorna multiplicador > 1.0 se o player realmente completou as fases necessárias
     */
    private double getValidatedProgressionMultiplier(PlayerProgressionData playerData) {
        // Verificar multiplicadores de fases customizadas primeiro (maior prioridade)
        // Usar a mesma lógica do método original mas de forma pública
        double customMultiplier = getCustomPhaseMultiplierForPlayer(playerData);
        if (customMultiplier > 1.0) {
            return customMultiplier;
        }
        
        // Verificar Fase 2 (deve ter completado Fase 1 + Fase 2)
        if (playerData.isPhase2EffectivelyComplete()) {
            return DimTrConfig.SERVER.phase2Multiplier.get();
        } 
        
        // Verificar Fase 1 (deve ter completado todos os requisitos da Fase 1)
        if (playerData.isPhase1EffectivelyComplete()) {
            return DimTrConfig.SERVER.phase1Multiplier.get();
        }
        
        // Nenhuma fase completa = sem multiplicador
        return 1.0;
    }

    /**
     * 🎯 NOVO: Obter multiplicador de fase customizada para um player específico
     */
    private double getCustomPhaseMultiplierForPlayer(PlayerProgressionData playerData) {
        // Usar a mesma lógica do método privado do PlayerProgressionData
        double highestMultiplier = 1.0;
        
        // Verificar todas as fases customizadas completadas
        for (String phaseId : playerData.getCustomPhaseCompletionMap().keySet()) {
            if (playerData.isCustomPhaseComplete(phaseId)) {
                var customPhase = CustomRequirements.getCustomPhase(phaseId);
                if (customPhase != null) {
                    double phaseMultiplier = Math.max(
                        Math.max(customPhase.healthMultiplier, customPhase.damageMultiplier),
                        customPhase.xpMultiplier
                    );
                    
                    if (phaseMultiplier > highestMultiplier) {
                        highestMultiplier = phaseMultiplier;
                    }
                }
            }
        }
        
        return highestMultiplier;
    }

    private List<ServerPlayer> getNearbyPlayers(double x, double y, double z, ServerLevel level) {
        List<ServerPlayer> nearbyPlayers = new ArrayList<>();

        for (ServerPlayer player : level.players()) {
            double distance = player.distanceToSqr(x, y, z);
            if (distance <= PROXIMITY_RADIUS * PROXIMITY_RADIUS) {
                nearbyPlayers.add(player);
            }
        }

        return nearbyPlayers;
    }

    // Verificar se um jogador pode acessar dimensões
    public boolean canPlayerAccessNether(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        return playerData.isPhase1EffectivelyComplete();
    }

    public boolean canPlayerAccessEnd(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        return playerData.isPhase2EffectivelyComplete();
    }

    // Métodos de update para objetivos especiais
    public boolean updateElderGuardianKilled(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        if (!playerData.elderGuardianKilled) {
            playerData.elderGuardianKilled = true;
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }
        return false;
    }

    public boolean updateRaidWon(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        if (!playerData.raidWon) {
            playerData.raidWon = true;
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }
        return false;
    }

    public boolean updateTrialVaultAdvancementEarned(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        if (!playerData.trialVaultAdvancementEarned) {
            playerData.trialVaultAdvancementEarned = true;
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }
        return false;
    }

    public boolean updateVoluntaireExileAdvancementEarned(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        if (!playerData.voluntaireExileAdvancementEarned) {
            playerData.voluntaireExileAdvancementEarned = true;
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }
        return false;
    }

    public boolean updateWitherKilled(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        if (!playerData.witherKilled) {
            playerData.witherKilled = true;
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }
        return false;
    }

    public boolean updateWardenKilled(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        if (!playerData.wardenKilled) {
            playerData.wardenKilled = true;
            checkAndUpdatePhaseCompletion(playerData);
            markDirtyAndSendUpdates(playerId);
            return true;
        }
        return false;
    }

    private void markDirtyAndSendUpdates(UUID playerId) {
        setDirty();

        if (serverForContext != null) {
            ServerPlayer player = serverForContext.getPlayerList().getPlayer(playerId);
            if (player != null) {
                sendToClient(player);
            }
        }
    }

    public void sendToClient(ServerPlayer player) {
        try {
            PlayerProgressionData playerData = getPlayerData(player.getUUID());
            UpdateProgressionToClientPayload payload = UpdateProgressionToClientPayload.createFromPlayerData(playerData);
            PacketDistributor.sendToPlayer(player, payload);
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Failed to send individual progression data to client: {}", e.getMessage());
        }
    }

    public void setServerContext(MinecraftServer server) {
        this.serverForContext = server;
    }

    // Métodos para comandos administrativos
    public void resetPlayerProgress(UUID playerId) {
        playerProgressions.remove(playerId);
        markDirtyAndSendUpdates(playerId);
    }

    public void completePhase1ForPlayer(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        playerData.phase1Completed = true;
        
        // 🎆 NOVO: Lançar fogos de artifício ao completar fase via método administrativo
        if (serverForContext != null) {
            ServerPlayer player = serverForContext.getPlayerList().getPlayer(playerId);
            if (player != null) {
                net.mirai.dimtr.util.NotificationHelper.launchCelebrationFireworks(player, 1);
            }
        }
        
        markDirtyAndSendUpdates(playerId);
    }

    public void completePhase2ForPlayer(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        playerData.phase1Completed = true;
        playerData.phase2Completed = true;
        
        // 🎆 NOVO: Lançar fogos de artifício ao completar fase via método administrativo
        if (serverForContext != null) {
            ServerPlayer player = serverForContext.getPlayerList().getPlayer(playerId);
            if (player != null) {
                net.mirai.dimtr.util.NotificationHelper.launchCelebrationFireworks(player, 2);
            }
        }
        
        markDirtyAndSendUpdates(playerId);
    }

    // 🎯 NOVO: Obter todos os mob kills de um jogador para transferência para party
    public Map<String, Integer> getPlayerMobKills(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        Map<String, Integer> mobKills = new HashMap<>();
        
        // Fase 1 mobs
        mobKills.put("zombie", playerData.zombieKills);
        mobKills.put("skeleton", playerData.skeletonKills);
        mobKills.put("stray", playerData.strayKills);
        mobKills.put("husk", playerData.huskKills);
        mobKills.put("spider", playerData.spiderKills);
        mobKills.put("creeper", playerData.creeperKills);
        mobKills.put("drowned", playerData.drownedKills);
        mobKills.put("enderman", playerData.endermanKills);
        mobKills.put("witch", playerData.witchKills);
        mobKills.put("pillager", playerData.pillagerKills);
        mobKills.put("captain", playerData.captainKills);
        mobKills.put("vindicator", playerData.vindicatorKills);
        mobKills.put("bogged", playerData.boggedKills);
        mobKills.put("breeze", playerData.breezeKills);
        mobKills.put("ravager", playerData.ravagerKills);
        mobKills.put("evoker", playerData.evokerKills);
        
        // Fase 2 mobs
        mobKills.put("blaze", playerData.blazeKills);
        mobKills.put("wither_skeleton", playerData.witherSkeletonKills);
        mobKills.put("piglin_brute", playerData.piglinBruteKills);
        mobKills.put("hoglin", playerData.hoglinKills);
        mobKills.put("zoglin", playerData.zoglinKills);
        mobKills.put("ghast", playerData.ghastKills);
        mobKills.put("piglin", playerData.piglinKills);
        
        return mobKills;
    }
    
    // 🎯 NOVO: Restaurar mob kills de um jogador após sair da party
    public void restorePlayerMobKills(UUID playerId, Map<String, Integer> mobKillsToRestore) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        
        // Restaurar apenas se o valor for maior que o atual
        playerData.zombieKills = Math.max(playerData.zombieKills, mobKillsToRestore.getOrDefault("zombie", 0));
        playerData.skeletonKills = Math.max(playerData.skeletonKills, mobKillsToRestore.getOrDefault("skeleton", 0));
        playerData.strayKills = Math.max(playerData.strayKills, mobKillsToRestore.getOrDefault("stray", 0));
        playerData.huskKills = Math.max(playerData.huskKills, mobKillsToRestore.getOrDefault("husk", 0));
        playerData.spiderKills = Math.max(playerData.spiderKills, mobKillsToRestore.getOrDefault("spider", 0));
        playerData.creeperKills = Math.max(playerData.creeperKills, mobKillsToRestore.getOrDefault("creeper", 0));
        playerData.drownedKills = Math.max(playerData.drownedKills, mobKillsToRestore.getOrDefault("drowned", 0));
        playerData.endermanKills = Math.max(playerData.endermanKills, mobKillsToRestore.getOrDefault("enderman", 0));
        playerData.witchKills = Math.max(playerData.witchKills, mobKillsToRestore.getOrDefault("witch", 0));
        playerData.pillagerKills = Math.max(playerData.pillagerKills, mobKillsToRestore.getOrDefault("pillager", 0));
        playerData.captainKills = Math.max(playerData.captainKills, mobKillsToRestore.getOrDefault("captain", 0));
        playerData.vindicatorKills = Math.max(playerData.vindicatorKills, mobKillsToRestore.getOrDefault("vindicator", 0));
        playerData.boggedKills = Math.max(playerData.boggedKills, mobKillsToRestore.getOrDefault("bogged", 0));
        playerData.breezeKills = Math.max(playerData.breezeKills, mobKillsToRestore.getOrDefault("breeze", 0));
        playerData.ravagerKills = Math.max(playerData.ravagerKills, mobKillsToRestore.getOrDefault("ravager", 0));
        playerData.evokerKills = Math.max(playerData.evokerKills, mobKillsToRestore.getOrDefault("evoker", 0));
        
        // Fase 2 mobs
        playerData.blazeKills = Math.max(playerData.blazeKills, mobKillsToRestore.getOrDefault("blaze", 0));
        playerData.witherSkeletonKills = Math.max(playerData.witherSkeletonKills, mobKillsToRestore.getOrDefault("wither_skeleton", 0));
        playerData.piglinBruteKills = Math.max(playerData.piglinBruteKills, mobKillsToRestore.getOrDefault("piglin_brute", 0));
        playerData.hoglinKills = Math.max(playerData.hoglinKills, mobKillsToRestore.getOrDefault("hoglin", 0));
        playerData.zoglinKills = Math.max(playerData.zoglinKills, mobKillsToRestore.getOrDefault("zoglin", 0));
        playerData.ghastKills = Math.max(playerData.ghastKills, mobKillsToRestore.getOrDefault("ghast", 0));
        playerData.piglinKills = Math.max(playerData.piglinKills, mobKillsToRestore.getOrDefault("piglin", 0));
        
        // Verificar e atualizar completude das fases após restauração
        checkAndUpdatePhaseCompletion(playerData);
        markDirtyAndSendUpdates(playerId);
    }
    
    /**
     * 🎯 NOVO: Verificar se um player deveria ter multiplicadores aplicados
     * baseado em sua proximidade e fase de progressão
     */
    public boolean shouldPlayerHaveMultipliers(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        return getValidatedProgressionMultiplier(playerData) > 1.0;
    }
    
    /**
     * 🎯 NOVO: Obter informações detalhadas sobre multiplicadores para um player
     */
    public String getMultiplierDebugInfo(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        double multiplier = getValidatedProgressionMultiplier(playerData);
        
        return String.format("Player %s: Phase1=%s, Phase2=%s, CustomPhases=%d, Multiplier=%.2f",
            playerId.toString().substring(0, 8),
            playerData.isPhase1EffectivelyComplete(),
            playerData.isPhase2EffectivelyComplete(),
            playerData.getCustomPhaseCompletionMap().size(),
            multiplier
        );
    }
    
    /**
     * Serializa os dados de progressão para um backup
     * @return CompoundTag contendo todos os dados serializados
     */
    public CompoundTag serializeForBackup() {
        CompoundTag root = new CompoundTag();
        
        // Serializar dados de todos os jogadores
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, PlayerProgressionData> entry : playerProgressions.entrySet()) {
            UUID playerId = entry.getKey();
            PlayerProgressionData data = entry.getValue();
            
            CompoundTag playerTag = new CompoundTag();
            data.writeToNBT(playerTag);
            playersTag.put(playerId.toString(), playerTag);
        }
        
        root.put("players", playersTag);
        root.putLong("backupTimestamp", System.currentTimeMillis());
        
        return root;
    }
    
    /**
     * Restaura os dados de progressão a partir de um backup
     * @param backupTag Tag contendo os dados do backup
     */
    public void deserializeFromBackup(CompoundTag backupTag) {
        if (!backupTag.contains("players")) {
            DimTrMod.LOGGER.error("Dados de backup inválidos: tag 'players' não encontrada");
            return;
        }
        
        CompoundTag playersTag = backupTag.getCompound("players");
        Map<UUID, PlayerProgressionData> restoredProgressions = new ConcurrentHashMap<>();
        
        for (String uuidString : playersTag.getAllKeys()) {
            try {
                UUID playerId = UUID.fromString(uuidString);
                CompoundTag playerTag = playersTag.getCompound(uuidString);
                
                PlayerProgressionData data = new PlayerProgressionData(playerId);
                data.readFromNBT(playerTag);
                
                restoredProgressions.put(playerId, data);
            } catch (IllegalArgumentException e) {
                DimTrMod.LOGGER.warn("UUID inválido no backup: {}", uuidString);
            }
        }
        
        // Substituir dados atuais pelos restaurados
        this.playerProgressions.clear();
        this.playerProgressions.putAll(restoredProgressions);
        
        // Marcar como alterado para salvar
        this.setDirty();
        
        DimTrMod.LOGGER.info("Dados de progressão restaurados para {} jogadores", restoredProgressions.size());
    }
}