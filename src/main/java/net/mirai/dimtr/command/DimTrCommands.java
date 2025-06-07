package net.mirai.dimtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.ProgressionData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

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
                        .then(Commands.literal("progress")
                                .executes(DimTrCommands::executeResetProgress))
                        .then(Commands.literal("mob_kills")
                                .executes(DimTrCommands::executeResetMobKills)))
                .then(Commands.literal("set")
                        .then(Commands.literal("goal")
                                .then(Commands.argument("goal_name", StringArgumentType.string())
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(DimTrCommands::executeSetGoal))))
                        .then(Commands.literal("mob_kills")
                                .then(Commands.argument("mob_type", StringArgumentType.string())
                                        .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                                .executes(DimTrCommands::executeSetMobKills)))))
                .then(Commands.literal("status")
                        .executes(DimTrCommands::executeStatus)));
    }

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

        // Definir diretamente os valores dos contadores de mobs da Fase 1
        progressionData.zombieKills = 50;
        progressionData.zombieVillagerKills = 3;
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

        // CORREÇÃO 5: Completar Ravager e Evoker como Goal Kills com valores atualizados
        progressionData.ravagerKills = 1; // Novo valor: 1
        progressionData.evokerKills = 5; // Novo valor: 5

        DimTrMod.LOGGER.info("Phase 1 mob counters set. Checking completion...");

        // CORREÇÃO: Usar o método de verificação sem announce para evitar problemas
        progressionData.checkAndCompletePhase1Internal();

        // Forçar que phase1Completed seja true
        progressionData.phase1Completed = true;

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        // CORREÇÃO: Forçar sincronização com todos os jogadores online
        serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
            progressionData.sendToClient(player);
        });

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
        progressionData.hoglinKills = 1; // CORREÇÃO 4: 10 -> 1
        progressionData.zoglinKills = 1; // CORREÇÃO 4: 5 -> 1
        progressionData.ghastKills = 10;
        progressionData.endermiteKills = 5;
        progressionData.piglinKills = 30;

        // ATUALIZADO: Completar os requisitos aumentados do Overworld (125% dos valores originais)
        progressionData.zombieKills = Math.max(progressionData.zombieKills, 63); // 50 * 1.25 = 62.5 -> 63
        progressionData.zombieVillagerKills = Math.max(progressionData.zombieVillagerKills, 4); // 3 * 1.25 = 3.75 -> 4
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

        // CORREÇÃO 5: Ravager e Evoker com valores atualizados e 125% para Fase 2
        progressionData.ravagerKills = Math.max(progressionData.ravagerKills, 2); // 1 * 1.25 = 1.25 -> 2
        progressionData.evokerKills = Math.max(progressionData.evokerKills, 7); // 5 * 1.25 = 6.25 -> 7

        DimTrMod.LOGGER.info("Phase 2 mob counters set. Checking completion...");

        // CORREÇÃO: Usar o método de verificação sem announce para evitar problemas
        progressionData.checkAndCompletePhase2Internal();

        // Forçar que phase2Completed seja true
        progressionData.phase2Completed = true;

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        // CORREÇÃO: Forçar sincronização com todos os jogadores online
        serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
            progressionData.sendToClient(player);
        });

        context.getSource().sendSuccess(() -> Component.literal("All Phase 2 requirements completed via command."), true);
        DimTrMod.LOGGER.info("Phase 2 completed via command by {} - All mob counters set and phase marked complete", context.getSource().getTextName());
        return 1;
    }

    private static int executeResetProgress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // CORREÇÃO: Marcar dirty ANTES
        progressionData.setDirty();

        // Resetar objetivos originais
        progressionData.elderGuardianKilled = false;
        progressionData.raidWon = false;
        progressionData.ravagerKilled = false;
        progressionData.evokerKilled = false;
        progressionData.trialVaultAdvancementEarned = false;
        progressionData.phase1Completed = false;
        progressionData.witherKilled = false;
        progressionData.wardenKilled = false;
        progressionData.phase2Completed = false;

        // Resetar contadores de mobs
        resetAllMobKills(progressionData);

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        // CORREÇÃO: Forçar sincronização com todos os jogadores online
        serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
            progressionData.sendToClient(player);
        });

        context.getSource().sendSuccess(() -> Component.literal("All progression data reset."), true);
        DimTrMod.LOGGER.info("All progression data reset via command by {}", context.getSource().getTextName());
        return 1;
    }

    private static int executeSetGoal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        String goalName = StringArgumentType.getString(context, "goal_name");
        boolean value = BoolArgumentType.getBool(context, "value");

        // CORREÇÃO: Marcar dirty ANTES
        progressionData.setDirty();

        switch (goalName.toLowerCase()) {
            case "elder_guardian" -> progressionData.elderGuardianKilled = value;
            case "raid_won" -> progressionData.raidWon = value;
            case "ravager_killed" -> progressionData.ravagerKilled = value;
            case "evoker_killed" -> progressionData.evokerKilled = value;
            case "trial_vault_adv" -> progressionData.trialVaultAdvancementEarned = value;
            case "phase1_completed" -> progressionData.phase1Completed = value;
            case "wither_killed" -> progressionData.witherKilled = value;
            case "warden_killed" -> progressionData.wardenKilled = value;
            default -> {
                context.getSource().sendFailure(Component.literal("Invalid goal name: " + goalName));
                return 0;
            }
        }

        // Verificar progresso das fases após mudança
        progressionData.checkAndCompletePhase1Internal();
        progressionData.checkAndCompletePhase2Internal();

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        // CORREÇÃO: Forçar sincronização com todos os jogadores online
        serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
            progressionData.sendToClient(player);
        });

        context.getSource().sendSuccess(() -> Component.literal("Goal " + goalName + " set to " + value), true);
        DimTrMod.LOGGER.info("Goal {} set to {} via command by {}", goalName, value, context.getSource().getTextName());
        return 1;
    }

    private static int executeSetMobKills(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        String mobType = StringArgumentType.getString(context, "mob_type");
        int count = IntegerArgumentType.getInteger(context, "count");

        // CORREÇÃO: Marcar dirty ANTES
        progressionData.setDirty();

        boolean success = setMobKillCount(progressionData, mobType, count);

        if (success) {
            progressionData.checkAndCompletePhase1Internal();
            progressionData.checkAndCompletePhase2Internal();

            // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
            progressionData.markDirtyAndSendUpdates();

            // CORREÇÃO: Forçar sincronização com todos os jogadores online
            serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
                progressionData.sendToClient(player);
            });

            context.getSource().sendSuccess(() -> Component.literal("Set " + mobType + " kills to " + count), true);
            DimTrMod.LOGGER.info("Set {} kills to {} via command by {}", mobType, count, context.getSource().getTextName());
            return 1;
        } else {
            context.getSource().sendFailure(Component.literal("Invalid mob type: " + mobType));
            return 0;
        }
    }

    private static int executeResetMobKills(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // CORREÇÃO: Marcar dirty ANTES
        progressionData.setDirty();

        resetAllMobKills(progressionData);

        progressionData.checkAndCompletePhase1Internal();
        progressionData.checkAndCompletePhase2Internal();

        // CORREÇÃO: Marcar como dirty e enviar atualizações imediatamente
        progressionData.markDirtyAndSendUpdates();

        // CORREÇÃO: Forçar sincronização com todos os jogadores online
        serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
            progressionData.sendToClient(player);
        });

        context.getSource().sendSuccess(() -> Component.literal("All mob kill counters reset via command."), true);
        DimTrMod.LOGGER.info("All mob kill counters reset via command by {}", context.getSource().getTextName());
        return 1;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // Mostrar status das fases
        context.getSource().sendSuccess(() -> Component.literal("=== DIMENSION TRIALS STATUS ==="), false);
        context.getSource().sendSuccess(() -> Component.literal("Phase 1 Complete: " + progressionData.phase1Completed), false);
        context.getSource().sendSuccess(() -> Component.literal("Phase 2 Complete: " + progressionData.phase2Completed), false);

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

    // Métodos auxiliares
    private static void resetAllMobKills(ProgressionData progressionData) {
        // Fase 1 mobs
        progressionData.zombieKills = 0;
        progressionData.zombieVillagerKills = 0;
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

        // NOVO: Resetar Ravager e Evoker
        progressionData.ravagerKills = 0;
        progressionData.evokerKills = 0;

        // Fase 2 mobs
        progressionData.blazeKills = 0;
        progressionData.witherSkeletonKills = 0;
        progressionData.piglinBruteKills = 0;
        progressionData.hoglinKills = 0;
        progressionData.zoglinKills = 0;
        progressionData.ghastKills = 0;
        progressionData.endermiteKills = 0;
        progressionData.piglinKills = 0;
    }

    private static boolean setMobKillCount(ProgressionData progressionData, String mobType, int count) {
        return switch (mobType.toLowerCase()) {
            // Fase 1 mobs
            case "zombie" -> { progressionData.zombieKills = count; yield true; }
            case "zombie_villager" -> { progressionData.zombieVillagerKills = count; yield true; }
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

            // NOVO: Ravager e Evoker
            case "ravager" -> { progressionData.ravagerKills = count; yield true; }
            case "evoker" -> { progressionData.evokerKills = count; yield true; }

            // Fase 2 mobs
            case "blaze" -> { progressionData.blazeKills = count; yield true; }
            case "wither_skeleton" -> { progressionData.witherSkeletonKills = count; yield true; }
            case "piglin_brute" -> { progressionData.piglinBruteKills = count; yield true; }
            case "hoglin" -> { progressionData.hoglinKills = count; yield true; }
            case "zoglin" -> { progressionData.zoglinKills = count; yield true; }
            case "ghast" -> { progressionData.ghastKills = count; yield true; }
            case "endermite" -> { progressionData.endermiteKills = count; yield true; }
            case "piglin" -> { progressionData.piglinKills = count; yield true; }

            default -> false;
        };
    }
}