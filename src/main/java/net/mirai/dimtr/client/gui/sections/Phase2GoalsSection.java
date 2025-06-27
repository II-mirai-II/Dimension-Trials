package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seção de objetivos/mobs da Fase 2
 */
public class Phase2GoalsSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.PHASE2_GOALS;
    }

    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase2_goals.desc");
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.isServerEnablePhase2() && progress.isServerEnableMobKillsPhase2() &&
                progress.isPhase1EffectivelyComplete();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.translatable("gui.dimtr.complete.phase1.first")
                    .withStyle(ChatFormatting.RED));
            return content;
        }

        if (!progress.isServerEnableMobKillsPhase2()) {
            content.add(Component.translatable("gui.dimtr.mob.elimination.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Mobs do Nether
        content.add(Component.translatable(Constants.HUD_SECTION_NETHER_MOBS)
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, Constants.HUD_MOB_BLAZE, "blaze", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_WITHER_SKELETON, "wither_skeleton", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_PIGLIN_BRUTE, "piglin_brute", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_HOGLIN, "hoglin", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_ZOGLIN, "zoglin", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_GHAST, "ghast", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_PIGLIN, "piglin", 2);

        content.add(Component.empty());

        // Mobs do Overworld repetidos
        content.add(Component.translatable(Constants.HUD_SECTION_REPEAT_OVERWORLD)
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        content.add(Component.translatable("gui.dimtr.requirements.increased")
                .withStyle(ChatFormatting.GRAY));
        addMobCounterLine(content, progress, Constants.HUD_MOB_ZOMBIE, "zombie", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_SKELETON, "skeleton", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_CREEPER, "creeper", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_SPIDER, "spider", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_ENDERMAN, "enderman", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_WITCH, "witch", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_PILLAGER, "pillager", 2);

        content.add(Component.empty());

        // Goal kills resetados
        content.add(Component.translatable("gui.dimtr.section.goal.kills.reset")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, Constants.HUD_MOB_RAVAGER, "ravager", 2);
        addMobCounterLine(content, progress, Constants.HUD_MOB_EVOKER, "evoker", 2);

        // Sumário
        content.add(Component.empty());
        content.add(Component.translatable("gui.dimtr.summary")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        int netherCompleted = 0;
        int netherTotal = 0;
        int overworldCompleted = 0;
        int overworldTotal = 0;

        String[] netherMobs = {"blaze", "wither_skeleton", "piglin_brute", "hoglin", "zoglin", "ghast", "piglin"};
        String[] overworldMobs = {"zombie", "skeleton", "creeper", "spider", "enderman", "witch", "pillager", "ravager", "evoker"};

        for (String mobType : netherMobs) {
            int current = progress.getMobKillCount(mobType);
            int required = progress.getMobKillRequirement(mobType, 2);
            if (required > 0) {
                netherTotal++;
                if (current >= required) netherCompleted++;
            }
        }

        for (String mobType : overworldMobs) {
            int current = progress.getMobKillCount(mobType);
            int required = progress.getMobKillRequirement(mobType, 2);
            if (required > 0) {
                overworldTotal++;
                if (current >= required) overworldCompleted++;
            }
        }

        ChatFormatting netherColor = netherCompleted == netherTotal ? ChatFormatting.GREEN : ChatFormatting.RED;
        ChatFormatting overworldColor = overworldCompleted == overworldTotal ? ChatFormatting.GREEN : ChatFormatting.YELLOW;

        // Formatação manual para garantir que os valores apareçam corretamente
        content.add(Component.literal("🔥 Nether Progress: " + netherCompleted + "/" + netherTotal)
                .withStyle(netherColor));
        content.add(Component.literal("🌍 Overworld Progress: " + overworldCompleted + "/" + overworldTotal)
                .withStyle(overworldColor));

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