package net.mirai.dimtr.client.gui.window;

import net.mirai.dimtr.client.ClientProgressionData;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.ArrayList;
import java.util.List;

public class Phase1GoalKillsWindow extends ProgressionWindow {

    @Override
    public String getTitle() {
        return "Fase 1: Eliminação de Mobs";
    }

    @Override
    public List<Component> getContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isServerEnableMobKillsPhase1()) {
            content.add(Component.literal("Goal Kills desabilitados").withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Status da seção
        content.add(Component.literal("⚔ Mobs do Overworld").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Mobs comuns
        addMobLine(content, progress, "🧟 Zumbis", "zombie", 1);
        addMobLine(content, progress, "💀 Esqueletos", "skeleton", 1);
        addMobLine(content, progress, "💥 Creepers", "creeper", 1);
        addMobLine(content, progress, "🕷 Aranhas", "spider", 1);
        addMobLine(content, progress, "🌊 Afogados", "drowned", 1);

        // Espaço para mobs especiais
        content.add(Component.empty());
        content.add(Component.literal("👑 Mobs Especiais").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        // Ravager e Evoker como Goal Kills
        addMobLine(content, progress, "🐗 Ravagers", "ravager", 1);
        addMobLine(content, progress, "🔮 Evokers", "evoker", 1);

        // Outros mobs especiais
        addMobLine(content, progress, "👹 Endermen", "enderman", 1);
        addMobLine(content, progress, "🧙 Bruxas", "witch", 1);

        return content;
    }

    private void addMobLine(List<Component> content, ClientProgressionData progress, String mobName, String mobType, int phase) {
        int current = progress.getMobKillCount(mobType);
        int required = progress.getMobKillRequirement(mobType, phase);

        if (required <= 0) return; // Não mostrar se não é necessário

        content.add(createMobCounterLine(mobName, current, required));
    }
}