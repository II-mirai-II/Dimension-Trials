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
    // NOVO: Conquista Voluntaire Exile
    private boolean voluntaireExileAdvancementEarned = false;
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

    // CORREÇÃO PRINCIPAL: Configurações de requisitos sincronizadas do servidor
    private int reqZombieKills = 50;
    private int reqZombieVillagerKills = 3;
    private int reqSkeletonKills = 40;
    private int reqStrayKills = 10;
    private int reqHuskKills = 10;
    private int reqSpiderKills = 30;
    private int reqCreeperKills = 30;
    private int reqDrownedKills = 20;
    private int reqEndermanKills = 5;
    private int reqWitchKills = 5;
    private int reqPillagerKills = 20;
    private int reqCaptainKills = 1;
    private int reqVindicatorKills = 10;
    private int reqBoggedKills = 10;
    private int reqBreezeKills = 5;
    private int reqRavagerKills = 1; // CORRETO: 1
    private int reqEvokerKills = 5;  // CORRETO: 5
    private int reqBlazeKills = 20;
    private int reqWitherSkeletonKills = 15;
    private int reqPiglinBruteKills = 5;
    private int reqHoglinKills = 1;  // CORRETO: 1
    private int reqZoglinKills = 1;  // CORRETO: 1
    private int reqGhastKills = 10;
    private int reqEndermiteKills = 5;
    private int reqPiglinKills = 30;

    // MÉTODO PRINCIPAL: Atualizar dados com payload do servidor
    public void updateData(UpdateProgressionToClientPayload payload) {
        // Objetivos originais
        this.elderGuardianKilled = payload.elderGuardianKilled();
        this.raidWon = payload.raidWon();
        this.ravagerKilled = payload.ravagerKilled(); // Manter para compatibilidade
        this.evokerKilled = payload.evokerKilled(); // Manter para compatibilidade
        this.trialVaultAdvancementEarned = payload.trialVaultAdvancementEarned();
        // NOVO: Conquista Voluntaire Exile
        this.voluntaireExileAdvancementEarned = payload.voluntaireExileAdvancementEarned();
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

        // CORREÇÃO PRINCIPAL: Atualizar configurações sincronizadas do servidor
        this.reqZombieKills = payload.reqZombieKills();
        this.reqZombieVillagerKills = payload.reqZombieVillagerKills();
        this.reqSkeletonKills = payload.reqSkeletonKills();
        this.reqStrayKills = payload.reqStrayKills();
        this.reqHuskKills = payload.reqHuskKills();
        this.reqSpiderKills = payload.reqSpiderKills();
        this.reqCreeperKills = payload.reqCreeperKills();
        this.reqDrownedKills = payload.reqDrownedKills();
        this.reqEndermanKills = payload.reqEndermanKills();
        this.reqWitchKills = payload.reqWitchKills();
        this.reqPillagerKills = payload.reqPillagerKills();
        this.reqCaptainKills = payload.reqCaptainKills();
        this.reqVindicatorKills = payload.reqVindicatorKills();
        this.reqBoggedKills = payload.reqBoggedKills();
        this.reqBreezeKills = payload.reqBreezeKills();
        this.reqRavagerKills = payload.reqRavagerKills(); // Agora sincronizado: 1
        this.reqEvokerKills = payload.reqEvokerKills();   // Agora sincronizado: 5
        this.reqBlazeKills = payload.reqBlazeKills();
        this.reqWitherSkeletonKills = payload.reqWitherSkeletonKills();
        this.reqPiglinBruteKills = payload.reqPiglinBruteKills();
        this.reqHoglinKills = payload.reqHoglinKills();   // Agora sincronizado: 1
        this.reqZoglinKills = payload.reqZoglinKills();   // Agora sincronizado: 1
        this.reqGhastKills = payload.reqGhastKills();
        this.reqEndermiteKills = payload.reqEndermiteKills();
        this.reqPiglinKills = payload.reqPiglinKills();

        // Debug log para verificar sincronização
        System.out.println("CLIENT DATA UPDATED:");
        System.out.println("Ravager req: " + this.reqRavagerKills + " (should be 1)");
        System.out.println("Evoker req: " + this.reqEvokerKills + " (should be 5)");
        System.out.println("Hoglin req: " + this.reqHoglinKills + " (should be 1)");
        System.out.println("Zoglin req: " + this.reqZoglinKills + " (should be 1)");
    }

    // Getters para objetivos originais
    public boolean isElderGuardianKilled() { return elderGuardianKilled; }
    public boolean isRaidWon() { return raidWon; }
    public boolean isRavagerKilled() { return ravagerKilled; } // Manter para compatibilidade
    public boolean isEvokerKilled() { return evokerKilled; } // Manter para compatibilidade
    public boolean isTrialVaultAdvancementEarned() { return trialVaultAdvancementEarned; }
    // NOVO: Getter para conquista Voluntaire Exile
    public boolean isVoluntaireExileAdvancementEarned() { return voluntaireExileAdvancementEarned; }
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
        return switch (mobType) {
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

    // CORREÇÃO PRINCIPAL: Usar valores sincronizados em vez de DimTrConfig.SERVER
    public int getMobKillRequirement(String mobType, int phase) {
        if (phase == 1) {
            return switch (mobType) {
                case "zombie" -> reqZombieKills;
                case "zombie_villager" -> reqZombieVillagerKills;
                case "skeleton" -> reqSkeletonKills;
                case "stray" -> reqStrayKills;
                case "husk" -> reqHuskKills;
                case "spider" -> reqSpiderKills;
                case "creeper" -> reqCreeperKills;
                case "drowned" -> reqDrownedKills;
                case "enderman" -> reqEndermanKills;
                case "witch" -> reqWitchKills;
                case "pillager" -> reqPillagerKills;
                case "captain" -> reqCaptainKills;
                case "vindicator" -> reqVindicatorKills;
                case "bogged" -> reqBoggedKills;
                case "breeze" -> reqBreezeKills;
                // CORREÇÃO: Agora usa valores sincronizados corretos
                case "ravager" -> reqRavagerKills; // Retorna 1 correto
                case "evoker" -> reqEvokerKills;   // Retorna 5 correto
                default -> 0;
            };
        } else if (phase == 2) {
            return switch (mobType) {
                // Mobs do Nether/End com valores corretos
                case "blaze" -> reqBlazeKills;
                case "wither_skeleton" -> reqWitherSkeletonKills;
                case "piglin_brute" -> reqPiglinBruteKills;
                case "hoglin" -> reqHoglinKills; // Retorna 1 correto
                case "zoglin" -> reqZoglinKills; // Retorna 1 correto
                case "ghast" -> reqGhastKills;
                case "endermite" -> reqEndermiteKills;
                case "piglin" -> reqPiglinKills;

                // Mobs do Overworld com requisitos aumentados (125%)
                case "zombie" -> getPhase2OverworldRequirement(reqZombieKills);
                case "skeleton" -> getPhase2OverworldRequirement(reqSkeletonKills);
                case "creeper" -> getPhase2OverworldRequirement(reqCreeperKills);
                case "spider" -> getPhase2OverworldRequirement(reqSpiderKills);
                case "enderman" -> getPhase2OverworldRequirement(reqEndermanKills);
                case "witch" -> getPhase2OverworldRequirement(reqWitchKills);
                case "pillager" -> getPhase2OverworldRequirement(reqPillagerKills);

                // CORREÇÃO: Ravager e Evoker Phase 2 com 125% dos valores da Phase 1
                case "ravager" -> getPhase2OverworldRequirement(reqRavagerKills); // 1 * 1.25 = 2
                case "evoker" -> getPhase2OverworldRequirement(reqEvokerKills);   // 5 * 1.25 = 7
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

    // CORREÇÃO PRINCIPAL: Adicionar método que estava faltando
    public boolean isPhase1EffectivelyComplete() {
        return phase1Completed;
    }
}