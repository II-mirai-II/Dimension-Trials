package net.mirai.dimtr.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
// import net.minecraft.client.gui.components.Button; // Não é mais necessário se PageButton foi removido e não há outros Buttons diretos
import net.minecraft.client.gui.components.Renderable; // Para o loop de renderables (se houver outros)
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW; // Para navegação por teclado

import java.util.ArrayList;
import java.util.List;

public class ProgressionBookScreen extends Screen {

    public static final ResourceLocation BOOK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/book.png");
    protected static final int BOOK_IMAGE_WIDTH = 192;
    protected static final int BOOK_IMAGE_HEIGHT = 192;

    private final ItemStack bookStack;

    private List<List<FormattedCharSequence>> pages = new ArrayList<>();
    private int currentPageIndex = 0;
    private int totalPages = 0;

    // Variáveis para nextPageButton e previousPageButton foram REMOVIDAS
    // private PageButton nextPageButton;
    // private PageButton previousPageButton;

    private int guiLeft;
    private int guiTop;

    // --- SEÇÃO DE CONSTANTES PARA AJUSTE MANUAL DE LAYOUT E ESPAÇAMENTO ---
    // Edite os valores AQUI para ajustar o layout e espaçamento do livro.

    // 1. Área de Texto Principal (Corpo do Texto) - Use os valores que funcionaram para você
    private final int TEXT_AREA_TOP_Y_FROM_BOOK_TOP = 29;    // Y (pixels do topo da imagem do livro) onde o texto principal começa.
    private final int TEXT_AREA_BOTTOM_Y_FROM_BOOK_TOP = 160; // Y (pixels do topo do livro) onde a área de texto principal termina.
    private final int TEXT_AREA_LEFT_X_FROM_BOOK_LEFT = 39;   // X (pixels da esquerda do livro) onde o texto principal começa.
    private final int TEXT_AREA_RIGHT_X_FROM_BOOK_LEFT = 150;  // X (pixels da esquerda do livro) onde o texto principal termina.

    // 2. Setas de Navegação - Constantes de posicionamento não são mais usadas pois os botões foram removidos
    // private final int ARROW_BUTTON_Y_FROM_BOOK_TOP = 172;
    // private final int PREV_ARROW_BUTTON_X_FROM_BOOK_LEFT = 10;
    // private final int NEXT_ARROW_BUTTON_X_FROM_BOOK_LEFT = 192 - 10 - 23;

    // 3. Indicador de Número da Página ("Página X de Y")
    private final int PAGE_INDICATOR_Y_FROM_BOOK_TOP = 15;
    private final int PAGE_INDICATOR_X_ALIGNMENT_MODE = 2; // 0=Esq, 1=Centro, 2=Dir (da área de texto)
    private final int PAGE_INDICATOR_X_FINE_OFFSET = 0;

    // 4. Espaçamentos Verticais (Número de linhas em branco - "Enters")
    private final int LINES_AFTER_MAIN_TITLE = 1;
    private final int LINES_BETWEEN_INTRO_PARAGRAPHS = 0;
    private final int LINES_AFTER_ALL_INTRO_BLOCK = 1;
    private final int LINES_AFTER_PHASE_HEADER_BLOCK = 1;
    private final int LINES_AFTER_EACH_REQUIREMENT_BLOCK = 1;

    // 5. Separador dos Títulos de Fase
    private final String PHASE_TITLE_SEPARATOR_TEXT = "-----------";

    // 6. Fator de Escala para a Fonte do Conteúdo
    private final float CONTENT_FONT_SCALE_FACTOR = 0.9f;
    // --- FIM DA SEÇÃO DE CONSTANTES DE AJUSTE ---

    private int textAreaActualWidth;
    private int effectiveTextHeightPerPage;
    private int linesPerPage;
    private int textStartX_absolute;
    private int textStartY_absolute;
    private int textEndY_absolute;
    private Component pageIndicatorText;

    public ProgressionBookScreen(ItemStack bookStack) {
        super(Component.translatable(Constants.HUD_TITLE));
        this.bookStack = bookStack;
    }

