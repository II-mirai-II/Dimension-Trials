package net.mirai.dimtr.command; // Pacote atualizado

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mirai.dimtr.DimTrMod; // Classe principal e pacote atualizados
import net.mirai.dimtr.data.ProgressionData; // Pacote atualizado
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class DimTrCommands { // Nome da classe atualizado

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Comando principal agora usa o novo MODID ("dimtr")
        LiteralArgumentBuilder<CommandSourceStack> dimTrCommand = Commands.literal(DimTrMod.MODID)
                .requires(source -> source.hasPermission(2));

        dimTrCommand.then(Commands.literal("completephase1")
                .executes(DimTrCommands::executeCompletePhase1) // Referência de método atualizada
        );

        dimTrCommand.then(Commands.literal("completephase2")
                .executes(DimTrCommands::executeCompletePhase2) // Referência de método atualizada
        );

        dimTrCommand.then(Commands.literal("resetprogress")
                .executes(DimTrCommands::executeResetProgress) // Referência de método atualizada
        );

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
                                .executes(DimTrCommands::executeSetGoal) // Referência de método atualizada
                        )
                )
        );

        dispatcher.register(dimTrCommand);
    }

    private static int executeCompletePhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        // ProgressionData será da classe no novo pacote net.mirai.dimtr.data
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        progressionData.elderGuardianKilled = true;
        progressionData.raidWon = true;
        progressionData.ravagerKilled = true;
        progressionData.evokerKilled = true;
        progressionData.trialVaultAdvancementEarned = true;
        progressionData.checkAndCompletePhase1Internal();
        progressionData.markDirtyAndSendUpdates();

        context.getSource().sendSuccess(() -> Component.literal("All Phase 1 requirements completed via command."), true);
        DimTrMod.LOGGER.info("Phase 1 completed via command by {}", context.getSource().getTextName()); // Logger atualizado
        return 1;
    }

    private static int executeCompletePhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        if (!progressionData.phase1Completed) {
            progressionData.elderGuardianKilled = true;
            progressionData.raidWon = true;
            progressionData.ravagerKilled = true;
            progressionData.evokerKilled = true;
            progressionData.trialVaultAdvancementEarned = true;
            progressionData.checkAndCompletePhase1Internal();
        }

        progressionData.witherKilled = true;
        progressionData.wardenKilled = true;
        progressionData.checkAndCompletePhase2Internal();
        progressionData.markDirtyAndSendUpdates();

        context.getSource().sendSuccess(() -> Component.literal("All Phase 2 requirements completed via command."), true);
        DimTrMod.LOGGER.info("Phase 2 completed via command by {}", context.getSource().getTextName()); // Logger atualizado
        return 1;
    }

    private static int executeResetProgress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        progressionData.elderGuardianKilled = false;
        progressionData.raidWon = false;
        progressionData.ravagerKilled = false;
        progressionData.evokerKilled = false;
        progressionData.trialVaultAdvancementEarned = false;
        progressionData.phase1Completed = false;
        progressionData.witherKilled = false;
        progressionData.wardenKilled = false;
        progressionData.phase2Completed = false;
        progressionData.markDirtyAndSendUpdates();

        context.getSource().sendSuccess(() -> Component.literal("All progression reset via command."), true);
        DimTrMod.LOGGER.info("All progression reset via command by {}", context.getSource().getTextName()); // Logger atualizado
        return 1;
    }

    private static int executeSetGoal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);
        String goalName = StringArgumentType.getString(context, "goal_name");
        boolean value = BoolArgumentType.getBool(context, "value");
        boolean updated = false;

        switch (goalName.toLowerCase()) {
            case "elder_guardian":
                progressionData.elderGuardianKilled = value; updated = true; break;
            case "raid_won":
                progressionData.raidWon = value; updated = true; break;
            case "ravager_killed":
                progressionData.ravagerKilled = value; updated = true; break;
            case "evoker_killed":
                progressionData.evokerKilled = value; updated = true; break;
            case "trial_vault_adv":
                progressionData.trialVaultAdvancementEarned = value; updated = true; break;
            case "wither_killed":
                progressionData.witherKilled = value; updated = true; break;
            case "warden_killed":
                progressionData.wardenKilled = value; updated = true; break;
            default:
                context.getSource().sendFailure(Component.literal("Unknown goal name: " + goalName));
                return 0;
        }

        if (updated) {
            progressionData.checkAndCompletePhase1Internal();
            progressionData.checkAndCompletePhase2Internal();
            progressionData.markDirtyAndSendUpdates();
            context.getSource().sendSuccess(() -> Component.literal("Goal '" + goalName + "' set to " + value + "."), true);
            DimTrMod.LOGGER.info("Goal '{}' set to {} via command by {}", goalName, value, context.getSource().getTextName()); // Logger atualizado
        }
        return 1;
    }
}