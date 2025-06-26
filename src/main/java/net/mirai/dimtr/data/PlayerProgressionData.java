package net.mirai.dimtr.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
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

    // Sistema de fases customizadas
    private Map<String, Boolean> customPhaseCompletion = new HashMap<>();
    private Map<String, Map<String, Boolean>> customObjectiveCompletion = new HashMap<>();
    private Map<String, Map<String, Integer>> customMobKills = new HashMap<>();

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

        // Salvar fases customizadas
        CompoundTag customPhasesTag = new CompoundTag();
        for (Map.Entry<String, Boolean> entry : customPhaseCompletion.entrySet()) {
            customPhasesTag.putBoolean(entry.getKey(), entry.getValue());
        }
        tag.put("customPhases", customPhasesTag);

        // Salvar objetivos customizados
        CompoundTag customObjectivesTag = new CompoundTag();
        for (Map.Entry<String, Map<String, Boolean>> entry : customObjectiveCompletion.entrySet()) {
            CompoundTag objectiveTag = new CompoundTag();
            for (Map.Entry<String, Boolean> objEntry : entry.getValue().entrySet()) {
                objectiveTag.putBoolean(objEntry.getKey(), objEntry.getValue());
            }
            customObjectivesTag.put(entry.getKey(), objectiveTag);
        }
        tag.put("customObjectives", customObjectivesTag);

        // Salvar contadores de mobs customizados
        CompoundTag customMobKillsTag = new CompoundTag();
        for (Map.Entry<String, Map<String, Integer>> entry : customMobKills.entrySet()) {
            CompoundTag mobKillTag = new CompoundTag();
            for (Map.Entry<String, Integer> mobEntry : entry.getValue().entrySet()) {
                mobKillTag.putInt(mobEntry.getKey(), mobEntry.getValue());
            }
            customMobKillsTag.put(entry.getKey(), mobKillTag);
        }
        tag.put("customMobKills", customMobKillsTag);

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

        // Carregar fases customizadas
        if (tag.contains("customPhases", 10)) {
            CompoundTag customPhasesTag = tag.getCompound("customPhases");
            for (String key : customPhasesTag.getAllKeys()) {
                data.customPhaseCompletion.put(key, customPhasesTag.getBoolean(key));
            }
        }

        // Carregar objetivos customizados
        if (tag.contains("customObjectives", 10)) {
            CompoundTag customObjectivesTag = tag.getCompound("customObjectives");
            for (String key : customObjectivesTag.getAllKeys()) {
                CompoundTag objectiveTag = customObjectivesTag.getCompound(key);
                Map<String, Boolean> objectiveMap = new HashMap<>();
                for (String objKey : objectiveTag.getAllKeys()) {
                    objectiveMap.put(objKey, objectiveTag.getBoolean(objKey));
                }
                data.customObjectiveCompletion.put(key, objectiveMap);
            }
        }

        // Carregar contadores de mobs customizados
        if (tag.contains("customMobKills", 10)) {
            CompoundTag customMobKillsTag = tag.getCompound("customMobKills");
            for (String key : customMobKillsTag.getAllKeys()) {
                CompoundTag mobKillTag = customMobKillsTag.getCompound(key);
                Map<String, Integer> mobKillMap = new HashMap<>();
                for (String mobKey : mobKillTag.getAllKeys()) {
                    mobKillMap.put(mobKey, mobKillTag.getInt(mobKey));
                }
                data.customMobKills.put(key, mobKillMap);
            }
        }

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
        // Verificar multiplicadores de fases customizadas primeiro
        double customMultiplier = getCustomPhaseMultiplier();
        if (customMultiplier > 1.0) {
            return customMultiplier;
        }
        
        if (isPhase2EffectivelyComplete()) {
            return DimTrConfig.SERVER.phase2Multiplier.get();
        } else if (isPhase1EffectivelyComplete()) {
            return DimTrConfig.SERVER.phase1Multiplier.get();
        }
        return 1.0;
    }
    
    // ============================================================================
    // MÉTODOS PARA FASES CUSTOMIZADAS
    // ============================================================================
    
    /**
     * Verificar se uma fase customizada está completa
     */
    public boolean isCustomPhaseComplete(String phaseId) {
        return customPhaseCompletion.getOrDefault(phaseId, false);
    }
    
    /**
     * Marcar fase customizada como completa
     */
    public void setCustomPhaseComplete(String phaseId, boolean complete) {
        customPhaseCompletion.put(phaseId, complete);
    }
    
    /**
     * Verificar se um objetivo customizado foi completado
     */
    public boolean isCustomObjectiveComplete(String phaseId, String objectiveId) {
        return customObjectiveCompletion
                .getOrDefault(phaseId, new HashMap<>())
                .getOrDefault(objectiveId, false);
    }
    
    /**
     * Marcar objetivo customizado como completo
     */
    public void setCustomObjectiveComplete(String phaseId, String objectiveId, boolean complete) {
        customObjectiveCompletion.computeIfAbsent(phaseId, k -> new HashMap<>()).put(objectiveId, complete);
    }
    
    /**
     * Obter contagem de kills de mob customizado
     */
    public int getCustomMobKills(String phaseId, String mobType) {
        return customMobKills
                .getOrDefault(phaseId, new HashMap<>())
                .getOrDefault(mobType, 0);
    }
    
    /**
     * Incrementar kill de mob customizado
     */
    public void incrementCustomMobKill(String phaseId, String mobType) {
        customMobKills.computeIfAbsent(phaseId, k -> new HashMap<>())
                .merge(mobType, 1, Integer::sum);
    }
    
    /**
     * Obter o maior multiplicador de fases customizadas completadas
     */
    private double getCustomPhaseMultiplier() {
        double maxMultiplier = 1.0;
        
        for (Map.Entry<String, Boolean> entry : customPhaseCompletion.entrySet()) {
            if (entry.getValue()) { // Se a fase está completa
                String phaseId = entry.getKey();
                var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
                if (customPhase != null) {
                    // Usar o maior multiplicador entre health, damage e xp
                    double phaseMultiplier = Math.max(
                        Math.max(customPhase.healthMultiplier, customPhase.damageMultiplier),
                        customPhase.xpMultiplier
                    );
                    maxMultiplier = Math.max(maxMultiplier, phaseMultiplier);
                }
            }
        }
        
        return maxMultiplier;
    }

    /**
     * 🎯 NOVO: Obter mapa de conclusão de fases customizadas (para ProgressionManager)
     */
    public Map<String, Boolean> getCustomPhaseCompletionMap() {
        return customPhaseCompletion;
    }
}