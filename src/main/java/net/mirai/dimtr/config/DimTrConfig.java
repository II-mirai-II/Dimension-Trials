package net.mirai.dimtr.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DimTrConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    // ADICIONADO: Cliente config spec
    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Server, ModConfigSpec> serverSpecPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();

        final Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }

    // Configurações do servidor
    public static class Server {

        // Configurações principais de fase
        public final ModConfigSpec.BooleanValue enablePhase1;
        public final ModConfigSpec.BooleanValue enablePhase2;
        public final ModConfigSpec.BooleanValue enableMobKillsPhase1;
        public final ModConfigSpec.BooleanValue enableMobKillsPhase2;
        public final ModConfigSpec.BooleanValue enableMultipliers;
        // NOVO: Configuração para multiplicador de XP
        public final ModConfigSpec.BooleanValue enableXpMultiplier;

        // Requisitos de objetivos especiais
        public final ModConfigSpec.BooleanValue reqElderGuardian;
        public final ModConfigSpec.BooleanValue reqRaid;
        public final ModConfigSpec.BooleanValue reqTrialVaultAdv;
        // NOVO: Configuração específica para Voluntary Exile
        public final ModConfigSpec.BooleanValue reqVoluntaryExile;
        public final ModConfigSpec.BooleanValue reqWither;
        public final ModConfigSpec.BooleanValue reqWarden;

        // Requisitos de quantidade de mobs - Fase 1
        public final ModConfigSpec.IntValue reqZombieKills;
        // REMOVIDO: reqZombieVillagerKills
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

        // CORREÇÃO: Ravager e Evoker como Goal Kills com valores corretos
        public final ModConfigSpec.IntValue reqRavagerKills;
        public final ModConfigSpec.IntValue reqEvokerKills;

        // Requisitos de quantidade de mobs - Fase 2
        public final ModConfigSpec.IntValue reqBlazeKills;
        public final ModConfigSpec.IntValue reqWitherSkeletonKills;
        public final ModConfigSpec.IntValue reqPiglinBruteKills;
        public final ModConfigSpec.IntValue reqHoglinKills;
        public final ModConfigSpec.IntValue reqZoglinKills;
        public final ModConfigSpec.IntValue reqGhastKills;
        // ✅ REMOVIDO COMPLETAMENTE: public final ModConfigSpec.IntValue reqEndermiteKills;
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

            // NOVO: Configuração para multiplicador de XP
            enableXpMultiplier = builder
                    .comment("Enable XP multiplier from mobs based on phase progression (same as health/damage multiplier)")
                    .define("enableXpMultiplier", true);

            builder.pop();

            builder.push("Phase 1 Special Objectives");

            reqElderGuardian = builder
                    .comment("Require Elder Guardian kill for Phase 1")
                    .define("reqElderGuardian", true);

            reqRaid = builder
                    .comment("Require winning a raid for Phase 1")
                    .define("reqRaid", true);

            reqTrialVaultAdv = builder
                    .comment("Require Trial Vault advancement for Phase 1")
                    .define("reqTrialVaultAdv", true);

            // NOVO: Configuração específica para Voluntary Exile
            reqVoluntaryExile = builder
                    .comment("Require Voluntary Exile advancement for Phase 1 (earned by killing raid captain)")
                    .define("reqVoluntaryExile", true);

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

            // REMOVIDO: reqZombieVillagerKills - não é mais necessário

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

            // CORREÇÃO: Ravager e Evoker como Goal Kills com valores corretos
            reqRavagerKills = builder
                    .comment("Number of Ravagers to kill for Phase 1 (Goal Kill)")
                    .defineInRange("reqRavagerKills", 1, 0, 20); // VALOR CORRETO: 1

            reqEvokerKills = builder
                    .comment("Number of Evokers to kill for Phase 1 (Goal Kill)")
                    .defineInRange("reqEvokerKills", 5, 0, 50); // VALOR CORRETO: 5

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
                    .defineInRange("reqHoglinKills", 1, 0, 100); // VALOR CORRETO: 1

            reqZoglinKills = builder
                    .comment("Number of Zoglins to kill for Phase 2")
                    .defineInRange("reqZoglinKills", 1, 0, 50); // VALOR CORRETO: 1

            reqGhastKills = builder
                    .comment("Number of Ghasts to kill for Phase 2")
                    .defineInRange("reqGhastKills", 10, 0, 100);

            // ✅ REMOVIDO COMPLETAMENTE: reqEndermiteKills
            // reqEndermiteKills = builder
            //         .comment("Number of Endermites to kill for Phase 2")
            //         .defineInRange("reqEndermiteKills", 5, 0, 50);

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

    // ADICIONADO: Configurações do cliente
    public static class Client {

        // Configurações de interface
        public final ModConfigSpec.BooleanValue enableHUD;
        public final ModConfigSpec.BooleanValue enableProgressionBook;
        public final ModConfigSpec.BooleanValue enableSounds;
        public final ModConfigSpec.BooleanValue enableParticles;

        // Configurações de exibição
        public final ModConfigSpec.EnumValue<HUDPosition> hudPosition;
        public final ModConfigSpec.DoubleValue hudScale;
        public final ModConfigSpec.IntValue hudXOffset;
        public final ModConfigSpec.IntValue hudYOffset;

        Client(ModConfigSpec.Builder builder) {
            builder.push("Interface Configuration");

            enableHUD = builder
                    .comment("Enable progression HUD display")
                    .define("enableHUD", true);

            enableProgressionBook = builder
                    .comment("Enable progression book item")
                    .define("enableProgressionBook", true);

            enableSounds = builder
                    .comment("Enable mod sound effects")
                    .define("enableSounds", true);

            enableParticles = builder
                    .comment("Enable mod particle effects")
                    .define("enableParticles", true);

            builder.pop();

            builder.push("HUD Configuration");

            hudPosition = builder
                    .comment("HUD position on screen")
                    .defineEnum("hudPosition", HUDPosition.TOP_LEFT);

            hudScale = builder
                    .comment("HUD scale factor")
                    .defineInRange("hudScale", 1.0, 0.5, 2.0);

            hudXOffset = builder
                    .comment("HUD horizontal offset")
                    .defineInRange("hudXOffset", 10, -1000, 1000);

            hudYOffset = builder
                    .comment("HUD vertical offset")
                    .defineInRange("hudYOffset", 10, -1000, 1000);

            builder.pop();
        }
    }

    // Enum para posição do HUD
    public enum HUDPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }
}