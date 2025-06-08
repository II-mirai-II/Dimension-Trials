package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.mirai.dimtr.util.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.network.PacketDistributor;

public class ProgressionData extends SavedData {

    // Objetivos originais
    public boolean elderGuardianKilled = false;
    public boolean raidWon = false;
    public boolean ravagerKilled = false; // Manter para compatibilidade, mas não usar na lógica principal
    public boolean evokerKilled = false; // Manter para compatibilidade, mas não usar na lógica principal
    public boolean trialVaultAdvancementEarned = false;
    // NOVO: Conquista Voluntaire Exile para Capitães Pillager
    public boolean voluntaireExileAdvancementEarned = false;
    public boolean phase1Completed = false;

    public boolean witherKilled = false;
    public boolean wardenKilled = false;
    public boolean phase2Completed = false;

    // Novos contadores de mobs - Fase 1 (Overworld)
    public int zombieKills = 0;
    // REMOVIDO: zombieVillagerKills - não é mais necessário
    public int skeletonKills = 0;
    public int strayKills = 0;
    public int huskKills = 0;
    public int spiderKills = 0;
    public int creeperKills = 0;
    public int drownedKills = 0;
    public int endermanKills = 0;
    public int witchKills = 0;
    public int pillagerKills = 0;
    public int captainKills = 0;
    public int vindicatorKills = 0;
    public int boggedKills = 0;
    public int breezeKills = 0;

    // NOVO: Ravager e Evoker agora são Goal Kills
    public int ravagerKills = 0;
    public int evokerKills = 0;

    // Novos contadores de mobs - Fase 2 (Nether + High Level)
    public int blazeKills = 0;
    public int witherSkeletonKills = 0;
    public int piglinBruteKills = 0;
    public int hoglinKills = 0;
    public int zoglinKills = 0;
    public int ghastKills = 0;
    public int endermiteKills = 0;
    public int piglinKills = 0;

    // CORREÇÃO: Tornar não-transient e inicializar adequadamente
    private MinecraftServer serverForContext;

    public ProgressionData() {
    }

    public ProgressionData(CompoundTag tag, HolderLookup.Provider registries) {
        // Carregar objetivos originais
        elderGuardianKilled = tag.getBoolean("elderGuardianKilled");
        raidWon = tag.getBoolean("raidWon");
        ravagerKilled = tag.getBoolean("ravagerKilled"); // Manter para compatibilidade
        evokerKilled = tag.getBoolean("evokerKilled"); // Manter para compatibilidade
        trialVaultAdvancementEarned = tag.getBoolean("trialVaultAdvancementEarned");
        // NOVO: Carregar conquista Voluntaire Exile
        voluntaireExileAdvancementEarned = tag.getBoolean("voluntaireExileAdvancementEarned");
        phase1Completed = tag.getBoolean("phase1Completed");
        witherKilled = tag.getBoolean("witherKilled");
        wardenKilled = tag.getBoolean("wardenKilled");
        phase2Completed = tag.getBoolean("phase2Completed");

        // Carregar contadores de mobs - Fase 1
        zombieKills = tag.getInt("zombieKills");
        // REMOVIDO: zombieVillagerKills - não carregar mais
        skeletonKills = tag.getInt("skeletonKills");
        strayKills = tag.getInt("strayKills");
        huskKills = tag.getInt("huskKills");
        spiderKills = tag.getInt("spiderKills");
        creeperKills = tag.getInt("creeperKills");
        drownedKills = tag.getInt("drownedKills");
        endermanKills = tag.getInt("endermanKills");
        witchKills = tag.getInt("witchKills");
        pillagerKills = tag.getInt("pillagerKills");
        captainKills = tag.getInt("captainKills");
        vindicatorKills = tag.getInt("vindicatorKills");
        boggedKills = tag.getInt("boggedKills");
        breezeKills = tag.getInt("breezeKills");

        // NOVO: Carregar Ravager e Evoker
        ravagerKills = tag.getInt("ravagerKills");
        evokerKills = tag.getInt("evokerKills");

        // Carregar contadores de mobs - Fase 2
        blazeKills = tag.getInt("blazeKills");
        witherSkeletonKills = tag.getInt("witherSkeletonKills");
        piglinBruteKills = tag.getInt("piglinBruteKills");
        hoglinKills = tag.getInt("hoglinKills");
        zoglinKills = tag.getInt("zoglinKills");
        ghastKills = tag.getInt("ghastKills");
        endermiteKills = tag.getInt("endermiteKills");
        piglinKills = tag.getInt("piglinKills");
    }

