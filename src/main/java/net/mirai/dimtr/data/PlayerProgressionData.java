package net.mirai.dimtr.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import java.util.UUID;
import net.mirai.dimtr.config.DimTrConfig;

/**
 * Dados de progressão individual de um jogador
 */
public class PlayerProgressionData {
    private final UUID playerId;

    // Objetivos especiais
    public boolean elderGuardianKilled = false;
    public boolean raidWon = false;
    public boolean trialVaultAdvancementEarned = false;
    public boolean voluntaireExileAdvancementEarned = false;
    public boolean phase1Completed = false;
    public boolean witherKilled = false;
    public boolean wardenKilled = false;
    public boolean phase2Completed = false;

    // Contadores de mobs - Fase 1
    public int zombieKills = 0;
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
    public int ravagerKills = 0;
    public int evokerKills = 0;

    // Contadores de mobs - Fase 2
    public int blazeKills = 0;
    public int witherSkeletonKills = 0;
    public int piglinBruteKills = 0;
    public int hoglinKills = 0;
    public int zoglinKills = 0;
    public int ghastKills = 0;
    public int piglinKills = 0;

    public PlayerProgressionData(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    // Serialização NBT
    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        // Salvar UUID
        tag.putString("playerId", playerId.toString());

        // Salvar objetivos
        tag.putBoolean("elderGuardianKilled", elderGuardianKilled);
        tag.putBoolean("raidWon", raidWon);
        tag.putBoolean("trialVaultAdvancementEarned", trialVaultAdvancementEarned);
        tag.putBoolean("voluntaireExileAdvancementEarned", voluntaireExileAdvancementEarned);
        tag.putBoolean("phase1Completed", phase1Completed);
        tag.putBoolean("witherKilled", witherKilled);
        tag.putBoolean("wardenKilled", wardenKilled);
        tag.putBoolean("phase2Completed", phase2Completed);

        // Salvar contadores - Fase 1
        tag.putInt("zombieKills", zombieKills);
        tag.putInt("skeletonKills", skeletonKills);
        tag.putInt("strayKills", strayKills);
        tag.putInt("huskKills", huskKills);
        tag.putInt("spiderKills", spiderKills);
        tag.putInt("creeperKills", creeperKills);
        tag.putInt("drownedKills", drownedKills);
        tag.putInt("endermanKills", endermanKills);
        tag.putInt("witchKills", witchKills);
        tag.putInt("pillagerKills", pillagerKills);
        tag.putInt("captainKills", captainKills);
        tag.putInt("vindicatorKills", vindicatorKills);
        tag.putInt("boggedKills", boggedKills);
        tag.putInt("breezeKills", breezeKills);
        tag.putInt("ravagerKills", ravagerKills);
        tag.putInt("evokerKills", evokerKills);

        // Salvar contadores - Fase 2
        tag.putInt("blazeKills", blazeKills);
        tag.putInt("witherSkeletonKills", witherSkeletonKills);
        tag.putInt("piglinBruteKills", piglinBruteKills);
        tag.putInt("hoglinKills", hoglinKills);
        tag.putInt("zoglinKills", zoglinKills);
        tag.putInt("ghastKills", ghastKills);
        tag.putInt("piglinKills", piglinKills);

        return tag;
    }