    @Override
    protected void init() {
        super.init(); // Limpa renderables da superclasse
        this.guiLeft = (this.width - BOOK_IMAGE_WIDTH) / 2;
        this.guiTop = (this.height - BOOK_IMAGE_HEIGHT) / 2;

        this.textStartX_absolute = this.guiLeft + TEXT_AREA_LEFT_X_FROM_BOOK_LEFT;
        this.textAreaActualWidth = TEXT_AREA_RIGHT_X_FROM_BOOK_LEFT - TEXT_AREA_LEFT_X_FROM_BOOK_LEFT;
        this.textStartY_absolute = this.guiTop + TEXT_AREA_TOP_Y_FROM_BOOK_TOP;
        this.textEndY_absolute = this.guiTop + TEXT_AREA_BOTTOM_Y_FROM_BOOK_TOP;

        float scaledLineHeight = this.font.lineHeight * CONTENT_FONT_SCALE_FACTOR;
        if (scaledLineHeight <= 0) scaledLineHeight = this.font.lineHeight;
        this.effectiveTextHeightPerPage = this.textEndY_absolute - this.textStartY_absolute;
        this.linesPerPage = Math.max(1, (int)(this.effectiveTextHeightPerPage / scaledLineHeight));

        this.preparePageContents();

        // Botões de seta foram removidos, não são mais adicionados como addRenderableWidget

        updatePageIndicator();

        System.out.println("--- BookScreen Init ---");
        System.out.println("textAreaActualWidth: " + this.textAreaActualWidth + ", linesPerPage: " + this.linesPerPage);
        System.out.println("textStartX_absolute: " + this.textStartX_absolute + ", textStartY_absolute: " + this.textStartY_absolute);
        System.out.println("Scaled Line Height for calc: " + scaledLineHeight);
        System.out.println("Total Pages from init: " + this.totalPages);
        System.out.println("---------------------");
    }

    private void turnPage(boolean forward) {
        boolean pageTurned = false;
        if (forward) {
            if (this.currentPageIndex < this.totalPages - 1) {
                this.currentPageIndex++;
                pageTurned = true;
            }
        } else {
            if (this.currentPageIndex > 0) {
                this.currentPageIndex--;
                pageTurned = true;
            }
        }
        if (pageTurned && this.minecraft != null) {
            playPageTurnSound();
        }
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        if (this.totalPages > 0) {
            this.pageIndicatorText = Component.translatable("book.pageIndicator", this.currentPageIndex + 1, Math.max(this.totalPages, 1));
        } else {
            this.pageIndicatorText = Component.empty();
        }
    }