    public static ProgressionData create() {
        return new ProgressionData();
    }

    public static ProgressionData load(CompoundTag tag, HolderLookup.Provider registries) {
        return new ProgressionData(tag, registries);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        // Salvar objetivos originais
        compoundTag.putBoolean("elderGuardianKilled", elderGuardianKilled);
        compoundTag.putBoolean("raidWon", raidWon);
        compoundTag.putBoolean("ravagerKilled", ravagerKilled);
        compoundTag.putBoolean("evokerKilled", evokerKilled);
        compoundTag.putBoolean("trialVaultAdvancementEarned", trialVaultAdvancementEarned);
        // NOVO: Salvar conquista Voluntaire Exile
        compoundTag.putBoolean("voluntaireExileAdvancementEarned", voluntaireExileAdvancementEarned);
        compoundTag.putBoolean("phase1Completed", phase1Completed);
        compoundTag.putBoolean("witherKilled", witherKilled);
        compoundTag.putBoolean("wardenKilled", wardenKilled);
        compoundTag.putBoolean("phase2Completed", phase2Completed);

        // Salvar contadores de mobs - Fase 1
        compoundTag.putInt("zombieKills", zombieKills);
        // REMOVIDO: zombieVillagerKills - não salvar mais
        compoundTag.putInt("skeletonKills", skeletonKills);
        compoundTag.putInt("strayKills", strayKills);
        compoundTag.putInt("huskKills", huskKills);
        compoundTag.putInt("spiderKills", spiderKills);
        compoundTag.putInt("creeperKills", creeperKills);
        compoundTag.putInt("drownedKills", drownedKills);
        compoundTag.putInt("endermanKills", endermanKills);
        compoundTag.putInt("witchKills", witchKills);
        compoundTag.putInt("pillagerKills", pillagerKills);
        compoundTag.putInt("captainKills", captainKills);
        compoundTag.putInt("vindicatorKills", vindicatorKills);
        compoundTag.putInt("boggedKills", boggedKills);
        compoundTag.putInt("breezeKills", breezeKills);

        // NOVO: Salvar Ravager e Evoker
        compoundTag.putInt("ravagerKills", ravagerKills);
        compoundTag.putInt("evokerKills", evokerKills);

        // Salvar contadores de mobs - Fase 2
        compoundTag.putInt("blazeKills", blazeKills);
        compoundTag.putInt("witherSkeletonKills", witherSkeletonKills);
        compoundTag.putInt("piglinBruteKills", piglinBruteKills);
        compoundTag.putInt("hoglinKills", hoglinKills);
        compoundTag.putInt("zoglinKills", zoglinKills);
        compoundTag.putInt("ghastKills", ghastKills);
        compoundTag.putInt("endermiteKills", endermiteKills);
        compoundTag.putInt("piglinKills", piglinKills);

        return compoundTag;
    }

