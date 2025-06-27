package net.mirai.dimtr.util;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for creating informative tooltips with consistent formatting.
 * Provides standardized tooltip components for various UI elements.
 */
public class TooltipHelper {

    /**
     * Creates a tooltip for progression objectives.
     * @param objectiveName The name of the objective
     * @param description The description of what needs to be done
     * @param isCompleted Whether the objective is completed
     * @return List of tooltip components
     */
    public static List<Component> createObjectiveTooltip(String objectiveName, String description, boolean isCompleted) {
        List<Component> tooltip = new ArrayList<>();
        
        // Title with status indicator
        Component title = Component.literal((isCompleted ? Constants.ICON_COMPLETED : Constants.ICON_PENDING) + objectiveName)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
        tooltip.add(title);
        
        // Description
        tooltip.add(Component.literal(description).withStyle(ChatFormatting.GRAY));
        
        // Status
        String status = isCompleted ? 
                Component.translatable(Constants.TOOLTIP_STATUS_COMPLETED).getString() : 
                Component.translatable(Constants.TOOLTIP_STATUS_IN_PROGRESS).getString();
        tooltip.add(Component.literal(Constants.TOOLTIP_STATUS_PREFIX + status)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        
        return tooltip;
    }

    /**
     * Creates a tooltip for mob kill requirements.
     * @param mobName The name of the mob
     * @param current Current kill count
     * @param required Required kill count
     * @return List of tooltip components
     */
    public static List<Component> createMobKillTooltip(String mobName, int current, int required) {
        List<Component> tooltip = new ArrayList<>();
        
        boolean isCompleted = current >= required;
        
        // Title with mob name
        Component title = Component.literal((isCompleted ? Constants.ICON_COMPLETED : Constants.ICON_COMBAT) + mobName)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.WHITE);
        tooltip.add(title);
        
        // Progress
        String progress = current + "/" + required;
        Component progressComponent = Component.literal(Constants.TOOLTIP_PROGRESS_PREFIX + progress)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
        tooltip.add(progressComponent);
        
        // Progress bar
        int percentage = Math.min(100, (current * 100) / required);
        String progressBar = createProgressBar(percentage);
        tooltip.add(Component.literal(progressBar)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.AQUA));
        
        // Remaining count
        if (!isCompleted) {
            int remaining = required - current;
            tooltip.add(Component.literal(Constants.TOOLTIP_REMAINING_PREFIX + remaining)
                    .withStyle(ChatFormatting.RED));
        }
        
