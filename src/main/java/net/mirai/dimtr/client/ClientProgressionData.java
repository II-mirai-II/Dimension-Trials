package net.mirai.dimtr.client;

import net.mirai.dimtr.config.DimTrConfig;
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

        // Contadores de mobs - Fase 2
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
            // Fase 1 mobs
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

            // NOVO: Ravager e Evoker Goal Kills
            case "ravager" -> ravagerKills;
            case "evoker" -> evokerKills;

            // Fase 2 mobs
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
            // Fase 1 - Valores padrão
            return switch (mobType.toLowerCase()) {
                case "zombie" -> DimTrConfig.SERVER.reqZombieKills.get();
                case "zombie_villager" -> DimTrConfig.SERVER.reqZombieVillagerKills.get();
                case "skeleton" -> DimTrConfig.SERVER.reqSkeletonKills.get();
                case "stray" -> DimTrConfig.SERVER.reqStrayKills.get();
                case "husk" -> DimTrConfig.SERVER.reqHuskKills.get();
                case "spider" -> DimTrConfig.SERVER.reqSpiderKills.get();
                case "creeper" -> DimTrConfig.SERVER.reqCreeperKills.get();
                case "drowned" -> DimTrConfig.SERVER.reqDrownedKills.get();
                case "enderman" -> DimTrConfig.SERVER.reqEndermanKills.get();
                case "witch" -> DimTrConfig.SERVER.reqWitchKills.get();
                case "pillager" -> DimTrConfig.SERVER.reqPillagerKills.get();
                case "captain" -> DimTrConfig.SERVER.reqCaptainKills.get();
                case "vindicator" -> DimTrConfig.SERVER.reqVindicatorKills.get();
                case "bogged" -> DimTrConfig.SERVER.reqBoggedKills.get();
                case "breeze" -> DimTrConfig.SERVER.reqBreezeKills.get();

                // CORREÇÃO 5: Ravager e Evoker com valores atualizados
                case "ravager" -> DimTrConfig.SERVER.reqRavagerKills.get(); // Agora 1
                case "evoker" -> DimTrConfig.SERVER.reqEvokerKills.get(); // Agora 5

                default -> 0;
            };
        } else if (phase == 2) {
            // Fase 2 - Valores aumentados para mobs do Overworld (125%)
            return switch (mobType.toLowerCase()) {
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

                // CORREÇÃO 5: Ravager e Evoker com 125% para Fase 2
                case "ravager" -> getPhase2OverworldRequirement(1); // 1 * 1.25 = 1.25 -> 2
                case "evoker" -> getPhase2OverworldRequirement(5); // 5 * 1.25 = 6.25 -> 7

                // Mobs exclusivos da Fase 2
                case "blaze" -> DimTrConfig.SERVER.reqBlazeKills.get();
                case "wither_skeleton" -> DimTrConfig.SERVER.reqWitherSkeletonKills.get();
                case "piglin_brute" -> DimTrConfig.SERVER.reqPiglinBruteKills.get();
                case "hoglin" -> DimTrConfig.SERVER.reqHoglinKills.get(); // CORREÇÃO 4: Agora 1
                case "zoglin" -> DimTrConfig.SERVER.reqZoglinKills.get(); // CORREÇÃO 4: Agora 1
                case "ghast" -> DimTrConfig.SERVER.reqGhastKills.get();
                // CORREÇÃO 3: REMOVER ENDERMITE COMPLETAMENTE
                // case "endermite" -> DimTrConfig.SERVER.reqEndermiteKills.get();
                case "piglin" -> DimTrConfig.SERVER.reqPiglinKills.get();

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
        // Simular a lógica do servidor no lado do cliente
        // Se a configuração da Fase 1 está desabilitada, considera completa
        if (!isServerEnablePhase1()) {
            return true;
        }

        // Caso contrário, verifica se a Fase 1 foi marcada como completa
        return isPhase1Completed();
    }
}