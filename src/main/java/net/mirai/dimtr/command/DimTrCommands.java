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
        var dimTrCommand = Commands.literal("dimtr")
                .requires(source -> source.hasPermission(2));

        // Comando para completar Fase 1
        dimTrCommand.then(Commands.literal("completephase1")
                .executes(DimTrCommands::executeCompletePhase1));

        // Comando para completar Fase 2
        dimTrCommand.then(Commands.literal("completephase2")
                .executes(DimTrCommands::executeCompletePhase2));

        // Comando para resetar progresso
        dimTrCommand.then(Commands.literal("resetprogress")
                .executes(DimTrCommands::executeResetProgress));

        // Comando para resetar apenas contadores de mobs
        dimTrCommand.then(Commands.literal("resetmobkills")
                .executes(DimTrCommands::executeResetMobKills));

        // Comando para definir objetivos específicos
        dimTrCommand.then(Commands.literal("setgoal")
                .then(Commands.argument("goal_name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("elder_guardian");
                            builder.suggest("raid_won");
                            builder.suggest("ravager_killed");
                            builder.suggest("evoker_killed");
                            builder.suggest("trial_vault_adv");
                            builder.suggest("wither_killed");
                            builder.suggest("warden_killed");
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(DimTrCommands::executeSetGoal)
                        )
                )
        );

        // Comando para definir contadores de mobs
        dimTrCommand.then(Commands.literal("setmobkills")
                .then(Commands.argument("mob_type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            // Fase 1 mobs
                            builder.suggest("zombie");
                            builder.suggest("zombie_villager");
                            builder.suggest("skeleton");
                            builder.suggest("stray");
                            builder.suggest("husk");
                            builder.suggest("spider");
                            builder.suggest("creeper");
                            builder.suggest("drowned");
                            builder.suggest("enderman");
                            builder.suggest("witch");
                            builder.suggest("pillager");
                            builder.suggest("captain");
                            builder.suggest("vindicator");
                            builder.suggest("bogged");
                            builder.suggest("breeze");

                            // NOVO: Ravager e Evoker
                            builder.suggest("ravager");
                            builder.suggest("evoker");

                            // Fase 2 mobs
                            builder.suggest("blaze");
                            builder.suggest("wither_skeleton");
                            builder.suggest("piglin_brute");
                            builder.suggest("hoglin");
                            builder.suggest("zoglin");
                            builder.suggest("ghast");
                            builder.suggest("endermite");
                            builder.suggest("piglin");
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                .executes(DimTrCommands::executeSetMobKills)
                        )
                )
        );

        dispatcher.register(dimTrCommand);
        DimTrMod.LOGGER.info("DimTr commands registered successfully!");
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

        // NOVO: Completar Ravager e Evoker como Goal Kills
        progressionData.ravagerKills = 3; // Valor balanceado
        progressionData.evokerKills = 2; // Valor balanceado

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
        progressionData.hoglinKills = 10;
        progressionData.zoglinKills = 5;
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

        // NOVO: Completar Ravager e Evoker com 125% para Fase 2
        progressionData.ravagerKills = Math.max(progressionData.ravagerKills, 4); // 3 * 1.25 = 3.75 -> 4
        progressionData.evokerKills = Math.max(progressionData.evokerKills, 3); // 2 * 1.25 = 2.5 -> 3

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
        DimTrMod.LOGGER.info("Phase 2 completed via command by {}", context.getSource().getTextName());
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

        context.getSource().sendSuccess(() -> Component.literal("Goal '" + goalName + "' set to " + value), true);
        DimTrMod.LOGGER.info("Goal '{}' set to {} via command by {}", goalName, value, context.getSource().getTextName());
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
        } else {
            context.getSource().sendFailure(Component.literal("Invalid mob type: " + mobType));
        }

        return 1;
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

    private static boolean setMobKillCount(ProgressionData data, String mobType, int count) {
        return switch (mobType.toLowerCase()) {
            case "zombie" -> { data.zombieKills = count; yield true; }
            case "zombie_villager" -> { data.zombieVillagerKills = count; yield true; }
            case "skeleton" -> { data.skeletonKills = count; yield true; }
            case "stray" -> { data.strayKills = count; yield true; }
            case "husk" -> { data.huskKills = count; yield true; }
            case "spider" -> { data.spiderKills = count; yield true; }
            case "creeper" -> { data.creeperKills = count; yield true; }
            case "drowned" -> { data.drownedKills = count; yield true; }
            case "enderman" -> { data.endermanKills = count; yield true; }
            case "witch" -> { data.witchKills = count; yield true; }
            case "pillager" -> { data.pillagerKills = count; yield true; }
            case "captain" -> { data.captainKills = count; yield true; }
            case "vindicator" -> { data.vindicatorKills = count; yield true; }
            case "bogged" -> { data.boggedKills = count; yield true; }
            case "breeze" -> { data.breezeKills = count; yield true; }

            // NOVO: Ravager e Evoker
            case "ravager" -> { data.ravagerKills = count; yield true; }
            case "evoker" -> { data.evokerKills = count; yield true; }

            case "blaze" -> { data.blazeKills = count; yield true; }
            case "wither_skeleton" -> { data.witherSkeletonKills = count; yield true; }
            case "piglin_brute" -> { data.piglinBruteKills = count; yield true; }
            case "hoglin" -> { data.hoglinKills = count; yield true; }
            case "zoglin" -> { data.zoglinKills = count; yield true; }
            case "ghast" -> { data.ghastKills = count; yield true; }
            case "endermite" -> { data.endermiteKills = count; yield true; }
            case "piglin" -> { data.piglinKills = count; yield true; }
            default -> false;
        };
    }

    private static void resetAllMobKills(ProgressionData data) {
        // Fase 1 mobs
        data.zombieKills = 0;
        data.zombieVillagerKills = 0;
        data.skeletonKills = 0;
        data.strayKills = 0;
        data.huskKills = 0;
        data.spiderKills = 0;
        data.creeperKills = 0;
        data.drownedKills = 0;
        data.endermanKills = 0;
        data.witchKills = 0;
        data.pillagerKills = 0;
        data.captainKills = 0;
        data.vindicatorKills = 0;
        data.boggedKills = 0;
        data.breezeKills = 0;

        // NOVO: Ravager e Evoker
        data.ravagerKills = 0;
        data.evokerKills = 0;

        // Fase 2 mobs
        data.blazeKills = 0;
        data.witherSkeletonKills = 0;
        data.piglinBruteKills = 0;
        data.hoglinKills = 0;
        data.zoglinKills = 0;
        data.ghastKills = 0;
        data.endermiteKills = 0;
        data.piglinKills = 0;
    }
}