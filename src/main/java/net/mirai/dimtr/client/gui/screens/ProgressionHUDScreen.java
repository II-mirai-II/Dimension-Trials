package net.mirai.dimtr.client.gui.screens;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.client.gui.sections.HUDSection;
import net.mirai.dimtr.client.gui.sections.SectionManager;
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

/**
 * Tela principal do HUD de progressão - VERSÃO COM SCROLL VERTICAL
 *
 * ✅ Sistema modular de seções
 * ✅ Integração com PartySystem
 * ✅ Interface de navegação intuitiva
 * ✅ Suporte completo a todas as seções
 * 🎯 NOVO: Sistema de scroll vertical no sumário
 */
public class ProgressionHUDScreen extends Screen {

    // Estados de navegação
    private ViewState currentView = ViewState.SUMMARY;
    private HUDSection currentSection = null;

    // Constantes do HUD (mantidas)
    private static final int HUD_WIDTH = 525;
    private static final int HUD_HEIGHT = 300;
    private static final int HUD_MARGIN = 20;
    private static final int TITLE_Y_OFFSET = 10;
    private static final int CONTENT_Y_OFFSET = 45;
    private static final int INSTRUCTIONS_Y_OFFSET = -25;
    private static final int COLUMN_GAP = 20;
    private static final int COLUMN_WIDTH = (HUD_WIDTH - (HUD_MARGIN * 2) - COLUMN_GAP) / 2;
    private static final int LINE_HEIGHT = 12;
    private static final int MAX_LINES_PER_COLUMN = 15;
    private static final int MAX_TOTAL_LINES = MAX_LINES_PER_COLUMN * 2;

    // 🎯 NOVAS CONSTANTES PARA SCROLL
    private static final int SCROLL_AREA_TOP_OFFSET = 70;  // Espaço para título + subtítulo
    private static final int SCROLL_AREA_BOTTOM_OFFSET = 85; // Espaço para estatísticas + instruções
    private static final int SECTION_HEIGHT = 40;
    private static final int SECTION_SPACING = 10;
    private static final int SCROLL_SPEED = 20; // Pixels por scroll
    private static final int SCROLLBAR_WIDTH = 6;

    // Cores (mantidas + novas para scroll)
    private static final int WINDOW_BACKGROUND = 0xE0000000;
    private static final int WINDOW_BORDER = 0xFF444444;
    private static final int TITLE_COLOR = 0xFFFFD700;
    private static final int SECTION_CLICKABLE_COLOR = 0xFF87CEEB;
    private static final int SECTION_HOVER_COLOR = 0xFFFFD700;
    private static final int SECTION_LOCKED_COLOR = 0xFF666666;

    // 🎯 NOVAS CORES PARA SCROLL
    private static final int SCROLLBAR_TRACK_COLOR = 0xFF2A2A2A;
    private static final int SCROLLBAR_THUMB_COLOR = 0xFF5A5A5A;
    private static final int SCROLLBAR_THUMB_HOVER_COLOR = 0xFF7A7A7A;

    // Sistema de páginas (para seções específicas)
    private int currentPage = 0;

    // 🎯 NOVO: Sistema de scroll para sumário
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private boolean isScrollbarHovered = false;

    // Lista de seções do sumário (agora modular)
    private final List<SummarySection> summarySections;

    public ProgressionHUDScreen() {
        super(Component.translatable(Constants.HUD_TITLE));
        this.summarySections = initializeSummarySections();
    }

    // Enums para estados (mantidos)
    private enum ViewState {
        SUMMARY,    // Tela do sumário
        SECTION     // Seção específica
    }

    // Classe para representar uma seção no sumário (adaptada)
    private static class SummarySection {
        final HUDSection hudSection;
        int x, y, width, height;
        boolean clickable;
        boolean visible; // 🎯 NOVO: Controle de visibilidade

        SummarySection(HUDSection hudSection) {
            this.hudSection = hudSection;
            this.clickable = true;
            this.visible = true;
        }
    }

