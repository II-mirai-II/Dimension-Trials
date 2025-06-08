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

        // Mobs comuns - ATUALIZADO: Removendo Zombie Villager
        addMobLine(content, progress, "🧟 Zumbis", "zombie", 1);
        // REMOVIDO: addMobLine(content, progress, "🧟‍♀️ Aldeões Zumbi", "zombie_villager", 1);
        addMobLine(content, progress, "💀 Esqueletos", "skeleton", 1);
        addMobLine(content, progress, "🏹 Strays", "stray", 1);
        addMobLine(content, progress, "🏜️ Husks", "husk", 1);
        addMobLine(content, progress, "💥 Creepers", "creeper", 1);
        addMobLine(content, progress, "🕷 Aranhas", "spider", 1);
        addMobLine(content, progress, "🌊 Afogados", "drowned", 1);

        // Espaço para mobs especiais
        content.add(Component.empty());
        content.add(Component.literal("👑 Mobs Especiais").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        addMobLine(content, progress, "👹 Endermen", "enderman", 1);
        addMobLine(content, progress, "🧙 Bruxas", "witch", 1);
        addMobLine(content, progress, "🏹 Pillagers", "pillager", 1);
        // REMOVIDO: Captain da lista de goal kills (agora é conquista Voluntary Exile)
        addMobLine(content, progress, "⚔️ Vindicators", "vindicator", 1);
        addMobLine(content, progress, "🏹 Boggeds", "bogged", 1);
        addMobLine(content, progress, "💨 Breezes", "breeze", 1);

        // Goal Kills especiais
        content.add(Component.empty());
        content.add(Component.literal("🎯 Goal Kills").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));

        // NOVO: Ravager e Evoker como Goal Kills principais
        addMobLine(content, progress, "🐗 Ravagers", "ravager", 1);
        addMobLine(content, progress, "🔮 Evokers", "evoker", 1);

        return content;
    }

    private void addMobLine(List<Component> content, ClientProgressionData progress, String mobName, String mobType, int phase) {
        int current = progress.getMobKillCount(mobType);
        int required = progress.getMobKillRequirement(mobType, phase);

        if (required <= 0) return; // Não mostrar se não é necessário

        content.add(createMobCounterLine(mobName, current, required));
    }
}