    private void playPageTurnSound() {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_E) {
            this.turnPage(true);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_Q) {
            this.turnPage(false);
            return true;
        }
        return false;
    }

    private void addBlankLines(List<Component> list, int count) {
        for (int i = 0; i < count; i++) {
            list.add(Component.literal(" "));
        }
    }

    private void preparePageContents() {
        this.pages.clear();
        ClientProgressionData progress = ClientProgressionData.INSTANCE;
        List<Component> allBookComponents = new ArrayList<>();

        allBookComponents.add(Component.translatable(Constants.BOOK_PAGE_TURN_INSTRUCTIONS).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        addBlankLines(allBookComponents, 1);

        allBookComponents.add(Component.translatable(Constants.BOOK_MAIN_TITLE)
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.UNDERLINE));
        addBlankLines(allBookComponents, LINES_AFTER_MAIN_TITLE);

        allBookComponents.add(Component.translatable(Constants.BOOK_INTRO_1)
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        addBlankLines(allBookComponents, LINES_BETWEEN_INTRO_PARAGRAPHS);

        allBookComponents.add(Component.translatable(Constants.BOOK_INTRO_2)
                .withStyle(ChatFormatting.DARK_GRAY));
        addBlankLines(allBookComponents, LINES_AFTER_ALL_INTRO_BLOCK);

        allBookComponents.add(Component.translatable(Constants.HUD_PHASE1_TITLE).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        allBookComponents.add(Component.literal(PHASE_TITLE_SEPARATOR_TEXT).withStyle(ChatFormatting.GOLD));
        addBlankLines(allBookComponents, LINES_AFTER_PHASE_HEADER_BLOCK);

        if (progress.isServerEnablePhase1()) {
            if (progress.isServerReqElderGuardian()) allBookComponents.addAll(formatGoal(Constants.HUD_ELDER_GUARDIAN, progress.isElderGuardianKilled(), Constants.HUD_TOOLTIP_ELDER_GUARDIAN));
            if (progress.isServerReqRaidAndRavager()) {
                allBookComponents.addAll(formatGoal(Constants.HUD_RAID_WON, progress.isRaidWon(), Constants.HUD_TOOLTIP_RAID_WON));
                allBookComponents.addAll(formatGoal(Constants.HUD_RAVAGER_KILLED, progress.isRavagerKilled(), Constants.HUD_TOOLTIP_RAVAGER_KILLED));
            }
            if (progress.isServerReqEvoker()) allBookComponents.addAll(formatGoal(Constants.HUD_EVOKER_KILLED, progress.isEvokerKilled(), Constants.HUD_TOOLTIP_EVOKER_KILLED));
            if (progress.isServerReqTrialVaultAdv()) allBookComponents.addAll(formatGoal(Constants.HUD_TRIAL_VAULTS, progress.isTrialVaultAdvancementEarned(), Constants.HUD_TOOLTIP_TRIAL_VAULTS));

            if (progress.isPhase1Completed()) {
                addBlankLines(allBookComponents, 1);
                allBookComponents.add(Component.translatable(Constants.MSG_PHASE1_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            }
        } else {
            allBookComponents.add(Component.translatable(Constants.HUD_MSG_PHASE_DISABLED).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        addBlankLines(allBookComponents, LINES_AFTER_ALL_INTRO_BLOCK);

        allBookComponents.add(Component.translatable(Constants.HUD_PHASE2_TITLE).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        allBookComponents.add(Component.literal(PHASE_TITLE_SEPARATOR_TEXT).withStyle(ChatFormatting.DARK_PURPLE));
        addBlankLines(allBookComponents, LINES_AFTER_PHASE_HEADER_BLOCK);

        if (!progress.isPhase1EffectivelyComplete()) {
            allBookComponents.add(Component.translatable(Constants.HUD_MSG_COMPLETE_PHASE1).withStyle(ChatFormatting.GRAY));
        } else if (progress.isServerEnablePhase2()) {
            if (progress.isServerReqWither()) allBookComponents.addAll(formatGoal(Constants.HUD_WITHER_KILLED, progress.isWitherKilled(), Constants.HUD_TOOLTIP_WITHER_KILLED));
            if (progress.isServerReqWarden()) allBookComponents.addAll(formatGoal(Constants.HUD_WARDEN_KILLED, progress.isWardenKilled(), Constants.HUD_TOOLTIP_WARDEN_KILLED));

            if (progress.isPhase2Completed()) {
                addBlankLines(allBookComponents, 1);
                allBookComponents.add(Component.translatable(Constants.MSG_PHASE2_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            }
        } else {
            allBookComponents.add(Component.translatable(Constants.HUD_MSG_PHASE_DISABLED).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        addBlankLines(allBookComponents, 1);

        paginateComponents(allBookComponents);
        System.out.println("Content Prepared: Total Pages = " + this.totalPages + ", Lines per page = " + this.linesPerPage);
    }

    private List<Component> formatGoal(String titleKey, boolean completed, String descriptionKey) {
        List<Component> goalComponents = new ArrayList<>();
        ChatFormatting statusFormat = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        Style titleStyle = Style.EMPTY.withColor(ChatFormatting.BLACK);
        Style descriptionStyle = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);

        MutableComponent statusIcon = Component.literal(completed ? "✔ " : "❌ ").withStyle(statusFormat);
        Component titleText = Component.translatable(titleKey).withStyle(titleStyle);
        goalComponents.add(statusIcon.append(titleText));

        Component descriptionText = Component.translatable(descriptionKey).withStyle(descriptionStyle);
        goalComponents.add(Component.literal("  ").append(descriptionText));
        addBlankLines(goalComponents, LINES_AFTER_EACH_REQUIREMENT_BLOCK);

        return goalComponents;
    }

    private void paginateComponents(List<Component> components) {
        this.pages.clear();
        List<FormattedCharSequence> currentSinglePageLines = new ArrayList<>();
        for (Component component : components) {
            List<FormattedCharSequence> wrappedLines = this.font.split(component, this.textAreaActualWidth);
            for (FormattedCharSequence line : wrappedLines) {
                if (currentSinglePageLines.size() >= this.linesPerPage) {
                    this.pages.add(new ArrayList<>(currentSinglePageLines));
                    currentSinglePageLines.clear();
                }
                currentSinglePageLines.add(line);
            }
        }
        if (!currentSinglePageLines.isEmpty()) {
            this.pages.add(currentSinglePageLines);
        }
        this.totalPages = this.pages.size();
        if (this.totalPages == 0) {
            this.pages.add(new ArrayList<>());
            this.totalPages = 1;
        }
        this.currentPageIndex = Math.min(this.currentPageIndex, Math.max(0, this.totalPages - 1));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.level != null) {
            guiGraphics.fillGradient(RenderType.guiOverlay(), 0, 0, this.width, this.height, 0x60000000, 0x70000000, 0);
        } else {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BOOK_TEXTURE, this.guiLeft, this.guiTop, 0, 0, BOOK_IMAGE_WIDTH, BOOK_IMAGE_HEIGHT);

        int contentRenderStartY = this.textStartY_absolute;

        if (this.pageIndicatorText != null && this.totalPages > 0) {
            int indicatorWidth = this.font.width(this.pageIndicatorText);
            int indicatorX;
            switch (PAGE_INDICATOR_X_ALIGNMENT_MODE) {
                case 0:
                    indicatorX = this.textStartX_absolute + PAGE_INDICATOR_X_FINE_OFFSET;
                    break;
                case 2:
                    indicatorX = this.textStartX_absolute + this.textAreaActualWidth - indicatorWidth - PAGE_INDICATOR_X_FINE_OFFSET;
                    break;
                case 1:
                default:
                    indicatorX = this.textStartX_absolute + (this.textAreaActualWidth / 2) - (indicatorWidth / 2) + PAGE_INDICATOR_X_FINE_OFFSET;
                    break;
            }
            int indicatorY = this.guiTop + PAGE_INDICATOR_Y_FROM_BOOK_TOP;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(indicatorX, indicatorY, 0);
            guiGraphics.pose().scale(CONTENT_FONT_SCALE_FACTOR, CONTENT_FONT_SCALE_FACTOR, 1.0f);
            guiGraphics.drawString(this.font, this.pageIndicatorText, 0, 0, ChatFormatting.BLACK.getColor(), false);
            guiGraphics.pose().popPose();

            if (indicatorY >= this.textStartY_absolute && indicatorY < this.textStartY_absolute + (this.font.lineHeight * CONTENT_FONT_SCALE_FACTOR) ) {
                contentRenderStartY = Math.max(contentRenderStartY, indicatorY + (int)(this.font.lineHeight * CONTENT_FONT_SCALE_FACTOR) + 2);
            }
        }

        if (this.totalPages > 0 && this.currentPageIndex < this.totalPages) {
            List<FormattedCharSequence> linesOfCurrentPage = this.pages.get(this.currentPageIndex);

            int currentLineY = contentRenderStartY;
            for (FormattedCharSequence line : linesOfCurrentPage) {
                if (currentLineY + (this.font.lineHeight * CONTENT_FONT_SCALE_FACTOR) -1 > this.textEndY_absolute) {
                    break;
                }
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(this.textStartX_absolute, currentLineY, 0);
                guiGraphics.pose().scale(CONTENT_FONT_SCALE_FACTOR, CONTENT_FONT_SCALE_FACTOR, 1.0f);
                guiGraphics.drawString(this.font, line, 0, 0, ChatFormatting.BLACK.getColor(), false);
                guiGraphics.pose().popPose();
                currentLineY += (int)(this.font.lineHeight * CONTENT_FONT_SCALE_FACTOR);
            }
        }

        // Renderiza outros 'Renderable' se houver (nenhum por padrão agora que os botões de seta foram removidos)
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // A classe interna PageButton foi removida ou comentada, pois não é mais usada.
}