    /**
     * 🎯 MÉTODO REFATORADO: Inicializar seções usando SectionManager
     */
    private List<SummarySection> initializeSummarySections() {
        List<SummarySection> sections = new ArrayList<>();

        // Obter todas as seções do manager na ordem correta
        sections.add(new SummarySection(SectionManager.getSection(HUDSection.SectionType.PHASE1_MAIN)));
        sections.add(new SummarySection(SectionManager.getSection(HUDSection.SectionType.PHASE1_GOALS)));
        sections.add(new SummarySection(SectionManager.getSection(HUDSection.SectionType.PHASE2_MAIN)));
        sections.add(new SummarySection(SectionManager.getSection(HUDSection.SectionType.PHASE2_GOALS)));
        sections.add(new SummarySection(SectionManager.getSection(HUDSection.SectionType.PARTIES))); // 🎯 NOVA SEÇÃO

        return sections;
    }

    @Override
    protected void init() {
        super.init();
        calculateSummarySectionPositions();
        calculateScrollLimits(); // 🎯 NOVO
        playHudOpenSound();
    }

    /**
     * 🎯 MÉTODO COMPLETAMENTE REFATORADO: Calcular posições com scroll
     */
    private void calculateSummarySectionPositions() {
        int hudX = (this.width - HUD_WIDTH) / 2;
        int hudY = (this.height - HUD_HEIGHT) / 2;

        int sectionStartY = hudY + SCROLL_AREA_TOP_OFFSET;
        int sectionWidth = HUD_WIDTH - (HUD_MARGIN * 2) - SCROLLBAR_WIDTH - 5; // Espaço para scrollbar

        for (int i = 0; i < summarySections.size(); i++) {
            SummarySection section = summarySections.get(i);
            section.x = hudX + HUD_MARGIN;
            section.y = sectionStartY + (i * (SECTION_HEIGHT + SECTION_SPACING)) - scrollOffset; // 🎯 APLICAR SCROLL
            section.width = sectionWidth;
            section.height = SECTION_HEIGHT;

            // 🎯 NOVO: Calcular visibilidade baseada na posição
            int scrollAreaTop = hudY + SCROLL_AREA_TOP_OFFSET;
            int scrollAreaBottom = hudY + HUD_HEIGHT - SCROLL_AREA_BOTTOM_OFFSET;

            section.visible = (section.y + section.height >= scrollAreaTop) &&
                    (section.y <= scrollAreaBottom);
        }
    }

    /**
     * 🎯 NOVO: Calcular limites do scroll
     */
    private void calculateScrollLimits() {
        if (summarySections.isEmpty()) {
            maxScrollOffset = 0;
            return;
        }

        int totalContentHeight = summarySections.size() * (SECTION_HEIGHT + SECTION_SPACING) - SECTION_SPACING;
        int availableHeight = HUD_HEIGHT - SCROLL_AREA_TOP_OFFSET - SCROLL_AREA_BOTTOM_OFFSET;

        maxScrollOffset = Math.max(0, totalContentHeight - availableHeight);

        // Garantir que o scroll não ultrapasse os limites
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Calcular posição centralizada da janela principal
        int hudX = (this.width - HUD_WIDTH) / 2;
        int hudY = (this.height - HUD_HEIGHT) / 2;

        // Renderizar fundo da janela principal
        guiGraphics.fill(hudX, hudY, hudX + HUD_WIDTH, hudY + HUD_HEIGHT, WINDOW_BACKGROUND);
        renderWindowBorder(guiGraphics, hudX, hudY, HUD_WIDTH, HUD_HEIGHT);

        if (currentView == ViewState.SUMMARY) {
            renderSummaryView(guiGraphics, hudX, hudY, mouseX, mouseY);
        } else {
            renderSectionView(guiGraphics, hudX, hudY);
        }

        // Renderizar instruções
        renderInstructions(guiGraphics, hudX, hudY);
    }