    public static ProgressionData get(ServerLevel level) {
        // CORREÇÃO: Garantir que o server context seja definido
        ProgressionData data = level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ProgressionData::create, ProgressionData::load, DataFixTypes.LEVEL),
                Constants.PROGRESSION_DATA_NAME
        );

        // CORREÇÃO: Definir server context se ainda não foi definido
        if (data.serverForContext == null) {
            data.serverForContext = level.getServer();
        }

        return data;
    }

    public void setServerContext(MinecraftServer server) {
        this.serverForContext = server;
    }

    // CORREÇÃO: Método seguro para verificar se a Fase 1 está efetivamente bloqueada
    public boolean isPhase1EffectivelyLocked() {
        boolean configEnabled = DimTrConfig.SERVER.enablePhase1.get();
        boolean isCompleted = phase1Completed;

        DimTrMod.LOGGER.debug("Phase 1 Lock Check - Config: {}, Completed: {}, Result: {}",
                configEnabled, isCompleted, configEnabled && !isCompleted);

        if (!configEnabled) {
            return false; // Se desabilitado na config, não está bloqueado
        }
        return !isCompleted; // Bloqueado se não foi completa
    }

    // CORREÇÃO: Método seguro para verificar se a Fase 2 está efetivamente bloqueada
    public boolean isPhase2EffectivelyLocked() {
        boolean configEnabled = DimTrConfig.SERVER.enablePhase2.get();
        boolean isCompleted = phase2Completed;
        boolean phase1Complete = isPhase1EffectivelyComplete();

        DimTrMod.LOGGER.debug("Phase 2 Lock Check - Config: {}, Phase1Complete: {}, Phase2Complete: {}, Result: {}",
                configEnabled, phase1Complete, isCompleted, configEnabled && (!phase1Complete || !isCompleted));

        if (!configEnabled) {
            return false; // Se desabilitado na config, não está bloqueado
        }
        if (!phase1Complete) {
            return true; // Se Fase 1 não está completa, Fase 2 está bloqueada
        }
        return !isCompleted; // Bloqueado se não foi completa
    }

    // CORREÇÃO: Método para verificar se Fase 1 está efetivamente completa
    private boolean isPhase1EffectivelyComplete() {
        boolean configEnabled = DimTrConfig.SERVER.enablePhase1.get();
        boolean isCompleted = phase1Completed;

        DimTrMod.LOGGER.debug("Phase 1 Complete Check - Config: {}, Completed: {}, Result: {}",
                configEnabled, isCompleted, !configEnabled || isCompleted);

        if (!configEnabled) {
            return true; // Se desabilitado na config, considera completa
        }
        return isCompleted; // Completa se foi marcada como completa
    }

    // Método para incrementar kills e verificar progresso
    public boolean incrementMobKill(String mobType) {
        boolean updated = false;

        switch (mobType) {
            case "zombie" -> { zombieKills++; updated = true; }
            // REMOVIDO: case "zombie_villager" - não incrementar mais
            case "skeleton" -> { skeletonKills++; updated = true; }
            case "stray" -> { strayKills++; updated = true; }
            case "husk" -> { huskKills++; updated = true; }
            case "spider" -> { spiderKills++; updated = true; }
            case "creeper" -> { creeperKills++; updated = true; }
            case "drowned" -> { drownedKills++; updated = true; }
            case "enderman" -> { endermanKills++; updated = true; }
            case "witch" -> { witchKills++; updated = true; }
            case "pillager" -> { pillagerKills++; updated = true; }
            case "captain" -> { captainKills++; updated = true; }
            case "vindicator" -> { vindicatorKills++; updated = true; }
            case "bogged" -> { boggedKills++; updated = true; }
            case "breeze" -> { breezeKills++; updated = true; }

            // NOVO: Ravager e Evoker agora são Goal Kills
            case "ravager" -> { ravagerKills++; updated = true; }
            case "evoker" -> { evokerKills++; updated = true; }

            // Fase 2 mobs
            case "blaze" -> { blazeKills++; updated = true; }
            case "wither_skeleton" -> { witherSkeletonKills++; updated = true; }
            case "piglin_brute" -> { piglinBruteKills++; updated = true; }
            case "hoglin" -> { hoglinKills++; updated = true; }
            case "zoglin" -> { zoglinKills++; updated = true; }
            case "ghast" -> { ghastKills++; updated = true; }
            case "endermite" -> { endermiteKills++; updated = true; }
            case "piglin" -> { piglinKills++; updated = true; }
        }

        if (updated) {
            DimTrMod.LOGGER.debug("Incremented {} kill count", mobType);
            checkAndCompletePhase1(true);
            checkAndCompletePhase2(true);
            markDirtyAndSendUpdates();
            return true;
        }

        return false;
    }

    // Método helper para obter contagem de um mob específico
    public int getMobKillCount(String mobType) {
        return switch (mobType) {
            case "zombie" -> zombieKills;
            // REMOVIDO: case "zombie_villager" - retornar 0
            case "skeleton" -> skeletonKills;
            case "stray" -> strayKills;
            case "husk" -> huskKills;
            case "spider" -> spiderKills;
            case "creeper" -> creeperKills;
            case "drowned" -> drownedKills;
            case "enderman" -> endermanKills;
            case "witch" -> witchKills;
            case "pillager" -> pillagerKills;
            case "captain" -> captainKills;
            case "vindicator" -> vindicatorKills;
            case "bogged" -> boggedKills;
            case "breeze" -> breezeKills;

            // NOVO: Ravager e Evoker
            case "ravager" -> ravagerKills;
            case "evoker" -> evokerKills;

            // Fase 2
            case "blaze" -> blazeKills;
            case "wither_skeleton" -> witherSkeletonKills;
            case "piglin_brute" -> piglinBruteKills;
            case "hoglin" -> hoglinKills;
            case "zoglin" -> zoglinKills;
            case "ghast" -> ghastKills;
            case "endermite" -> endermiteKills;
            case "piglin" -> piglinKills;
            default -> 0;
        };
    }

    public void markDirtyAndSendUpdates() {
        setDirty();

        // CORREÇÃO: Verificação de null mais robusta
        if (serverForContext != null && serverForContext.getPlayerList() != null) {
            for (ServerPlayer player : serverForContext.getPlayerList().getPlayers()) {
                sendToClient(player);
            }
        }
    }

    public void sendToClient(ServerPlayer player) {
        try {
            UpdateProgressionToClientPayload payload = createPayload();
            // CORREÇÃO: Usar PacketDistributor.sendToPlayer() diretamente
            PacketDistributor.sendToPlayer(player, payload);
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Failed to send progression data to client: {}", e.getMessage());
        }
    }

    public UpdateProgressionToClientPayload createPayload() {
        return new UpdateProgressionToClientPayload(
                elderGuardianKilled, raidWon, ravagerKilled, evokerKilled, trialVaultAdvancementEarned,
                voluntaireExileAdvancementEarned, phase1Completed, witherKilled, wardenKilled, phase2Completed,
                zombieKills, 0, // NOVO: zombieVillagerKills sempre 0
                skeletonKills, strayKills, huskKills, spiderKills,
                creeperKills, drownedKills, endermanKills, witchKills, pillagerKills, captainKills,
                vindicatorKills, boggedKills, breezeKills, ravagerKills, evokerKills,
                blazeKills, witherSkeletonKills, piglinBruteKills, hoglinKills, zoglinKills,
                ghastKills, endermiteKills, piglinKills,
                // NOVO: Sincronizar configurações do servidor para o cliente
                DimTrConfig.SERVER.reqZombieKills.get(),
                0, // NOVO: reqZombieVillagerKills sempre 0
                DimTrConfig.SERVER.reqSkeletonKills.get(),
                DimTrConfig.SERVER.reqStrayKills.get(),
                DimTrConfig.SERVER.reqHuskKills.get(),
                DimTrConfig.SERVER.reqSpiderKills.get(),
                DimTrConfig.SERVER.reqCreeperKills.get(),
                DimTrConfig.SERVER.reqDrownedKills.get(),
                DimTrConfig.SERVER.reqEndermanKills.get(),
                DimTrConfig.SERVER.reqWitchKills.get(),
                DimTrConfig.SERVER.reqPillagerKills.get(),
                DimTrConfig.SERVER.reqCaptainKills.get(),
                DimTrConfig.SERVER.reqVindicatorKills.get(),
                DimTrConfig.SERVER.reqBoggedKills.get(),
                DimTrConfig.SERVER.reqBreezeKills.get(),
                DimTrConfig.SERVER.reqRavagerKills.get(), // Enviará 1
                DimTrConfig.SERVER.reqEvokerKills.get(),  // Enviará 5
                DimTrConfig.SERVER.reqBlazeKills.get(),
                DimTrConfig.SERVER.reqWitherSkeletonKills.get(),
                DimTrConfig.SERVER.reqPiglinBruteKills.get(),
                DimTrConfig.SERVER.reqHoglinKills.get(),  // Enviará 1
                DimTrConfig.SERVER.reqZoglinKills.get(),  // Enviará 1
                DimTrConfig.SERVER.reqGhastKills.get(),
                DimTrConfig.SERVER.reqEndermiteKills.get(),
                DimTrConfig.SERVER.reqPiglinKills.get(),
                // NOVO: Sincronizar configuração Voluntary Exile
                DimTrConfig.SERVER.reqVoluntaryExile.get()
        );
    }

    private void broadcastMessage(Component message) {
        if (serverForContext != null && serverForContext.getPlayerList() != null) {
            serverForContext.getPlayerList().broadcastSystemMessage(message, false);
        } else {
            DimTrMod.LOGGER.warn("Attempted to broadcast message, but server context was null!");
        }
    }

    // Métodos de update existentes permanecem iguais
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
        if (ravagerKilled != value) {
            ravagerKilled = value;
            DimTrMod.LOGGER.info("Ravager killed status updated: {}", value);
            return true;
        }
        return false;
    }

    public boolean updateEvokerKilled(boolean value) {
        if (evokerKilled != value) {
            evokerKilled = value;
            DimTrMod.LOGGER.info("Evoker killed status updated: {}", value);
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

    public boolean updateVoluntaireExileAdvancementEarned(boolean value) {
        if (!this.voluntaireExileAdvancementEarned && value) {
            this.voluntaireExileAdvancementEarned = true;
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
        if (!phase1Completed && DimTrConfig.SERVER.enablePhase1.get()) {
            boolean elderGuardianMet = DimTrConfig.SERVER.reqElderGuardian.get() ? elderGuardianKilled : true;
            boolean raidMet = DimTrConfig.SERVER.reqRaid.get() ? raidWon : true;
            boolean trialVaultMet = DimTrConfig.SERVER.reqTrialVaultAdv.get() ? trialVaultAdvancementEarned : true;

            // CORRIGIDO: Usar configuração específica para Voluntary Exile
            boolean voluntaryExileMet = DimTrConfig.SERVER.reqVoluntaryExile.get() ? voluntaireExileAdvancementEarned : true;

            // Verificar novos requisitos de mobs
            boolean mobKillsMet = checkPhase1MobRequirements();

            if (elderGuardianMet && raidMet && trialVaultMet && voluntaryExileMet && mobKillsMet) {
                phase1Completed = true;
                DimTrMod.LOGGER.info("Phase 1 requirements met and completed!");
                if (announce) broadcastMessage(Component.translatable(Constants.MSG_PHASE1_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN));
            }
        }
    }

    private void checkAndCompletePhase2(boolean announce) {
        if (!phase2Completed && DimTrConfig.SERVER.enablePhase2.get() && phase1Completed) {
            boolean witherMet = DimTrConfig.SERVER.reqWither.get() ? witherKilled : true;
            boolean wardenMet = DimTrConfig.SERVER.reqWarden.get() ? wardenKilled : true;

            // Verificar requisitos de mobs da Fase 2
            boolean mobKillsMet = checkPhase2MobRequirements();

            if (witherMet && wardenMet && mobKillsMet) {
                phase2Completed = true;
                DimTrMod.LOGGER.info("Phase 2 requirements met and completed!");
                if (announce) broadcastMessage(Component.translatable(Constants.MSG_PHASE2_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN));
            }
        }
    }

    // Métodos auxiliares para verificar requisitos de mobs - IMPLEMENTAÇÃO COMPLETA
    private boolean checkPhase1MobRequirements() {
        // Se os mob kills da Fase 1 estão desabilitados, considerar como cumprido
        if (!DimTrConfig.SERVER.enableMobKillsPhase1.get()) {
            return true;
        }

        // Verificar cada tipo de mob e sua quantidade exigida
        boolean zombiesMet = zombieKills >= DimTrConfig.SERVER.reqZombieKills.get();
        // REMOVIDO: zombieVillagersMet - não verificar mais
        boolean skeletonsMet = skeletonKills >= DimTrConfig.SERVER.reqSkeletonKills.get();
        boolean straysMet = strayKills >= DimTrConfig.SERVER.reqStrayKills.get();
        boolean husksMet = huskKills >= DimTrConfig.SERVER.reqHuskKills.get();
        boolean spidersMet = spiderKills >= DimTrConfig.SERVER.reqSpiderKills.get();
        boolean creepersMet = creeperKills >= DimTrConfig.SERVER.reqCreeperKills.get();
        boolean drownedMet = drownedKills >= DimTrConfig.SERVER.reqDrownedKills.get();
        boolean endermenMet = endermanKills >= DimTrConfig.SERVER.reqEndermanKills.get();
        boolean witchesMet = witchKills >= DimTrConfig.SERVER.reqWitchKills.get();
        boolean pillagersMet = pillagerKills >= DimTrConfig.SERVER.reqPillagerKills.get();
        // REMOVIDO: captainsMet dos goal kills
        boolean vindicatorsMet = vindicatorKills >= DimTrConfig.SERVER.reqVindicatorKills.get();
        boolean boggedMet = boggedKills >= DimTrConfig.SERVER.reqBoggedKills.get();
        boolean breezeMet = breezeKills >= DimTrConfig.SERVER.reqBreezeKills.get();

        // NOVO: Incluir Ravager e Evoker Goal Kills
        boolean ravagersMet = ravagerKills >= DimTrConfig.SERVER.reqRavagerKills.get();
        boolean evokersMet = evokerKills >= DimTrConfig.SERVER.reqEvokerKills.get();

        // ATUALIZADO: Remover zombieVillagersMet da verificação
        boolean allMet = zombiesMet && skeletonsMet && straysMet && husksMet &&
                spidersMet && creepersMet && drownedMet && endermenMet && witchesMet &&
                // REMOVIDO: captainsMet da verificação
                pillagersMet && vindicatorsMet && boggedMet && breezeMet &&
                ravagersMet && evokersMet; // NOVO: Incluir Ravager e Evoker

        if (!allMet) {
            DimTrMod.LOGGER.debug("Phase 1 mob requirements not met. Progress: " +
                            "Zombies: {}/{}, Skeletons: {}/{}, Creepers: {}/{}, Ravagers: {}/{}, Evokers: {}/{}, etc.",
                    zombieKills, DimTrConfig.SERVER.reqZombieKills.get(),
                    skeletonKills, DimTrConfig.SERVER.reqSkeletonKills.get(),
                    creeperKills, DimTrConfig.SERVER.reqCreeperKills.get(),
                    ravagerKills, DimTrConfig.SERVER.reqRavagerKills.get(),
                    evokerKills, DimTrConfig.SERVER.reqEvokerKills.get());
        }

        return allMet;
    }

    private boolean checkPhase2MobRequirements() {
        // Se os mob kills da Fase 2 estão desabilitados, considerar como cumprido
        if (!DimTrConfig.SERVER.enableMobKillsPhase2.get()) {
            return true;
        }

        // Verificar mobs do Nether/End
        boolean blazesMet = blazeKills >= DimTrConfig.SERVER.reqBlazeKills.get();
        boolean witherSkeletonsMet = witherSkeletonKills >= DimTrConfig.SERVER.reqWitherSkeletonKills.get();
        boolean piglinBrutesMet = piglinBruteKills >= DimTrConfig.SERVER.reqPiglinBruteKills.get();
        boolean hoglinsMet = hoglinKills >= DimTrConfig.SERVER.reqHoglinKills.get();
        boolean zoglinsMet = zoglinKills >= DimTrConfig.SERVER.reqZoglinKills.get();
        boolean ghastsMet = ghastKills >= DimTrConfig.SERVER.reqGhastKills.get();
        boolean endermitesMet = endermiteKills >= DimTrConfig.SERVER.reqEndermiteKills.get();
        boolean piglinsMet = piglinKills >= DimTrConfig.SERVER.reqPiglinKills.get();

        // Verificar mobs do Overworld com requisitos aumentados (125%)
        boolean overworldMobsMet = checkPhase2OverworldRequirements();

        boolean allMet = blazesMet && witherSkeletonsMet && piglinBrutesMet && hoglinsMet &&
                zoglinsMet && ghastsMet && endermitesMet && piglinsMet && overworldMobsMet;

        if (!allMet) {
            DimTrMod.LOGGER.debug("Phase 2 mob requirements not met");
        }

        return allMet;
    }

    private boolean checkPhase2OverworldRequirements() {
        // ATUALIZADO: Remover zombieVillagersMet da verificação
        // Verificar mobs do Overworld com 125% dos valores da Fase 1
        boolean zombiesMet = zombieKills >= getPhase2OverworldRequirement(DimTrConfig.SERVER.reqZombieKills.get());
        boolean skeletonsMet = skeletonKills >= getPhase2OverworldRequirement(DimTrConfig.SERVER.reqSkeletonKills.get());
        boolean creepersMet = creeperKills >= getPhase2OverworldRequirement(DimTrConfig.SERVER.reqCreeperKills.get());
        boolean spidersMet = spiderKills >= getPhase2OverworldRequirement(DimTrConfig.SERVER.reqSpiderKills.get());
        boolean endermenMet = endermanKills >= getPhase2OverworldRequirement(DimTrConfig.SERVER.reqEndermanKills.get());

        return zombiesMet && skeletonsMet && creepersMet && spidersMet && endermenMet;
    }

    // ATUALIZADO: Método auxiliar para calcular requisitos da Fase 2 para mobs do Overworld (125%)
    private int getPhase2OverworldRequirement(int originalRequirement) {
        // Calcular 125% do valor original (100% + 25%)
        // Exemplo: Zumbis 50 -> 50 + (50 * 0.25) = 62.5 -> 63
        return (int) Math.ceil(originalRequirement * 1.25);
    }
}