package net.mirai.dimtr.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.client.ClientPartyData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Sistema de Gerenciamento de Componentes HUD
 * 
 * Este sistema permite:
 * ✅ Componentes independentes e reutilizáveis
 * ✅ Configuração flexível de layout
 * ✅ Personalização da aparência
 * ✅ Ativação/desativação individual de componentes
 */
public class HUDComponentManager {
    
    // Singleton
    private static HUDComponentManager INSTANCE;
    
    // Componentes registrados
    private final Map<String, HUDComponent> components = new HashMap<>();
    
    // Componentes ativos
    private final Set<String> activeComponents = new HashSet<>();
    
    // Última posição usada para componentes
    private int lastYPosition = 5;
    
    // Configurações de layout
    private int spacing = 2;
    private HUDPosition defaultPosition = HUDPosition.TOP_RIGHT;
    
    // Tamanho padrão dos componentes
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 20;
    
    // Estado de renderização
    private boolean isRendering = false;
    
    private HUDComponentManager() {
        registerDefaultComponents();
    }
    
    public static HUDComponentManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUDComponentManager();
        }
        return INSTANCE;
    }
    
    /**
     * Registrar componentes padrão
     */
    private void registerDefaultComponents() {
        // Componente de Progressão de Fase
        registerComponent(new HUDComponent(
            "phase_progress",
            "Progressão de Fase",
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT,
            HUDPosition.TOP_RIGHT,
            this::renderPhaseProgress,
            () -> true // Sempre visível, verificaremos internamente
        ));
        
        // Componente de Status de Party
        registerComponent(new HUDComponent(
            "party_status",
            "Status da Party",
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT * 2,
            HUDPosition.TOP_RIGHT,
            this::renderPartyStatus,
            () -> true // Sempre visível, verificaremos internamente
        ));
        
        // Componente de Progresso de Mobs
        registerComponent(new HUDComponent(
            "mob_kills",
            "Progresso de Mobs",
            DEFAULT_WIDTH, 
            DEFAULT_HEIGHT * 3,
            HUDPosition.TOP_RIGHT,
            this::renderMobProgress,
            () -> true // Sempre visível, verificaremos internamente
        ));
        
        // Componente de Objetivos Especiais
        registerComponent(new HUDComponent(
            "special_objectives",
            "Objetivos Especiais",
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT * 2,
            HUDPosition.TOP_RIGHT,
            this::renderSpecialObjectives,
            () -> true // Sempre visível, verificaremos internamente
        ));
        
        // Ativar componentes padrão
        activateComponent("phase_progress");
        activateComponent("party_status");
    }
    
    /**
     * Registrar um novo componente
     */
    public void registerComponent(HUDComponent component) {
        components.put(component.getId(), component);
    }
    
    /**
     * Ativar um componente
     */
    public void activateComponent(String componentId) {
        if (components.containsKey(componentId)) {
            activeComponents.add(componentId);
        }
    }
    
    /**
     * Desativar um componente
     */
    public void deactivateComponent(String componentId) {
        activeComponents.remove(componentId);
    }
    
    /**
     * Alternar ativação de um componente
     */
    public void toggleComponent(String componentId) {
        if (activeComponents.contains(componentId)) {
            deactivateComponent(componentId);
        } else {
            activateComponent(componentId);
        }
    }
    
    /**
     * Renderizar todos os componentes ativos
     */
    public void render(GuiGraphics graphics, float partialTick) {
        if (isRendering) return; // Evitar recursão
        isRendering = true;
        
        try {
            PoseStack poseStack = graphics.pose();
            Minecraft minecraft = Minecraft.getInstance();
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            
            // Resetar posição Y para novo frame
            lastYPosition = 5;
            
            // Renderizar cada componente ativo
            for (String componentId : activeComponents) {
                HUDComponent component = components.get(componentId);
                if (component != null && component.isVisible()) {
                    // Calcular posição baseada no posicionamento do componente
                    int x = calculateXPosition(component, screenWidth);
                    int y = calculateYPosition(component, screenHeight);
                    
                    poseStack.pushPose();
                    // Renderizar componente
                    component.render(graphics, x, y, partialTick);
                    poseStack.popPose();
                    
                    // Atualizar última posição Y
                    if (component.getPosition() == HUDPosition.TOP_RIGHT || 
                        component.getPosition() == HUDPosition.TOP_LEFT) {
                        lastYPosition += component.getHeight() + spacing;
                    }
                }
            }
        } finally {
            isRendering = false;
        }
    }
    
    /**
     * Calcular posição X para um componente
     */
    private int calculateXPosition(HUDComponent component, int screenWidth) {
        return switch (component.getPosition()) {
            case TOP_LEFT, BOTTOM_LEFT -> 5;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - component.getWidth() - 5;
            case CENTER_TOP, CENTER_BOTTOM -> (screenWidth - component.getWidth()) / 2;
        };
    }
    
    /**
     * Calcular posição Y para um componente
     */
    private int calculateYPosition(HUDComponent component, int screenHeight) {
        return switch (component.getPosition()) {
            case TOP_LEFT, TOP_RIGHT -> lastYPosition;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - component.getHeight() - 5;
            case CENTER_TOP -> 5;
            case CENTER_BOTTOM -> screenHeight - component.getHeight() - 5;
        };
    }
    
    /**
     * Renderizar componente de progresso de fase
     */
    private void renderPhaseProgress(GuiGraphics graphics, int x, int y, float partialTick) {
        PoseStack poseStack = graphics.pose();
        ClientProgressionData data = ClientProgressionData.INSTANCE;
        
        // Verificar se há progresso para exibir
        if (!hasAnyProgress(data)) {
            return;
        }
        
        // Fundo semi-transparente
        graphics.fill(x, y, x + DEFAULT_WIDTH, y + DEFAULT_HEIGHT, 0x80000000);
        
        // Texto de status das fases
        String phase1Status = data.isPhase1Completed() ? 
                ChatFormatting.GREEN + "✓ Fase 1 Completa" : 
                ChatFormatting.RED + "✗ Fase 1 Incompleta";
        
        String phase2Status = data.isPhase2Completed() ? 
                ChatFormatting.GREEN + "✓ Fase 2 Completa" : 
                ChatFormatting.RED + "✗ Fase 2 Incompleta";
        
        graphics.drawString(Minecraft.getInstance().font, phase1Status, x + 5, y + 3, 0xFFFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, phase2Status, x + 5, y + 13, 0xFFFFFFFF);
    }
    
    /**
     * Renderizar componente de status de party
     */
    private void renderPartyStatus(GuiGraphics graphics, int x, int y, float partialTick) {
        PoseStack poseStack = graphics.pose();
        ClientPartyData data = ClientPartyData.INSTANCE;
        
        if (data.getPartyId() == null) {
            return;
        }
        
        // Fundo semi-transparente
        graphics.fill(x, y, x + DEFAULT_WIDTH, y + DEFAULT_HEIGHT * 2, 0x80000000);
        
        // Nome da party
        String partyName = ChatFormatting.GOLD + "Party: " + ChatFormatting.WHITE + data.getPartyName();
        graphics.drawString(Minecraft.getInstance().font, partyName, x + 5, y + 3, 0xFFFFFFFF);
        
        // Membros
        String members = ChatFormatting.AQUA + "Membros: " + ChatFormatting.WHITE + data.getMemberCount();
        graphics.drawString(Minecraft.getInstance().font, members, x + 5, y + 13, 0xFFFFFFFF);
        
        // Multiplicador
        String multiplier = ChatFormatting.YELLOW + "Mult.: " + 
                ChatFormatting.WHITE + String.format("%.1fx", data.getRequirementMultiplier());
        graphics.drawString(Minecraft.getInstance().font, multiplier, x + 5, y + 23, 0xFFFFFFFF);
        
        // Status das fases compartilhadas
        String phaseStatus = "";
        if (data.isPhase1SharedCompleted() && data.isPhase2SharedCompleted()) {
            phaseStatus = ChatFormatting.GREEN + "Todas Fases Completas";
        } else if (data.isPhase1SharedCompleted()) {
            phaseStatus = ChatFormatting.YELLOW + "Fase 1 Completa";
        } else {
            phaseStatus = ChatFormatting.RED + "Fases Incompletas";
        }
        graphics.drawString(Minecraft.getInstance().font, phaseStatus, x + 5, y + 33, 0xFFFFFFFF);
    }
    
    /**
     * Renderizar componente de progresso de mobs
     */
    private void renderMobProgress(GuiGraphics graphics, int x, int y, float partialTick) {
        PoseStack poseStack = graphics.pose();
        ClientProgressionData data = ClientProgressionData.getInstance();
        
        // Fundo semi-transparente
        graphics.fill(x, y, x + DEFAULT_WIDTH, y + DEFAULT_HEIGHT * 3, 0x80000000);
        
        // Título
        String title = ChatFormatting.YELLOW + "Progresso de Mobs:";
        graphics.drawString(Minecraft.getInstance().font, title, x + 5, y + 3, 0xFFFFFFFF);
        
        // Limitar para mostrar apenas os 5 mais relevantes
        List<Map.Entry<String, Integer>> mobEntries = new ArrayList<>(data.getMobKills().entrySet());
        mobEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        
        int yOffset = 15;
        int count = 0;
        
        for (Map.Entry<String, Integer> entry : mobEntries) {
            if (count >= 5) break;
            
            String mobName = formatMobName(entry.getKey());
            int kills = entry.getValue();
            
            String mobProgress = ChatFormatting.WHITE + mobName + ": " + 
                    ChatFormatting.GREEN + kills;
            
            graphics.drawString(Minecraft.getInstance().font, mobProgress, x + 5, y + yOffset, 0xFFFFFFFF);
            
            yOffset += 10;
            count++;
        }
    }
    
    /**
     * Renderizar componente de objetivos especiais
     */
    private void renderSpecialObjectives(GuiGraphics graphics, int x, int y, float partialTick) {
        PoseStack poseStack = graphics.pose();
        ClientProgressionData data = ClientProgressionData.getInstance();
        
        // Fundo semi-transparente
        graphics.fill(x, y, x + DEFAULT_WIDTH, y + DEFAULT_HEIGHT * 2, 0x80000000);
        
        // Título
        String title = ChatFormatting.GOLD + "Objetivos Especiais:";
        graphics.drawString(Minecraft.getInstance().font, title, x + 5, y + 3, 0xFFFFFFFF);
        
        // Objetivos Fase 1
        String elderGuardian = formatObjective("Elder Guardian", data.isElderGuardianKilled());
        String raid = formatObjective("Raid", data.isRaidWon());
        
        graphics.drawString(Minecraft.getInstance().font, elderGuardian, x + 5, y + 15, 0xFFFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, raid, x + 5, y + 25, 0xFFFFFFFF);
        
        // Objetivos Fase 2
        String wither = formatObjective("Wither", data.isWitherKilled());
        String warden = formatObjective("Warden", data.isWardenKilled());
        
        graphics.drawString(Minecraft.getInstance().font, wither, x + 5, y + 35, 0xFFFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, warden, x + 5, y + 45, 0xFFFFFFFF);
    }
    
    /**
     * Formatar nome do mob para exibição
     */
    private String formatMobName(String mobId) {
        return switch (mobId.toLowerCase()) {
            case "zombie" -> "Zumbi";
            case "skeleton" -> "Esqueleto";
            case "creeper" -> "Creeper";
            case "spider" -> "Aranha";
            case "enderman" -> "Enderman";
            case "blaze" -> "Blaze";
            case "wither_skeleton" -> "Esqueleto Wither";
            default -> capitalizeFirstLetter(mobId.replace("_", " "));
        };
    }
    
    /**
     * Formatar objetivo para exibição
     */
    private String formatObjective(String name, boolean completed) {
        return (completed ? ChatFormatting.GREEN + "✓ " : ChatFormatting.RED + "✗ ") + 
               ChatFormatting.WHITE + name;
    }
    
    /**
     * Capitalizar primeira letra
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    /**
     * Posições possíveis para componentes
     */
    public enum HUDPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER_TOP,
        CENTER_BOTTOM
    }
    
    /**
     * Classe que representa um componente do HUD
     */
    public static class HUDComponent {
        private final String id;
        private final String name;
        private final int width;
        private final int height;
        private final HUDPosition position;
        private final HUDRenderer renderer;
        private final Supplier<Boolean> visibilityCondition;
        
        public HUDComponent(String id, String name, int width, int height, 
                            HUDPosition position, HUDRenderer renderer,
                            Supplier<Boolean> visibilityCondition) {
            this.id = id;
            this.name = name;
            this.width = width;
            this.height = height;
            this.position = position;
            this.renderer = renderer;
            this.visibilityCondition = visibilityCondition;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public HUDPosition getPosition() {
            return position;
        }
        
        public boolean isVisible() {
            return visibilityCondition.get();
        }
        
        public void render(GuiGraphics graphics, int x, int y, float partialTick) {
            renderer.render(graphics, x, y, partialTick);
        }
    }
    
    /**
     * Interface para renderizadores de componentes
     */
    @FunctionalInterface
    public interface HUDRenderer {
        void render(GuiGraphics graphics, int x, int y, float partialTick);
    }
    
    // Métodos auxiliares
    private boolean hasAnyProgress(ClientProgressionData data) {
        return data.isPhase1Completed() || data.isPhase2Completed() || 
               data.isElderGuardianKilled() || data.isRaidWon() || 
               data.getZombieKills() > 0 || data.getSkeletonKills() > 0;
    }
    
    // Métodos para acessar dados de forma segura com getters/setters compatíveis com ClientPartyData
    private boolean isInParty(ClientPartyData data) {
        return data.getPartyId() != null;
    }
    
    private String getPartyName(ClientPartyData data) {
        return data.getPartyName();
    }
    
    private int getMemberCount(ClientPartyData data) {
        return data.getMemberCount();
    }
    
    private double getProgressionMultiplier(ClientPartyData data) {
        return data.getRequirementMultiplier();
    }
    
    private boolean isPartyPhase1Complete(ClientPartyData data) {
        return data.isPhase1SharedCompleted();
    }
    
    private boolean isPartyPhase2Complete(ClientPartyData data) {
        return data.isPhase2SharedCompleted();
    }
    
    // Métodos para acessar dados de forma segura com getters/setters compatíveis com ClientProgressionData
    private boolean isPhase1Complete(ClientProgressionData data) {
        return data.isPhase1Completed();
    }
    
    private boolean isPhase2Complete(ClientProgressionData data) {
        return data.isPhase2Completed();
    }
    
    private boolean isElderGuardianKilled(ClientProgressionData data) {
        return data.isElderGuardianKilled();
    }
    
    private boolean isRaidWon(ClientProgressionData data) {
        return data.isRaidWon();
    }
    
    private boolean isWitherKilled(ClientProgressionData data) {
        return data.isWitherKilled();
    }
    
    private boolean isWardenKilled(ClientProgressionData data) {
        return data.isWardenKilled();
    }
    
    private Map<String, Integer> getMobKillsMap(ClientProgressionData data) {
        Map<String, Integer> mobKills = new HashMap<>();
        mobKills.put("zombie", data.getZombieKills());
        mobKills.put("skeleton", data.getSkeletonKills());
        mobKills.put("creeper", data.getCreeperKills());
        mobKills.put("spider", data.getSpiderKills());
        mobKills.put("enderman", data.getEndermanKills());
        mobKills.put("witch", data.getWitchKills());
        mobKills.put("blaze", data.getBlazeKills());
        // Adicionar outros mobs conforme necessário
        return mobKills;
    }
}
