package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.mirai.dimtr.util.Constants;
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
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
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
                }
            }
        }
    }

    // Implementar verificações de requisitos (similares à versão original)
    private boolean checkPhase1MobRequirements(PlayerProgressionData playerData) {
        if (!DimTrConfig.SERVER.enableMobKillsPhase1.get()) {
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
            return 1.0; // Sem players próximos = multiplicador padrão
        }

        double totalMultiplier = 0.0;
        int validPlayers = 0;

        for (ServerPlayer player : nearbyPlayers) {
            PlayerProgressionData playerData = getPlayerData(player);
            double playerMultiplier = playerData.getProgressionMultiplier();
            totalMultiplier += playerMultiplier;
            validPlayers++;
        }

        if (validPlayers == 0) {
            return 1.0;
        }

        return totalMultiplier / validPlayers;
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
        markDirtyAndSendUpdates(playerId);
    }

    public void completePhase2ForPlayer(UUID playerId) {
        PlayerProgressionData playerData = getPlayerData(playerId);
        playerData.phase1Completed = true;
        playerData.phase2Completed = true;
        markDirtyAndSendUpdates(playerId);
    }
}