    /**
     * 🎯 MÉTODO REFATORADO: Renderizar sumário com scroll
     */
    private void renderSummaryView(GuiGraphics guiGraphics, int hudX, int hudY, int mouseX, int mouseY) {
        // Título do sumário
        Component title = Component.translatable("gui.dimtr.summary.title")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int titleX = hudX + (HUD_WIDTH - this.font.width(title)) / 2;
        int titleY = hudY + TITLE_Y_OFFSET;
        guiGraphics.drawString(this.font, title, titleX, titleY, TITLE_COLOR);

        // Subtítulo
        Component subtitle = Component.translatable("gui.dimtr.summary.subtitle")
                .withStyle(ChatFormatting.GRAY);
        int subtitleX = hudX + (HUD_WIDTH - this.font.width(subtitle)) / 2;
        int subtitleY = hudY + TITLE_Y_OFFSET + 15;
        guiGraphics.drawString(this.font, subtitle, subtitleX, subtitleY, 0xFFAAAAAA);

        // 🎯 NOVO: Definir área de clipping para scroll
        int clipTop = hudY + SCROLL_AREA_TOP_OFFSET;
        int clipBottom = hudY + HUD_HEIGHT - SCROLL_AREA_BOTTOM_OFFSET;

        // Aplicar clipping
        guiGraphics.enableScissor(hudX, clipTop, hudX + HUD_WIDTH, clipBottom);

        // Renderizar seções usando o sistema modular (com scroll)
        ClientProgressionData progress = ClientProgressionData.INSTANCE;

        for (SummarySection section : summarySections) {
            if (!section.visible) continue; // 🎯 Só renderizar seções visíveis

            boolean isHovered = isMouseOverSection(mouseX, mouseY, section, clipTop, clipBottom);
            section.clickable = section.hudSection.isAccessible(progress);

            renderSummarySection(guiGraphics, section, isHovered);
        }

        // Desabilitar clipping
        guiGraphics.disableScissor();

        // 🎯 NOVO: Renderizar scrollbar se necessário
        if (maxScrollOffset > 0) {
            renderScrollbar(guiGraphics, hudX, hudY, mouseX, mouseY);
        }

        // Estatísticas gerais no final (sempre visível)
        renderGeneralStats(guiGraphics, hudX, hudY, progress);
    }

    /**
     * 🎯 NOVO: Renderizar scrollbar
     */
    private void renderScrollbar(GuiGraphics guiGraphics, int hudX, int hudY, int mouseX, int mouseY) {
        int scrollbarX = hudX + HUD_WIDTH - HUD_MARGIN - SCROLLBAR_WIDTH;
        int scrollbarY = hudY + SCROLL_AREA_TOP_OFFSET;
        int scrollbarHeight = HUD_HEIGHT - SCROLL_AREA_TOP_OFFSET - SCROLL_AREA_BOTTOM_OFFSET;

        // Track da scrollbar
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH,
                scrollbarY + scrollbarHeight, SCROLLBAR_TRACK_COLOR);

        // Calcular posição e tamanho do thumb
        float scrollPercentage = (float) scrollOffset / maxScrollOffset;
        int availableThumbArea = scrollbarHeight - 20; // Margem para o thumb
        int thumbY = scrollbarY + 10 + (int) (scrollPercentage * availableThumbArea);
        int thumbHeight = Math.max(20, availableThumbArea / 3); // Altura mínima e proporcional

        // Verificar se mouse está sobre a scrollbar
        isScrollbarHovered = mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                mouseY >= thumbY && mouseY <= thumbY + thumbHeight;

        int thumbColor = isScrollbarHovered ? SCROLLBAR_THUMB_HOVER_COLOR : SCROLLBAR_THUMB_COLOR;

        // Thumb da scrollbar
        guiGraphics.fill(scrollbarX + 1, thumbY, scrollbarX + SCROLLBAR_WIDTH - 1,
                thumbY + thumbHeight, thumbColor);

