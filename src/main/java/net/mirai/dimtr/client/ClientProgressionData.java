package net.mirai.dimtr.client;

import net.mirai.dimtr.network.UpdateProgressionToClientPayload;

public class ClientProgressionData {
    public static final ClientProgressionData INSTANCE = new ClientProgressionData();

    // Objetivos originais
    private boolean elderGuardianKilled = false;
    private boolean raidWon = false;
    private boolean ravagerKilled = false; // Manter para compatibilidade
    private boolean evokerKilled = false; // Manter para compatibilidade
    private boolean trialVaultAdvancementEarned = false;
    private boolean phase1Completed = false;

    private boolean witherKilled = false;
    private boolean wardenKilled = false;
    private boolean phase2Completed = false;

    // Novos contadores de mobs - Fase 1 (Overworld)
    private int zombieKills = 0;
    private int zombieVillagerKills = 0;
    private int skeletonKills = 0;
    private int strayKills = 0;
    private int huskKills = 0;
    private int spiderKills = 0;
    private int creeperKills = 0;
    private int drownedKills = 0;
    private int endermanKills = 0;
    private int witchKills = 0;
    private int pillagerKills = 0;
    private int captainKills = 0;
    private int vindicatorKills = 0;
    private int boggedKills = 0;
    private int breezeKills = 0;

    // NOVO: Ravager e Evoker agora são Goal Kills
    private int ravagerKills = 0;
    private int evokerKills = 0;

    // Novos contadores de mobs - Fase 2 (Nether + High Level)
    private int blazeKills = 0;
    private int witherSkeletonKills = 0;
    private int piglinBruteKills = 0;
    private int hoglinKills = 0;
    private int zoglinKills = 0;
    private int ghastKills = 0;
    private int endermiteKills = 0;
    private int piglinKills = 0;

    private ClientProgressionData() {
        // Singleton
    }

    public void updateData(UpdateProgressionToClientPayload payload) {
        // Objetivos originais
        this.elderGuardianKilled = payload.elderGuardianKilled();
        this.raidWon = payload.raidWon();
        this.ravagerKilled = payload.ravagerKilled(); // Manter para compatibilidade
        this.evokerKilled = payload.evokerKilled(); // Manter para compatibilidade
        this.trialVaultAdvancementEarned = payload.trialVaultAdvancementEarned();
        this.phase1Completed = payload.phase1Completed();
        this.witherKilled = payload.witherKilled();
        this.wardenKilled = payload.wardenKilled();
        this.phase2Completed = payload.phase2Completed();

        // Novos contadores de mobs - Fase 1 (incluindo Ravager e Evoker)
        this.zombieKills = payload.zombieKills();
        this.zombieVillagerKills = payload.zombieVillagerKills();
        this.skeletonKills = payload.skeletonKills();
        this.strayKills = payload.strayKills();
        this.huskKills = payload.huskKills();
        this.spiderKills = payload.spiderKills();
        this.creeperKills = payload.creeperKills();
        this.drownedKills = payload.drownedKills();
        this.endermanKills = payload.endermanKills();
        this.witchKills = payload.witchKills();
        this.pillagerKills = payload.pillagerKills();
        this.captainKills = payload.captainKills();
        this.vindicatorKills = payload.vindicatorKills();
        this.boggedKills = payload.boggedKills();
        this.breezeKills = payload.breezeKills();

        // NOVO: Ravager e Evoker
        this.ravagerKills = payload.ravagerKills();
        this.evokerKills = payload.evokerKills();

        // Novos contadores de mobs - Fase 2
        this.blazeKills = payload.blazeKills();
        this.witherSkeletonKills = payload.witherSkeletonKills();
        this.piglinBruteKills = payload.piglinBruteKills();
        this.hoglinKills = payload.hoglinKills();
        this.zoglinKills = payload.zoglinKills();
        this.ghastKills = payload.ghastKills();
        this.endermiteKills = payload.endermiteKills();
        this.piglinKills = payload.piglinKills();
    }

    // Getters para objetivos originais
    public boolean isElderGuardianKilled() { return elderGuardianKilled; }
    public boolean isRaidWon() { return raidWon; }
    public boolean isRavagerKilled() { return ravagerKilled; } // Manter para compatibilidade
    public boolean isEvokerKilled() { return evokerKilled; } // Manter para compatibilidade
    public boolean isTrialVaultAdvancementEarned() { return trialVaultAdvancementEarned; }
    public boolean isPhase1Completed() { return phase1Completed; }
    public boolean isWitherKilled() { return witherKilled; }
    public boolean isWardenKilled() { return wardenKilled; }
    public boolean isPhase2Completed() { return phase2Completed; }

    // Getters para contadores de mobs - Fase 1
    public int getZombieKills() { return zombieKills; }
    public int getZombieVillagerKills() { return zombieVillagerKills; }
    public int getSkeletonKills() { return skeletonKills; }
    public int getStrayKills() { return strayKills; }
    public int getHuskKills() { return huskKills; }
    public int getSpiderKills() { return spiderKills; }
    public int getCreeperKills() { return creeperKills; }
    public int getDrownedKills() { return drownedKills; }
    public int getEndermanKills() { return endermanKills; }
    public int getWitchKills() { return witchKills; }
    public int getPillagerKills() { return pillagerKills; }
    public int getCaptainKills() { return captainKills; }
    public int getVindicatorKills() { return vindicatorKills; }
    public int getBoggedKills() { return boggedKills; }
    public int getBreezeKills() { return breezeKills; }

