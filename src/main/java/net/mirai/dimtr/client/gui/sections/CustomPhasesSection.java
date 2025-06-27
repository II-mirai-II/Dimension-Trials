package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.config.CustomRequirements;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Seção para exibir progresso de fases customizadas
 */
public class CustomPhasesSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.CUSTOM_PHASES;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Constants.GUI_CUSTOM_PHASES_TITLE);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(Constants.GUI_CUSTOM_PHASES_DESCRIPTION);
    }

    @Override
    public String getIcon() {
        return "🔧";
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        // Accessible if any custom phases are loaded
        return !CustomRequirements.getAllCustomPhases().isEmpty();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();
        
        var customPhases = CustomRequirements.getAllCustomPhases();
        if (customPhases.isEmpty()) {
            content.add(Component.translatable(Constants.GUI_CUSTOM_PHASES_NO_PHASES).withStyle(ChatFormatting.GRAY));
            content.add(Component.literal(""));
            content.add(Component.translatable(Constants.GUI_CUSTOM_PHASES_CONFIG_INFO)
                    .withStyle(ChatFormatting.DARK_GRAY));
            content.add(Component.translatable(Constants.GUI_CUSTOM_PHASES_CONFIG_PATH)
                    .withStyle(ChatFormatting.DARK_GRAY));
            return content;
        }
        
        for (Map.Entry<String, CustomRequirements.CustomPhase> entry : customPhases.entrySet()) {
            String phaseId = entry.getKey();
            var phase = entry.getValue();
            
            boolean isCompleted = progress.isCustomPhaseComplete(phaseId);
            ChatFormatting phaseColor = isCompleted ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
            String status = isCompleted ? " ✓" : " ⏳";
            
            content.add(Component.literal(phase.name + status).withStyle(phaseColor));
            
            if (phase.description != null && !phase.description.isEmpty()) {
                content.add(Component.literal("  " + phase.description).withStyle(ChatFormatting.GRAY));
            }
            
            // Add progress details
            if (phase.mobRequirements != null && !phase.mobRequirements.isEmpty()) {
                content.add(Component.translatable(Constants.GUI_CUSTOM_PHASES_MOB_REQUIREMENTS).withStyle(ChatFormatting.AQUA));
                for (Map.Entry<String, Integer> mobEntry : phase.mobRequirements.entrySet()) {
                    String mobType = mobEntry.getKey();
                    // 🎯 NOVO: Usar requisito ajustado por party
                    int required = progress.getCustomMobRequirementAdjusted(phaseId, mobType);
                    int current = progress.getCustomMobKills(phaseId, mobType);
                    
                    String progressText = String.format("    %s: %d/%d", mobType, current, required);
                    ChatFormatting mobColor = current >= required ? ChatFormatting.GREEN : ChatFormatting.WHITE;
                    content.add(Component.literal(progressText).withStyle(mobColor));
                }
            }
            
            if (phase.specialObjectives != null && !phase.specialObjectives.isEmpty()) {
                content.add(Component.literal("  Special Objectives:").withStyle(ChatFormatting.LIGHT_PURPLE));
                for (Map.Entry<String, CustomRequirements.CustomObjective> objEntry : phase.specialObjectives.entrySet()) {
                    String objectiveId = objEntry.getKey();
                    var objective = objEntry.getValue();
                    
                    boolean objCompleted = progress.isCustomObjectiveComplete(phaseId, objectiveId);
                    String objStatus = objCompleted ? "✓" : "✗";
                    ChatFormatting objColor = objCompleted ? ChatFormatting.GREEN : ChatFormatting.RED;
                    
                    content.add(Component.literal("    " + objStatus + " " + objective.displayName).withStyle(objColor));
                }
            }
            
            content.add(Component.literal("")); // Empty line between phases
        }
        
        return content;
    }
}