    public static PlayerProgressionData load(CompoundTag tag, HolderLookup.Provider registries) {
        UUID playerId = UUID.fromString(tag.getString("playerId"));
        PlayerProgressionData data = new PlayerProgressionData(playerId);

        // Carregar objetivos
        data.elderGuardianKilled = tag.getBoolean("elderGuardianKilled");
        data.raidWon = tag.getBoolean("raidWon");
        data.trialVaultAdvancementEarned = tag.getBoolean("trialVaultAdvancementEarned");
        data.voluntaireExileAdvancementEarned = tag.getBoolean("voluntaireExileAdvancementEarned");
        data.phase1Completed = tag.getBoolean("phase1Completed");
        data.witherKilled = tag.getBoolean("witherKilled");
        data.wardenKilled = tag.getBoolean("wardenKilled");
        data.phase2Completed = tag.getBoolean("phase2Completed");

        // Carregar contadores - Fase 1
        data.zombieKills = tag.getInt("zombieKills");
        data.skeletonKills = tag.getInt("skeletonKills");
        data.strayKills = tag.getInt("strayKills");
        data.huskKills = tag.getInt("huskKills");
        data.spiderKills = tag.getInt("spiderKills");
        data.creeperKills = tag.getInt("creeperKills");
        data.drownedKills = tag.getInt("drownedKills");
        data.endermanKills = tag.getInt("endermanKills");
        data.witchKills = tag.getInt("witchKills");
        data.pillagerKills = tag.getInt("pillagerKills");
        data.captainKills = tag.getInt("captainKills");
        data.vindicatorKills = tag.getInt("vindicatorKills");
        data.boggedKills = tag.getInt("boggedKills");
        data.breezeKills = tag.getInt("breezeKills");
        data.ravagerKills = tag.getInt("ravagerKills");
        data.evokerKills = tag.getInt("evokerKills");

        // Carregar contadores - Fase 2
        data.blazeKills = tag.getInt("blazeKills");
        data.witherSkeletonKills = tag.getInt("witherSkeletonKills");
        data.piglinBruteKills = tag.getInt("piglinBruteKills");
        data.hoglinKills = tag.getInt("hoglinKills");
        data.zoglinKills = tag.getInt("zoglinKills");
        data.ghastKills = tag.getInt("ghastKills");
        data.piglinKills = tag.getInt("piglinKills");

        return data;
    }

    // Métodos auxiliares para progressão
    public int getMobKillCount(String mobType) {
        return switch (mobType) {
            case "zombie" -> zombieKills;
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
            case "ravager" -> ravagerKills;
            case "evoker" -> evokerKills;
            case "blaze" -> blazeKills;
            case "wither_skeleton" -> witherSkeletonKills;
            case "piglin_brute" -> piglinBruteKills;
            case "hoglin" -> hoglinKills;
            case "zoglin" -> zoglinKills;
            case "ghast" -> ghastKills;
            case "piglin" -> piglinKills;
            default -> 0;
        };
    }

    public boolean incrementMobKill(String mobType) {
        return switch (mobType) {
            case "zombie" -> { zombieKills++; yield true; }
            case "skeleton" -> { skeletonKills++; yield true; }
            case "stray" -> { strayKills++; yield true; }
            case "husk" -> { huskKills++; yield true; }
            case "spider" -> { spiderKills++; yield true; }
            case "creeper" -> { creeperKills++; yield true; }
            case "drowned" -> { drownedKills++; yield true; }
            case "enderman" -> { endermanKills++; yield true; }
            case "witch" -> { witchKills++; yield true; }
            case "pillager" -> { pillagerKills++; yield true; }
            case "captain" -> { captainKills++; yield true; }
            case "vindicator" -> { vindicatorKills++; yield true; }
            case "bogged" -> { boggedKills++; yield true; }
            case "breeze" -> { breezeKills++; yield true; }
            case "ravager" -> { ravagerKills++; yield true; }
            case "evoker" -> { evokerKills++; yield true; }
            case "blaze" -> { blazeKills++; yield true; }
            case "wither_skeleton" -> { witherSkeletonKills++; yield true; }
            case "piglin_brute" -> { piglinBruteKills++; yield true; }
            case "hoglin" -> { hoglinKills++; yield true; }
            case "zoglin" -> { zoglinKills++; yield true; }
            case "ghast" -> { ghastKills++; yield true; }
            case "piglin" -> { piglinKills++; yield true; }
            default -> false;
        };
    }

    // Métodos para verificar completude das fases
    public boolean isPhase1EffectivelyComplete() {
        if (!DimTrConfig.SERVER.enablePhase1.get()) {
            return true;
        }
        return phase1Completed;
    }

    public boolean isPhase2EffectivelyComplete() {
        if (!DimTrConfig.SERVER.enablePhase2.get()) {
            return true;
        }
        return phase2Completed && isPhase1EffectivelyComplete();
    }

    // Calcular multiplicador baseado na progressão individual
    public double getProgressionMultiplier() {
        if (isPhase2EffectivelyComplete()) {
            return DimTrConfig.SERVER.phase2Multiplier.get();
        } else if (isPhase1EffectivelyComplete()) {
            return DimTrConfig.SERVER.phase1Multiplier.get();
        }
        return 1.0;
    }
}