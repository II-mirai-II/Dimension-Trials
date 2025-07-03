package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seção principal da Fase 3 (End Dimension)
 */
public class Phase3MainSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.PHASE3_MAIN;
    }

    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase3_main.desc");
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.shouldShowPhase3() && progress.isPhase2Completed();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        // 🎯 NOVO: Ensure client-side external mod integration is initialized
        net.mirai.dimtr.integration.ExternalModIntegration.initializeClientSide();
        
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.complete.phase2.first")
                    .withStyle(ChatFormatting.RED));
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.phase3.locked.line1")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase3.locked.line2")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase3.locked.line3")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (!progress.shouldShowPhase3()) {
            content.add(Component.translatable("gui.dimtr.phase3.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (progress.isPhase3Completed()) {
            content.add(Component.translatable("gui.dimtr.phase.complete")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }

        // Objetivos especiais da Fase 3 (Bosses do End)
        content.add(Component.translatable("gui.dimtr.end.boss.objectives")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        // Bosses de mods externos para Fase 3
        var externalBossesPhase3 = progress.getExternalBossesForPhase(3);
        if (!externalBossesPhase3.isEmpty()) {
            for (var boss : externalBossesPhase3) {
                boolean killed = progress.isExternalBossKilled(boss.entityId);
                content.add(createGoalLine(
                        Component.literal(boss.displayName),
                        killed));
            }
        } else {
            content.add(Component.translatable("gui.dimtr.phase3.no.bosses")
                    .withStyle(ChatFormatting.GRAY));
        }

        content.add(Component.empty());

        // Status da fase
        if (progress.isPhase3Completed()) {
            content.add(Component.translatable("gui.dimtr.phase3.complete.ultimate")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.translatable("gui.dimtr.complete.end.objectives")
                    .withStyle(ChatFormatting.YELLOW));
        }

        content.add(Component.empty());
        content.add(Component.translatable("gui.dimtr.end.dimension.challenges")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        content.add(Component.translatable("gui.dimtr.challenge.end.exploration")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.powerful.bosses")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.ultimate.rewards")
                .withStyle(ChatFormatting.GRAY));

        return content;
    }

    private Component createGoalLine(Component text, boolean completed) {
        ChatFormatting statusColor = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        String statusIcon = completed ? "✔" : "❌";

        return Component.literal(statusIcon + " ").withStyle(statusColor)
                .append(text.copy().withStyle(ChatFormatting.WHITE));
    }
}
