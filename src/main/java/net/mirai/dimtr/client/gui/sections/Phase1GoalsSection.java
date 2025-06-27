package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seção de objetivos/mobs da Fase 1
 */
public class Phase1GoalsSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.PHASE1_GOALS;
    }

    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase1_goals.desc");
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.isServerEnablePhase1() && progress.isServerEnableMobKillsPhase1();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isServerEnablePhase1()) {
            content.add(Component.translatable("gui.dimtr.phase1.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (!progress.isServerEnableMobKillsPhase1()) {
            content.add(Component.translatable("gui.dimtr.mob.elimination.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Mobs comuns
        content.add(Component.translatable("gui.dimtr.section.common.mobs")
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, Constants.HUD_MOB_ZOMBIE, "zombie", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_SKELETON, "skeleton", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_STRAY, "stray", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_HUSK, "husk", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_SPIDER, "spider", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_CREEPER, "creeper", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_DROWNED, "drowned", 1);

        content.add(Component.empty());

        // Mobs especiais
        content.add(Component.translatable("gui.dimtr.section.special.mobs")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, Constants.HUD_MOB_ENDERMAN, "enderman", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_WITCH, "witch", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_PILLAGER, "pillager", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_VINDICATOR, "vindicator", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_BOGGED, "bogged", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_BREEZE, "breeze", 1);

        content.add(Component.empty());

        // Goal kills
        content.add(Component.translatable("gui.dimtr.section.goal.kills")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, Constants.HUD_MOB_RAVAGER, "ravager", 1);
        addMobCounterLine(content, progress, Constants.HUD_MOB_EVOKER, "evoker", 1);

        // Sumário
        content.add(Component.empty());
        content.add(Component.translatable("gui.dimtr.summary")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        int totalKills = 0;
        int totalRequired = 0;
        int completedTypes = 0;
        int totalTypes = 0;

        String[] allMobs = {"zombie", "skeleton", "stray", "husk", "spider",
                "creeper", "drowned", "enderman", "witch", "pillager",
                "vindicator", "bogged", "breeze", "ravager", "evoker"};

        for (String mobType : allMobs) {
            int current = progress.getMobKillCount(mobType);
            int required = progress.getMobKillRequirement(mobType, 1);
            if (required > 0) {
                totalKills += current;
                totalRequired += required;
                totalTypes++;
                if (current >= required) completedTypes++;
            }
        }

        ChatFormatting summaryColor = completedTypes == totalTypes ?
                ChatFormatting.GREEN : ChatFormatting.YELLOW;

        // Formatação manual para garantir que os valores apareçam corretamente
        content.add(Component.literal("Total Kills: " + totalKills + "/" + totalRequired)
                .withStyle(summaryColor));
        content.add(Component.literal(completedTypes + "/" + totalTypes + " Types Complete")
                .withStyle(summaryColor));

        return content;
    }

    private void addMobCounterLine(List<Component> contentList, ClientProgressionData progress,
                                   String translationKey, String mobType, int phase) {
        int current = progress.getMobKillCount(mobType);
        int required = progress.getMobKillRequirement(mobType, phase);

        if (required <= 0) return;

        contentList.add(createMobCounterLine(translationKey, mobType, current, required));
    }

    private Component createMobCounterLine(String translationKey, String mobType, int current, int required) {
        boolean completed = current >= required;
        ChatFormatting countColor = completed ? ChatFormatting.GREEN :
                (current > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
        String statusIcon = completed ? "✔" : "⚔";

        return Component.literal(statusIcon + " ").withStyle(completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED)
                .append(Component.translatable(translationKey).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(current + "/" + required).withStyle(countColor));
    }
}