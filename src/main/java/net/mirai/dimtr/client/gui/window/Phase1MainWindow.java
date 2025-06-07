package net.mirai.dimtr.client.gui.window;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.ArrayList;
import java.util.List;

public class Phase1MainWindow extends ProgressionWindow {

    @Override
    public String getTitle() {
        return "Fase 1: Overworld → Nether";
    }

    @Override
    public List<Component> getContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isServerEnablePhase1()) {
            content.add(Component.literal("Fase 1 desabilitada").withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Status da fase
        if (progress.isPhase1Completed()) {
            content.add(Component.literal("✅ FASE COMPLETA!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }

        // Objetivos Especiais
        content.add(Component.literal("🎯 Objetivos Especiais").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        if (progress.isServerReqElderGuardian()) {
            content.add(createGoalLine("🛡 Guardião Ancião", progress.isElderGuardianKilled()));
        }

        if (progress.isServerReqRaid()) {
            content.add(createGoalLine("🏴 Vencer Invasão", progress.isRaidWon()));
        }

        if (progress.isServerReqTrialVaultAdv()) {
            content.add(createGoalLine("🗝 Trial Vault", progress.isTrialVaultAdvancementEarned()));
        }

        // Status de progresso geral
        content.add(Component.empty());
        if (progress.isPhase1Completed()) {
            content.add(Component.literal("Nether liberado para todos!").withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.literal("Complete todos os objetivos").withStyle(ChatFormatting.YELLOW));
            content.add(Component.literal("para liberar o Nether").withStyle(ChatFormatting.YELLOW));
        }

        return content;
    }
}