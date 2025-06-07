package net.mirai.dimtr.client.gui.window;

import net.mirai.dimtr.client.ClientProgressionData;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.ArrayList;
import java.util.List;

public class Phase2MainWindow extends ProgressionWindow {

    @Override
    public String getTitle() {
        return "Fase 2: Nether → End";
    }

    @Override
    public List<Component> getContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.literal("❌ Complete a Fase 1 primeiro").withStyle(ChatFormatting.RED));
            return content;
        }

        if (!progress.isServerEnablePhase2()) {
            content.add(Component.literal("Fase 2 desabilitada").withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Status da fase
        if (progress.isPhase2Completed()) {
            content.add(Component.literal("✅ FASE COMPLETA!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }

        // Objetivos Especiais
        content.add(Component.literal("🎯 Objetivos Especiais").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        if (progress.isServerReqWither()) {
            content.add(createGoalLine("💀 Wither", progress.isWitherKilled()));
        }

        if (progress.isServerReqWarden()) {
            content.add(createGoalLine("🌑 Warden", progress.isWardenKilled()));
        }

        // Status de progresso geral
        content.add(Component.empty());
        if (progress.isPhase2Completed()) {
            content.add(Component.literal("The End liberado para todos!").withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.literal("Complete todos os objetivos").withStyle(ChatFormatting.YELLOW));
            content.add(Component.literal("para liberar o The End").withStyle(ChatFormatting.YELLOW));
        }

        return content;
    }
}