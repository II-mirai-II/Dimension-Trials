package net.mirai.dimtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.PartyManager;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Comandos para gerenciamento de parties - VERSÃO COMPLETA E CORRIGIDA
 *
 * ✅ Sistema completo de parties
 * ✅ Criação, entrada, saída de parties
 * ✅ Gerenciamento de liderança
 * ✅ Listagem e informações
 * ✅ Integração com progressão
 * ✅ Comandos administrativos
 */
public class PartyCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // ✅ CORREÇÃO: Registrar comandos de party separadamente sem necessidade de OP
        dispatcher.register(
                Commands.literal("party")
                        .requires(source -> source.getEntity() instanceof ServerPlayer) // Apenas jogadores, sem OP
                        
                        // /party create <nome> [senha]
                        .then(Commands.literal("create")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(PartyCommands::executeCreatePublicParty)
                                        .then(Commands.argument("password", StringArgumentType.string())
                                                .executes(PartyCommands::executeCreatePrivateParty))))

                        // /party join <nome> [senha]
                        .then(Commands.literal("join")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(PartyCommands::executeJoinParty)
                                        .then(Commands.argument("password", StringArgumentType.string())
                                                .executes(PartyCommands::executeJoinPartyWithPassword))))

                        // /party leave
                        .then(Commands.literal("leave")
                                .executes(PartyCommands::executeLeaveParty))

                        // /party list
                        .then(Commands.literal("list")
                                .executes(PartyCommands::executeListParties))

                        // /party info
                        .then(Commands.literal("info")
                                .executes(PartyCommands::executePartyInfo))

                        // /party disband (apenas líder)
                        .then(Commands.literal("disband")
                                .executes(PartyCommands::executeDisbandParty))

                        // /party kick <jogador> (apenas líder)
                        .then(Commands.literal("kick")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(PartyCommands::executeKickPlayer)))

                        // /party promote <jogador> (apenas líder)
                        .then(Commands.literal("promote")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(PartyCommands::executePromotePlayer)))

                        // /party invite <jogador> (apenas líder)
                        .then(Commands.literal("invite")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(PartyCommands::executeInvitePlayer)))
        );
    }

    // ============================================================================
    // 🎯 COMANDOS DE CRIAÇÃO E ENTRADA (VERSÃO ATUALIZADA)
    // ============================================================================

    private static int executeCreatePublicParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String partyName = StringArgumentType.getString(context, "name");
        return createParty(context, partyName, null, true);
    }

    private static int executeCreatePrivateParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String partyName = StringArgumentType.getString(context, "name");
        String password = StringArgumentType.getString(context, "password");
        return createParty(context, partyName, password, false);
    }

    private static int createParty(CommandContext<CommandSourceStack> context, String partyName, String password, boolean isPublic) {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_COMMAND_PLAYER_ONLY));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        PartyManager.CreatePartyResult result = partyManager.createParty(
                player.getUUID(), partyName, password);

        switch (result) {
            case SUCCESS -> {
                String typeText = isPublic ? Component.translatable(Constants.PARTY_CREATE_TYPE_PUBLIC).getString() : Component.translatable(Constants.PARTY_CREATE_TYPE_PRIVATE).getString();
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_CREATE_SUCCESS_FORMAT_NEW, partyName, typeText)
                                .withStyle(ChatFormatting.GREEN), true);

                // Show party created information
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_CREATE_LEADER_NOTIFICATION)
                                .withStyle(ChatFormatting.GOLD), false);

                if (isPublic) {
                    context.getSource().sendSuccess(() ->
                            Component.translatable(Constants.PARTY_CREATE_JOIN_INFO_PUBLIC, partyName)
                                    .withStyle(ChatFormatting.GRAY), false);
                } else {
                    context.getSource().sendSuccess(() ->
                            Component.translatable(Constants.PARTY_CREATE_PASSWORD_INFO_NEW)
                                    .withStyle(ChatFormatting.GRAY), false);
                }
                
                // 🎯 NOVO: Informar sobre multiplicador
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_CREATE_MULTIPLIER_INFO)
                                .withStyle(ChatFormatting.AQUA), false);

                return 1;
            }
            case ALREADY_IN_PARTY -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_ALREADY_IN_PARTY_LEAVE_FIRST));
                return 0;
            }
            case INVALID_NAME -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_INVALID_NAME_LENGTH_RANGE));
                return 0;
            }
            case NAME_TAKEN -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_NAME_ALREADY_EXISTS_SIMPLE));
                return 0;
            }
            default -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_UNKNOWN_CREATE_ERROR));
                return 0;
            }
        }
    }

    private static int executeJoinParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String partyName = StringArgumentType.getString(context, "name");
        return joinParty(context, partyName, "");
    }

    private static int executeJoinPartyWithPassword(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String partyName = StringArgumentType.getString(context, "name");
        String password = StringArgumentType.getString(context, "password");
        return joinParty(context, partyName, password);
    }

    private static int joinParty(CommandContext<CommandSourceStack> context, String partyName, String password) {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_COMMAND_PLAYER_ONLY));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        PartyManager.JoinPartyResult result = partyManager.joinParty(
                player.getUUID(), partyName, password);

        switch (result) {
            case SUCCESS -> {
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_JOIN_SUCCESS_FORMAT, partyName)
                                .withStyle(ChatFormatting.GREEN), true);

                // Mostrar benefícios da party
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_JOIN_PROGRESS_SHARING_INFO)
                                .withStyle(ChatFormatting.GOLD), false);

                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_JOIN_INFO_COMMAND_TIP)
                                .withStyle(ChatFormatting.GRAY), false);

                return 1;
            }
            case ALREADY_IN_PARTY -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_ALREADY_IN_PARTY_LEAVE_FIRST));
                return 0;
            }
            case PARTY_NOT_FOUND -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_PARTY_NOT_FOUND_FORMAT, partyName));
                return 0;
            }
            case WRONG_PASSWORD -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_WRONG_PASSWORD, partyName));
                return 0;
            }
            case PARTY_FULL -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_PARTY_FULL, partyName));
                return 0;
            }
            default -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_UNKNOWN_JOIN));
                return 0;
            }
        }
    }

    // ============================================================================
    // 🎯 COMANDOS DE SAÍDA E INFORMAÇÃO
    // ============================================================================

    private static int executeLeaveParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_NOT_PLAYER));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        PartyManager.LeavePartyResult result = partyManager.leaveParty(player.getUUID());

        switch (result) {
            case SUCCESS -> {
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_LEAVE_SUCCESS)
                                .withStyle(ChatFormatting.GREEN), true);

                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_LEAVE_SUCCESS_INDIVIDUAL)
                                .withStyle(ChatFormatting.YELLOW), false);

                return 1;
            }
            case NOT_IN_PARTY -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_NOT_IN_PARTY));
                return 0;
            }
            default -> {
                context.getSource().sendFailure(
                        Component.translatable(Constants.PARTY_ERROR_UNKNOWN_LEAVE));
                return 0;
            }
        }
    }

    private static int executeListParties(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        List<PartyManager.PartyInfo> publicParties = partyManager.getPublicParties();

        if (publicParties.isEmpty()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_LIST_EMPTY)
                            .withStyle(ChatFormatting.YELLOW), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_LIST_EMPTY_TIP)
                            .withStyle(ChatFormatting.GRAY), false);

            return 1;
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_LIST_HEADER)
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        for (PartyManager.PartyInfo party : publicParties) {
            ChatFormatting statusColor = party.currentMembers >= party.maxMembers ?
                    ChatFormatting.RED : ChatFormatting.GREEN;

            // 🎯 NOVO: Calcular multiplicador e mostrar informações detalhadas
            double multiplier = 1.0 + (party.currentMembers - 1) * 0.75;
            int multiplierPercent = (int)((multiplier - 1.0) * 100);

            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_LIST_ENTRY, party.name, party.currentMembers, party.maxMembers, multiplierPercent)
                            .withStyle(statusColor), false);
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_LIST_JOIN_TIP)
                        .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    private static int executePartyInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_NOT_PLAYER));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_IN_PARTY));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_GET_PARTY_INFO));
            return 0;
        }

        // Cabeçalho da party
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_HEADER)
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        // Informações básicas
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_NAME, party.getName())
                        .withStyle(ChatFormatting.WHITE), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_MEMBERS, party.getMemberCount(), 10)
                        .withStyle(ChatFormatting.GRAY), false);

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_TYPE, party.isPublic() ? 
                    Component.translatable(Constants.PARTY_INFO_TYPE_PUBLIC) : 
                    Component.translatable(Constants.PARTY_INFO_TYPE_PRIVATE))
                        .withStyle(party.isPublic() ? ChatFormatting.GREEN : ChatFormatting.YELLOW), false);

        // Multiplicador
        double multiplier = party.getRequirementMultiplier();
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_MULTIPLIER, String.format("%.1fx", multiplier))
                        .withStyle(multiplier > 1.0 ? ChatFormatting.RED : ChatFormatting.GREEN), false);

        // Lista de membros
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_MEMBERS_HEADER)
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);

        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            String memberName = member != null ? member.getName().getString() : 
                Component.translatable(Constants.PARTY_MEMBER_OFFLINE).getString();
            boolean isLeader = memberId.equals(party.getLeaderId());
            boolean isCurrentPlayer = memberId.equals(player.getUUID());

            Component prefix = isLeader ? 
                Component.translatable(Constants.PARTY_MEMBER_LEADER_PREFIX) : 
                Component.translatable(Constants.PARTY_MEMBER_REGULAR_PREFIX);
            Component suffix = isCurrentPlayer ? 
                Component.translatable(Constants.PARTY_MEMBER_YOU_SUFFIX) : 
                Component.empty();
            ChatFormatting color = isLeader ? ChatFormatting.GOLD : ChatFormatting.WHITE;

            context.getSource().sendSuccess(() ->
                    Component.empty()
                            .append(prefix)
                            .append(Component.literal(memberName))
                            .append(suffix)
                            .withStyle(color), false);
        }

        // Progresso compartilhado
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_PROGRESS_HEADER)
                        .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD), false);

        // Objetivos especiais
        if (party.isSharedElderGuardianKilled()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_PROGRESS_ELDER_GUARDIAN)
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedRaidWon()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_PROGRESS_RAID_WON)
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedTrialVaultAdvancementEarned()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_PROGRESS_TRIAL_VAULT)
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedVoluntaireExileAdvancementEarned()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_PROGRESS_VOLUNTARY_EXILE)
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedWitherKilled()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_PROGRESS_WITHER_KILLED)
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedWardenKilled()) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_PROGRESS_WARDEN_KILLED)
                            .withStyle(ChatFormatting.GREEN), false);
        }

        // Shared kills (only the most important ones)
        Map<String, Integer> sharedKills = party.getSharedMobKills();
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_KILLS_HEADER)
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), false);

        String[] importantMobs = {"zombie", "skeleton", "creeper", "spider", "blaze", "wither_skeleton", "ravager", "evoker"};
        for (String mobType : importantMobs) {
            int kills = sharedKills.getOrDefault(mobType, 0);
            if (kills > 0) {
                context.getSource().sendSuccess(() ->
                        Component.translatable(Constants.PARTY_INFO_KILL_ENTRY, capitalizeFirst(mobType), kills)
                                .withStyle(ChatFormatting.GRAY), false);
            }
        }

        // Comandos disponíveis
        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_COMMANDS_HEADER)
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD), false);

        if (party.getLeaderId().equals(player.getUUID())) {
            // Comandos de líder
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_INFO_LEADER_COMMANDS)
                            .withStyle(ChatFormatting.GOLD), false);
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_INFO_LEADER_KICK)
                            .withStyle(ChatFormatting.GRAY), false);
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_INFO_LEADER_PROMOTE)
                            .withStyle(ChatFormatting.GRAY), false);
            context.getSource().sendSuccess(() ->
                    Component.translatable(Constants.PARTY_INFO_LEADER_DISBAND)
                            .withStyle(ChatFormatting.GRAY), false);
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INFO_MEMBER_LEAVE)
                        .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    // ============================================================================
    // 🎯 COMANDOS DE GERENCIAMENTO (LÍDER)
    // ============================================================================

    private static int executeDisbandParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_NOT_PLAYER));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_IN_PARTY));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_LEADER));
            return 0;
        }

        String partyName = party.getName();

        // Notificar todos os membros antes de dissolver
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null && !member.equals(player)) {
                member.sendSystemMessage(Component.translatable(Constants.PARTY_DISBAND_NOTIFICATION, partyName)
                        .withStyle(ChatFormatting.RED));
            }
        }

        // Forçar saída de todos os membros
        for (UUID memberId : party.getMembers()) {
            partyManager.leaveParty(memberId);
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_DISBAND_SUCCESS, partyName)
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int executeKickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_NOT_PLAYER));
            return 0;
        }

        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_IN_PARTY));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_LEADER));
            return 0;
        }

        if (!party.getMembers().contains(targetPlayer.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_PLAYER_NOT_IN_PARTY, targetPlayer.getName().getString()));
            return 0;
        }

        if (targetPlayer.getUUID().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_CANNOT_KICK_SELF));
            return 0;
        }

        partyManager.leaveParty(targetPlayer.getUUID());

        // Notificar o jogador expulso
        targetPlayer.sendSystemMessage(Component.translatable(Constants.PARTY_KICK_NOTIFICATION, party.getName())
                .withStyle(ChatFormatting.RED));

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_KICK_SUCCESS, targetPlayer.getName().getString())
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int executePromotePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_NOT_PLAYER));
            return 0;
        }

        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_IN_PARTY));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_LEADER));
            return 0;
        }

        if (!party.getMembers().contains(targetPlayer.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_PLAYER_NOT_IN_PARTY, targetPlayer.getName().getString()));
            return 0;
        }

        if (targetPlayer.getUUID().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_ALREADY_LEADER));
            return 0;
        }

        // Transferir liderança
        party.setLeaderId(targetPlayer.getUUID());
        partyManager.setDirty();

        // Notificar todos os membros
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                if (member.equals(targetPlayer)) {
                    member.sendSystemMessage(Component.translatable(Constants.PARTY_PROMOTE_NEW_LEADER, party.getName())
                            .withStyle(ChatFormatting.GOLD));
                } else if (member.equals(player)) {
                    member.sendSystemMessage(Component.translatable(Constants.PARTY_PROMOTE_OLD_LEADER, targetPlayer.getName().getString())
                            .withStyle(ChatFormatting.GREEN));
                } else {
                    member.sendSystemMessage(Component.translatable(Constants.PARTY_PROMOTE_NOTIFICATION, targetPlayer.getName().getString())
                            .withStyle(ChatFormatting.YELLOW));
                }
            }
        }

        return 1;
    }

    private static int executeInvitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable(Constants.PARTY_ERROR_NOT_PLAYER));
            return 0;
        }

        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_IN_PARTY));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_NOT_LEADER));
            return 0;
        }

        if (partyManager.isPlayerInParty(targetPlayer.getUUID())) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_PLAYER_ALREADY_IN_PARTY, targetPlayer.getName().getString()));
            return 0;
        }

        if (party.getMemberCount() >= 4) {
            context.getSource().sendFailure(
                    Component.translatable(Constants.PARTY_ERROR_PARTY_FULL_INVITE));
            return 0;
        }

        // Enviar convite
        targetPlayer.sendSystemMessage(Component.translatable(Constants.PARTY_INVITE_RECEIVED, player.getName().getString(), party.getName())
                .withStyle(ChatFormatting.GOLD));

        if (party.isPublic()) {
            targetPlayer.sendSystemMessage(Component.translatable(Constants.PARTY_INVITE_JOIN_PUBLIC, party.getName())
                    .withStyle(ChatFormatting.GRAY));
        } else {
            targetPlayer.sendSystemMessage(Component.translatable(Constants.PARTY_INVITE_JOIN_PRIVATE)
                    .withStyle(ChatFormatting.GRAY));
        }

        context.getSource().sendSuccess(() ->
                Component.translatable(Constants.PARTY_INVITE_SUCCESS, targetPlayer.getName().getString())
                        .withStyle(ChatFormatting.GREEN), false);

        return 1;
    }

    // ============================================================================
    // 🎯 MÉTODOS AUXILIARES
    // ============================================================================

    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase().replace("_", " ");
    }
}