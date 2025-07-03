package net.mirai.dimtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.data.PlayerProgressionData;
import net.mirai.dimtr.system.CustomPhaseSystem;
import net.mirai.dimtr.system.BossKillValidator;
import net.mirai.dimtr.system.ProgressTransferService;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.mirai.dimtr.util.Constants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;

import java.util.UUID;

/**
 * Sistema de comandos para Dimension Trials - VERSÃO INDIVIDUAL COMPLETA
 *
 * ✅ Sistema individual de progressão por jogador
 * ✅ Comandos administrativos avançados
 * ✅ Debug e sincronização melhorados
 * ✅ Suporte para múltiplos jogadores
 * ✅ Comandos de reset detalhados
 * ✅ Integração com sistema de parties
 */
public class DimTrCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimtr")
                .requires(source -> source.hasPermission(2)) // OP level 2

                // ============================================================================
                // 🎯 COMANDOS INDIVIDUAIS (NOVO SISTEMA)
                // ============================================================================
                .then(Commands.literal("player")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.literal("complete")
                                        .then(Commands.literal("phase1")
                                                .executes(DimTrCommands::executeCompletePlayerPhase1))
                                        .then(Commands.literal("phase2")
                                                .executes(DimTrCommands::executeCompletePlayerPhase2)))
                                .then(Commands.literal("reset")
                                        .then(Commands.literal("all")
                                                .executes(DimTrCommands::executeResetPlayerAll))
                                        .then(Commands.literal("phase1")
                                                .executes(DimTrCommands::executeResetPlayerPhase1))
                                        .then(Commands.literal("phase2")
                                                .executes(DimTrCommands::executeResetPlayerPhase2))
                                        .then(Commands.literal("mob_kills")
                                                .executes(DimTrCommands::executeResetPlayerMobKills)))
                                .then(Commands.literal("set")
                                        .then(Commands.literal("goal")
                                                .then(Commands.argument("goal_name", StringArgumentType.string())
                                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                                .executes(DimTrCommands::executeSetPlayerGoal))))
                                        .then(Commands.literal("mob_kill")
                                                .then(Commands.argument("mob_type", StringArgumentType.string())
                                                        .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                                                .executes(DimTrCommands::executeSetPlayerMobKill)))))
                                .then(Commands.literal("status")
                                        .executes(DimTrCommands::executePlayerStatus))
                                .then(Commands.literal("sync")
                                        .executes(DimTrCommands::executePlayerSync))))

                // ============================================================================
                // 🎯 COMANDOS GLOBAIS (COMPATIBILIDADE + SELF-TARGET)
                // ============================================================================
                .then(Commands.literal("complete")
                        .then(Commands.literal("phase1")
                                .executes(DimTrCommands::executeCompleteSelfPhase1))
                        .then(Commands.literal("phase2")
                                .executes(DimTrCommands::executeCompleteSelfPhase2)))
                .then(Commands.literal("reset")
                        .then(Commands.literal("all")
                                .executes(DimTrCommands::executeResetSelfAll))
                        .then(Commands.literal("phase1")
                                .executes(DimTrCommands::executeResetSelfPhase1))
                        .then(Commands.literal("phase2")
                                .executes(DimTrCommands::executeResetSelfPhase2))
                        .then(Commands.literal("mob_kills")
                                .executes(DimTrCommands::executeResetSelfMobKills)))
                .then(Commands.literal("set")
                        .then(Commands.literal("goal")
                                .then(Commands.argument("goal_name", StringArgumentType.string())
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(DimTrCommands::executeSetSelfGoal))))
                        .then(Commands.literal("mob_kill")
                                .then(Commands.argument("mob_type", StringArgumentType.string())
                                        .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                                .executes(DimTrCommands::executeSetSelfMobKill)))))
                .then(Commands.literal("status")
                        .executes(DimTrCommands::executeSelfStatus))
                .then(Commands.literal("sync")
                        .executes(DimTrCommands::executeSelfSync))

                // ============================================================================
                // 🎯 COMANDOS DE DEBUG AVANÇADOS
                // ============================================================================
                .then(Commands.literal("debug")
                        .then(Commands.literal("payload")
                                .executes(DimTrCommands::executeDebugSelfPayload)
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(DimTrCommands::executeDebugPlayerPayload)))
                        .then(Commands.literal("global_status")
                                .executes(DimTrCommands::executeDebugGlobalStatus))
                        .then(Commands.literal("list_players")
                                .executes(DimTrCommands::executeDebugListPlayers))
                        .then(Commands.literal("multipliers")
                                .executes(DimTrCommands::executeDebugMultipliers)))

                // ============================================================================
                // 🆕 COMANDOS DOS NOVOS SISTEMAS FUNCIONAIS
                // ============================================================================
                .then(Commands.literal("systems")
                        .then(Commands.literal("transfer")
                                .then(Commands.literal("to_party")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(DimTrCommands::executeTransferToParty)))
                                .then(Commands.literal("to_individual")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(DimTrCommands::executeTransferToIndividual))))
                        .then(Commands.literal("custom_phase")
                                .then(Commands.literal("reload")
                                        .executes(DimTrCommands::executeReloadCustomPhases))
                                .then(Commands.literal("status")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(DimTrCommands::executeCustomPhaseStatus))))
                        .then(Commands.literal("boss_validation")
                                .then(Commands.literal("reload")
                                        .executes(DimTrCommands::executeReloadBossValidation))
                                .then(Commands.literal("reputation")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(DimTrCommands::executeBossValidationReputation))))));
    }

    // ============================================================================
    // 🎯 COMANDOS INDIVIDUAIS PARA JOGADORES ESPECÍFICOS
    // ============================================================================

    private static int executeCompletePlayerPhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return completePhase1ForPlayer(context, targetPlayer.getUUID(), targetPlayer.getName().getString());
    }

    private static int executeCompletePlayerPhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return completePhase2ForPlayer(context, targetPlayer.getUUID(), targetPlayer.getName().getString());
    }

    private static int executeResetPlayerAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return resetPlayerProgress(context, targetPlayer.getUUID(), targetPlayer.getName().getString(), "all");
    }

    private static int executeResetPlayerPhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return resetPlayerProgress(context, targetPlayer.getUUID(), targetPlayer.getName().getString(), "phase1");
    }

    private static int executeResetPlayerPhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return resetPlayerProgress(context, targetPlayer.getUUID(), targetPlayer.getName().getString(), "phase2");
    }

    private static int executeResetPlayerMobKills(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return resetPlayerProgress(context, targetPlayer.getUUID(), targetPlayer.getName().getString(), "mob_kills");
    }

    private static int executeSetPlayerGoal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        String goalName = StringArgumentType.getString(context, "goal_name");
        boolean value = BoolArgumentType.getBool(context, "value");
        return setPlayerGoal(context, targetPlayer.getUUID(), targetPlayer.getName().getString(), goalName, value);
    }

    private static int executeSetPlayerMobKill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        String mobType = StringArgumentType.getString(context, "mob_type");
        int count = IntegerArgumentType.getInteger(context, "count");
        return setPlayerMobKill(context, targetPlayer.getUUID(), targetPlayer.getName().getString(), mobType, count);
    }

    private static int executePlayerStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return showPlayerStatus(context, targetPlayer.getUUID(), targetPlayer.getName().getString());
    }

    private static int executePlayerSync(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return syncPlayer(context, targetPlayer);
    }

    // ============================================================================
    // 🎯 COMANDOS SELF-TARGET (PARA O PRÓPRIO EXECUTANTE)
    // ============================================================================

    private static int executeCompleteSelfPhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return completePhase1ForPlayer(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOU).getString());
    }

    private static int executeCompleteSelfPhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return completePhase2ForPlayer(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOU).getString());
    }

    private static int executeResetSelfAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return resetPlayerProgress(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_FEM).getString(), "all");
    }

    private static int executeResetSelfPhase1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return resetPlayerProgress(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_FEM).getString(), "phase1");
    }

    private static int executeResetSelfPhase2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return resetPlayerProgress(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_FEM).getString(), "phase2");
    }

    private static int executeResetSelfMobKills(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return resetPlayerProgress(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_PLURAL).getString(), "mob_kills");
    }

    private static int executeSetSelfGoal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        String goalName = StringArgumentType.getString(context, "goal_name");
        boolean value = BoolArgumentType.getBool(context, "value");
        return setPlayerGoal(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_MASC).getString(), goalName, value);
    }

    private static int executeSetSelfMobKill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        String mobType = StringArgumentType.getString(context, "mob_type");
        int count = IntegerArgumentType.getInteger(context, "count");
        return setPlayerMobKill(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_PLURAL).getString(), mobType, count);
    }

    private static int executeSelfStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return showPlayerStatus(context, player.getUUID(), Component.translatable(Constants.PRONOUN_YOUR_FEM).getString());
    }

    private static int executeSelfSync(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return syncPlayer(context, player);
    }

    // ============================================================================
    // 🎯 MÉTODOS CORE DE IMPLEMENTAÇÃO
    // ============================================================================

    private static int completePhase1ForPlayer(CommandContext<CommandSourceStack> context, UUID playerId, String playerName) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);

        // Completar objetivos especiais da Fase 1
        playerData.elderGuardianKilled = true;
        playerData.raidWon = true;
        playerData.trialVaultAdvancementEarned = true;
        playerData.voluntaireExileAdvancementEarned = true;

        // Completar mobs da Fase 1
        playerData.zombieKills = DimTrConfig.SERVER.reqZombieKills.get();
        playerData.skeletonKills = DimTrConfig.SERVER.reqSkeletonKills.get();
        playerData.strayKills = DimTrConfig.SERVER.reqStrayKills.get();
        playerData.huskKills = DimTrConfig.SERVER.reqHuskKills.get();
        playerData.spiderKills = DimTrConfig.SERVER.reqSpiderKills.get();
        playerData.creeperKills = DimTrConfig.SERVER.reqCreeperKills.get();
        playerData.drownedKills = DimTrConfig.SERVER.reqDrownedKills.get();
        playerData.endermanKills = DimTrConfig.SERVER.reqEndermanKills.get();
        playerData.witchKills = DimTrConfig.SERVER.reqWitchKills.get();
        playerData.pillagerKills = DimTrConfig.SERVER.reqPillagerKills.get();
        playerData.captainKills = DimTrConfig.SERVER.reqCaptainKills.get();
        playerData.vindicatorKills = DimTrConfig.SERVER.reqVindicatorKills.get();
        playerData.boggedKills = DimTrConfig.SERVER.reqBoggedKills.get();
        playerData.breezeKills = DimTrConfig.SERVER.reqBreezeKills.get();
        playerData.ravagerKills = DimTrConfig.SERVER.reqRavagerKills.get();
        playerData.evokerKills = DimTrConfig.SERVER.reqEvokerKills.get();
        
        // NOVO: Completar objetivos de mods externos da Fase 1
        if (DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            // Verificar se há bosses externos para completar
            java.util.List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> phase1Bosses = 
                net.mirai.dimtr.integration.ExternalModIntegration.getBossesForPhase(1);
            
            // Marcar todos os bosses externos da fase 1 como derrotados
            for (net.mirai.dimtr.integration.ExternalModIntegration.BossInfo boss : phase1Bosses) {
                String bossKey = boss.entityId.replace(":", "_");
                playerData.setCustomObjectiveComplete("external_bosses", bossKey, true);
                DimTrMod.LOGGER.info("Admin command: Marking external boss {} as defeated for player {}", 
                    boss.displayName, playerName);
            }
        }

        // Forçar completude da Fase 1
        playerData.phase1Completed = true;

        // Salvar e sincronizar
        progressionManager.setDirty();
        if (context.getSource().getLevel().getServer() != null) {
            ServerPlayer player = context.getSource().getLevel().getServer().getPlayerList().getPlayer(playerId);
            if (player != null) {
                progressionManager.sendToClient(player);
                
                // 🎆 NOVO: Lançar fogos de artifício ao completar fase via comando admin
                net.mirai.dimtr.util.NotificationHelper.launchCelebrationFireworks(player, 1);
            }
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.DIMTR_PHASE1_COMPLETE_SUCCESS, playerName)
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int completePhase2ForPlayer(CommandContext<CommandSourceStack> context, UUID playerId, String playerName) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);

        // Completar Fase 1 primeiro se necessário
        if (!playerData.phase1Completed) {
            completePhase1ForPlayer(context, playerId, playerName);
        }

        // Completar objetivos especiais da Fase 2
        playerData.witherKilled = true;
        playerData.wardenKilled = true;

        // Completar mobs do Nether
        playerData.blazeKills = DimTrConfig.SERVER.reqBlazeKills.get();
        playerData.witherSkeletonKills = DimTrConfig.SERVER.reqWitherSkeletonKills.get();
        playerData.piglinBruteKills = DimTrConfig.SERVER.reqPiglinBruteKills.get();
        playerData.hoglinKills = DimTrConfig.SERVER.reqHoglinKills.get();
        playerData.zoglinKills = DimTrConfig.SERVER.reqZoglinKills.get();
        playerData.ghastKills = DimTrConfig.SERVER.reqGhastKills.get();
        playerData.piglinKills = DimTrConfig.SERVER.reqPiglinKills.get();

        // Completar requisitos aumentados do Overworld (125%)
        playerData.zombieKills = Math.max(playerData.zombieKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqZombieKills.get()));
        playerData.skeletonKills = Math.max(playerData.skeletonKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqSkeletonKills.get()));
        playerData.strayKills = Math.max(playerData.strayKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqStrayKills.get()));
        playerData.huskKills = Math.max(playerData.huskKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqHuskKills.get()));
        playerData.spiderKills = Math.max(playerData.spiderKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqSpiderKills.get()));
        playerData.creeperKills = Math.max(playerData.creeperKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqCreeperKills.get()));
        playerData.drownedKills = Math.max(playerData.drownedKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqDrownedKills.get()));
        playerData.endermanKills = Math.max(playerData.endermanKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqEndermanKills.get()));
        playerData.witchKills = Math.max(playerData.witchKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqWitchKills.get()));
        playerData.pillagerKills = Math.max(playerData.pillagerKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqPillagerKills.get()));
        playerData.captainKills = Math.max(playerData.captainKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqCaptainKills.get()));
        playerData.vindicatorKills = Math.max(playerData.vindicatorKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqVindicatorKills.get()));
        playerData.boggedKills = Math.max(playerData.boggedKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqBoggedKills.get()));
        playerData.breezeKills = Math.max(playerData.breezeKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqBreezeKills.get()));
        playerData.ravagerKills = Math.max(playerData.ravagerKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqRavagerKills.get()));
        playerData.evokerKills = Math.max(playerData.evokerKills, getPhase2OverworldRequirement(DimTrConfig.SERVER.reqEvokerKills.get()));

        // NOVO: Completar objetivos de mods externos da Fase 2
        if (DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            // Verificar se há bosses externos para completar
            java.util.List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> phase2Bosses = 
                net.mirai.dimtr.integration.ExternalModIntegration.getBossesForPhase(2);
            
            // Marcar todos os bosses externos da fase 2 como derrotados
            for (net.mirai.dimtr.integration.ExternalModIntegration.BossInfo boss : phase2Bosses) {
                String bossKey = boss.entityId.replace(":", "_");
                playerData.setCustomObjectiveComplete("external_bosses", bossKey, true);
                DimTrMod.LOGGER.info("Admin command: Marking external boss {} as defeated for player {}", 
                    boss.displayName, playerName);
            }
        }

        // Forçar completude da Fase 2
        playerData.phase2Completed = true;

        // Salvar e sincronizar
        progressionManager.setDirty();
        if (context.getSource().getLevel().getServer() != null) {
            ServerPlayer player = context.getSource().getLevel().getServer().getPlayerList().getPlayer(playerId);
            if (player != null) {
                progressionManager.sendToClient(player);
                
                // 🎆 NOVO: Lançar fogos de artifício ao completar fase via comando admin
                net.mirai.dimtr.util.NotificationHelper.launchCelebrationFireworks(player, 2);
            }
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.DIMTR_PHASE2_COMPLETE_SUCCESS, playerName)
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int resetPlayerProgress(CommandContext<CommandSourceStack> context, UUID playerId, String playerName, String resetType) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);

        switch (resetType) {
            case "all" -> {
                // Reset completo
                playerData.elderGuardianKilled = false;
                playerData.raidWon = false;
                playerData.trialVaultAdvancementEarned = false;
                playerData.voluntaireExileAdvancementEarned = false;
                playerData.phase1Completed = false;
                playerData.witherKilled = false;
                playerData.wardenKilled = false;
                playerData.phase2Completed = false;
                resetAllMobKills(playerData);
            }
            case "phase1" -> {
                // Reset apenas Fase 1
                playerData.elderGuardianKilled = false;
                playerData.raidWon = false;
                playerData.trialVaultAdvancementEarned = false;
                playerData.voluntaireExileAdvancementEarned = false;
                playerData.phase1Completed = false;
                resetPhase1MobKills(playerData);
            }
            case "phase2" -> {
                // Reset apenas Fase 2
                playerData.witherKilled = false;
                playerData.wardenKilled = false;
                playerData.phase2Completed = false;
                resetPhase2MobKills(playerData);
            }
            case "mob_kills" -> {
                // Reset only mob counters
                resetAllMobKills(playerData);
            }
        }

        // Salvar e sincronizar
        progressionManager.setDirty();
        if (context.getSource().getLevel().getServer() != null) {
            ServerPlayer player = context.getSource().getLevel().getServer().getPlayerList().getPlayer(playerId);
            if (player != null) {
                progressionManager.sendToClient(player);
            }
        }

        String resetDescription = switch (resetType) {
            case "all" -> Component.translatable(Constants.RESET_TYPE_ALL_ENG).getString();
            case "phase1" -> Component.translatable(Constants.RESET_TYPE_PHASE1_ENG).getString();
            case "phase2" -> Component.translatable(Constants.RESET_TYPE_PHASE2_ENG).getString();
            case "mob_kills" -> Component.translatable(Constants.RESET_TYPE_MOB_KILLS_ENG).getString();
            default -> Component.translatable(Constants.RESET_TYPE_DEFAULT_ENG).getString();
        };

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.DIMTR_RESET_SUCCESS_MESSAGE, resetDescription, playerName)
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int setPlayerGoal(CommandContext<CommandSourceStack> context, UUID playerId, String playerName, String goalName, boolean value) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);

        boolean success = switch (goalName.toLowerCase()) {
            case "elder_guardian" -> { playerData.elderGuardianKilled = value; yield true; }
            case "raid" -> { playerData.raidWon = value; yield true; }
            case "trial_vault" -> { playerData.trialVaultAdvancementEarned = value; yield true; }
            case "voluntaire_exile" -> { playerData.voluntaireExileAdvancementEarned = value; yield true; }
            case "wither" -> { playerData.witherKilled = value; yield true; }
            case "warden" -> { playerData.wardenKilled = value; yield true; }
            default -> {
                context.getSource().sendFailure(Component.translatable(Constants.DIMTR_INVALID_GOAL_ERROR, goalName));
                yield false;
            }
        };

        if (success) {
            // Salvar e sincronizar
            progressionManager.setDirty();
            if (context.getSource().getLevel().getServer() != null) {
                ServerPlayer player = context.getSource().getLevel().getServer().getPlayerList().getPlayer(playerId);
                if (player != null) {
                    progressionManager.sendToClient(player);
                }
            }

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_GOAL_SET_SUCCESS, goalName, value, playerName)
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        }

        return 0;
    }

    private static int setPlayerMobKill(CommandContext<CommandSourceStack> context, UUID playerId, String playerName, String mobType, int count) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);

        boolean success = switch (mobType.toLowerCase()) {
            case "zombie" -> { playerData.zombieKills = count; yield true; }
            case "skeleton" -> { playerData.skeletonKills = count; yield true; }
            case "stray" -> { playerData.strayKills = count; yield true; }
            case "husk" -> { playerData.huskKills = count; yield true; }
            case "spider" -> { playerData.spiderKills = count; yield true; }
            case "creeper" -> { playerData.creeperKills = count; yield true; }
            case "drowned" -> { playerData.drownedKills = count; yield true; }
            case "enderman" -> { playerData.endermanKills = count; yield true; }
            case "witch" -> { playerData.witchKills = count; yield true; }
            case "pillager" -> { playerData.pillagerKills = count; yield true; }
            case "captain" -> { playerData.captainKills = count; yield true; }
            case "vindicator" -> { playerData.vindicatorKills = count; yield true; }
            case "bogged" -> { playerData.boggedKills = count; yield true; }
            case "breeze" -> { playerData.breezeKills = count; yield true; }
            case "ravager" -> { playerData.ravagerKills = count; yield true; }
            case "evoker" -> { playerData.evokerKills = count; yield true; }
            case "blaze" -> { playerData.blazeKills = count; yield true; }
            case "wither_skeleton" -> { playerData.witherSkeletonKills = count; yield true; }
            case "piglin_brute" -> { playerData.piglinBruteKills = count; yield true; }
            case "hoglin" -> { playerData.hoglinKills = count; yield true; }
            case "zoglin" -> { playerData.zoglinKills = count; yield true; }
            case "ghast" -> { playerData.ghastKills = count; yield true; }
            case "piglin" -> { playerData.piglinKills = count; yield true; }
            default -> {
                context.getSource().sendFailure(Component.translatable(Constants.CMD_INVALID_MOB_FORMAT, mobType));
                yield false;
            }
        };

        if (success) {
            // Salvar e sincronizar
            progressionManager.setDirty();
            if (context.getSource().getLevel().getServer() != null) {
                ServerPlayer player = context.getSource().getLevel().getServer().getPlayerList().getPlayer(playerId);
                if (player != null) {
                    progressionManager.sendToClient(player);
                }
            }

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_MOB_COUNT_SET_SUCCESS, mobType, count, playerName)
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        }

        return 0;
    }

    private static int showPlayerStatus(CommandContext<CommandSourceStack> context, UUID playerId, String playerName) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);

        // Cabeçalho
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_STATUS_PROGRESSION_HEADER, playerName.toUpperCase())
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        // Status das fases
        ChatFormatting phase1Color = playerData.phase1Completed ? ChatFormatting.GREEN : ChatFormatting.RED;
        ChatFormatting phase2Color = playerData.phase2Completed ? ChatFormatting.GREEN : ChatFormatting.RED;

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_PLAYER_PHASE1_STATUS, 
                        playerData.phase1Completed ? Constants.STATUS_COMPLETE : Constants.STATUS_INCOMPLETE)
                        .withStyle(phase1Color), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_PLAYER_PHASE2_STATUS, 
                        playerData.phase2Completed ? Constants.STATUS_COMPLETE : Constants.STATUS_INCOMPLETE)
                        .withStyle(phase2Color), false);

        // Current multiplier
        double multiplier = playerData.getProgressionMultiplier();
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_PLAYER_MULTIPLIER, String.format("%.1fx", multiplier))
                        .withStyle(multiplier > 1.0 ? ChatFormatting.GOLD : ChatFormatting.GRAY), false);

        // Special objectives
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_PLAYER_OBJECTIVES_HEADER)
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);

        showGoalStatus(context, Component.translatable(Constants.GOAL_STATUS_ELDER_GUARDIAN).getString(), playerData.elderGuardianKilled);
        showGoalStatus(context, Component.translatable(Constants.GOAL_STATUS_RAID_WON).getString(), playerData.raidWon);
        showGoalStatus(context, Component.translatable(Constants.GOAL_STATUS_TRIAL_VAULT).getString(), playerData.trialVaultAdvancementEarned);
        showGoalStatus(context, Component.translatable(Constants.GOAL_STATUS_VOLUNTARY_EXILE).getString(), playerData.voluntaireExileAdvancementEarned);
        showGoalStatus(context, Component.translatable(Constants.GOAL_STATUS_WITHER_KILLED).getString(), playerData.witherKilled);
        showGoalStatus(context, Component.translatable(Constants.GOAL_STATUS_WARDEN_KILLED).getString(), playerData.wardenKilled);

        // Important counters
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_PLAYER_COUNTERS_HEADER)
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), false);

        String[] importantMobs = {"zombie", "skeleton", "creeper", "spider", "ravager", "evoker", "blaze", "wither_skeleton", "hoglin", "zoglin"};
        for (String mobType : importantMobs) {
            int kills = playerData.getMobKillCount(mobType);
            if (kills > 0) {
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.CMD_STATUS_MOB_KILL_FORMAT, capitalizeFirst(mobType), kills)
                                .withStyle(ChatFormatting.GRAY), false);
            }
        }

        return 1;
    }

    private static int syncPlayer(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);

        try {
            progressionManager.sendToClient(player);
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_SYNC_SUCCESS, player.getName().getString())
                            .withStyle(ChatFormatting.GREEN), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_SYNC_INFO)
                            .withStyle(ChatFormatting.GRAY), false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.CMD_SYNC_FAILURE, e.getMessage()));
            DimTrMod.LOGGER.error("Failed to sync player data", e);
            return 0;
        }
    }

    // ============================================================================
    // 🎯 COMANDOS DE DEBUG AVANÇADOS
    // ============================================================================

    private static int executeDebugSelfPayload(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) return 0;
        return debugPlayerPayload(context, player);
    }

    private static int executeDebugPlayerPayload(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
        return debugPlayerPayload(context, targetPlayer);
    }

    private static int debugPlayerPayload(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PlayerProgressionData playerData = progressionManager.getPlayerData(player.getUUID());

        try {
            UpdateProgressionToClientPayload payload = UpdateProgressionToClientPayload.createFromPlayerData(playerData);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PAYLOAD_HEADER, player.getName().getString())
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

            // Debug critical values  
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_REQUIREMENTS)
                            .withStyle(ChatFormatting.AQUA), false);

            debugMobComparison(context, "Ravager", payload.reqRavagerKills(), payload.ravagerKills());
            debugMobComparison(context, "Evoker", payload.reqEvokerKills(), payload.evokerKills());
            debugMobComparison(context, "Hoglin", payload.reqHoglinKills(), payload.hoglinKills());
            debugMobComparison(context, "Zoglin", payload.reqZoglinKills(), payload.zoglinKills());

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PHASE_STATUS)
                            .withStyle(ChatFormatting.LIGHT_PURPLE), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PHASE_STATUS_FORMAT, "Phase 1", payload.phase1Completed())
                            .withStyle(payload.phase1Completed() ? ChatFormatting.GREEN : ChatFormatting.RED), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PHASE_STATUS_FORMAT, "Phase 2", payload.phase2Completed())
                            .withStyle(payload.phase2Completed() ? ChatFormatting.GREEN : ChatFormatting.RED), false);

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.CMD_DEBUG_ERROR_CREATING_PAYLOAD, e.getMessage()));
            return 0;
        }
    }

    private static int executeDebugGlobalStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_STATUS_GLOBAL_HEADER)
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_SYSTEM_TITLE)
                        .withStyle(ChatFormatting.GREEN), false);

        // Estatísticas de configuração
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_CONFIGURATIONS_ACTIVE)
                        .withStyle(ChatFormatting.AQUA), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_PHASE1_STATUS, 
                        DimTrConfig.SERVER.enablePhase1.get() ? Component.translatable(Constants.STATUS_ENABLED).getString() : Component.translatable(Constants.STATUS_DISABLED).getString())
                        .withStyle(DimTrConfig.SERVER.enablePhase1.get() ? ChatFormatting.GREEN : ChatFormatting.RED), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_PHASE2_STATUS,
                        DimTrConfig.SERVER.enablePhase2.get() ? Component.translatable(Constants.STATUS_ENABLED).getString() : Component.translatable(Constants.STATUS_DISABLED).getString())
                        .withStyle(DimTrConfig.SERVER.enablePhase2.get() ? ChatFormatting.GREEN : ChatFormatting.RED), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_VOLUNTARY_EXILE_STATUS,
                        DimTrConfig.SERVER.reqVoluntaryExile.get() ? Component.translatable(Constants.STATUS_REQUIRED).getString() : Component.translatable(Constants.STATUS_OPTIONAL).getString())
                        .withStyle(DimTrConfig.SERVER.reqVoluntaryExile.get() ? ChatFormatting.YELLOW : ChatFormatting.GRAY), false);

        return 1;
    }

    private static int executeDebugListPlayers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_ONLINE_PLAYERS_HEADER)
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        var playerList = serverLevel.getServer().getPlayerList().getPlayers();
        if (playerList.isEmpty()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_NO_ONLINE_PLAYERS)
                            .withStyle(ChatFormatting.RED), false);
            return 1;
        }

        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);

        for (ServerPlayer player : playerList) {
            PlayerProgressionData playerData = progressionManager.getPlayerData(player.getUUID());
            String phase1Status = playerData.phase1Completed ? "✅" : "❌";
            String phase2Status = playerData.phase2Completed ? "✅" : "❌";
            double multiplier = playerData.getProgressionMultiplier();

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PLAYER_FORMAT, 
                            player.getName().getString(), phase1Status, phase2Status, String.format("%.1fx", multiplier))
                            .withStyle(ChatFormatting.WHITE), false);
        }

        return 1;
    }

    private static int executeDebugMultipliers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.CMD_DEBUG_MULTIPLIERS_HEADER)
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        var playerList = serverLevel.getServer().getPlayerList().getPlayers();
        if (playerList.isEmpty()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_NO_ONLINE_PLAYERS)
                            .withStyle(ChatFormatting.RED), false);
            return 1;
        }

        for (ServerPlayer player : playerList) {
            PlayerProgressionData playerData = progressionManager.getPlayerData(player.getUUID());
            double personalMultiplier = playerData.getProgressionMultiplier();

            // Calcular multiplicador médio próximo
            double averageMultiplier = progressionManager.calculateAverageMultiplierNearPosition(
                    player.getX(), player.getY(), player.getZ(), serverLevel);

            ChatFormatting color = personalMultiplier > 1.0 ? ChatFormatting.GOLD : ChatFormatting.GRAY;

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PLAYER_MULTIPLIER_HEADER, player.getName().getString())
                            .withStyle(ChatFormatting.WHITE), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PLAYER_MULTIPLIER_INDIVIDUAL, String.format("%.1fx", personalMultiplier))
                            .withStyle(color), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.CMD_DEBUG_PLAYER_MULTIPLIER_NEARBY, String.format("%.1fx", averageMultiplier))
                            .withStyle(ChatFormatting.GRAY), false);
        }

        return 1;
    }

    // ============================================================================
    // 🎯 MÉTODOS AUXILIARES
    // ============================================================================

    private static ServerPlayer getPlayerFromContext(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            return player;
        }
        context.getSource().sendFailure(Component.translatable(Constants.CMD_COMMAND_PLAYER_ONLY));
        return null;
    }

    private static void resetAllMobKills(PlayerProgressionData playerData) {
        resetPhase1MobKills(playerData);
        resetPhase2MobKills(playerData);
    }

    private static void resetPhase1MobKills(PlayerProgressionData playerData) {
        playerData.zombieKills = 0;
        playerData.skeletonKills = 0;
        playerData.strayKills = 0;
        playerData.huskKills = 0;
        playerData.spiderKills = 0;
        playerData.creeperKills = 0;
        playerData.drownedKills = 0;
        playerData.endermanKills = 0;
        playerData.witchKills = 0;
        playerData.pillagerKills = 0;
        playerData.captainKills = 0;
        playerData.vindicatorKills = 0;
        playerData.boggedKills = 0;
        playerData.breezeKills = 0;
        playerData.ravagerKills = 0;
        playerData.evokerKills = 0;
    }

    private static void resetPhase2MobKills(PlayerProgressionData playerData) {
        playerData.blazeKills = 0;
        playerData.witherSkeletonKills = 0;
        playerData.piglinBruteKills = 0;
        playerData.hoglinKills = 0;
        playerData.zoglinKills = 0;
        playerData.ghastKills = 0;
        playerData.piglinKills = 0;
    }

    private static int getPhase2OverworldRequirement(int originalRequirement) {
        return (int) Math.ceil(originalRequirement * 1.25);
    }

    private static void showGoalStatus(CommandContext<CommandSourceStack> context, String goalName, boolean completed) {
        ChatFormatting color = completed ? ChatFormatting.GREEN : ChatFormatting.RED;
        String status = completed ? "✅" : "❌";

        context.getSource().sendSuccess(() ->
                Component.literal(status + " " + goalName)
                        .withStyle(color), false);
    }

    private static void debugMobComparison(CommandContext<CommandSourceStack> context, String mobName, int required, int current) {
        boolean isComplete = current >= required;
        ChatFormatting color = isComplete ? ChatFormatting.GREEN : ChatFormatting.RED;
        String status = isComplete ? "✅" : "❌";

        context.getSource().sendSuccess(() ->
                Component.literal("  " + status + " " + mobName + ": " + current + "/" + required)
                        .withStyle(color), false);
    }

    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase().replace("_", " ");
    }

    // ============================================================================
    // 🆕 MÉTODOS DOS NOVOS SISTEMAS FUNCIONAIS
    // ============================================================================
    
    /**
     * 🔄 Transferir progresso de individual para party
     */
    private static int executeTransferToParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        UUID playerId = target.getUUID();
        
        try {
            ProgressTransferService.transferFromIndividualToParty(playerId);
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Progresso transferido para party: " + target.getName().getString())
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao transferir progresso: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    /**
     * 🔄 Transferir progresso de party para individual
     */
    private static int executeTransferToIndividual(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        UUID playerId = target.getUUID();
        
        try {
            ProgressTransferService.transferFromPartyToIndividual(playerId);
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Progresso transferido para individual: " + target.getName().getString())
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao transferir progresso: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    /**
     * 🔧 Recarregar configurações de custom phases
     */
    private static int executeReloadCustomPhases(CommandContext<CommandSourceStack> context) {
        try {
            CustomPhaseSystem.loadPhaseDefinitions();
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Configurações de custom phases recarregadas!")
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao recarregar custom phases: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    /**
     * 📊 Status de custom phases do jogador
     */
    private static int executeCustomPhaseStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        UUID playerId = target.getUUID();
        
        try {
            var phaseProgress = CustomPhaseSystem.getPlayerPhaseProgress(playerId);
            context.getSource().sendSuccess(() ->
                    Component.literal("📊 Custom Phases - " + target.getName().getString())
                            .withStyle(ChatFormatting.AQUA), false);
            
            if (phaseProgress.isEmpty()) {
                context.getSource().sendSuccess(() ->
                        Component.literal("  Nenhuma custom phase ativa")
                                .withStyle(ChatFormatting.GRAY), false);
            } else {
                for (var entry : phaseProgress.entrySet()) {
                    String phaseId = entry.getKey();
                    var progress = entry.getValue();
                    boolean isComplete = progress.completed;
                    ChatFormatting color = isComplete ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
                    String status = isComplete ? "✅" : "🔄";
                    
                    int completedObjs = (int) progress.currentObjectives.values().stream().mapToInt(b -> b ? 1 : 0).sum();
                    int totalObjs = progress.currentObjectives.size();
                    
                    context.getSource().sendSuccess(() ->
                            Component.literal("  " + status + " " + phaseId + ": " + 
                                    completedObjs + "/" + totalObjs + " (" + String.format("%.1f%%", progress.completionPercentage) + ")")
                                    .withStyle(color), false);
                }
            }
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao obter status: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    /**
     * 🔧 Recarregar configurações de boss validation
     */
    private static int executeReloadBossValidation(CommandContext<CommandSourceStack> context) {
        try {
            // Recarregar através do initialize que chama loadBossConfigurations
            BossKillValidator.initialize();
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Configurações de boss validation recarregadas!")
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao recarregar boss validation: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    /**
     * 📊 Reputação de boss validation do jogador
     */
    private static int executeBossValidationReputation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        UUID playerId = target.getUUID();
        
        try {
            var reputation = BossKillValidator.getPlayerReputation(playerId);
            context.getSource().sendSuccess(() ->
                    Component.literal("📊 Boss Validation - " + target.getName().getString())
                            .withStyle(ChatFormatting.AQUA), false);
            
            if (reputation == null) {
                context.getSource().sendSuccess(() ->
                        Component.literal("  Sem histórico de boss kills")
                                .withStyle(ChatFormatting.GRAY), false);
            } else {
                context.getSource().sendSuccess(() ->
                        Component.literal("  🎯 Reputação: " + String.format("%.1f/100", reputation.reputationScore))
                                .withStyle(ChatFormatting.YELLOW), false);
                context.getSource().sendSuccess(() ->
                        Component.literal("  ✅ Kills legítimos: " + reputation.legitimateKills)
                                .withStyle(ChatFormatting.GREEN), false);
                context.getSource().sendSuccess(() ->
                        Component.literal("  ⚠️ Kills suspeitos: " + reputation.suspiciousKills)
                                .withStyle(ChatFormatting.YELLOW), false);
                context.getSource().sendSuccess(() ->
                        Component.literal("  ❌ Kills inválidos: " + reputation.invalidKills)
                                .withStyle(ChatFormatting.RED), false);
                
                if (reputation.isBlacklisted) {
                    context.getSource().sendSuccess(() ->
                            Component.literal("  🚫 Status: BLACKLISTED")
                                    .withStyle(ChatFormatting.DARK_RED), false);
                }
            }
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao obter reputação: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
}