package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.data.CustomPhaseCoordinator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Seção para exibir informações sobre fases customizadas
 */
public class CustomPhasesSection implements HUDSection {

    @Override
    public SectionType getType() {
        return SectionType.CUSTOM_PHASES;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.dimtr.custom_phases.title")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.custom_phases.description")
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        // Sempre acessível se existirem fases customizadas configuradas
        return progress.hasCustomPhases();
    }

    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        // Adicionar título principal
        content.add(Component.literal("✧ ").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable("gui.dimtr.custom_phases.header")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)));
        content.add(Component.literal(""));

        // Verificar se existem fases customizadas
        if (!progress.hasCustomPhases()) {
            content.add(Component.translatable("gui.dimtr.custom_phases.none_configured")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return content;
        }

        // Adicionar informações sobre cada fase customizada
        Map<String, Boolean> customPhases = progress.getCustomPhases();
        
        content.add(Component.translatable("gui.dimtr.custom_phases.status_header")
                .withStyle(ChatFormatting.YELLOW));
        content.add(Component.literal(""));

        // Listar as fases customizadas e seus status
        for (Map.Entry<String, Boolean> entry : customPhases.entrySet()) {
            String phaseId = entry.getKey();
            boolean completed = entry.getValue();

            // Obter nome da fase através do CustomPhaseCoordinator se possível
            String phaseName = phaseId;
            
            // Criar componente para exibir status
            MutableComponent phaseComponent = Component.literal("• ")
                    .withStyle(completed ? ChatFormatting.GREEN : ChatFormatting.RED);
            
            phaseComponent.append(Component.literal(phaseName)
                    .withStyle(ChatFormatting.WHITE));
            
            // Adicionar status
            phaseComponent.append(Component.literal(" - ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(completed ? 
                            "gui.dimtr.custom_phases.completed" : 
                            "gui.dimtr.custom_phases.pending")
                            .withStyle(completed ? ChatFormatting.GREEN : ChatFormatting.RED)));
            
            content.add(phaseComponent);
        }

        return content;
    }
} 