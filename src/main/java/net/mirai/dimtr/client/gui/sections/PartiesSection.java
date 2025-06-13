package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientPartyData;
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants; // 🔧 ADICIONAR IMPORT
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Seção de Parties/Grupos - VERSÃO COMPLETA
 */
public class PartiesSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.PARTIES;
    }

    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }

    @Override
    public Component getDescription() {
        // 🔧 CORRIGIDO: Usar constante
        return Component.translatable(Constants.GUI_SUMMARY_PARTIES_DESC);
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return true;
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        ClientPartyData partyData = ClientPartyData.INSTANCE;

        // 🔧 CORRIGIDO: Usar constantes
        content.add(Component.translatable(Constants.GUI_PARTIES_WELCOME)
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        content.add(Component.empty());

        content.add(Component.translatable(Constants.GUI_PARTIES_CURRENT_STATUS)
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        if (partyData.isInParty()) {
            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_IN_PARTY)
                    .withStyle(ChatFormatting.GREEN));

            content.add(Component.literal("Party: " + partyData.getPartyName())
                    .withStyle(ChatFormatting.WHITE));

            content.add(Component.literal("Membros: " + partyData.getMemberCount() + "/4")
                    .withStyle(ChatFormatting.GRAY));

            content.add(Component.literal("Multiplicador: " + String.format("%.1fx", partyData.getRequirementMultiplier()))
                    .withStyle(ChatFormatting.YELLOW));

            content.add(Component.empty());

            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_MEMBERS)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

            UUID currentPlayerId = Minecraft.getInstance().player != null ?
                    Minecraft.getInstance().player.getUUID() : null;

            for (UUID memberId : partyData.getMembers()) {
                String memberName = getMemberName(memberId);
                boolean isLeader = memberId.equals(partyData.getLeaderId());
                boolean isCurrentPlayer = memberId.equals(currentPlayerId);

                Component memberComponent = Component.literal(
                        (isLeader ? "👑 " : "👤 ") + memberName +
                                (isCurrentPlayer ? " (Você)" : "")
                ).withStyle(isLeader ? ChatFormatting.GOLD : ChatFormatting.WHITE);

                content.add(memberComponent);
            }

            content.add(Component.empty());

            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_PROGRESS)
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

            if (partyData.isSharedElderGuardianKilled()) {
                content.add(Component.literal("✔ Elder Guardian (Compartilhado)")
                        .withStyle(ChatFormatting.GREEN));
            }

            if (partyData.isSharedRaidWon()) {
                content.add(Component.literal("✔ Raid Vencida (Compartilhado)")
                        .withStyle(ChatFormatting.GREEN));
            }

            if (partyData.isSharedWitherKilled()) {
                content.add(Component.literal("✔ Wither Morto (Compartilhado)")
                        .withStyle(ChatFormatting.GREEN));
            }

            if (partyData.isSharedWardenKilled()) {
                content.add(Component.literal("✔ Warden Morto (Compartilhado)")
                        .withStyle(ChatFormatting.GREEN));
            }

            content.add(Component.empty());
            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_MOBS)
                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));

            String[] importantMobs = {"zombie", "skeleton", "creeper", "blaze", "wither_skeleton"};
            for (String mobType : importantMobs) {
                int sharedKills = partyData.getSharedMobKillCount(mobType);
                if (sharedKills > 0) {
                    content.add(Component.literal("⚔ " + capitalizeFirst(mobType) + ": " + sharedKills)
                            .withStyle(ChatFormatting.GRAY));
                }
            }

        } else {
            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_NO_PARTY)
                    .withStyle(ChatFormatting.YELLOW));

            content.add(Component.empty());

            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_ACTIONS)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

            content.add(Component.translatable(Constants.GUI_PARTIES_ACTION_CREATE)
                    .withStyle(ChatFormatting.GREEN));
            content.add(Component.literal("  /dimtr party create <nome> [senha]")
                    .withStyle(ChatFormatting.GRAY));

            content.add(Component.translatable(Constants.GUI_PARTIES_ACTION_JOIN)
                    .withStyle(ChatFormatting.BLUE));
            content.add(Component.literal("  /dimtr party join <nome> [senha]")
                    .withStyle(ChatFormatting.GRAY));

            content.add(Component.translatable(Constants.GUI_PARTIES_ACTION_LIST)
                    .withStyle(ChatFormatting.AQUA));
            content.add(Component.literal("  /dimtr party list")
                    .withStyle(ChatFormatting.GRAY));
        }

        content.add(Component.empty());

        // 🔧 CORRIGIDO: Usar constantes
        content.add(Component.translatable(Constants.GUI_PARTIES_BENEFITS)
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        content.add(Component.translatable(Constants.GUI_PARTIES_BENEFIT_SHARED)
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable(Constants.GUI_PARTIES_BENEFIT_MULTIPLIER)
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.literal("• Máximo de 4 membros por party")
                .withStyle(ChatFormatting.GRAY));

        content.add(Component.empty());

        // 🔧 CORRIGIDO: Usar constantes
        if (partyData.isInParty()) {
            content.add(Component.translatable(Constants.GUI_PARTIES_COMMANDS)
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            content.add(Component.literal("/dimtr party leave - Sair da party")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.literal("/dimtr party info - Info da party")
                    .withStyle(ChatFormatting.GRAY));

            UUID currentPlayerId = Minecraft.getInstance().player != null ?
                    Minecraft.getInstance().player.getUUID() : null;

            if (currentPlayerId != null && currentPlayerId.equals(partyData.getLeaderId())) {
                content.add(Component.literal("/dimtr party kick <jogador> - Expulsar")
                        .withStyle(ChatFormatting.GRAY));
                content.add(Component.literal("/dimtr party promote <jogador> - Promover")
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        return content;
    }

    private String getMemberName(UUID memberId) {
        return "Player-" + memberId.toString().substring(0, 8);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}