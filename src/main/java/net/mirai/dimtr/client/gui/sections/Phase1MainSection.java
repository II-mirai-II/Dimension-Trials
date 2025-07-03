package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seção principal da Fase 1
 */
public class Phase1MainSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.PHASE1_MAIN;
    }

    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase1_main.desc");
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.isServerEnablePhase1();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        // 🎯 NOVO: Ensure client-side external mod integration is initialized
        net.mirai.dimtr.integration.ExternalModIntegration.initializeClientSide();
        
        List<Component> content = new ArrayList<>();

        if (!progress.isServerEnablePhase1()) {
            content.add(Component.translatable("gui.dimtr.phase1.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (progress.isPhase1Completed()) {
            content.add(Component.translatable("gui.dimtr.phase.complete")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }

        // Objetivos especiais
        content.add(Component.translatable(Constants.HUD_SECTION_SPECIAL_OBJECTIVES)
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        if (progress.isServerReqElderGuardian()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_ELDER_GUARDIAN),
                    progress.isElderGuardianKilled()));
        }

        if (progress.isServerReqRaid()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_RAID_WON),
                    progress.isRaidWon()));
        }

        if (progress.isServerReqTrialVaultAdv()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_TRIAL_VAULT_ADV),
                    progress.isTrialVaultAdvancementEarned()));
        }

        if (progress.isServerReqVoluntaryExile()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_VOLUNTAIRE_EXILE),
                    progress.isVoluntaireExileAdvancementEarned()));
        }

        content.add(Component.empty());

        // Status da fase
        if (progress.isPhase1Completed()) {
            content.add(Component.translatable("gui.dimtr.nether.unlocked")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.translatable("gui.dimtr.complete.objectives")
                    .withStyle(ChatFormatting.YELLOW));
            content.add(Component.translatable("gui.dimtr.unlock.nether")
                    .withStyle(ChatFormatting.YELLOW));
        }

        // Progresso de mobs se habilitado
        if (progress.isServerEnableMobKillsPhase1()) {
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.mob.progress")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

            int totalMobsCompleted = 0;
            int totalMobsRequired = 0;

            String[] mobTypes = {"zombie", "skeleton", "creeper", "spider",
                    "enderman", "witch", "pillager", "ravager", "evoker"};

            for (String mobType : mobTypes) {
                int current = progress.getMobKillCount(mobType);
                int required = progress.getMobKillRequirement(mobType, 1);
                if (required > 0) {
                    totalMobsRequired++;
                    if (current >= required) {
                        totalMobsCompleted++;
                    }
                }
            }

            if (totalMobsRequired > 0) {
                // Formatação manual para garantir que os valores apareçam corretamente
                content.add(Component.literal(totalMobsCompleted + "/" + totalMobsRequired + " Mobs Completed")
                        .withStyle(totalMobsCompleted == totalMobsRequired ?
                                ChatFormatting.GREEN : ChatFormatting.YELLOW));
            }
        }

        // 🎯 NOVO: Bosses de mods externos para Fase 1
        var externalBossesPhase1 = progress.getExternalBossesForPhase(1);
        if (!externalBossesPhase1.isEmpty()) {
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.external.bosses.phase1")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

            for (var boss : externalBossesPhase1) {
                boolean killed = progress.isExternalBossKilled(boss.entityId);
                content.add(createGoalLine(
                        Component.literal(boss.displayName),
                        killed));
            }
        }

        return content;
    }

    private Component createGoalLine(Component text, boolean completed) {
        ChatFormatting statusColor = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        String statusIcon = completed ? "✔" : "❌";

        return Component.literal(statusIcon + " ").withStyle(statusColor)
                .append(text.copy().withStyle(ChatFormatting.WHITE));
    }
}