package net.mirai.dimtr.client.gui.screens;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ProgressionHUDScreen extends Screen {

    // Sistema de janelas
    private final List<WindowTab> windowTabs;
    private int activeWindowIndex = 0;

    // Constantes do HUD - otimizadas para duas colunas
    private static final int HUD_WIDTH = 400;  // Largura ajustada para duas colunas
    private static final int HUD_HEIGHT = 270; // Altura otimizada
    private static final int HUD_MARGIN = 15;   // Margem interna

    // Posições dos elementos
    private static final int TITLE_Y_OFFSET = 15;        // Título no topo
    private static final int TABS_Y_OFFSET = 40;         // Tabs abaixo do título
    private static final int CONTENT_Y_OFFSET = 85;      // Conteúdo principal
    private static final int INSTRUCTIONS_Y_OFFSET = -25; // Instruções na parte inferior

    // Sistema de colunas
    private static final int COLUMN_GAP = 20;              // Espaço entre colunas
    private static final int COLUMN_WIDTH = (HUD_WIDTH - (HUD_MARGIN * 2) - COLUMN_GAP) / 2;

    // Constantes das tabs
    private static final int TAB_HEIGHT = 25;
    private static final int TAB_MARGIN = 3;
    private static final int TAB_PADDING = 8;

    // Constantes do conteúdo
    private static final int LINE_HEIGHT = 10;
    private static final int MAX_LINES_PER_COLUMN = 15;   // Linhas por coluna
    private static final int MAX_TOTAL_LINES = MAX_LINES_PER_COLUMN * 2; // Total (2 colunas)

    // Sistema de páginas por janela
    private final int[] currentPagePerTab = new int[4]; // Uma página para cada tab

    // Cores
    private static final int WINDOW_BACKGROUND = 0xE0000000;
    private static final int WINDOW_BORDER = 0xFF555555;
    private static final int TITLE_COLOR = 0xFFFFD700;
    private static final int PAGE_INDICATOR_COLOR = 0xFFCCCCCC;

    public ProgressionHUDScreen() {
        super(Component.translatable(Constants.HUD_TITLE));
        this.windowTabs = initializeWindowTabs();
        // Inicializar todas as páginas em 0
        for (int i = 0; i < currentPagePerTab.length; i++) {
            currentPagePerTab[i] = 0;
        }
    }

    private List<WindowTab> initializeWindowTabs() {
        List<WindowTab> tabs = new ArrayList<>();

        tabs.add(new WindowTab("🎯 Fase 1", "Objetivos Especiais", this::generatePhase1MainContent));
        tabs.add(new WindowTab("⚔ Fase 1", "Eliminação de Mobs", this::generatePhase1MobsContent));
        tabs.add(new WindowTab("🌟 Fase 2", "Objetivos Especiais", this::generatePhase2MainContent));
        tabs.add(new WindowTab("🔥 Fase 2", "Mobs + Reset", this::generatePhase2MobsContent));

        return tabs;
    }

    @Override
    protected void init() {
        super.init();
        calculateWindowTabSizes();

        // CORREÇÃO: Som de abertura tocado após a inicialização completa
        playHudOpenSound();
    }

    private void calculateWindowTabSizes() {
        if (windowTabs.isEmpty()) return;

        // Calcular posições centralizadas na janela
        int hudStartX = (this.width - HUD_WIDTH) / 2;

        int totalTabsWidth = 0;
        for (WindowTab tab : windowTabs) {
            int tabWidth = this.font.width(tab.title) + (TAB_PADDING * 2);
            tab.width = Math.max(tabWidth, 80); // Largura mínima
            totalTabsWidth += tab.width;
        }

        totalTabsWidth += (windowTabs.size() - 1) * TAB_MARGIN;

        // Centralizar tabs dentro da janela
        int tabStartX = hudStartX + (HUD_WIDTH - totalTabsWidth) / 2;
        int currentX = tabStartX;

        for (WindowTab tab : windowTabs) {
            tab.x = currentX;
            tab.y = (this.height - HUD_HEIGHT) / 2 + TABS_Y_OFFSET;
            currentX += tab.width + TAB_MARGIN;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Calcular posição centralizada da janela principal
        int hudX = (this.width - HUD_WIDTH) / 2;
        int hudY = (this.height - HUD_HEIGHT) / 2;

        // Renderizar fundo da janela principal
        guiGraphics.fill(hudX, hudY, hudX + HUD_WIDTH, hudY + HUD_HEIGHT, WINDOW_BACKGROUND);

        // Renderizar borda da janela principal
        renderWindowBorder(guiGraphics, hudX, hudY, HUD_WIDTH, HUD_HEIGHT);

        // Título no topo da janela
        Component title = Component.translatable(Constants.HUD_TITLE).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int titleX = hudX + (HUD_WIDTH - this.font.width(title)) / 2;
        int titleY = hudY + TITLE_Y_OFFSET;
        guiGraphics.drawString(this.font, title, titleX, titleY, TITLE_COLOR);

        // Renderizar tabs
        renderWindowTabs(guiGraphics, mouseX, mouseY);

        // Renderizar conteúdo em colunas
        renderActiveWindowContentInColumns(guiGraphics, hudX, hudY);

        // Renderizar indicador de página se necessário
        renderPageIndicator(guiGraphics, hudX, hudY);

        // Renderizar instruções na parte inferior
        renderInstructions(guiGraphics, hudX, hudY);
    }

    private void renderWindowBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int borderThickness = 2;

        // Borda superior
        guiGraphics.fill(x, y, x + width, y + borderThickness, WINDOW_BORDER);
        // Borda inferior
        guiGraphics.fill(x, y + height - borderThickness, x + width, y + height, WINDOW_BORDER);
        // Borda esquerda
        guiGraphics.fill(x, y, x + borderThickness, y + height, WINDOW_BORDER);
        // Borda direita
        guiGraphics.fill(x + width - borderThickness, y, x + width, y + height, WINDOW_BORDER);
    }

    private void renderWindowTabs(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (int i = 0; i < windowTabs.size(); i++) {
            WindowTab tab = windowTabs.get(i);
            boolean isActive = (i == activeWindowIndex);
            boolean isHovered = isMouseOverTab(mouseX, mouseY, tab);

            // Cores das tabs
            int backgroundColor = isActive ? 0xFF4A4A4A : (isHovered ? 0xFF3A3A3A : 0xFF2A2A2A);
            int borderColor = isActive ? 0xFF00AA00 : (isHovered ? 0xFF666666 : 0xFF444444);
            int textColor = isActive ? 0xFFFFFFFF : 0xFFCCCCCC;

            // Renderizar fundo da tab
            guiGraphics.fill(tab.x, tab.y, tab.x + tab.width, tab.y + TAB_HEIGHT, backgroundColor);

            // Renderizar borda da tab
            renderTabBorder(guiGraphics, tab, borderColor, isActive);

            // Renderizar texto da tab (centralizado)
            int textX = tab.x + (tab.width - this.font.width(tab.title)) / 2;
            int textY = tab.y + (TAB_HEIGHT - this.font.lineHeight) / 2;
            guiGraphics.drawString(this.font, tab.title, textX, textY, textColor);
        }

        // Renderizar subtitle da tab ativa
        if (activeWindowIndex < windowTabs.size()) {
            WindowTab activeTab = windowTabs.get(activeWindowIndex);
            if (activeTab.subtitle != null && !activeTab.subtitle.isEmpty()) {
                Component subtitle = Component.literal(activeTab.subtitle).withStyle(ChatFormatting.GRAY);
                int hudX = (this.width - HUD_WIDTH) / 2;
                int subtitleX = hudX + (HUD_WIDTH - this.font.width(subtitle)) / 2;
                int subtitleY = activeTab.y + TAB_HEIGHT + 3;
                guiGraphics.drawString(this.font, subtitle, subtitleX, subtitleY, 0xFFAAAAAA);
            }
        }
    }

    private void renderTabBorder(GuiGraphics guiGraphics, WindowTab tab, int borderColor, boolean isActive) {
        int borderThickness = isActive ? 2 : 1;

        // Borda superior
        guiGraphics.fill(tab.x, tab.y, tab.x + tab.width, tab.y + borderThickness, borderColor);
        // Borda inferior (só se não for ativa)
        if (!isActive) {
            guiGraphics.fill(tab.x, tab.y + TAB_HEIGHT - borderThickness,
                    tab.x + tab.width, tab.y + TAB_HEIGHT, borderColor);
        }
        // Borda esquerda
        guiGraphics.fill(tab.x, tab.y, tab.x + borderThickness, tab.y + TAB_HEIGHT, borderColor);
        // Borda direita
        guiGraphics.fill(tab.x + tab.width - borderThickness, tab.y,
                tab.x + tab.width, tab.y + TAB_HEIGHT, borderColor);
    }

    private boolean isMouseOverTab(int mouseX, int mouseY, WindowTab tab) {
        return mouseX >= tab.x && mouseX <= tab.x + tab.width &&
                mouseY >= tab.y && mouseY <= tab.y + TAB_HEIGHT;
    }

    // Renderizar conteúdo em duas colunas
    private void renderActiveWindowContentInColumns(GuiGraphics guiGraphics, int hudX, int hudY) {
        if (activeWindowIndex >= windowTabs.size()) return;

        WindowTab activeTab = windowTabs.get(activeWindowIndex);
        ClientProgressionData progress = ClientProgressionData.INSTANCE;

        // Gerar todo o conteúdo
        List<Component> allContent = activeTab.contentGenerator.generate(progress);

        if (allContent.isEmpty()) {
            Component emptyMsg = Component.literal("Nenhum conteúdo disponível").withStyle(ChatFormatting.GRAY);
            int emptyX = hudX + (HUD_WIDTH - this.font.width(emptyMsg)) / 2;
            int emptyY = hudY + CONTENT_Y_OFFSET + 50;
            guiGraphics.drawString(this.font, emptyMsg, emptyX, emptyY, 0xFF888888);
            return;
        }

        // Calcular paginação
        int currentPage = currentPagePerTab[activeWindowIndex];
        int startIndex = currentPage * MAX_TOTAL_LINES;
        int endIndex = Math.min(startIndex + MAX_TOTAL_LINES, allContent.size());

        List<Component> pageContent = allContent.subList(startIndex, endIndex);

        // Dividir em duas colunas
        List<Component> leftColumn = new ArrayList<>();
        List<Component> rightColumn = new ArrayList<>();

        for (int i = 0; i < pageContent.size(); i++) {
            if (i < MAX_LINES_PER_COLUMN) {
                leftColumn.add(pageContent.get(i));
            } else {
                rightColumn.add(pageContent.get(i));
            }
        }

        // Renderizar coluna esquerda
        int leftColumnX = hudX + HUD_MARGIN;
        renderColumn(guiGraphics, leftColumn, leftColumnX, hudY + CONTENT_Y_OFFSET);

        // Renderizar coluna direita
        int rightColumnX = hudX + HUD_MARGIN + COLUMN_WIDTH + COLUMN_GAP;
        renderColumn(guiGraphics, rightColumn, rightColumnX, hudY + CONTENT_Y_OFFSET);

        // Renderizar linha divisória entre colunas
        int dividerX = hudX + HUD_MARGIN + COLUMN_WIDTH + (COLUMN_GAP / 2);
        int dividerStartY = hudY + CONTENT_Y_OFFSET - 5;
        int dividerEndY = hudY + HUD_HEIGHT - 50;
        guiGraphics.fill(dividerX, dividerStartY, dividerX + 1, dividerEndY, 0xFF444444);
    }

    private void renderColumn(GuiGraphics guiGraphics, List<Component> content, int startX, int startY) {
        int currentY = startY;

        for (int i = 0; i < content.size() && i < MAX_LINES_PER_COLUMN; i++) {
            Component line = content.get(i);

            // Verificar se é linha vazia (espaçamento)
            if (line.getString().trim().isEmpty()) {
                currentY += LINE_HEIGHT / 2;
                continue;
            }

            // Renderizar linha
            FormattedCharSequence formattedLine = line.getVisualOrderText();
            guiGraphics.drawString(this.font, formattedLine, startX, currentY, 0xFFFFFFFF);

            currentY += LINE_HEIGHT;
        }
    }

    // Renderizar indicador de página
    private void renderPageIndicator(GuiGraphics guiGraphics, int hudX, int hudY) {
        if (activeWindowIndex >= windowTabs.size()) return;

        WindowTab activeTab = windowTabs.get(activeWindowIndex);
        ClientProgressionData progress = ClientProgressionData.INSTANCE;
        List<Component> allContent = activeTab.contentGenerator.generate(progress);

        if (allContent.isEmpty()) return;

        int totalPages = (int) Math.ceil((double) allContent.size() / MAX_TOTAL_LINES);

        if (totalPages <= 1) return; // Não mostrar indicador se só há uma página

        int currentPage = currentPagePerTab[activeWindowIndex];
        Component pageIndicator = Component.literal("Página " + (currentPage + 1) + " de " + totalPages)
                .withStyle(ChatFormatting.GRAY);

        int indicatorX = hudX + HUD_WIDTH - HUD_MARGIN - this.font.width(pageIndicator);
        int indicatorY = hudY + HUD_HEIGHT - 45;

        guiGraphics.drawString(this.font, pageIndicator, indicatorX, indicatorY, PAGE_INDICATOR_COLOR);
    }

    private void renderInstructions(GuiGraphics guiGraphics, int hudX, int hudY) {
        List<Component> instructions = new ArrayList<>();

        // Verificar se há páginas para mostrar instruções de navegação
        boolean hasPages = false;
        if (activeWindowIndex < windowTabs.size()) {
            WindowTab activeTab = windowTabs.get(activeWindowIndex);
            ClientProgressionData progress = ClientProgressionData.INSTANCE;
            List<Component> allContent = activeTab.contentGenerator.generate(progress);
            int totalPages = (int) Math.ceil((double) allContent.size() / MAX_TOTAL_LINES);
            hasPages = totalPages > 1;
        }

        // ATUALIZADO: Instruções simplificadas - apenas clique e Q/E
        instructions.add(Component.literal("Clique nas abas para navegar entre janelas")
                .withStyle(ChatFormatting.YELLOW));

        if (hasPages) {
            instructions.add(Component.literal("Q/E para páginas • J/ESC para fechar")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            instructions.add(Component.literal("J/ESC para fechar")
                    .withStyle(ChatFormatting.GRAY));
        }

        // Renderizar instruções centralizadas na parte inferior da janela
        int instructionY = hudY + HUD_HEIGHT + INSTRUCTIONS_Y_OFFSET;
        for (int i = 0; i < instructions.size(); i++) {
            Component instruction = instructions.get(i);
            int instructionX = hudX + (HUD_WIDTH - this.font.width(instruction)) / 2;
            guiGraphics.drawString(this.font, instruction, instructionX, instructionY + (i * 10), 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Clique esquerdo
            // MANTIDO: Verificar clique nas tabs (única forma de trocar janelas)
            for (int i = 0; i < windowTabs.size(); i++) {
                WindowTab tab = windowTabs.get(i);
                if (isMouseOverTab((int)mouseX, (int)mouseY, tab)) {
                    // NOVO: Só trocar se não for a aba ativa
                    if (i != activeWindowIndex) {
                        activeWindowIndex = i;
                        // Resetar página ao trocar de janela
                        currentPagePerTab[activeWindowIndex] = 0;
                        // NOVO: Som de clique na aba
                        playTabClickSound();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_J) {
            // NOVO: Som de fechamento do HUD
            playHudCloseSound();
            this.onClose();
            return true;
        }

        // REMOVIDO: Navegação de janelas por teclado (←/→, teclas 1-4)
        // Janelas agora são acessíveis APENAS por clique do mouse

        // MANTIDO: Navegação de páginas apenas com Q/E
        if (keyCode == GLFW.GLFW_KEY_Q) {
            navigatePage(-1); // Página anterior
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_E) {
            navigatePage(1); // Próxima página
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Método para navegar entre páginas (apenas Q/E)
    private void navigatePage(int direction) {
        if (activeWindowIndex >= windowTabs.size()) return;

        WindowTab activeTab = windowTabs.get(activeWindowIndex);
        ClientProgressionData progress = ClientProgressionData.INSTANCE;
        List<Component> allContent = activeTab.contentGenerator.generate(progress);

        if (allContent.isEmpty()) return;

        int totalPages = (int) Math.ceil((double) allContent.size() / MAX_TOTAL_LINES);

        if (totalPages <= 1) return; // Não há páginas para navegar

        int currentPage = currentPagePerTab[activeWindowIndex];
        int newPage = currentPage + direction;

        // Limitar navegação aos limites válidos
        newPage = Math.max(0, Math.min(newPage, totalPages - 1));

        if (newPage != currentPage) {
            currentPagePerTab[activeWindowIndex] = newPage;
            playPageTurnSound();
        }
    }

    // ============================================================================
    // MÉTODOS DE SONS
    // ============================================================================

    // CORREÇÃO: Som de abertura do HUD
    private void playHudOpenSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.8F));
        }
    }

    // NOVO: Som de fechamento do HUD
    private void playHudCloseSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.6F));
        }
    }

    // NOVO: Som de clique nas abas
    private void playTabClickSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.UI_BUTTON_CLICK.value(), 0.8F, 1.3F));
        }
    }

    // Método para tocar som de virar página
    private void playPageTurnSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    // ============================================================================
    // GERADORES DE CONTEÚDO OTIMIZADOS PARA DUAS COLUNAS
    // ============================================================================

    private List<Component> generatePhase1MainContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        // Verificar se a Fase 1 está habilitada
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
        content.add(Component.literal("🎯 Objetivos Especiais").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        if (progress.isServerReqElderGuardian()) {
            content.add(createGoalLine("🌊 Elder Guardian", progress.isElderGuardianKilled()));
        }

        if (progress.isServerReqRaid()) {
            content.add(createGoalLine("🏆 Raid Vencido", progress.isRaidWon()));
        }

        if (progress.isServerReqTrialVaultAdv()) {
            content.add(createGoalLine("🔑 Trial Vault", progress.isTrialVaultAdvancementEarned()));
        }

        // Status de progresso geral
        content.add(Component.empty());
        if (progress.isPhase1Completed()) {
            content.add(Component.literal("✅ Nether liberado!").withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.literal("⏳ Complete os objetivos").withStyle(ChatFormatting.YELLOW));
            content.add(Component.literal("para liberar o Nether").withStyle(ChatFormatting.YELLOW));
        }

        // Adicionar informações de progresso de mobs se habilitado
        if (progress.isServerEnableMobKillsPhase1()) {
            content.add(Component.empty());
            content.add(Component.literal("⚔ Progresso de Eliminação:").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

            // Calcular progresso geral dos mobs
            int totalMobsCompleted = 0;
            int totalMobsRequired = 0;

            String[] mobTypes = {"zombie", "skeleton", "creeper", "spider", "enderman", "witch", "pillager", "ravager", "evoker"};
            for (String mobType : mobTypes) {
                int current = progress.getMobKillCount(mobType);
                int required = progress.getMobKillRequirement(mobType, 1);
                if (required > 0) {
                    totalMobsRequired++;
                    if (current >= required) totalMobsCompleted++;
                }
            }

            ChatFormatting progressColor = totalMobsCompleted == totalMobsRequired ?
                    ChatFormatting.GREEN : ChatFormatting.YELLOW;

            content.add(Component.literal("📊 " + totalMobsCompleted + "/" + totalMobsRequired + " tipos completos")
                    .withStyle(progressColor));
        }

        return content;
    }

    private List<Component> generatePhase1MobsContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isServerEnablePhase1()) {
            content.add(Component.literal("Fase 1 desabilitada").withStyle(ChatFormatting.GRAY));
            return content;
        }

        if (!progress.isServerEnableMobKillsPhase1()) {
            content.add(Component.literal("Eliminação de Mobs desabilitada").withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Mobs Comuns
        content.add(Component.literal("👥 Mobs Comuns").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, "🧟 Zumbis", "zombie", 1);
        // CORREÇÃO: Emojis limpos sem caracteres especiais
        addMobCounterLine(content, progress, "🧟 Zombie Villagers", "zombie_villager", 1);
        addMobCounterLine(content, progress, "💀 Esqueletos", "skeleton", 1);
        addMobCounterLine(content, progress, "🏹 Strays", "stray", 1);
        addMobCounterLine(content, progress, "🏜 Husks", "husk", 1);
        addMobCounterLine(content, progress, "🕷 Aranhas", "spider", 1);
        addMobCounterLine(content, progress, "💥 Creepers", "creeper", 1);
        addMobCounterLine(content, progress, "🌊 Drowneds", "drowned", 1);

        content.add(Component.empty());

        // Mobs Especiais
        content.add(Component.literal("⭐ Mobs Especiais").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, "👤 Endermen", "enderman", 1);
        // CORREÇÃO: Emojis limpos sem caracteres especiais
        addMobCounterLine(content, progress, "🧙 Bruxas", "witch", 1);
        addMobCounterLine(content, progress, "🏹 Pillagers", "pillager", 1);
        addMobCounterLine(content, progress, "🚩 Captains", "captain", 1);
        addMobCounterLine(content, progress, "⚔ Vindicators", "vindicator", 1);
        addMobCounterLine(content, progress, "🏹 Boggeds", "bogged", 1);
        addMobCounterLine(content, progress, "💨 Breezes", "breeze", 1);

        content.add(Component.empty());

        // Goal Kills
        content.add(Component.literal("🎯 Goal Kills").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, "🐗 Ravagers", "ravager", 1);
        addMobCounterLine(content, progress, "🔮 Evokers", "evoker", 1);

        // Resumo de progresso
        content.add(Component.empty());
        content.add(Component.literal("📈 Resumo:").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        // Calcular estatísticas
        int totalKills = 0;
        int totalRequired = 0;
        int completedTypes = 0;
        int totalTypes = 0;

        String[] allMobs = {"zombie", "zombie_villager", "skeleton", "stray", "husk", "spider",
                "creeper", "drowned", "enderman", "witch", "pillager", "captain",
                "vindicator", "bogged", "breeze", "ravager", "evoker"};

        for (String mobType : allMobs) {
            int current = progress.getMobKillCount(mobType);
            int required = progress.getMobKillRequirement(mobType, 1);
            if (required > 0) {
                totalKills += current;
                totalRequired += required;
                totalTypes++;
                if (current >= required) completedTypes++;
            }
        }

        ChatFormatting summaryColor = completedTypes == totalTypes ?
                ChatFormatting.GREEN : ChatFormatting.YELLOW;

        content.add(Component.literal("🔢 Total: " + totalKills + "/" + totalRequired + " kills")
                .withStyle(summaryColor));
        content.add(Component.literal("✅ Tipos: " + completedTypes + "/" + totalTypes + " completos")
                .withStyle(summaryColor));

        return content;
    }

    private List<Component> generatePhase2MainContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.literal("❌ Complete a Fase 1 primeiro").withStyle(ChatFormatting.RED));
            content.add(Component.empty());
            content.add(Component.literal("⚠ A Fase 2 só será").withStyle(ChatFormatting.GRAY));
            content.add(Component.literal("desbloqueada após").withStyle(ChatFormatting.GRAY));
            content.add(Component.literal("completar a Fase 1").withStyle(ChatFormatting.GRAY));
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
            content.add(Component.literal("✅ The End liberado!").withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.literal("⏳ Complete os objetivos").withStyle(ChatFormatting.YELLOW));
            content.add(Component.literal("para liberar o The End").withStyle(ChatFormatting.YELLOW));
        }

        // Informações sobre os desafios únicos
        content.add(Component.empty());
        content.add(Component.literal("⚡ Desafios Únicos:").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        content.add(Component.literal("• Enfrente o Wither").withStyle(ChatFormatting.GRAY));
        content.add(Component.literal("• Sobreviva ao Warden").withStyle(ChatFormatting.GRAY));
        content.add(Component.literal("• Conquiste o Nether").withStyle(ChatFormatting.GRAY));
        content.add(Component.literal("• Domine novos mobs").withStyle(ChatFormatting.GRAY));

        return content;
    }

    private List<Component> generatePhase2MobsContent(ClientProgressionData progress) {
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.literal("❌ Complete a Fase 1 primeiro").withStyle(ChatFormatting.RED));
            return content;
        }

        if (!progress.isServerEnableMobKillsPhase2()) {
            content.add(Component.literal("Eliminação de Mobs desabilitada").withStyle(ChatFormatting.GRAY));
            return content;
        }

        // Mobs do Nether
        content.add(Component.literal("🔥 Mobs do Nether").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, "🔥 Blazes", "blaze", 2);
        addMobCounterLine(content, progress, "💀 Wither Skeletons", "wither_skeleton", 2);
        addMobCounterLine(content, progress, "🐷 Piglin Brutes", "piglin_brute", 2);
        addMobCounterLine(content, progress, "🐗 Hoglins", "hoglin", 2);
        addMobCounterLine(content, progress, "💀 Zoglins", "zoglin", 2);
        addMobCounterLine(content, progress, "👻 Ghasts", "ghast", 2);
        addMobCounterLine(content, progress, "🐛 Endermites", "endermite", 2);
        addMobCounterLine(content, progress, "🐷 Piglins", "piglin", 2);

        content.add(Component.empty());

        // Reset Overworld (125%)
        content.add(Component.literal("🔄 Reset Overworld (+25%)").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        content.add(Component.literal("Requisitos aumentados:").withStyle(ChatFormatting.GRAY));
        addMobCounterLine(content, progress, "🧟 Zumbis", "zombie", 2);
        addMobCounterLine(content, progress, "💀 Esqueletos", "skeleton", 2);
        addMobCounterLine(content, progress, "💥 Creepers", "creeper", 2);
        addMobCounterLine(content, progress, "🕷 Aranhas", "spider", 2);
        addMobCounterLine(content, progress, "👤 Endermen", "enderman", 2);
        // CORREÇÃO: Emoji limpo sem caracteres especiais
        addMobCounterLine(content, progress, "🧙 Bruxas", "witch", 2);
        addMobCounterLine(content, progress, "🏹 Pillagers", "pillager", 2);

        content.add(Component.empty());

        // Goal Kills Reset (125%)
        content.add(Component.literal("🎯 Goal Kills (+25%)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        addMobCounterLine(content, progress, "🐗 Ravagers", "ravager", 2);
        addMobCounterLine(content, progress, "🔮 Evokers", "evoker", 2);

        // Estatísticas detalhadas
        content.add(Component.empty());
        content.add(Component.literal("📊 Estatísticas:").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        // Calcular progresso Nether vs Overworld
        int netherCompleted = 0;
        int netherTotal = 0;
        int overworldCompleted = 0;
        int overworldTotal = 0;

        String[] netherMobs = {"blaze", "wither_skeleton", "piglin_brute", "hoglin", "zoglin", "ghast", "endermite", "piglin"};
        String[] overworldMobs = {"zombie", "skeleton", "creeper", "spider", "enderman", "witch", "pillager", "ravager", "evoker"};

        for (String mobType : netherMobs) {
            int current = progress.getMobKillCount(mobType);
            int required = progress.getMobKillRequirement(mobType, 2);
            if (required > 0) {
                netherTotal++;
                if (current >= required) netherCompleted++;
            }
        }

        for (String mobType : overworldMobs) {
            int current = progress.getMobKillCount(mobType);
            int required = progress.getMobKillRequirement(mobType, 2);
            if (required > 0) {
                overworldTotal++;
                if (current >= required) overworldCompleted++;
            }
        }

        ChatFormatting netherColor = netherCompleted == netherTotal ? ChatFormatting.GREEN : ChatFormatting.RED;
        ChatFormatting overworldColor = overworldCompleted == overworldTotal ? ChatFormatting.GREEN : ChatFormatting.YELLOW;

        content.add(Component.literal("🔥 Nether: " + netherCompleted + "/" + netherTotal)
                .withStyle(netherColor));
        content.add(Component.literal("🌍 Overworld: " + overworldCompleted + "/" + overworldTotal)
                .withStyle(overworldColor));

        return content;
    }

    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================

    private Component createGoalLine(String text, boolean completed) {
        ChatFormatting statusColor = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        String statusIcon = completed ? "✔" : "❌";

        return Component.literal(statusIcon + " ").withStyle(statusColor)
                .append(Component.literal(text).withStyle(ChatFormatting.WHITE));
    }

    private Component createMobCounterLine(String text, String mobType, int current, int required) {
        boolean completed = current >= required;
        ChatFormatting countColor = completed ? ChatFormatting.GREEN :
                (current > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
        String statusIcon = completed ? "✔" : "⚔";

        return Component.literal(statusIcon + " ").withStyle(completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED)
                .append(Component.literal(text).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(current + "/" + required).withStyle(countColor));
    }

    private void addMobCounterLine(List<Component> contentList, ClientProgressionData progress,
                                   String text, String mobType, int phase) {
        int current = progress.getMobKillCount(mobType);
        int required = progress.getMobKillRequirement(mobType, phase);

        if (required <= 0) return; // Não mostrar se não é necessário

        contentList.add(createMobCounterLine(text, mobType, current, required));
    }

    // Classe interna para representar uma tab de janela
    private static class WindowTab {
        String title;
        String subtitle;
        ContentGenerator contentGenerator;
        int x, y, width;

        WindowTab(String title, String subtitle, ContentGenerator contentGenerator) {
            this.title = title;
            this.subtitle = subtitle;
            this.contentGenerator = contentGenerator;
        }
    }

    // Interface funcional para gerar conteúdo
    @FunctionalInterface
    private interface ContentGenerator {
        List<Component> generate(ClientProgressionData progress);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}