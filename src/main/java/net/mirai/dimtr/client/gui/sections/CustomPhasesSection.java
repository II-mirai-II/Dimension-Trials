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
        // Always accessible if external mod integration is enabled and mods are loaded
        try {
            // Check if external mod integration mods are loaded
            boolean mowziesLoaded = net.neoforged.fml.ModList.get().isLoaded("mowziesmobs");
            boolean cataclysmLoaded = net.neoforged.fml.ModList.get().isLoaded("cataclysm");
            
            boolean hasExternalMods = mowziesLoaded || cataclysmLoaded;
            
            // Also check if any custom phases are loaded (fallback)
            boolean hasCustomPhases = !CustomRequirements.getAllCustomPhases().isEmpty();
            
            return hasExternalMods || hasCustomPhases;
        } catch (Exception e) {
            // Fallback - show if any custom phases exist
            return !CustomRequirements.getAllCustomPhases().isEmpty();
        }
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();
        
        // Check for external mods first
        boolean mowziesLoaded = net.neoforged.fml.ModList.get().isLoaded("mowziesmobs");
        boolean cataclysmLoaded = net.neoforged.fml.ModList.get().isLoaded("cataclysm");
        
        if (mowziesLoaded || cataclysmLoaded) {
            content.add(Component.literal("🎯 External Mod Bosses Detected").withStyle(ChatFormatting.GOLD));
            content.add(Component.literal(""));
            
            if (mowziesLoaded) {
                content.add(Component.literal("⚔️ Mowzie's Mobs Bosses:").withStyle(ChatFormatting.YELLOW));
                content.add(Component.literal("  • Ferrous Wroughtnaut (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal("  • Frostmaw (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal("  • Barako (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal("  • Umvuthi (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal("  • Naga (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal(""));
            }
            
            if (cataclysmLoaded) {
                content.add(Component.literal("🏛️ L_Ender's Cataclysm Bosses:").withStyle(ChatFormatting.YELLOW));
                content.add(Component.literal("  • Ancient Remnant (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal("  • The Leviathan (Phase 1)").withStyle(ChatFormatting.WHITE));
                content.add(Component.literal("  • Ignis (Phase 2)").withStyle(ChatFormatting.GOLD));
                content.add(Component.literal("  • Netherite Monstrosity (Phase 2)").withStyle(ChatFormatting.GOLD));
                content.add(Component.literal("  • The Harbinger (Phase 2)").withStyle(ChatFormatting.GOLD));
                content.add(Component.literal("  • Maledictus (Phase 2)").withStyle(ChatFormatting.GOLD));
                content.add(Component.literal("  • Ender Guardian (Phase 3)").withStyle(ChatFormatting.LIGHT_PURPLE));
                content.add(Component.literal("  • Ender Golem (Phase 3)").withStyle(ChatFormatting.LIGHT_PURPLE));
                content.add(Component.literal(""));
            }
            
            content.add(Component.literal("📋 Boss kills will count as special objectives").withStyle(ChatFormatting.GRAY));
            content.add(Component.literal("🎯 Phase 3 objectives unlock End access").withStyle(ChatFormatting.GRAY));
        }
        
        // Also show custom phases if any exist
        var customPhases = CustomRequirements.getAllCustomPhases();
        if (!customPhases.isEmpty()) {
            if (mowziesLoaded || cataclysmLoaded) {
                content.add(Component.literal(""));
                content.add(Component.literal("═══════════════════════").withStyle(ChatFormatting.DARK_GRAY));
                content.add(Component.literal(""));
            }
            
            content.add(Component.literal("🔧 Additional Custom Phases:").withStyle(ChatFormatting.AQUA));
            
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
            }
        }
        
        if (content.isEmpty()) {
            content.add(Component.translatable(Constants.GUI_CUSTOM_PHASES_NO_PHASES).withStyle(ChatFormatting.GRAY));
            content.add(Component.literal(""));
            content.add(Component.translatable(Constants.GUI_CUSTOM_PHASES_CONFIG_INFO)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        
        return content;
    }
}
