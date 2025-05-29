package net.mirai.dimtr.data; // Pacote atualizado

import net.mirai.dimtr.DimTrMod; // Pacote e classe principal atualizados
import net.mirai.dimtr.config.DimTrConfig; // Pacote de config corrigido e importado
import net.mirai.dimtr.network.UpdateProgressionToClientPayload; // Pacote atualizado
import net.mirai.dimtr.util.Constants; // Pacote atualizado
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ProgressionData extends SavedData {

    public boolean elderGuardianKilled = false;
    public boolean raidWon = false;
    public boolean ravagerKilled = false;
    public boolean evokerKilled = false;
    public boolean trialVaultAdvancementEarned = false;
    public boolean phase1Completed = false;

    public boolean witherKilled = false;
    public boolean wardenKilled = false;
    public boolean phase2Completed = false;

    private transient MinecraftServer serverForContext;

    public ProgressionData() {
    }

    public ProgressionData(CompoundTag tag, HolderLookup.Provider registries) {
        elderGuardianKilled = tag.getBoolean("elderGuardianKilled");
        raidWon = tag.getBoolean("raidWon");
        ravagerKilled = tag.getBoolean("ravagerKilled");
        evokerKilled = tag.getBoolean("evokerKilled");
        trialVaultAdvancementEarned = tag.getBoolean("trialVaultAdvancementEarned");
        phase1Completed = tag.getBoolean("phase1Completed");
        witherKilled = tag.getBoolean("witherKilled");
        wardenKilled = tag.getBoolean("wardenKilled");
        phase2Completed = tag.getBoolean("phase2Completed");
    }

    public static ProgressionData create() {
        ProgressionData data = new ProgressionData();
        data.serverForContext = ServerLifecycleHooks.getCurrentServer();
        return data;
    }

    public static ProgressionData load(CompoundTag tag, HolderLookup.Provider registries) {
        ProgressionData data = new ProgressionData(tag, registries);
        data.serverForContext = ServerLifecycleHooks.getCurrentServer();
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        compoundTag.putBoolean("elderGuardianKilled", elderGuardianKilled);
        compoundTag.putBoolean("raidWon", raidWon);
        compoundTag.putBoolean("ravagerKilled", ravagerKilled);
        compoundTag.putBoolean("evokerKilled", evokerKilled);
        compoundTag.putBoolean("trialVaultAdvancementEarned", trialVaultAdvancementEarned);
        compoundTag.putBoolean("phase1Completed", phase1Completed);
        compoundTag.putBoolean("witherKilled", witherKilled);
        compoundTag.putBoolean("wardenKilled", wardenKilled);
        compoundTag.putBoolean("phase2Completed", phase2Completed);
        return compoundTag;
    }

    public static ProgressionData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        // ATENÇÃO: O valor de Constants.PROGRESSION_DATA_NAME deve ser atualizado
        // na classe Constants se ele continha "bvp".
        ProgressionData data = storage.computeIfAbsent(new SavedData.Factory<>(
                ProgressionData::create,
                ProgressionData::load,
                null // dataFixerType, mantido como null
        ), Constants.PROGRESSION_DATA_NAME);
        if (data.serverForContext == null) {
            data.serverForContext = level.getServer();
        }
        return data;
    }

    public void markDirtyAndSendUpdates() {
        setDirty();
        // UpdateProgressionToClientPayload será da classe no novo pacote net.mirai.dimtr.network
        UpdateProgressionToClientPayload payload = new UpdateProgressionToClientPayload(
                this.elderGuardianKilled, this.raidWon, this.ravagerKilled, this.evokerKilled,
                this.trialVaultAdvancementEarned,
                this.phase1Completed, this.witherKilled, this.wardenKilled,
                this.phase2Completed
        );
        if (serverForContext != null && serverForContext.getPlayerList() != null) {
            PacketDistributor.sendToAllPlayers(payload);
        }
        DimTrMod.LOGGER.debug("ProgressionData marked dirty and update packet sent."); // Logger atualizado
    }

    private void broadcastMessage(Component message) {
        if (serverForContext != null && serverForContext.getPlayerList() != null) {
            serverForContext.getPlayerList().broadcastSystemMessage(message, false);
        } else {
            DimTrMod.LOGGER.warn("Attempted to broadcast message, but server context was null!"); // Logger atualizado
        }
    }

    public boolean updateElderGuardianKilled(boolean value) {
        if (!this.elderGuardianKilled && value) {
            this.elderGuardianKilled = true;
            checkAndCompletePhase1(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public boolean updateRaidWon(boolean value) {
        if (!this.raidWon && value) {
            this.raidWon = true;
            checkAndCompletePhase1(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public boolean updateRavagerKilled(boolean value) {
        if (!this.ravagerKilled && value) {
            this.ravagerKilled = true;
            checkAndCompletePhase1(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public boolean updateEvokerKilled(boolean value) {
        if (!this.evokerKilled && value) {
            this.evokerKilled = true;
            checkAndCompletePhase1(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public boolean updateTrialVaultAdvancementEarned(boolean value) {
        if (!this.trialVaultAdvancementEarned && value) {
            this.trialVaultAdvancementEarned = true;
            checkAndCompletePhase1(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public boolean updateWitherKilled(boolean value) {
        if (!this.witherKilled && value) {
            this.witherKilled = true;
            checkAndCompletePhase2(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public boolean updateWardenKilled(boolean value) {
        if (!this.wardenKilled && value) {
            this.wardenKilled = true;
            checkAndCompletePhase2(true);
            markDirtyAndSendUpdates();
            return true;
        }
        return false;
    }

    public void checkAndCompletePhase1Internal() {
        checkAndCompletePhase1(false);
    }
    public void checkAndCompletePhase2Internal() {
        checkAndCompletePhase2(false);
    }

    private void checkAndCompletePhase1(boolean announce) {
        // Acesso à config corrigido para usar a classe importada DimTrConfig
        if (!DimTrConfig.SERVER.enablePhase1.get()) {
            if(!phase1Completed) {
                phase1Completed = true;
                DimTrMod.LOGGER.info("Phase 1 is disabled by server config, marking as complete."); // Logger atualizado
                if (announce) broadcastMessage(Component.translatable(Constants.MSG_PHASE1_UNLOCKED_GLOBAL_CONFIG_DISABLED).withStyle(ChatFormatting.GREEN));
            }
            checkAndCompletePhase2(announce);
            return;
        }

        boolean elderGuardianMet = DimTrConfig.SERVER.reqElderGuardian.get() ? elderGuardianKilled : true;
        boolean raidAndRavagerMet = DimTrConfig.SERVER.reqRaidAndRavager.get() ? (raidWon && ravagerKilled) : true;
        boolean evokerMet = DimTrConfig.SERVER.reqEvoker.get() ? evokerKilled : true;
        boolean trialVaultMet = DimTrConfig.SERVER.reqTrialVaultAdv.get() ? trialVaultAdvancementEarned : true;

        if (!phase1Completed && elderGuardianMet && raidAndRavagerMet && evokerMet && trialVaultMet) {
            phase1Completed = true;
            DimTrMod.LOGGER.info("Phase 1 requirements met and completed!"); // Logger atualizado
            if (announce) broadcastMessage(Component.translatable(Constants.MSG_PHASE1_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN));
            checkAndCompletePhase2(announce);
        }
    }

    private void checkAndCompletePhase2(boolean announce) {
        // Acesso à config corrigido
        if (!DimTrConfig.SERVER.enablePhase2.get()) {
            boolean phase1RequirementMetForConfig = phase1Completed || !DimTrConfig.SERVER.enablePhase1.get();
            if(phase1RequirementMetForConfig && !phase2Completed) {
                phase2Completed = true;
                DimTrMod.LOGGER.info("Phase 2 is disabled by server config, marking as complete (Phase 1 also complete/disabled)."); // Logger atualizado
                if (announce) broadcastMessage(Component.translatable(Constants.MSG_PHASE2_UNLOCKED_GLOBAL_CONFIG_DISABLED).withStyle(ChatFormatting.GREEN));
            }
            return;
        }

        boolean phase1RequirementMet = phase1Completed || !DimTrConfig.SERVER.enablePhase1.get();

        if (phase1RequirementMet && !phase2Completed) {
            boolean witherMet = DimTrConfig.SERVER.reqWither.get() ? witherKilled : true;
            boolean wardenMet = DimTrConfig.SERVER.reqWarden.get() ? wardenKilled : true;

            if (witherMet && wardenMet) {
                phase2Completed = true;
                DimTrMod.LOGGER.info("Phase 2 requirements met and completed!"); // Logger atualizado
                if (announce) broadcastMessage(Component.translatable(Constants.MSG_PHASE2_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN));
            }
        }
    }

    public boolean isPhase1EffectivelyLocked() {
        // Acesso à config corrigido
        return DimTrConfig.SERVER.enablePhase1.get() && !this.phase1Completed;
    }

    public boolean isPhase2EffectivelyLocked() {
        // Acesso à config corrigido
        boolean phase1RequirementMet = this.phase1Completed || !DimTrConfig.SERVER.enablePhase1.get();
        return DimTrConfig.SERVER.enablePhase2.get() && (!phase1RequirementMet || !this.phase2Completed);
    }
}