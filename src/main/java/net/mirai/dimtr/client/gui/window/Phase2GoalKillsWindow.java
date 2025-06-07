package net.mirai.dimtr.client.gui.window;

import net.mirai.dimtr.client.ClientProgressionData;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.ArrayList;
import java.util.List;

public class Phase2GoalKillsWindow extends ProgressionWindow {

    @Override
    public String getTitle() {
        return "Fase 2: Goal Kills + Reset";
    }

    @Override
    public List<Component> getContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.literal("❌ Complete a Fase 1 primeiro").withStyle(ChatFormatting.RED));
            return content;
        }

        if (!progress.isServerEnableMobKillsPhase2()) {
            content.add(Component.literal("Goal Kills desabilitados").withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Mobs do Nether
        content.add(Component.literal("🔥 Mobs do Nether").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        addMobLine(content, progress, "🔥 Blazes", "blaze", 2);
        addMobLine(content, progress, "💀 Wither Skeletons", "wither_skeleton", 2);
        addMobLine(content, progress, "🐷 Piglin Brutes", "piglin_brute", 2);
        addMobLine(content, progress, "🐗 Hoglins", "hoglin", 2);
        addMobLine(content, progress, "👻 Ghasts", "ghast", 2);

        // Repetir Overworld (125%)
        content.add(Component.empty());
        content.add(Component.literal("🔄 Reset Overworld (125%)").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        addMobLine(content, progress, "🧟 Zumbis", "zombie", 2);
        addMobLine(content, progress, "💀 Esqueletos", "skeleton", 2);
        addMobLine(content, progress, "🐗 Ravagers", "ravager", 2); // 125% do valor da Fase 1
        addMobLine(content, progress, "🔮 Evokers", "evoker", 2); // 125% do valor da Fase 1

        return content;
    }

    private void addMobLine(List<Component> content, ClientProgressionData progress, String mobName, String mobType, int phase) {
        int current = progress.getMobKillCount(mobType);
        int required = progress.getMobKillRequirement(mobType, phase);

        if (required <= 0) return; // Não mostrar se não é necessário

        content.add(createMobCounterLine(mobName, current, required));
    }
}