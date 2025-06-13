package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seção principal da Fase 2
 */
public class Phase2MainSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.PHASE2_MAIN;
    }

    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase2_main.desc");
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.isServerEnablePhase2() && progress.isPhase1EffectivelyComplete();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.translatable("gui.dimtr.complete.phase1.first")
                    .withStyle(ChatFormatting.RED));
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.phase2.locked.line1")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase2.locked.line2")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase2.locked.line3")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (!progress.isServerEnablePhase2()) {
            content.add(Component.translatable("gui.dimtr.phase2.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.phase.complete")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }

        // Objetivos especiais
        content.add(Component.translatable(Constants.HUD_SECTION_SPECIAL_OBJECTIVES)
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        if (progress.isServerReqWither()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_WITHER_KILLED),
                    progress.isWitherKilled()));
        }

        if (progress.isServerReqWarden()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_WARDEN_KILLED),
                    progress.isWardenKilled()));
        }

        content.add(Component.empty());

        // Status da fase
        if (progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.end.unlocked")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.translatable("gui.dimtr.complete.objectives")
                    .withStyle(ChatFormatting.YELLOW));
            content.add(Component.translatable("gui.dimtr.unlock.end")
                    .withStyle(ChatFormatting.YELLOW));
        }

        content.add(Component.empty());
        content.add(Component.translatable("gui.dimtr.unique.challenges")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        content.add(Component.translatable("gui.dimtr.challenge.wither")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.warden")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.nether")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.new.mobs")
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