        return tooltip;
    }

    /**
     * Creates a tooltip for party information.
     * @param partyName The name of the party
     * @param memberCount Current member count
     * @param maxMembers Maximum members allowed
     * @param multiplier Current multiplier value
     * @param isPublic Whether the party is public
     * @return List of tooltip components
     */
    public static List<Component> createPartyTooltip(String partyName, int memberCount, int maxMembers, 
                                                    float multiplier, boolean isPublic) {
        List<Component> tooltip = new ArrayList<>();
        
        // Party name
        tooltip.add(Component.literal(Constants.ICON_PARTY + partyName)
                .withStyle(ChatFormatting.GOLD));
        
        // Member count
        tooltip.add(Component.literal(Constants.TOOLTIP_MEMBERS_PREFIX + memberCount + "/" + maxMembers)
                .withStyle(ChatFormatting.GRAY));
        
        // Type
        String type = isPublic ? 
                Component.translatable(Constants.TOOLTIP_TYPE_PUBLIC).getString() : 
                Component.translatable(Constants.TOOLTIP_TYPE_PRIVATE).getString();
        ChatFormatting typeColor = isPublic ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
        tooltip.add(Component.literal(Constants.TOOLTIP_TYPE_PREFIX + type).withStyle(typeColor));
        
        // Multiplier
        tooltip.add(Component.literal(Constants.TOOLTIP_MULTIPLIER_PREFIX + String.format("%.1fx", multiplier))
                .withStyle(ChatFormatting.AQUA));
        
        // Benefits
        tooltip.add(Component.literal("").withStyle(ChatFormatting.RESET));
        tooltip.add(Component.literal(Constants.TOOLTIP_BENEFITS_HEADER).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal(Constants.TOOLTIP_SHARED_PROGRESSION).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(Constants.TOOLTIP_SOCIAL_GAMEPLAY).withStyle(ChatFormatting.GRAY));
        
        return tooltip;
    }

    /**
     * Creates a tooltip for phase information.
     * @param phaseName The name of the phase
     * @param isCompleted Whether the phase is completed
     * @param objectivesCompleted Number of completed objectives
     * @param totalObjectives Total number of objectives
     * @return List of tooltip components
     */
    public static List<Component> createPhaseTooltip(String phaseName, boolean isCompleted, 
                                                    int objectivesCompleted, int totalObjectives) {
        List<Component> tooltip = new ArrayList<>();
        
        // Phase title
        Component title = Component.literal((isCompleted ? Constants.ICON_COMPLETED : Constants.ICON_TARGET) + phaseName)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.GOLD);
        tooltip.add(title);
        
        // Progress
        String progress = objectivesCompleted + "/" + totalObjectives;
        tooltip.add(Component.literal(Constants.TOOLTIP_PROGRESS_PREFIX + progress)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        
        // Progress bar
        int percentage = totalObjectives > 0 ? (objectivesCompleted * 100) / totalObjectives : 0;
        String progressBar = createProgressBar(percentage);
        tooltip.add(Component.literal(progressBar)
                .withStyle(isCompleted ? ChatFormatting.GREEN : ChatFormatting.AQUA));
        
        // Status
        String status = isCompleted ? 
                Component.translatable(Constants.TOOLTIP_PHASE_COMPLETE).getString() : 
                Component.translatable(Constants.TOOLTIP_STATUS_IN_PROGRESS).getString();
        ChatFormatting statusColor = isCompleted ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
        tooltip.add(Component.literal(status).withStyle(statusColor));
        
        return tooltip;
    }

    /**
     * Creates a tooltip for help commands.
     * @param command The command syntax
     * @param description The command description
     * @param example An example usage (optional)
     * @return List of tooltip components
     */
    public static List<Component> createCommandTooltip(String command, String description, String example) {
        List<Component> tooltip = new ArrayList<>();
        
        // Command
        tooltip.add(Component.literal(Constants.ICON_SETTINGS + command)
                .withStyle(ChatFormatting.GOLD));
        
        // Description
        tooltip.add(Component.literal(description)
                .withStyle(ChatFormatting.GRAY));
        
        // Example (if provided)
        if (example != null && !example.isEmpty()) {
            tooltip.add(Component.literal("").withStyle(ChatFormatting.RESET));
            tooltip.add(Component.literal(Constants.TOOLTIP_EXAMPLE_HEADER).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.literal(example).withStyle(ChatFormatting.AQUA));
        }
        
        return tooltip;
    }

    /**
     * Creates a visual progress bar string.
     * @param percentage Percentage complete (0-100)
     * @return Progress bar string
     */
    private static String createProgressBar(int percentage) {
        int totalBars = 20;
        int filledBars = (percentage * totalBars) / 100;
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("] ").append(percentage).append("%");
        
        return bar.toString();
    }

    /**
     * Creates a separator line for tooltips.
     * @return Separator component
     */
    public static Component createSeparator() {
        return Component.literal(Constants.TOOLTIP_SEPARATOR)
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    /**
     * Adds a footer with contextual help information.
     * @param tooltipList The tooltip list to add the footer to
     * @param helpText The help text to display
     */
    public static void addHelpFooter(List<Component> tooltipList, String helpText) {
        tooltipList.add(Component.literal("").withStyle(ChatFormatting.RESET));
        tooltipList.add(createSeparator());
        tooltipList.add(Component.literal(Constants.ICON_HELP + helpText)
                .withStyle(ChatFormatting.YELLOW));
    }
}
