package net.mirai.dimtr.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DimTrConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Configurações do servidor
    public static class Server {

        // Configurações principais de fase
        public final ModConfigSpec.BooleanValue enablePhase1;
        public final ModConfigSpec.BooleanValue enablePhase2;
        public final ModConfigSpec.BooleanValue enableMobKillsPhase1;
        public final ModConfigSpec.BooleanValue enableMobKillsPhase2;
        public final ModConfigSpec.BooleanValue enableMultipliers;

        // Requisitos de objetivos especiais
        public final ModConfigSpec.BooleanValue reqElderGuardian;
        public final ModConfigSpec.BooleanValue reqRaid; // ATUALIZADO: Removido "AndRavager"
        public final ModConfigSpec.BooleanValue reqTrialVaultAdv;
        public final ModConfigSpec.BooleanValue reqWither;
        public final ModConfigSpec.BooleanValue reqWarden;

        // Requisitos de quantidade de mobs - Fase 1
        public final ModConfigSpec.IntValue reqZombieKills;
        public final ModConfigSpec.IntValue reqZombieVillagerKills;
        public final ModConfigSpec.IntValue reqSkeletonKills;
        public final ModConfigSpec.IntValue reqStrayKills;
        public final ModConfigSpec.IntValue reqHuskKills;
        public final ModConfigSpec.IntValue reqSpiderKills;
        public final ModConfigSpec.IntValue reqCreeperKills;
        public final ModConfigSpec.IntValue reqDrownedKills;
        public final ModConfigSpec.IntValue reqEndermanKills;
        public final ModConfigSpec.IntValue reqWitchKills;
        public final ModConfigSpec.IntValue reqPillagerKills;
        public final ModConfigSpec.IntValue reqCaptainKills;
        public final ModConfigSpec.IntValue reqVindicatorKills;
        public final ModConfigSpec.IntValue reqBoggedKills;
        public final ModConfigSpec.IntValue reqBreezeKills;

        // NOVO: Ravager e Evoker como Goal Kills
        public final ModConfigSpec.IntValue reqRavagerKills;
        public final ModConfigSpec.IntValue reqEvokerKills;

        // Requisitos de quantidade de mobs - Fase 2
        public final ModConfigSpec.IntValue reqBlazeKills;
        public final ModConfigSpec.IntValue reqWitherSkeletonKills;
        public final ModConfigSpec.IntValue reqPiglinBruteKills;
        public final ModConfigSpec.IntValue reqHoglinKills;
        public final ModConfigSpec.IntValue reqZoglinKills;
        public final ModConfigSpec.IntValue reqGhastKills;
        public final ModConfigSpec.IntValue reqEndermiteKills;
        public final ModConfigSpec.IntValue reqPiglinKills;

        // Multiplicadores de dificuldade
        public final ModConfigSpec.DoubleValue phase1Multiplier;
        public final ModConfigSpec.DoubleValue phase2Multiplier;

        Server(ModConfigSpec.Builder builder) {
            builder.push("Phase Configuration");

            enablePhase1 = builder
                    .comment("Enable Phase 1 requirements (Nether access)")
                    .define("enablePhase1", true);

            enablePhase2 = builder
                    .comment("Enable Phase 2 requirements (End access)")
                    .define("enablePhase2", true);

            enableMobKillsPhase1 = builder
                    .comment("Enable mob kill requirements for Phase 1")
                    .define("enableMobKillsPhase1", true);

            enableMobKillsPhase2 = builder
                    .comment("Enable mob kill requirements for Phase 2")
                    .define("enableMobKillsPhase2", true);

            enableMultipliers = builder
                    .comment("Enable mob health/damage multipliers after phase completion")
                    .define("enableMultipliers", true);

            builder.pop();

            builder.push("Phase 1 Special Objectives");

            reqElderGuardian = builder
                    .comment("Require Elder Guardian kill for Phase 1")
                    .define("reqElderGuardian", true);

            reqRaid = builder // ATUALIZADO: Apenas Raid
                    .comment("Require winning a raid for Phase 1")
                    .define("reqRaid", true);

            reqTrialVaultAdv = builder
                    .comment("Require Trial Vault advancement for Phase 1")
                    .define("reqTrialVaultAdv", true);

            builder.pop();

            builder.push("Phase 2 Special Objectives");

            reqWither = builder
                    .comment("Require Wither kill for Phase 2")
                    .define("reqWither", true);

            reqWarden = builder
                    .comment("Require Warden kill for Phase 2")
                    .define("reqWarden", true);

            builder.pop();

            builder.push("Phase 1 Mob Kill Requirements");

            reqZombieKills = builder
                    .comment("Number of Zombies to kill for Phase 1")
                    .defineInRange("reqZombieKills", 50, 0, 1000);

            reqZombieVillagerKills = builder
                    .comment("Number of Zombie Villagers to kill for Phase 1")
                    .defineInRange("reqZombieVillagerKills", 3, 0, 100);

            reqSkeletonKills = builder
                    .comment("Number of Skeletons to kill for Phase 1")
                    .defineInRange("reqSkeletonKills", 40, 0, 1000);

            reqStrayKills = builder
                    .comment("Number of Strays to kill for Phase 1")
                    .defineInRange("reqStrayKills", 10, 0, 100);

            reqHuskKills = builder
                    .comment("Number of Husks to kill for Phase 1")
                    .defineInRange("reqHuskKills", 10, 0, 100);

            reqSpiderKills = builder
                    .comment("Number of Spiders to kill for Phase 1")
                    .defineInRange("reqSpiderKills", 30, 0, 1000);

            reqCreeperKills = builder
                    .comment("Number of Creepers to kill for Phase 1")
                    .defineInRange("reqCreeperKills", 30, 0, 1000);

            reqDrownedKills = builder
                    .comment("Number of Drowned to kill for Phase 1")
                    .defineInRange("reqDrownedKills", 20, 0, 200);

            reqEndermanKills = builder
                    .comment("Number of Endermen to kill for Phase 1")
                    .defineInRange("reqEndermanKills", 5, 0, 50);

            reqWitchKills = builder
                    .comment("Number of Witches to kill for Phase 1")
                    .defineInRange("reqWitchKills", 5, 0, 50);

            reqPillagerKills = builder
                    .comment("Number of Pillagers to kill for Phase 1")
                    .defineInRange("reqPillagerKills", 20, 0, 200);

            reqCaptainKills = builder
                    .comment("Number of Raid Captains to kill for Phase 1")
                    .defineInRange("reqCaptainKills", 1, 0, 10);

            reqVindicatorKills = builder
                    .comment("Number of Vindicators to kill for Phase 1")
                    .defineInRange("reqVindicatorKills", 10, 0, 100);

            reqBoggedKills = builder
                    .comment("Number of Bogged to kill for Phase 1")
                    .defineInRange("reqBoggedKills", 10, 0, 100);

            reqBreezeKills = builder
                    .comment("Number of Breezes to kill for Phase 1")
                    .defineInRange("reqBreezeKills", 5, 0, 50);

            // NOVO: Ravager e Evoker como Goal Kills
            reqRavagerKills = builder
                    .comment("Number of Ravagers to kill for Phase 1 (Goal Kill)")
                    .defineInRange("reqRavagerKills", 3, 0, 20); // Valor balanceado: 3 (mais baixo pois são raros e fortes)

            reqEvokerKills = builder
                    .comment("Number of Evokers to kill for Phase 1 (Goal Kill)")
                    .defineInRange("reqEvokerKills", 2, 0, 10); // Valor balanceado: 2 (mais baixo pois são raros e fortes)

            builder.pop();

            builder.push("Phase 2 Mob Kill Requirements");

            reqBlazeKills = builder
                    .comment("Number of Blazes to kill for Phase 2")
                    .defineInRange("reqBlazeKills", 20, 0, 200);

            reqWitherSkeletonKills = builder
                    .comment("Number of Wither Skeletons to kill for Phase 2")
                    .defineInRange("reqWitherSkeletonKills", 15, 0, 100);

            reqPiglinBruteKills = builder
                    .comment("Number of Piglin Brutes to kill for Phase 2")
                    .defineInRange("reqPiglinBruteKills", 5, 0, 50);

            reqHoglinKills = builder
                    .comment("Number of Hoglins to kill for Phase 2")
                    .defineInRange("reqHoglinKills", 10, 0, 100);

            reqZoglinKills = builder
                    .comment("Number of Zoglins to kill for Phase 2")
                    .defineInRange("reqZoglinKills", 5, 0, 50);

            reqGhastKills = builder
                    .comment("Number of Ghasts to kill for Phase 2")
                    .defineInRange("reqGhastKills", 10, 0, 100);

            reqEndermiteKills = builder
                    .comment("Number of Endermites to kill for Phase 2")
                    .defineInRange("reqEndermiteKills", 5, 0, 50);

            reqPiglinKills = builder
                    .comment("Number of hostile Piglins to kill for Phase 2")
                    .defineInRange("reqPiglinKills", 30, 0, 300);

            builder.pop();

            builder.push("Difficulty Multipliers");

            phase1Multiplier = builder
                    .comment("Mob health/damage multiplier when Phase 1 is completed")
                    .defineInRange("phase1Multiplier", 1.5, 1.0, 10.0);

            phase2Multiplier = builder
                    .comment("Mob health/damage multiplier when Phase 2 is completed")
                    .defineInRange("phase2Multiplier", 2.0, 1.0, 10.0);

            builder.pop();
        }
    }

    // Configurações do cliente (vazio por enquanto)
    public static class Client {
        Client(ModConfigSpec.Builder builder) {
            // Configurações do cliente podem ser adicionadas aqui no futuro
        }
    }

    public static final Server SERVER = new Server(BUILDER);
    public static final ModConfigSpec SERVER_SPEC = BUILDER.build();

    // Criar client config separado
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
}