    // NOVO: Getters para Ravager e Evoker
    public int getRavagerKills() { return ravagerKills; }
    public int getEvokerKills() { return evokerKills; }

    // Getters para contadores de mobs - Fase 2
    public int getBlazeKills() { return blazeKills; }
    public int getWitherSkeletonKills() { return witherSkeletonKills; }
    public int getPiglinBruteKills() { return piglinBruteKills; }
    public int getHoglinKills() { return hoglinKills; }
    public int getZoglinKills() { return zoglinKills; }
    public int getGhastKills() { return ghastKills; }
    public int getEndermiteKills() { return endermiteKills; }
    public int getPiglinKills() { return piglinKills; }

    // Método auxiliar para obter contagem de um mob específico
    public int getMobKillCount(String mobType) {
        return switch (mobType.toLowerCase()) {
            case "zombie" -> zombieKills;
            case "zombie_villager" -> zombieVillagerKills;
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

    // Método auxiliar para obter requisito de um mob específico baseado na fase
    public int getMobKillRequirement(String mobType, int phase) {
        if (phase == 1) {
            return switch (mobType.toLowerCase()) {
                case "zombie" -> 50;
                case "zombie_villager" -> 3;
                case "skeleton" -> 40;
                case "stray" -> 10;
                case "husk" -> 10;
                case "spider" -> 30;
                case "creeper" -> 30;
                case "drowned" -> 20;
                case "enderman" -> 5;
                case "witch" -> 5;
                case "pillager" -> 20;
                case "captain" -> 1;
                case "vindicator" -> 10;
                case "bogged" -> 10;
                case "breeze" -> 5;
                case "ravager" -> 3; // NOVO: Goal Kill
                case "evoker" -> 2; // NOVO: Goal Kill
                default -> 0;
            };
        } else if (phase == 2) {
            return switch (mobType.toLowerCase()) {
                // Fase 2 mobs
                case "blaze" -> 20;
                case "wither_skeleton" -> 15;
                case "piglin_brute" -> 5;
                case "hoglin" -> 10;
                case "zoglin" -> 5;
                case "ghast" -> 10;
                case "endermite" -> 5;
                case "piglin" -> 30;

                // ATUALIZADO: Requisitos aumentados da Fase 1 (125% do original)
                case "zombie" -> getPhase2OverworldRequirement(50);
                case "zombie_villager" -> getPhase2OverworldRequirement(3);
                case "skeleton" -> getPhase2OverworldRequirement(40);
                case "stray" -> getPhase2OverworldRequirement(10);
                case "husk" -> getPhase2OverworldRequirement(10);
                case "spider" -> getPhase2OverworldRequirement(30);
                case "creeper" -> getPhase2OverworldRequirement(30);
                case "drowned" -> getPhase2OverworldRequirement(20);
                case "enderman" -> getPhase2OverworldRequirement(5);
                case "witch" -> getPhase2OverworldRequirement(5);
                case "pillager" -> getPhase2OverworldRequirement(20);
                case "captain" -> getPhase2OverworldRequirement(1);
                case "vindicator" -> getPhase2OverworldRequirement(10);
                case "bogged" -> getPhase2OverworldRequirement(10);
                case "breeze" -> getPhase2OverworldRequirement(5);

                // NOVO: Ravager e Evoker com 125% para Fase 2
                case "ravager" -> getPhase2OverworldRequirement(3);
                case "evoker" -> getPhase2OverworldRequirement(2);

                default -> 0;
            };
        }
        return 0;
    }

    // ATUALIZADO: Método auxiliar para calcular requisitos da Fase 2 para mobs do Overworld (125%)
    private int getPhase2OverworldRequirement(int originalRequirement) {
        // Calcular 125% do valor original (100% + 25%)
        // Exemplo: Zumbis 50 -> 50 + (50 * 0.25) = 62.5 -> 63
        return (int) Math.ceil(originalRequirement * 1.25);
    }

    // CORREÇÃO: Implementar métodos que estavam como placeholder
    public boolean isServerEnablePhase1() { return true; }
    public boolean isServerEnablePhase2() { return true; }
    public boolean isServerEnableMobKillsPhase1() { return true; }
    public boolean isServerEnableMobKillsPhase2() { return true; }
    public boolean isServerReqElderGuardian() { return true; }
    public boolean isServerReqRaid() { return true; }
    public boolean isServerReqTrialVaultAdv() { return true; }
    public boolean isServerReqWither() { return true; }
    public boolean isServerReqWarden() { return true; }

    // Método auxiliar para verificar se a Fase 1 está efetivamente completa
    public boolean isPhase1EffectivelyComplete() {
        return phase1Completed || !isServerEnablePhase1();
    }

    // Método auxiliar para verificar se a Fase 2 está efetivamente completa
    public boolean isPhase2EffectivelyComplete() {
        return phase2Completed || !isServerEnablePhase2();
    }
}