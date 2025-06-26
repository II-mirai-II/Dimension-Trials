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

            content.add(Component.translatable(Constants.GUI_PARTIES_PARTY_TYPE,
                    Component.translatable(partyData.isPartyPublic() ? 
                            Constants.GUI_PARTIES_TYPE_PUBLIC : Constants.GUI_PARTIES_TYPE_PRIVATE).getString())
                    .withStyle(partyData.isPartyPublic() ? ChatFormatting.GREEN : ChatFormatting.YELLOW));

            content.add(Component.translatable(Constants.GUI_PARTIES_MEMBER_COUNT, 
                    partyData.getMemberCount())
                    .withStyle(ChatFormatting.GRAY));

            int percentageIncrease = (int)((partyData.getRequirementMultiplier() - 1.0) * 100);
            content.add(Component.translatable(Constants.GUI_PARTIES_MULTIPLIER,
                    partyData.getRequirementMultiplier(), percentageIncrease)
                    .withStyle(ChatFormatting.YELLOW));

            content.add(Component.empty());

            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_MEMBERS)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

            UUID currentPlayerId = Minecraft.getInstance().player != null ?
                    Minecraft.getInstance().player.getUUID() : null;

            for (UUID memberId : partyData.getMembers()) {
                String memberName = partyData.getMemberName(memberId);
                boolean isLeader = memberId.equals(partyData.getLeaderId());
                boolean isCurrentPlayer = memberId.equals(currentPlayerId);

                Component memberComponent = Component.literal(
                        (isLeader ? "👑 " : "👤 ") + memberName)
                        .append(isCurrentPlayer ? Component.translatable(Constants.GUI_PARTIES_YOU_INDICATOR) : Component.empty())
                        .withStyle(isLeader ? ChatFormatting.GOLD : ChatFormatting.WHITE);

                content.add(memberComponent);
            }

            content.add(Component.empty());

            // 🔧 CORRIGIDO: Usar constantes
            content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_PROGRESS)
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

            if (partyData.isSharedElderGuardianKilled()) {
                content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_ELDER_GUARDIAN)
                        .withStyle(ChatFormatting.GREEN));
            }

            if (partyData.isSharedRaidWon()) {
                content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_RAID)
                        .withStyle(ChatFormatting.GREEN));
            }

            if (partyData.isSharedWitherKilled()) {
                content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_WITHER)
                        .withStyle(ChatFormatting.GREEN));
            }

            if (partyData.isSharedWardenKilled()) {
                content.add(Component.translatable(Constants.GUI_PARTIES_SHARED_WARDEN)
                        .withStyle(ChatFormatting.GREEN));
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
        content.add(Component.translatable(Constants.GUI_PARTIES_MAX_MEMBERS)
                .withStyle(ChatFormatting.GRAY));
        
        // 🎯 NOVO: Informações sobre transferência de progresso
        if (partyData.isInParty()) {
            content.add(Component.translatable(Constants.GUI_PARTIES_PROGRESS_TRANSFERRED_IN)
                    .withStyle(ChatFormatting.GREEN));
            content.add(Component.translatable(Constants.GUI_PARTIES_PROGRESS_PRESERVED_OUT)
                    .withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.translatable(Constants.GUI_PARTIES_PROGRESS_WILL_TRANSFER)
                    .withStyle(ChatFormatting.YELLOW));
            content.add(Component.translatable(Constants.GUI_PARTIES_PROGRESS_WILL_PRESERVE)
                    .withStyle(ChatFormatting.YELLOW));
        }

        content.add(Component.empty());

        // 🔧 CORRIGIDO: Usar constantes
        if (partyData.isInParty()) {
            content.add(Component.translatable(Constants.GUI_PARTIES_COMMANDS)
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            content.add(Component.translatable(Constants.GUI_PARTIES_CMD_LEAVE)
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable(Constants.GUI_PARTIES_CMD_INFO)
                    .withStyle(ChatFormatting.GRAY));

            var player = Minecraft.getInstance().player;
            UUID currentPlayerId = player != null ? player.getUUID() : null;

            if (currentPlayerId != null && currentPlayerId.equals(partyData.getLeaderId())) {
                content.add(Component.translatable(Constants.GUI_PARTIES_CMD_KICK)
                        .withStyle(ChatFormatting.GRAY));
                content.add(Component.translatable(Constants.GUI_PARTIES_CMD_PROMOTE)
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        return content;
    }
}