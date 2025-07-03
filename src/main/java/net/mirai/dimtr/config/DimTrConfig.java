package net.mirai.dimtr.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Configurações do mod Dimension Trials - VERSÃO COMPLETA E ATUALIZADA
 *
 * ✅ Sistema de progressão individual
 * ✅ Sistema de parties colaborativas
 * ✅ Configurações de cliente completas
 * ✅ Valores corretos para todos os mobs
 * ✅ Remoção de configs obsoletos
 */
public class DimTrConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

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

    // ============================================================================
    // 🎯 CONFIGURAÇÕES DO SERVIDOR
    // ============================================================================
    public static class Server {

        // Configurações principais de fase
        public final ModConfigSpec.BooleanValue enablePhase1;
        public final ModConfigSpec.BooleanValue enablePhase2;
        public final ModConfigSpec.BooleanValue enableMobKillsPhase1;
        public final ModConfigSpec.BooleanValue enableMobKillsPhase2;
        public final ModConfigSpec.BooleanValue enableMultipliers;
        public final ModConfigSpec.BooleanValue enableXpMultiplier;

        // Requisitos de objetivos especiais - Fase 1
        public final ModConfigSpec.BooleanValue reqElderGuardian;
        public final ModConfigSpec.BooleanValue reqRaid;
        public final ModConfigSpec.BooleanValue reqTrialVaultAdv;
        public final ModConfigSpec.BooleanValue reqVoluntaryExile;

        // Requisitos de objetivos especiais - Fase 2
        public final ModConfigSpec.BooleanValue reqWither;
        public final ModConfigSpec.BooleanValue reqWarden;

        // Requisitos de quantidade de mobs - Fase 1
        public final ModConfigSpec.IntValue reqZombieKills;
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
        public final ModConfigSpec.IntValue reqRavagerKills; // Goal Kill: 1
        public final ModConfigSpec.IntValue reqEvokerKills;  // Goal Kill: 5

        // Requisitos de quantidade de mobs - Fase 2
        public final ModConfigSpec.IntValue reqBlazeKills;
        public final ModConfigSpec.IntValue reqWitherSkeletonKills;
        public final ModConfigSpec.IntValue reqPiglinBruteKills;
        public final ModConfigSpec.IntValue reqHoglinKills;
        public final ModConfigSpec.IntValue reqZoglinKills;
        public final ModConfigSpec.IntValue reqGhastKills;
        public final ModConfigSpec.IntValue reqPiglinKills;

        // Multiplicadores de dificuldade
        public final ModConfigSpec.DoubleValue phase1Multiplier;
        public final ModConfigSpec.DoubleValue phase2Multiplier;

        // 🎯 NOVO: Configurações do sistema de parties
        public final ModConfigSpec.BooleanValue enablePartySystem;
        public final ModConfigSpec.IntValue maxPartySize;
        public final ModConfigSpec.DoubleValue partyProgressionMultiplier;
        public final ModConfigSpec.DoubleValue partyProximityRadius;

        // 🎯 NOVO: Configurações de debug e sincronização
        public final ModConfigSpec.BooleanValue enableDebugLogging;
        public final ModConfigSpec.BooleanValue enableProgressionSync;
        public final ModConfigSpec.IntValue syncInterval;

        // 🎯 NOVO: Configurações para integração com mods externos
        public final ModConfigSpec.BooleanValue enableExternalModIntegration;
        public final ModConfigSpec.BooleanValue enableMowziesModsIntegration;
        public final ModConfigSpec.BooleanValue enableCataclysmIntegration;
        public final ModConfigSpec.BooleanValue requireExternalModBosses;
        public final ModConfigSpec.BooleanValue createPhase3ForEndBosses;

        Server(ModConfigSpec.Builder builder) {
            // ========================================================================
            // 🎯 CONFIGURAÇÕES PRINCIPAIS DE FASE
            // ========================================================================
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

            enableXpMultiplier = builder
                    .comment("Enable XP multiplier from mobs based on phase progression (same as health/damage multiplier)")
                    .define("enableXpMultiplier", true);

            builder.pop();

            // ========================================================================
            // 🎯 OBJETIVOS ESPECIAIS - FASE 1
            // ========================================================================
            builder.push("Phase 1 Special Objectives");

            reqElderGuardian = builder
                    .comment("Require Elder Guardian kill for Phase 1")
                    .define("reqElderGuardian", true);

            reqRaid = builder
                    .comment("Require winning a raid for Phase 1 (Hero of the Village advancement)")
                    .define("reqRaid", true);

            reqTrialVaultAdv = builder
                    .comment("Require Trial Vault advancement for Phase 1 (Under Lock and Key)")
                    .define("reqTrialVaultAdv", true);

            reqVoluntaryExile = builder
                    .comment("Require Voluntary Exile advancement for Phase 1 (earned by killing raid captain)")
                    .define("reqVoluntaryExile", true);

            builder.pop();

            // ========================================================================
            // 🎯 OBJETIVOS ESPECIAIS - FASE 2
            // ========================================================================
            builder.push("Phase 2 Special Objectives");

            reqWither = builder
                    .comment("Require Wither kill for Phase 2")
                    .define("reqWither", true);

            reqWarden = builder
                    .comment("Require Warden kill for Phase 2")
                    .define("reqWarden", true);

            builder.pop();

            // ========================================================================
            // 🎯 REQUISITOS DE MOBS - FASE 1
            // ========================================================================
            builder.push("Phase 1 Mob Kill Requirements");

            reqZombieKills = builder
                    .comment("Number of Zombies to kill for Phase 1")
                    .defineInRange("reqZombieKills", 50, 0, 1000);

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

            // Goal Kills da Fase 1 (valores corretos)
            reqRavagerKills = builder
                    .comment("Number of Ravagers to kill for Phase 1 (Goal Kill - rare mob)")
                    .defineInRange("reqRavagerKills", 1, 0, 20);

            reqEvokerKills = builder
                    .comment("Number of Evokers to kill for Phase 1 (Goal Kill - hard mob)")
                    .defineInRange("reqEvokerKills", 5, 0, 50);

            builder.pop();

            // ========================================================================
            // 🎯 REQUISITOS DE MOBS - FASE 2
            // ========================================================================
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
                    .comment("Number of Hoglins to kill for Phase 2 (rare spawn)")
                    .defineInRange("reqHoglinKills", 1, 0, 100);

            reqZoglinKills = builder
                    .comment("Number of Zoglins to kill for Phase 2 (rare spawn)")
                    .defineInRange("reqZoglinKills", 1, 0, 50);

            reqGhastKills = builder
                    .comment("Number of Ghasts to kill for Phase 2")
                    .defineInRange("reqGhastKills", 10, 0, 100);

            reqPiglinKills = builder
                    .comment("Number of hostile Piglins to kill for Phase 2")
                    .defineInRange("reqPiglinKills", 30, 0, 300);

            builder.pop();

            // ========================================================================
            // 🎯 MULTIPLICADORES DE DIFICULDADE
            // ========================================================================
            builder.push("Difficulty Multipliers");

            // CORREÇÃO: Multiplicadores devem ser >= 1.0 (não podem reduzir dificuldade)
            phase1Multiplier = builder
                    .comment("Mob health/damage multiplier when Phase 1 is completed (minimum 1.0)")
                    .defineInRange("phase1Multiplier", 1.5, 1.0, 10.0);

            phase2Multiplier = builder
                    .comment("Mob health/damage multiplier when Phase 2 is completed (minimum 1.0)")
                    .defineInRange("phase2Multiplier", 2.0, 1.0, 10.0);

            builder.pop();

            // ========================================================================
            // 🎯 SISTEMA DE PARTIES
            // ========================================================================
            builder.push("Party System Configuration");

            enablePartySystem = builder
                    .comment("Enable collaborative party system for shared progression")
                    .define("enablePartySystem", true);

            maxPartySize = builder
                    .comment("Maximum number of players per party")
                    .defineInRange("maxPartySize", 4, 2, 10);

            partyProgressionMultiplier = builder
                    .comment("Progression multiplier per additional party member (0.75 = 75% faster per member)")
                    .defineInRange("partyProgressionMultiplier", 0.75, 0.0, 2.0);

            partyProximityRadius = builder
                    .comment("Radius in blocks for party members to share progress (0 = unlimited)")
                    .defineInRange("partyProximityRadius", 64.0, 0.0, 256.0);

            builder.pop();

            // ========================================================================
            // 🎯 DEBUG E SINCRONIZAÇÃO
            // ========================================================================
            builder.push("Debug and Synchronization");

            enableDebugLogging = builder
                    .comment("Enable detailed debug logging for troubleshooting")
                    .define("enableDebugLogging", false);

            enableProgressionSync = builder
                    .comment("Enable automatic progression synchronization between server and client")
                    .define("enableProgressionSync", true);

            syncInterval = builder
                    .comment("Progression sync interval in ticks (20 ticks = 1 second)")
                    .defineInRange("syncInterval", 100, 20, 1200);

            builder.pop();

            // ========================================================================
            // 🎯 INTEGRAÇÃO COM MODS EXTERNOS
            // ========================================================================
            builder.push("External Mod Integration");

            enableExternalModIntegration = builder
                    .comment("Enable automatic integration with supported external mods (Mowzie's Mobs, L_Ender's Cataclysm)")
                    .define("enableExternalModIntegration", true);

            enableMowziesModsIntegration = builder
                    .comment("Enable integration with Mowzie's Mobs (requires enableExternalModIntegration)")
                    .define("enableMowziesModsIntegration", true);

            enableCataclysmIntegration = builder
                    .comment("Enable integration with L_Ender's Cataclysm (requires enableExternalModIntegration)")
                    .define("enableCataclysmIntegration", true);

            requireExternalModBosses = builder
                    .comment("Make external mod bosses required for progression (if set to false, they become optional objectives)")
                    .define("requireExternalModBosses", true);

            createPhase3ForEndBosses = builder
                    .comment("Create Phase 3 for End-tier bosses from external mods")
                    .define("createPhase3ForEndBosses", true);

            builder.pop();
        }
    }

    // ============================================================================
    // 🎯 CONFIGURAÇÕES DO CLIENTE
    // ============================================================================
    public static class Client {

        // Configurações de interface
        public final ModConfigSpec.BooleanValue enableHUD;
        public final ModConfigSpec.BooleanValue enableSounds;
        public final ModConfigSpec.BooleanValue enableParticles;

        // Configurações de exibição do HUD
        public final ModConfigSpec.EnumValue<HUDPosition> hudPosition;
        public final ModConfigSpec.DoubleValue hudScale;
        public final ModConfigSpec.IntValue hudXOffset;
        public final ModConfigSpec.IntValue hudYOffset;

        // 🎯 NOVO: Configurações do sistema de parties no cliente
        public final ModConfigSpec.BooleanValue showPartyHUD;
        public final ModConfigSpec.BooleanValue showPartyNotifications;
        public final ModConfigSpec.BooleanValue showProximityIndicator;

        Client(ModConfigSpec.Builder builder) {
            // ====================================================================
            // 🎯 CONFIGURAÇÕES DE INTERFACE
            // ====================================================================
            builder.push("Interface Configuration");

            enableHUD = builder
                    .comment("Enable progression HUD display")
                    .define("enableHUD", true);

            enableSounds = builder
                    .comment("Enable mod sound effects")
                    .define("enableSounds", true);

            enableParticles = builder
                    .comment("Enable mod particle effects")
                    .define("enableParticles", true);

            builder.pop();

            // ====================================================================
            // 🎯 CONFIGURAÇÕES DO HUD
            // ====================================================================
            builder.push("HUD Configuration");

            hudPosition = builder
                    .comment("HUD position on screen")
                    .defineEnum("hudPosition", HUDPosition.TOP_LEFT);

            hudScale = builder
                    .comment("HUD scale factor")
                    .defineInRange("hudScale", 1.0, 0.5, 2.0);

            hudXOffset = builder
                    .comment("HUD horizontal offset from the chosen position")
                    .defineInRange("hudXOffset", 10, -1000, 1000);

            hudYOffset = builder
                    .comment("HUD vertical offset from the chosen position")
                    .defineInRange("hudYOffset", 10, -1000, 1000);

            builder.pop();

            // ====================================================================
            // 🎯 CONFIGURAÇÕES DE PARTY NO CLIENTE
            // ====================================================================
            builder.push("Party Interface Configuration");

            showPartyHUD = builder
                    .comment("Show party information in the HUD when in a party")
                    .define("showPartyHUD", true);

            showPartyNotifications = builder
                    .comment("Show notifications when party members achieve objectives")
                    .define("showPartyNotifications", true);

            showProximityIndicator = builder
                    .comment("Show visual indicator when within party proximity range")
                    .define("showProximityIndicator", true);

            builder.pop();
        }
    }

    // ============================================================================
    // 🎯 ENUM PARA POSIÇÃO DO HUD
    // ============================================================================
    public enum HUDPosition {
        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right"),
        CENTER("Center");

        private final String displayName;

        HUDPosition(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}