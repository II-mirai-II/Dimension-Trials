package net.mirai.dimtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.PartyManager;
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
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        PartyManager.CreatePartyResult result = partyManager.createParty(
                player.getUUID(), partyName, password);

        switch (result) {
            case SUCCESS -> {
                String typeText = isPublic ? "pública" : "privada";
                context.getSource().sendSuccess(() ->
                        Component.literal("✅ Party '" + partyName + "' (" + typeText + ") criada com sucesso!")
                                .withStyle(ChatFormatting.GREEN), true);

                // Mostrar informações da party criada
                context.getSource().sendSuccess(() ->
                        Component.literal("👑 Você é o líder da party!")
                                .withStyle(ChatFormatting.GOLD), false);

                if (isPublic) {
                    context.getSource().sendSuccess(() ->
                            Component.literal("💡 Outros jogadores podem entrar com '/dimtr party join " + partyName + "'")
                                    .withStyle(ChatFormatting.GRAY), false);
                } else {
                    context.getSource().sendSuccess(() ->
                            Component.literal("🔒 Party protegida por senha. Compartilhe a senha com quem quiser convidar!")
                                    .withStyle(ChatFormatting.GRAY), false);
                }
                
                // 🎯 NOVO: Informar sobre multiplicador
                context.getSource().sendSuccess(() ->
                        Component.literal("⚡ Multiplicador atual: +0% (1 membro). +25% por cada membro adicional!")
                                .withStyle(ChatFormatting.AQUA), false);

                return 1;
            }
            case ALREADY_IN_PARTY -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Você já está em uma party! Use '/dimtr party leave' primeiro"));
                return 0;
            }
            case INVALID_NAME -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Nome da party inválido! Use 1-20 caracteres"));
                return 0;
            }
            case NAME_TAKEN -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Já existe uma party com esse nome!"));
                return 0;
            }
            default -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Erro desconhecido ao criar party"));
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
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        PartyManager.JoinPartyResult result = partyManager.joinParty(
                player.getUUID(), partyName, password);

        switch (result) {
            case SUCCESS -> {
                context.getSource().sendSuccess(() ->
                        Component.literal("✅ Você entrou na party '" + partyName + "'!")
                                .withStyle(ChatFormatting.GREEN), true);

                // Mostrar benefícios da party
                context.getSource().sendSuccess(() ->
                        Component.literal("🎉 Agora você compartilha progresso com os membros da party!")
                                .withStyle(ChatFormatting.GOLD), false);

                context.getSource().sendSuccess(() ->
                        Component.literal("💡 Use '/dimtr party info' para ver detalhes da party")
                                .withStyle(ChatFormatting.GRAY), false);

                return 1;
            }
            case ALREADY_IN_PARTY -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Você já está em uma party! Use '/dimtr party leave' primeiro"));
                return 0;
            }
            case PARTY_NOT_FOUND -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Party '" + partyName + "' não encontrada!"));
                return 0;
            }
            case WRONG_PASSWORD -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Senha incorreta para a party '" + partyName + "'!"));
                return 0;
            }
            case PARTY_FULL -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Party '" + partyName + "' está cheia! (10/10 membros)"));
                return 0;
            }
            default -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Erro desconhecido ao entrar na party"));
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
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        PartyManager.LeavePartyResult result = partyManager.leaveParty(player.getUUID());

        switch (result) {
            case SUCCESS -> {
                context.getSource().sendSuccess(() ->
                        Component.literal("✅ Você saiu da party!")
                                .withStyle(ChatFormatting.GREEN), true);

                context.getSource().sendSuccess(() ->
                        Component.literal("📊 Sua progressão agora é individual novamente")
                                .withStyle(ChatFormatting.YELLOW), false);

                return 1;
            }
            case NOT_IN_PARTY -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Você não está em nenhuma party!"));
                return 0;
            }
            default -> {
                context.getSource().sendFailure(
                        Component.literal("❌ Erro desconhecido ao sair da party"));
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
                    Component.literal("📋 Nenhuma party pública disponível")
                            .withStyle(ChatFormatting.YELLOW), false);

            context.getSource().sendSuccess(() ->
                    Component.literal("💡 Crie uma party com '/dimtr party create <nome>'")
                            .withStyle(ChatFormatting.GRAY), false);

            return 1;
        }

        context.getSource().sendSuccess(() ->
                Component.literal("📋 Parties Públicas Disponíveis:")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        for (PartyManager.PartyInfo party : publicParties) {
            ChatFormatting statusColor = party.currentMembers >= party.maxMembers ?
                    ChatFormatting.RED : ChatFormatting.GREEN;

            // 🎯 NOVO: Calcular multiplicador e mostrar informações detalhadas
            double multiplier = 1.0 + (party.currentMembers - 1) * 0.75;
            int multiplierPercent = (int)((multiplier - 1.0) * 100);

            context.getSource().sendSuccess(() ->
                    Component.literal("• " + party.name + " (" + party.currentMembers + "/" + party.maxMembers + ") [+" + multiplierPercent + "%]")
                            .withStyle(statusColor), false);
        }

        context.getSource().sendSuccess(() ->
                Component.literal("💡 Use '/dimtr party join <nome>' para entrar!")
                        .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    private static int executePartyInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você não está em nenhuma party!"));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null) {
            context.getSource().sendFailure(
                    Component.literal("❌ Erro ao obter informações da party"));
            return 0;
        }

        // Cabeçalho da party
        context.getSource().sendSuccess(() ->
                Component.literal("=== INFORMAÇÕES DA PARTY ===")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        // Informações básicas
        context.getSource().sendSuccess(() ->
                Component.literal("📛 Nome: " + party.getName())
                        .withStyle(ChatFormatting.WHITE), false);

        context.getSource().sendSuccess(() ->
                Component.literal("👥 Membros: " + party.getMemberCount() + "/10")
                        .withStyle(ChatFormatting.GRAY), false);

        context.getSource().sendSuccess(() ->
                Component.literal("🔒 Tipo: " + (party.isPublic() ? "Pública" : "Privada"))
                        .withStyle(party.isPublic() ? ChatFormatting.GREEN : ChatFormatting.YELLOW), false);

        // Multiplicador
        double multiplier = party.getRequirementMultiplier();
        context.getSource().sendSuccess(() ->
                Component.literal("⚡ Multiplicador: " + String.format("%.1fx", multiplier))
                        .withStyle(multiplier > 1.0 ? ChatFormatting.RED : ChatFormatting.GREEN), false);

        // Lista de membros
        context.getSource().sendSuccess(() ->
                Component.literal("--- MEMBROS ---")
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);

        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            String memberName = member != null ? member.getName().getString() : "Jogador Offline";
            boolean isLeader = memberId.equals(party.getLeaderId());
            boolean isCurrentPlayer = memberId.equals(player.getUUID());

            String prefix = isLeader ? "👑 " : "👤 ";
            String suffix = isCurrentPlayer ? " (Você)" : "";
            ChatFormatting color = isLeader ? ChatFormatting.GOLD : ChatFormatting.WHITE;

            context.getSource().sendSuccess(() ->
                    Component.literal(prefix + memberName + suffix)
                            .withStyle(color), false);
        }

        // Progresso compartilhado
        context.getSource().sendSuccess(() ->
                Component.literal("--- PROGRESSO COMPARTILHADO ---")
                        .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD), false);

        // Objetivos especiais
        if (party.isSharedElderGuardianKilled()) {
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Elder Guardian Morto")
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedRaidWon()) {
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Raid Vencida")
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedTrialVaultAdvancementEarned()) {
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Trial Vault Conquistado")
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedVoluntaireExileAdvancementEarned()) {
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Voluntary Exile Conquistado")
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedWitherKilled()) {
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Wither Morto")
                            .withStyle(ChatFormatting.GREEN), false);
        }

        if (party.isSharedWardenKilled()) {
            context.getSource().sendSuccess(() ->
                    Component.literal("✅ Warden Morto")
                            .withStyle(ChatFormatting.GREEN), false);
        }

        // Kills compartilhados (apenas os mais importantes)
        Map<String, Integer> sharedKills = party.getSharedMobKills();
        context.getSource().sendSuccess(() ->
                Component.literal("--- KILLS COMPARTILHADOS ---")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), false);

        String[] importantMobs = {"zombie", "skeleton", "creeper", "spider", "blaze", "wither_skeleton", "ravager", "evoker"};
        for (String mobType : importantMobs) {
            int kills = sharedKills.getOrDefault(mobType, 0);
            if (kills > 0) {
                context.getSource().sendSuccess(() ->
                        Component.literal("⚔ " + capitalizeFirst(mobType) + ": " + kills)
                                .withStyle(ChatFormatting.GRAY), false);
            }
        }

        // Comandos disponíveis
        context.getSource().sendSuccess(() ->
                Component.literal("--- COMANDOS DISPONÍVEIS ---")
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD), false);

        if (party.getLeaderId().equals(player.getUUID())) {
            // Comandos de líder
            context.getSource().sendSuccess(() ->
                    Component.literal("👑 Comandos de Líder:")
                            .withStyle(ChatFormatting.GOLD), false);
            context.getSource().sendSuccess(() ->
                    Component.literal("• /dimtr party kick <jogador> - Expulsar membro")
                            .withStyle(ChatFormatting.GRAY), false);
            context.getSource().sendSuccess(() ->
                    Component.literal("• /dimtr party promote <jogador> - Transferir liderança")
                            .withStyle(ChatFormatting.GRAY), false);
            context.getSource().sendSuccess(() ->
                    Component.literal("• /dimtr party disband - Dissolver party")
                            .withStyle(ChatFormatting.GRAY), false);
        }

        context.getSource().sendSuccess(() ->
                Component.literal("• /dimtr party leave - Sair da party")
                        .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    // ============================================================================
    // 🎯 COMANDOS DE GERENCIAMENTO (LÍDER)
    // ============================================================================

    private static int executeDisbandParty(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você não está em nenhuma party!"));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Apenas o líder pode dissolver a party!"));
            return 0;
        }

        String partyName = party.getName();

        // Notificar todos os membros antes de dissolver
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (member != null && !member.equals(player)) {
                member.sendSystemMessage(Component.literal("💔 A party '" + partyName + "' foi dissolvida pelo líder")
                        .withStyle(ChatFormatting.RED));
            }
        }

        // Forçar saída de todos os membros
        for (UUID memberId : party.getMembers()) {
            partyManager.leaveParty(memberId);
        }

        context.getSource().sendSuccess(() ->
                Component.literal("✅ Party '" + partyName + "' foi dissolvida!")
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int executeKickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você não está em nenhuma party!"));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Apenas o líder pode expulsar membros!"));
            return 0;
        }

        if (!party.getMembers().contains(targetPlayer.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ " + targetPlayer.getName().getString() + " não está na sua party!"));
            return 0;
        }

        if (targetPlayer.getUUID().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você não pode se expulsar! Use '/dimtr party leave' ou '/dimtr party disband'"));
            return 0;
        }

        partyManager.leaveParty(targetPlayer.getUUID());

        // Notificar o jogador expulso
        targetPlayer.sendSystemMessage(Component.literal("💔 Você foi expulso da party '" + party.getName() + "' pelo líder")
                .withStyle(ChatFormatting.RED));

        context.getSource().sendSuccess(() ->
                Component.literal("✅ " + targetPlayer.getName().getString() + " foi expulso da party!")
                        .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }

    private static int executePromotePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você não está em nenhuma party!"));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Apenas o líder pode promover membros!"));
            return 0;
        }

        if (!party.getMembers().contains(targetPlayer.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ " + targetPlayer.getName().getString() + " não está na sua party!"));
            return 0;
        }

        if (targetPlayer.getUUID().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você já é o líder da party!"));
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
                    member.sendSystemMessage(Component.literal("👑 Você foi promovido a líder da party '" + party.getName() + "'!")
                            .withStyle(ChatFormatting.GOLD));
                } else if (member.equals(player)) {
                    member.sendSystemMessage(Component.literal("✅ Você transferiu a liderança para " + targetPlayer.getName().getString())
                            .withStyle(ChatFormatting.GREEN));
                } else {
                    member.sendSystemMessage(Component.literal("👑 " + targetPlayer.getName().getString() + " é o novo líder da party!")
                            .withStyle(ChatFormatting.YELLOW));
                }
            }
        }

        return 1;
    }

    private static int executeInvitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("❌ Comando deve ser executado por um jogador"));
            return 0;
        }

        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        ServerLevel serverLevel = context.getSource().getLevel();
        PartyManager partyManager = PartyManager.get(serverLevel);

        if (!partyManager.isPlayerInParty(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Você não está em nenhuma party!"));
            return 0;
        }

        PartyData party = partyManager.getPlayerParty(player.getUUID());
        if (party == null || !party.getLeaderId().equals(player.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ Apenas o líder pode convidar jogadores!"));
            return 0;
        }

        if (partyManager.isPlayerInParty(targetPlayer.getUUID())) {
            context.getSource().sendFailure(
                    Component.literal("❌ " + targetPlayer.getName().getString() + " já está em uma party!"));
            return 0;
        }

        if (party.getMemberCount() >= 4) {
            context.getSource().sendFailure(
                    Component.literal("❌ Sua party já está cheia! (10/10 membros)"));
            return 0;
        }

        // Enviar convite
        targetPlayer.sendSystemMessage(Component.literal("📨 " + player.getName().getString() + " convidou você para a party '" + party.getName() + "'!")
                .withStyle(ChatFormatting.GOLD));

        if (party.isPublic()) {
            targetPlayer.sendSystemMessage(Component.literal("💡 Use '/dimtr party join " + party.getName() + "' para aceitar")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            targetPlayer.sendSystemMessage(Component.literal("🔒 Party privada - aguarde a senha do líder")
                    .withStyle(ChatFormatting.GRAY));
        }

        context.getSource().sendSuccess(() ->
                Component.literal("✅ Convite enviado para " + targetPlayer.getName().getString() + "!")
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