        // Indicadores de scroll
        if (scrollOffset > 0) {
            // Seta para cima
            Component upArrow = Component.literal("▲").withStyle(ChatFormatting.WHITE);
            int upArrowX = scrollbarX + (SCROLLBAR_WIDTH - this.font.width(upArrow)) / 2;
            guiGraphics.drawString(this.font, upArrow, upArrowX, scrollbarY - 15, 0xFFFFFFFF);
        }

        if (scrollOffset < maxScrollOffset) {
            // Seta para baixo
            Component downArrow = Component.literal("▼").withStyle(ChatFormatting.WHITE);
            int downArrowX = scrollbarX + (SCROLLBAR_WIDTH - this.font.width(downArrow)) / 2;
            guiGraphics.drawString(this.font, downArrow, downArrowX,
                    scrollbarY + scrollbarHeight + 5, 0xFFFFFFFF);
        }
    }

    /**
     * 🎯 MÉTODO ADAPTADO: Renderizar seção individual
     */
    private void renderSummarySection(GuiGraphics guiGraphics, SummarySection section, boolean isHovered) {
        // Determinar cores baseadas no estado
        int backgroundColor;
        int borderColor;
        int textColor;

        if (!section.clickable) {
            backgroundColor = 0xFF2A2A2A;
            borderColor = 0xFF444444;
            textColor = SECTION_LOCKED_COLOR;
        } else if (isHovered) {
            backgroundColor = 0xFF4A4A4A;
            borderColor = 0xFF666666;
            textColor = SECTION_HOVER_COLOR;
        } else {
            backgroundColor = 0xFF3A3A3A;
            borderColor = 0xFF555555;
            textColor = SECTION_CLICKABLE_COLOR;
        }

        // Renderizar fundo da seção
        guiGraphics.fill(section.x, section.y, section.x + section.width,
                section.y + section.height, backgroundColor);

        // Renderizar borda
        renderSectionBorder(guiGraphics, section, borderColor);

        // Renderizar título (usando dados da seção modular)
        int titleX = section.x + 10;
        int titleY = section.y + 8;
        guiGraphics.drawString(this.font, section.hudSection.getTitle(), titleX, titleY, textColor);

        // Renderizar descrição (usando dados da seção modular)
        int descX = section.x + 10;
        int descY = section.y + 22;
        guiGraphics.drawString(this.font, section.hudSection.getDescription(), descX, descY, 0xFFCCCCCC);

        // Indicador de acesso
        if (!section.clickable) {
            Component lockIcon = Component.literal("🔒").withStyle(ChatFormatting.RED);
            int lockX = section.x + section.width - 25;
            int lockY = section.y + (section.height - this.font.lineHeight) / 2;
            guiGraphics.drawString(this.font, lockIcon, lockX, lockY, 0xFFFF4444);
        } else {
            Component arrow = Component.literal("➤").withStyle(ChatFormatting.GREEN);
            int arrowX = section.x + section.width - 25;
            int arrowY = section.y + (section.height - this.font.lineHeight) / 2;
            guiGraphics.drawString(this.font, arrow, arrowX, arrowY, 0xFF44FF44);
        }
    }

    private void renderSectionBorder(GuiGraphics guiGraphics, SummarySection section, int borderColor) {
        int borderThickness = 1;
        // Borda superior
        guiGraphics.fill(section.x, section.y, section.x + section.width, section.y + borderThickness, borderColor);
        // Borda inferior
        guiGraphics.fill(section.x, section.y + section.height - borderThickness, section.x + section.width, section.y + section.height, borderColor);
        // Borda esquerda
        guiGraphics.fill(section.x, section.y, section.x + borderThickness, section.y + section.height, borderColor);
        // Borda direita
        guiGraphics.fill(section.x + section.width - borderThickness, section.y, section.x + section.width, section.y + section.height, borderColor);
    }

    private void renderGeneralStats(GuiGraphics guiGraphics, int hudX, int hudY, ClientProgressionData progress) {
        int statsY = hudY + HUD_HEIGHT - 70;

        // Título das estatísticas
        Component statsTitle = Component.translatable("gui.dimtr.summary.general_stats").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        int statsTitleX = hudX + HUD_MARGIN;
        guiGraphics.drawString(this.font, statsTitle, statsTitleX, statsY, 0xFF44FFFF);

        // Estatísticas
        List<Component> stats = new ArrayList<>();

        // Status das fases
        ChatFormatting phase1Color = progress.isPhase1Completed() ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
        ChatFormatting phase2Color = progress.isPhase2Completed() ? ChatFormatting.GREEN :
                (progress.isPhase1EffectivelyComplete() ? ChatFormatting.YELLOW : ChatFormatting.RED);

        stats.add(Component.translatable("gui.dimtr.summary.phase1_status",
                progress.isPhase1Completed() ? "✔ Completa" : "⚠ Incompleta").withStyle(phase1Color));
        stats.add(Component.translatable("gui.dimtr.summary.phase2_status",
                progress.isPhase2Completed() ? "✔ Completa" :
                        (progress.isPhase1EffectivelyComplete() ? "⚠ Incompleta" : "🔒 Bloqueada")).withStyle(phase2Color));

        // Renderizar estatísticas
        for (int i = 0; i < stats.size(); i++) {
            int statY = statsY + 15 + (i * 12);
            guiGraphics.drawString(this.font, stats.get(i), statsTitleX, statY, 0xFFFFFFFF);
        }
    }

    /**
     * 🎯 MÉTODO REFATORADO: Renderizar seção específica usando sistema modular
     */
    private void renderSectionView(GuiGraphics guiGraphics, int hudX, int hudY) {
        if (currentSection == null) return;

        // Título da seção (usando dados modulares)
        Component sectionTitle = currentSection.getTitle()
                .copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int titleX = hudX + (HUD_WIDTH - this.font.width(sectionTitle)) / 2;
        int titleY = hudY + TITLE_Y_OFFSET;
        guiGraphics.drawString(this.font, sectionTitle, titleX, titleY, TITLE_COLOR);

        // Renderizar conteúdo da seção em colunas
        renderSectionContentInColumns(guiGraphics, hudX, hudY);

        // Renderizar indicador de página se necessário
        renderPageIndicator(guiGraphics, hudX, hudY);

        // Botão de volta (visual)
        Component backButton = Component.translatable("gui.dimtr.back_to_summary")
                .withStyle(ChatFormatting.GRAY);
        int backX = hudX + HUD_MARGIN;
        int backY = hudY + HUD_HEIGHT - 35;
        guiGraphics.drawString(this.font, backButton, backX, backY, 0xFF888888);
    }

    /**
     * 🎯 MÉTODO REFATORADO: Renderizar conteúdo usando seção modular
     */
    private void renderSectionContentInColumns(GuiGraphics guiGraphics, int hudX, int hudY) {
        ClientProgressionData progress = ClientProgressionData.INSTANCE;

        // Usar o método generateContent da seção modular
        List<Component> allContent = currentSection.generateContent(progress);

        if (allContent.isEmpty()) {
            Component emptyMsg = Component.translatable("gui.dimtr.no.content")
                    .withStyle(ChatFormatting.GRAY);
            int emptyX = hudX + (HUD_WIDTH - this.font.width(emptyMsg)) / 2;
            int emptyY = hudY + CONTENT_Y_OFFSET + 50;
            guiGraphics.drawString(this.font, emptyMsg, emptyX, emptyY, 0xFF888888);
            return;
        }

        // Calcular paginação
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

        // Renderizar colunas
        int leftColumnX = hudX + HUD_MARGIN;
        int rightColumnX = hudX + HUD_MARGIN + COLUMN_WIDTH + COLUMN_GAP;

        renderColumn(guiGraphics, leftColumn, leftColumnX, hudY + CONTENT_Y_OFFSET);
        renderColumn(guiGraphics, rightColumn, rightColumnX, hudY + CONTENT_Y_OFFSET);

        // Linha divisória
        int dividerX = hudX + HUD_MARGIN + COLUMN_WIDTH + (COLUMN_GAP / 2);
        int dividerStartY = hudY + CONTENT_Y_OFFSET - 5;
        int dividerEndY = hudY + HUD_HEIGHT - 50;
        guiGraphics.fill(dividerX, dividerStartY, dividerX + 1, dividerEndY, 0xFF444444);
    }

    private void renderColumn(GuiGraphics guiGraphics, List<Component> content, int startX, int startY) {
        int currentY = startY;

        for (int i = 0; i < content.size() && i < MAX_LINES_PER_COLUMN; i++) {
            Component line = content.get(i);

            if (line.getString().trim().isEmpty()) {
                currentY += LINE_HEIGHT / 2;
                continue;
            }

            FormattedCharSequence formattedLine = line.getVisualOrderText();
            guiGraphics.drawString(this.font, formattedLine, startX, currentY, 0xFFFFFFFF);
            currentY += LINE_HEIGHT;
        }
    }

    private void renderPageIndicator(GuiGraphics guiGraphics, int hudX, int hudY) {
        ClientProgressionData progress = ClientProgressionData.INSTANCE;

        // 🎯 USAR SISTEMA MODULAR
        List<Component> allContent = currentSection.generateContent(progress);

        if (allContent.isEmpty()) return;

        int totalPages = (int) Math.ceil((double) allContent.size() / MAX_TOTAL_LINES);
        if (totalPages <= 1) return;

        Component pageIndicator = Component.translatable("gui.dimtr.page.indicator", currentPage + 1, totalPages)
                .withStyle(ChatFormatting.GRAY);

        int indicatorX = hudX + HUD_WIDTH - HUD_MARGIN - this.font.width(pageIndicator);
        int indicatorY = hudY + HUD_HEIGHT - 45;
        guiGraphics.drawString(this.font, pageIndicator, indicatorX, indicatorY, 0xFFFFFFFF);
    }

    private void renderWindowBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int borderThickness = 2;
        guiGraphics.fill(x, y, x + width, y + borderThickness, WINDOW_BORDER);
        guiGraphics.fill(x, y + height - borderThickness, x + width, y + height, WINDOW_BORDER);
        guiGraphics.fill(x, y, x + borderThickness, y + height, WINDOW_BORDER);
        guiGraphics.fill(x + width - borderThickness, y, x + width, y + height, WINDOW_BORDER);
    }

    private void renderInstructions(GuiGraphics guiGraphics, int hudX, int hudY) {
        List<Component> instructions = new ArrayList<>();

        if (currentView == ViewState.SUMMARY) {
            instructions.add(Component.translatable("gui.dimtr.summary.instructions.click"));
            instructions.add(Component.translatable("gui.dimtr.summary.instructions.close"));

            // 🎯 NOVO: Instruções de scroll se necessário
            if (maxScrollOffset > 0) {
                instructions.add(Component.literal("Use mouse wheel or ↑/↓ to scroll").withStyle(ChatFormatting.GRAY));
            }
        } else {
            // Verificar se há páginas
            ClientProgressionData progress = ClientProgressionData.INSTANCE;
            List<Component> allContent = currentSection.generateContent(progress);
            int totalPages = (int) Math.ceil((double) allContent.size() / MAX_TOTAL_LINES);

            if (totalPages > 1) {
                instructions.add(Component.translatable("gui.dimtr.section.instructions.navigate"));
            }
            instructions.add(Component.translatable("gui.dimtr.section.instructions.back"));
        }

        // Renderizar instruções
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
            if (currentView == ViewState.SUMMARY) {
                // Verificar clique nas seções do sumário
                int hudY = (this.height - HUD_HEIGHT) / 2;
                int clipTop = hudY + SCROLL_AREA_TOP_OFFSET;
                int clipBottom = hudY + HUD_HEIGHT - SCROLL_AREA_BOTTOM_OFFSET;

                for (SummarySection section : summarySections) {
                    if (section.clickable && section.visible &&
                            isMouseOverSection((int)mouseX, (int)mouseY, section, clipTop, clipBottom)) {
                        navigateToSection(section.hudSection);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 🎯 NOVO: Suporte a mouse wheel para scroll
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        if (currentView == ViewState.SUMMARY && maxScrollOffset > 0) {
            int previousOffset = scrollOffset;
            scrollOffset -= (int) (scrollDeltaY * SCROLL_SPEED);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));

            if (scrollOffset != previousOffset) {
                calculateSummarySectionPositions(); // Recalcular posições
                playScrollSound();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_J) {
            if (currentView == ViewState.SECTION) {
                // ESC volta ao sumário
                backToSummary();
                return true;
            } else {
                // ESC fecha o HUD
                playHudCloseSound();
                this.onClose();
                return true;
            }
        }

        // 🎯 NOVO: Navegação de scroll no sumário
        if (currentView == ViewState.SUMMARY && maxScrollOffset > 0) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                scrollUp();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                scrollDown();
                return true;
            }
        }

        // Navegação de páginas apenas quando em seção específica
        if (currentView == ViewState.SECTION) {
            if (keyCode == GLFW.GLFW_KEY_Q) {
                navigatePage(-1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_E) {
                navigatePage(1);
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * 🎯 NOVOS MÉTODOS DE SCROLL
     */
    private void scrollUp() {
        int previousOffset = scrollOffset;
        scrollOffset = Math.max(0, scrollOffset - SCROLL_SPEED);

        if (scrollOffset != previousOffset) {
            calculateSummarySectionPositions();
            playScrollSound();
        }
    }

    private void scrollDown() {
        int previousOffset = scrollOffset;
        scrollOffset = Math.min(maxScrollOffset, scrollOffset + SCROLL_SPEED);

        if (scrollOffset != previousOffset) {
            calculateSummarySectionPositions();
            playScrollSound();
        }
    }

    /**
     * 🎯 MÉTODO CORRETO: Navegar para seção usando sistema modular
     */
    private void navigateToSection(HUDSection section) {
        currentView = ViewState.SECTION;
        currentSection = section;
        currentPage = 0; // Resetar página
        playTabClickSound();
    }

    private void backToSummary() {
        currentView = ViewState.SUMMARY;
        currentSection = null;
        currentPage = 0;
        scrollOffset = 0; // 🎯 NOVO: Resetar scroll ao voltar
        calculateSummarySectionPositions();
        playPageTurnSound();
    }

    private void navigatePage(int direction) {
        if (currentSection == null) return;

        ClientProgressionData progress = ClientProgressionData.INSTANCE;
        List<Component> allContent = currentSection.generateContent(progress);

        if (allContent.isEmpty()) return;

        int totalPages = (int) Math.ceil((double) allContent.size() / MAX_TOTAL_LINES);
        if (totalPages <= 1) return;

        int newPage = currentPage + direction;
        newPage = Math.max(0, Math.min(newPage, totalPages - 1));

        if (newPage != currentPage) {
            currentPage = newPage;
            playPageTurnSound();
        }
    }

    /**
     * 🎯 MÉTODO ATUALIZADO: Verificar mouse over com clipping
     */
    private boolean isMouseOverSection(int mouseX, int mouseY, SummarySection section, int clipTop, int clipBottom) {
        return mouseX >= section.x && mouseX <= section.x + section.width &&
                mouseY >= Math.max(section.y, clipTop) && mouseY <= Math.min(section.y + section.height, clipBottom) &&
                mouseY >= section.y && mouseY <= section.y + section.height;
    }

    // ============================================================================
    // 🎯 MÉTODOS DE SONS (MANTIDOS + NOVO)
    // ============================================================================

    private void playHudOpenSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.8F));
        }
    }

    private void playHudCloseSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.6F));
        }
    }

    private void playTabClickSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.UI_BUTTON_CLICK.value(), 0.8F, 1.3F));
        }
    }

    private void playPageTurnSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    /**
     * 🎯 NOVO: Som de scroll
     */
    private void playScrollSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.8F));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}