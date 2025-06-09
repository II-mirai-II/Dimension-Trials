package net.mirai.dimtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.ProgressionData;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DimTrCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimtr")
                .requires(source -> source.hasPermission(2)) // OP level 2
                .then(Commands.literal("complete")
                        .then(Commands.literal("phase1")
                                .executes(DimTrCommands::executeCompletePhase1))
                        .then(Commands.literal("phase2")
                                .executes(DimTrCommands::executeCompletePhase2)))
                .then(Commands.literal("reset")
                        .then(Commands.literal("all")
                                .executes(DimTrCommands::executeResetAll))
                        .then(Commands.literal("phase1")
                                .executes(DimTrCommands::executeResetPhase1))
                        .then(Commands.literal("phase2")
                                .executes(DimTrCommands::executeResetPhase2))
                        .then(Commands.literal("mob_kills")
                                .executes(DimTrCommands::executeResetMobKills)))
                .then(Commands.literal("set")
                        .then(Commands.literal("goal")
                                .then(Commands.argument("goal_name", StringArgumentType.string())
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(DimTrCommands::executeSetGoal))))
                        .then(Commands.literal("mob_kill")
                                .then(Commands.argument("mob_type", StringArgumentType.string())
                                        .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                                .executes(DimTrCommands::executeSetMobKill)))))
                .then(Commands.literal("status")
                        .executes(DimTrCommands::executeStatus))
                .then(Commands.literal("sync")
                        .executes(DimTrCommands::executeForceSync))
                .then(Commands.literal("debug")
                        .then(Commands.literal("payload")
                                .executes(DimTrCommands::executeDebugPayload))));
    }

    // ============================================================================
    // COMANDOS DE COMPLETAR FASES (PARA TESTES)
    // ============================================================================

    private static int executeCompletePhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        DimTrMod.LOGGER.info("Starting Phase 1 completion via command...");

        // CORREÇÃO: Marcar dirty ANTES para garantir que as mudanças sejam salvas
        progressionData.setDirty();

        // Completar objetivos especiais
        progressionData.elderGuardianKilled = true;
        progressionData.raidWon = true;
        progressionData.ravagerKilled = true; // Manter por compatibilidade
        progressionData.evokerKilled = true; // Manter por compatibilidade
        progressionData.trialVaultAdvancementEarned = true;
        progressionData.voluntaireExileAdvancementEarned = true; // NOVO: Completar Voluntaire Exile

        // Definir diretamente os valores dos contadores de mobs da Fase 1
        progressionData.zombieKills = 50;
        // ✅ REMOVIDO: progressionData.zombieVillagerKills = 3;
        progressionData.skeletonKills = 40;
        progressionData.strayKills = 10;
        progressionData.huskKills = 10;
        progressionData.spiderKills = 30;
        progressionData.creeperKills = 30;
        progressionData.drownedKills = 20;
        progressionData.endermanKills = 5;
        progressionData.witchKills = 5;
        progressionData.pillagerKills = 20;
        progressionData.captainKills = 1;
        progressionData.vindicatorKills = 10;
        progressionData.boggedKills = 10;
        progressionData.breezeKills = 5;

        // CORREÇÃO: Completar Ravager e Evoker como Goal Kills com valores atualizados
        progressionData.ravagerKills = 1; // Novo valor: 1
        progressionData.evokerKills = 5; // Novo valor: 5

        DimTrMod.LOGGER.info("Phase 1 mob counters set. Checking completion...");

        // CORREÇÃO: Usar o método de verificação sem announce para evitar problemas
        progressionData.checkAndCompletePhase1Internal();

        // Forçar que phase1Completed seja true
        progressionData.phase1Completed = true;

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        context.getSource().sendSuccess(() -> Component.literal("All Phase 1 requirements completed via command."), true);
        DimTrMod.LOGGER.info("Phase 1 completed via command by {} - Mob counters set and phase marked complete", context.getSource().getTextName());
        return 1;
    }

    private static int executeCompletePhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        DimTrMod.LOGGER.info("Starting Phase 2 completion via command...");

        // CORREÇÃO: Marcar dirty ANTES para garantir que as mudanças sejam salvas
        progressionData.setDirty();

        // Completar Fase 1 primeiro se não estiver
        if (!progressionData.phase1Completed) {
            executeCompletePhase1(context);
        }

        // Completar objetivos especiais da Fase 2
        progressionData.witherKilled = true;
        progressionData.wardenKilled = true;

        // Completar todos os mob kills da Fase 2 (Nether mobs)
        progressionData.blazeKills = 20;
        progressionData.witherSkeletonKills = 15;
        progressionData.piglinBruteKills = 5;
        progressionData.hoglinKills = 1; // CORREÇÃO: Valor correto
        progressionData.zoglinKills = 1; // CORREÇÃO: Valor correto
        progressionData.ghastKills = 10;
        // ✅ REMOVIDO: progressionData.endermiteKills = 5;
        progressionData.piglinKills = 30;

        // ATUALIZADO: Completar os requisitos aumentados do Overworld (125% dos valores originais)
        progressionData.zombieKills = Math.max(progressionData.zombieKills, 63); // 50 * 1.25 = 62.5 -> 63
        // ✅ REMOVIDO: progressionData.zombieVillagerKills = Math.max(progressionData.zombieVillagerKills, 4);
        progressionData.skeletonKills = Math.max(progressionData.skeletonKills, 50); // 40 * 1.25 = 50
        progressionData.strayKills = Math.max(progressionData.strayKills, 13); // 10 * 1.25 = 12.5 -> 13
        progressionData.huskKills = Math.max(progressionData.huskKills, 13); // 10 * 1.25 = 12.5 -> 13
        progressionData.spiderKills = Math.max(progressionData.spiderKills, 38); // 30 * 1.25 = 37.5 -> 38
        progressionData.creeperKills = Math.max(progressionData.creeperKills, 38); // 30 * 1.25 = 37.5 -> 38
        progressionData.drownedKills = Math.max(progressionData.drownedKills, 25); // 20 * 1.25 = 25
        progressionData.endermanKills = Math.max(progressionData.endermanKills, 7); // 5 * 1.25 = 6.25 -> 7
        progressionData.witchKills = Math.max(progressionData.witchKills, 7); // 5 * 1.25 = 6.25 -> 7
        progressionData.pillagerKills = Math.max(progressionData.pillagerKills, 25); // 20 * 1.25 = 25
        progressionData.captainKills = Math.max(progressionData.captainKills, 2); // 1 * 1.25 = 1.25 -> 2
        progressionData.vindicatorKills = Math.max(progressionData.vindicatorKills, 13); // 10 * 1.25 = 12.5 -> 13
        progressionData.boggedKills = Math.max(progressionData.boggedKills, 13); // 10 * 1.25 = 12.5 -> 13
        progressionData.breezeKills = Math.max(progressionData.breezeKills, 7); // 5 * 1.25 = 6.25 -> 7

        // CORREÇÃO: Ravager e Evoker com valores atualizados e 125% para Fase 2
        progressionData.ravagerKills = Math.max(progressionData.ravagerKills, 2); // 1 * 1.25 = 1.25 -> 2
        progressionData.evokerKills = Math.max(progressionData.evokerKills, 7); // 5 * 1.25 = 6.25 -> 7

        DimTrMod.LOGGER.info("Phase 2 mob counters set. Checking completion...");

        // CORREÇÃO: Usar o método de verificação sem announce para evitar problemas
        progressionData.checkAndCompletePhase2Internal();

        // Forçar que phase2Completed seja true
        progressionData.phase2Completed = true;

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        context.getSource().sendSuccess(() -> Component.literal("All Phase 2 requirements completed via command."), true);
        DimTrMod.LOGGER.info("Phase 2 completed via command by {} - All mob counters set and phase marked complete", context.getSource().getTextName());
        return 1;
    }

    // ============================================================================
    // COMANDOS EXISTENTES (MANTIDOS)
    // ============================================================================

    private static int executeSetGoal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        String goalName = StringArgumentType.getString(context, "goal_name");
        boolean value = BoolArgumentType.getBool(context, "value");

        // CORREÇÃO: Marcar dirty ANTES
        progressionData.setDirty();

        boolean success = switch (goalName.toLowerCase()) {
            case "elder_guardian" -> progressionData.updateElderGuardianKilled(value);
            case "raid" -> progressionData.updateRaidWon(value);
            case "ravager" -> progressionData.updateRavagerKilled(value);
            case "evoker" -> progressionData.updateEvokerKilled(value);
            case "trial_vault" -> progressionData.updateTrialVaultAdvancementEarned(value);
            // NOVO: Suporte para Voluntaire Exile
            case "voluntaire_exile" -> progressionData.updateVoluntaireExileAdvancementEarned(value);
            case "wither" -> progressionData.updateWitherKilled(value);
            case "warden" -> progressionData.updateWardenKilled(value);
            default -> {
                context.getSource().sendFailure(Component.literal("Invalid goal name: " + goalName));
                yield false;
            }
        };

        if (success) {
            context.getSource().sendSuccess(() -> Component.literal("Goal '" + goalName + "' set to " + value), true);
            return 1;
        }

        return 0;
    }

    private static int executeSetMobKill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        String mobType = StringArgumentType.getString(context, "mob_type");
        int count = IntegerArgumentType.getInteger(context, "count");

        // CORREÇÃO: Marcar dirty ANTES
        progressionData.setDirty();

        boolean success = switch (mobType.toLowerCase()) {
            case "zombie" -> { progressionData.zombieKills = count; yield true; }
            // ✅ REMOVIDO: case "zombie_villager" -> { progressionData.zombieVillagerKills = count; yield true; }
            case "skeleton" -> { progressionData.skeletonKills = count; yield true; }
            case "stray" -> { progressionData.strayKills = count; yield true; }
            case "husk" -> { progressionData.huskKills = count; yield true; }
            case "spider" -> { progressionData.spiderKills = count; yield true; }
            case "creeper" -> { progressionData.creeperKills = count; yield true; }
            case "drowned" -> { progressionData.drownedKills = count; yield true; }
            case "enderman" -> { progressionData.endermanKills = count; yield true; }
            case "witch" -> { progressionData.witchKills = count; yield true; }
            case "pillager" -> { progressionData.pillagerKills = count; yield true; }
            case "captain" -> { progressionData.captainKills = count; yield true; }
            case "vindicator" -> { progressionData.vindicatorKills = count; yield true; }
            case "bogged" -> { progressionData.boggedKills = count; yield true; }
            case "breeze" -> { progressionData.breezeKills = count; yield true; }
            case "ravager" -> { progressionData.ravagerKills = count; yield true; }
            case "evoker" -> { progressionData.evokerKills = count; yield true; }
            case "blaze" -> { progressionData.blazeKills = count; yield true; }
            case "wither_skeleton" -> { progressionData.witherSkeletonKills = count; yield true; }
            case "piglin_brute" -> { progressionData.piglinBruteKills = count; yield true; }
            case "hoglin" -> { progressionData.hoglinKills = count; yield true; }
            case "zoglin" -> { progressionData.zoglinKills = count; yield true; }
            case "ghast" -> { progressionData.ghastKills = count; yield true; }
            // ✅ REMOVIDO: case "endermite" -> { progressionData.endermiteKills = count; yield true; }
            case "piglin" -> { progressionData.piglinKills = count; yield true; }
            default -> {
                context.getSource().sendFailure(Component.literal("Invalid mob type: " + mobType));
                yield false;
            }
        };

        if (success) {
            progressionData.markDirtyAndSendUpdates();
            context.getSource().sendSuccess(() -> Component.literal("Mob kill count for '" + mobType + "' set to " + count), true);
            return 1;
        }

        return 0;
    }

    private static int executeResetAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // Reset all progression data
        progressionData.elderGuardianKilled = false;
        progressionData.raidWon = false;
        progressionData.ravagerKilled = false;
        progressionData.evokerKilled = false;
        progressionData.trialVaultAdvancementEarned = false;
        progressionData.voluntaireExileAdvancementEarned = false;
        progressionData.phase1Completed = false;
        progressionData.witherKilled = false;
        progressionData.wardenKilled = false;
        progressionData.phase2Completed = false;

        // Reset all mob kill counters
        progressionData.zombieKills = 0;
        // ✅ REMOVIDO: progressionData.zombieVillagerKills = 0;
        progressionData.skeletonKills = 0;
        progressionData.strayKills = 0;
        progressionData.huskKills = 0;
        progressionData.spiderKills = 0;
        progressionData.creeperKills = 0;
        progressionData.drownedKills = 0;
        progressionData.endermanKills = 0;
        progressionData.witchKills = 0;
        progressionData.pillagerKills = 0;
        progressionData.captainKills = 0;
        progressionData.vindicatorKills = 0;
        progressionData.boggedKills = 0;
        progressionData.breezeKills = 0;
        progressionData.ravagerKills = 0;
        progressionData.evokerKills = 0;
        progressionData.blazeKills = 0;
        progressionData.witherSkeletonKills = 0;
        progressionData.piglinBruteKills = 0;
        progressionData.hoglinKills = 0;
        progressionData.zoglinKills = 0;
        progressionData.ghastKills = 0;
        // ✅ REMOVIDO: progressionData.endermiteKills = 0;
        progressionData.piglinKills = 0;

        progressionData.markDirtyAndSendUpdates();
        context.getSource().sendSuccess(() -> Component.literal("All progression data has been reset"), true);
        return 1;
    }

    private static int executeResetPhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // Reset Phase 1 specific data
        progressionData.elderGuardianKilled = false;
        progressionData.raidWon = false;
        progressionData.ravagerKilled = false;
        progressionData.evokerKilled = false;
        progressionData.trialVaultAdvancementEarned = false;
        progressionData.voluntaireExileAdvancementEarned = false;
        progressionData.phase1Completed = false;

        // Reset Phase 1 mob counters
        progressionData.zombieKills = 0;
        // ✅ REMOVIDO: progressionData.zombieVillagerKills = 0;
        progressionData.skeletonKills = 0;
        progressionData.strayKills = 0;
        progressionData.huskKills = 0;
        progressionData.spiderKills = 0;
        progressionData.creeperKills = 0;
        progressionData.drownedKills = 0;
        progressionData.endermanKills = 0;
        progressionData.witchKills = 0;
        progressionData.pillagerKills = 0;
        progressionData.captainKills = 0;
        progressionData.vindicatorKills = 0;
        progressionData.boggedKills = 0;
        progressionData.breezeKills = 0;
        progressionData.ravagerKills = 0;
        progressionData.evokerKills = 0;

        progressionData.markDirtyAndSendUpdates();
        context.getSource().sendSuccess(() -> Component.literal("Phase 1 progression data has been reset"), true);
        return 1;
    }

    private static int executeResetPhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // Reset Phase 2 specific data
        progressionData.witherKilled = false;
        progressionData.wardenKilled = false;
        progressionData.phase2Completed = false;

        // Reset Phase 2 mob counters
        progressionData.blazeKills = 0;
        progressionData.witherSkeletonKills = 0;
        progressionData.piglinBruteKills = 0;
        progressionData.hoglinKills = 0;
        progressionData.zoglinKills = 0;
        progressionData.ghastKills = 0;
        // ✅ REMOVIDO: progressionData.endermiteKills = 0;
        progressionData.piglinKills = 0;

        progressionData.markDirtyAndSendUpdates();
        context.getSource().sendSuccess(() -> Component.literal("Phase 2 progression data has been reset"), true);
        return 1;
    }

    private static int executeResetMobKills(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // Reset all mob kill counters but keep objectives
        progressionData.zombieKills = 0;
        // ✅ REMOVIDO: progressionData.zombieVillagerKills = 0;
        progressionData.skeletonKills = 0;
        progressionData.strayKills = 0;
        progressionData.huskKills = 0;
        progressionData.spiderKills = 0;
        progressionData.creeperKills = 0;
        progressionData.drownedKills = 0;
        progressionData.endermanKills = 0;
        progressionData.witchKills = 0;
        progressionData.pillagerKills = 0;
        progressionData.captainKills = 0;
        progressionData.vindicatorKills = 0;
        progressionData.boggedKills = 0;
        progressionData.breezeKills = 0;
        progressionData.ravagerKills = 0;
        progressionData.evokerKills = 0;
        progressionData.blazeKills = 0;
        progressionData.witherSkeletonKills = 0;
        progressionData.piglinBruteKills = 0;
        progressionData.hoglinKills = 0;
        progressionData.zoglinKills = 0;
        progressionData.ghastKills = 0;
        // ✅ REMOVIDO: progressionData.endermiteKills = 0;
        progressionData.piglinKills = 0;

        progressionData.markDirtyAndSendUpdates();
        context.getSource().sendSuccess(() -> Component.literal("All mob kill counters have been reset"), true);
        return 1;
    }

    // ============================================================================
    // COMANDOS DE DEBUG AVANÇADOS
    // ============================================================================

    private static int executeStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // DEBUG: Verificar valores do servidor
        context.getSource().sendSuccess(() -> Component.literal("=== SERVER CONFIG DEBUG ==="), false);
        context.getSource().sendSuccess(() -> Component.literal("Server Ravager req: " + DimTrConfig.SERVER.reqRavagerKills.get()), false);
        context.getSource().sendSuccess(() -> Component.literal("Server Evoker req: " + DimTrConfig.SERVER.reqEvokerKills.get()), false);
        context.getSource().sendSuccess(() -> Component.literal("Server Hoglin req: " + DimTrConfig.SERVER.reqHoglinKills.get()), false);
        context.getSource().sendSuccess(() -> Component.literal("Server Zoglin req: " + DimTrConfig.SERVER.reqZoglinKills.get()), false);

        // DEBUG: Verificar payload que seria enviado
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            UpdateProgressionToClientPayload payload = progressionData.createPayload();
            context.getSource().sendSuccess(() -> Component.literal("=== PAYLOAD DEBUG ==="), false);
            context.getSource().sendSuccess(() -> Component.literal("Payload Ravager req: " + payload.reqRavagerKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("Payload Evoker req: " + payload.reqEvokerKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("Payload Hoglin req: " + payload.reqHoglinKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("Payload Zoglin req: " + payload.reqZoglinKills()), false);

            // Forçar envio do payload
            try {
                context.getSource().sendSuccess(() -> Component.literal("Attempting to send payload..."), false);
                progressionData.sendToClient(player);
                context.getSource().sendSuccess(() -> Component.literal("✅ Payload sent successfully!"), false);
                context.getSource().sendSuccess(() -> Component.literal("Check client logs for 'PAYLOAD HANDLER CALLED'"), false);
            } catch (Exception e) {
                context.getSource().sendFailure(Component.literal("❌ Failed to send payload: " + e.getMessage()));
                e.printStackTrace();
            }
        }

        // Mostrar status das fases
        context.getSource().sendSuccess(() -> Component.literal("=== DIMENSION TRIALS STATUS ==="), false);
        context.getSource().sendSuccess(() -> Component.literal("Phase 1 Complete: " + progressionData.phase1Completed), false);
        context.getSource().sendSuccess(() -> Component.literal("Phase 2 Complete: " + progressionData.phase2Completed), false);

        // Mostrar objetivos especiais
        context.getSource().sendSuccess(() -> Component.literal("--- Special Objectives ---"), false);
        context.getSource().sendSuccess(() -> Component.literal("Elder Guardian: " + progressionData.elderGuardianKilled), false);
        context.getSource().sendSuccess(() -> Component.literal("Raid Won: " + progressionData.raidWon), false);
        context.getSource().sendSuccess(() -> Component.literal("Trial Vault: " + progressionData.trialVaultAdvancementEarned), false);
        context.getSource().sendSuccess(() -> Component.literal("Voluntaire Exile: " + progressionData.voluntaireExileAdvancementEarned), false);
        context.getSource().sendSuccess(() -> Component.literal("Wither: " + progressionData.witherKilled), false);
        context.getSource().sendSuccess(() -> Component.literal("Warden: " + progressionData.wardenKilled), false);

        // Mostrar alguns contadores importantes
        context.getSource().sendSuccess(() -> Component.literal("--- Phase 1 Mobs ---"), false);
        context.getSource().sendSuccess(() -> Component.literal("Zombies: " + progressionData.zombieKills), false);
        context.getSource().sendSuccess(() -> Component.literal("Skeletons: " + progressionData.skeletonKills), false);
        context.getSource().sendSuccess(() -> Component.literal("Ravagers: " + progressionData.ravagerKills), false);
        context.getSource().sendSuccess(() -> Component.literal("Evokers: " + progressionData.evokerKills), false);

        context.getSource().sendSuccess(() -> Component.literal("--- Phase 2 Mobs ---"), false);
        context.getSource().sendSuccess(() -> Component.literal("Blazes: " + progressionData.blazeKills), false);
        context.getSource().sendSuccess(() -> Component.literal("Hoglins: " + progressionData.hoglinKills), false);
        context.getSource().sendSuccess(() -> Component.literal("Zoglins: " + progressionData.zoglinKills), false);

        return 1;
    }

    // NOVO: Comando para forçar sincronização
    private static int executeForceSync(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            context.getSource().sendSuccess(() -> Component.literal("🔄 Forcing client sync..."), false);

            try {
                progressionData.sendToClient(player);
                context.getSource().sendSuccess(() -> Component.literal("✅ Sync command sent to client!"), false);
                context.getSource().sendSuccess(() -> Component.literal("Check logs for 'PAYLOAD HANDLER CALLED'"), false);
                context.getSource().sendSuccess(() -> Component.literal("Open HUD (J) to verify correct values."), false);
            } catch (Exception e) {
                context.getSource().sendFailure(Component.literal("❌ Failed to sync: " + e.getMessage()));
                e.printStackTrace();
            }
        } else {
            context.getSource().sendFailure(Component.literal("This command must be run by a player."));
        }

        return 1;
    }

    // NOVO: Comando para debug detalhado do payload
    private static int executeDebugPayload(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            UpdateProgressionToClientPayload payload = progressionData.createPayload();

            context.getSource().sendSuccess(() -> Component.literal("=== DETAILED PAYLOAD DEBUG ==="), false);

            // Debug todos os valores importantes
            context.getSource().sendSuccess(() -> Component.literal("Phase 1 Goal Requirements:"), false);
            context.getSource().sendSuccess(() -> Component.literal("  Zombies: " + payload.reqZombieKills() + " | Current: " + payload.zombieKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("  Skeletons: " + payload.reqSkeletonKills() + " | Current: " + payload.skeletonKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("  Ravagers: " + payload.reqRavagerKills() + " | Current: " + payload.ravagerKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("  Evokers: " + payload.reqEvokerKills() + " | Current: " + payload.evokerKills()), false);

            context.getSource().sendSuccess(() -> Component.literal("Phase 2 Goal Requirements:"), false);
            context.getSource().sendSuccess(() -> Component.literal("  Blazes: " + payload.reqBlazeKills() + " | Current: " + payload.blazeKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("  Hoglins: " + payload.reqHoglinKills() + " | Current: " + payload.hoglinKills()), false);
            context.getSource().sendSuccess(() -> Component.literal("  Zoglins: " + payload.reqZoglinKills() + " | Current: " + payload.zoglinKills()), false);

            context.getSource().sendSuccess(() -> Component.literal("Expected Values:"), false);
            context.getSource().sendSuccess(() -> Component.literal("  Ravager req should be 1"), false);
            context.getSource().sendSuccess(() -> Component.literal("  Evoker req should be 5"), false);
            context.getSource().sendSuccess(() -> Component.literal("  Hoglin req should be 1"), false);
            context.getSource().sendSuccess(() -> Component.literal("  Zoglin req should be 1"), false);

        } else {
            context.getSource().sendFailure(Component.literal("This command must be run by a player."));
        }

        return 1;
    }
}