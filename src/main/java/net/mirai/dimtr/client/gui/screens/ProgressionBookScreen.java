package net.mirai.dimtr.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.client.Minecraft;
// Removido Font já que this.font é herdado de Screen
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class ProgressionBookScreen extends Screen {

    public static final ResourceLocation BOOK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/book.png");
    protected static final int BOOK_IMAGE_WIDTH = 192; // Largura da imagem do livro (ainda útil para centralizar)
    protected static final int BOOK_IMAGE_HEIGHT = 192; // Altura da imagem do livro

    private final ItemStack bookStack;

    private List<List<FormattedCharSequence>> pages = new ArrayList<>(); // Alterado de pageSpreads
    private int currentPageIndex = 0; // Alterado de currentPageSpreadIndex
    private int totalPages = 0;       // Alterado de totalPageSpreads

    private Button nextPageButton;
    private Button previousPageButton;
    private Button doneButton;

    private int guiLeft; // X da borda esquerda da GUI do livro
    private int guiTop;  // Y da borda superior da GUI do livro

    // Constantes de Layout para UMA PÁGINA
    private final int PAGE_CONTENT_TOP_MARGIN = 28;    // Margem do topo da textura do livro até o início do texto
    private final int PAGE_CONTENT_BOTTOM_MARGIN = 32; // Margem da base da textura do livro até o fim do texto (para botões etc)
    private final int STANDARD_TEXT_WIDTH = 116;       // Largura padrão para texto em livros vanilla

    private int textAreaActualWidth;            // Largura efetiva para texto (será STANDARD_TEXT_WIDTH)
    private int effectiveTextHeightPerPage;     // Altura efetiva para texto
    private int linesPerPage;                   // Linhas que cabem em uma página
    private int textStartX;                     // X para iniciar o texto (centralizado)


    public ProgressionBookScreen(ItemStack bookStack) {
        super(Component.translatable("gui.dimtr.progression_book.title"));
        this.bookStack = bookStack;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - BOOK_IMAGE_WIDTH) / 2;
        this.guiTop = (this.height - BOOK_IMAGE_HEIGHT) / 2;

        this.textAreaActualWidth = STANDARD_TEXT_WIDTH;
        // O X inicial para o texto ser centralizado dentro da largura da imagem do livro
        this.textStartX = this.guiLeft + (BOOK_IMAGE_WIDTH - this.textAreaActualWidth) / 2;

        this.effectiveTextHeightPerPage = BOOK_IMAGE_HEIGHT - PAGE_CONTENT_TOP_MARGIN - PAGE_CONTENT_BOTTOM_MARGIN;
        this.linesPerPage = Math.max(1, this.effectiveTextHeightPerPage / this.font.lineHeight);

        this.preparePageContents(); // Prepara o conteúdo e o pagina

        // Botão "Feito"
        this.doneButton = Button.builder(Component.translatable("gui.done"), (button) -> {
            this.minecraft.setScreen(null);
        }).bounds(this.width / 2 - 50, this.guiTop + BOOK_IMAGE_HEIGHT - PAGE_CONTENT_BOTTOM_MARGIN + 8, 100, 20).build();
        this.addRenderableWidget(this.doneButton);

        // Botões de Navegação de Página
        int navButtonY = this.guiTop + BOOK_IMAGE_HEIGHT - PAGE_CONTENT_BOTTOM_MARGIN + (PAGE_CONTENT_BOTTOM_MARGIN - 13 - 8) / 2 +2;
        int navButtonXEdgeOffset = 28; // Um pouco mais para dentro para uma página única
        this.previousPageButton = new PageButton(
                this.guiLeft + navButtonXEdgeOffset, navButtonY, false,
                (button) -> turnPage(false), true);
        this.addRenderableWidget(this.previousPageButton);

        this.nextPageButton = new PageButton(
                this.guiLeft + BOOK_IMAGE_WIDTH - navButtonXEdgeOffset - 23, navButtonY, true, // 23 é a largura do botão
                (button) -> turnPage(true), true);
        this.addRenderableWidget(this.nextPageButton);

        updateNavButtonVisibility();

        // Debug:
        System.out.println("BookScreen Init: textAreaWidth=" + this.textAreaActualWidth + ", linesPerPage=" + this.linesPerPage + ", textStartX=" + this.textStartX);
        System.out.println("Total Pages after init: " + this.totalPages);

    }

    private void turnPage(boolean forward) {
        if (forward) {
            if (this.currentPageIndex < this.totalPages - 1) {
                this.currentPageIndex++;
            }
        } else {
            if (this.currentPageIndex > 0) {
                this.currentPageIndex--;
            }
        }
        updateNavButtonVisibility();
    }

    private void updateNavButtonVisibility() {
        this.previousPageButton.visible = this.currentPageIndex > 0;
        this.nextPageButton.visible = this.currentPageIndex < this.totalPages - 1;
    }

    private void preparePageContents() {
        this.pages.clear();
        ClientProgressionData progress = ClientProgressionData.INSTANCE;
        List<Component> allBookComponents = new ArrayList<>();

        // --- Conteúdo --- (igual ao anterior)
        allBookComponents.add(Component.literal("Provas Dimensionais")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
                .withStyle(style -> style.withFont(ResourceLocation.withDefaultNamespace("uniform"))));
        allBookComponents.add(Component.literal(""));
        allBookComponents.add(Component.literal("Este livro guia sua jornada através das dimensões, detalhando os desafios a serem superados.")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        allBookComponents.add(Component.literal(""));
        allBookComponents.add(Component.literal("Conclua os objetivos de cada fase para desbloquear o acesso à próxima dimensão.")
                .withStyle(ChatFormatting.DARK_GRAY));
        allBookComponents.add(Component.literal(""));
        allBookComponents.add(Component.literal(""));

        allBookComponents.add(Component.translatable(Constants.HUD_PHASE1_TITLE).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        allBookComponents.add(Component.literal("--------------------").withStyle(ChatFormatting.GOLD));

        if (progress.isServerEnablePhase1()) {
            if (progress.isServerReqElderGuardian()) allBookComponents.addAll(formatGoal(Constants.HUD_ELDER_GUARDIAN, progress.isElderGuardianKilled(), Constants.HUD_TOOLTIP_ELDER_GUARDIAN));
            if (progress.isServerReqRaidAndRavager()) {
                allBookComponents.addAll(formatGoal(Constants.HUD_RAID_WON, progress.isRaidWon(), Constants.HUD_TOOLTIP_RAID_WON));
                allBookComponents.addAll(formatGoal(Constants.HUD_RAVAGER_KILLED, progress.isRavagerKilled(), Constants.HUD_TOOLTIP_RAVAGER_KILLED));
            }
            if (progress.isServerReqEvoker()) allBookComponents.addAll(formatGoal(Constants.HUD_EVOKER_KILLED, progress.isEvokerKilled(), Constants.HUD_TOOLTIP_EVOKER_KILLED));
            if (progress.isServerReqTrialVaultAdv()) allBookComponents.addAll(formatGoal(Constants.HUD_TRIAL_VAULTS, progress.isTrialVaultAdvancementEarned(), Constants.HUD_TOOLTIP_TRIAL_VAULTS));

            if (progress.isPhase1Completed()) {
                allBookComponents.add(Component.literal(""));
                allBookComponents.add(Component.translatable(Constants.MSG_PHASE1_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            }
        } else {
            allBookComponents.add(Component.translatable(Constants.HUD_MSG_PHASE_DISABLED).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        allBookComponents.add(Component.literal(""));

        allBookComponents.add(Component.translatable(Constants.HUD_PHASE2_TITLE).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        allBookComponents.add(Component.literal("--------------------").withStyle(ChatFormatting.DARK_PURPLE));

        if (!progress.isPhase1EffectivelyComplete()) {
            allBookComponents.add(Component.translatable(Constants.HUD_MSG_COMPLETE_PHASE1).withStyle(ChatFormatting.GRAY));
        } else if (progress.isServerEnablePhase2()) {
            if (progress.isServerReqWither()) allBookComponents.addAll(formatGoal(Constants.HUD_WITHER_KILLED, progress.isWitherKilled(), Constants.HUD_TOOLTIP_WITHER_KILLED));
            if (progress.isServerReqWarden()) allBookComponents.addAll(formatGoal(Constants.HUD_WARDEN_KILLED, progress.isWardenKilled(), Constants.HUD_TOOLTIP_WARDEN_KILLED));

            if (progress.isPhase2Completed()) {
                allBookComponents.add(Component.literal(""));
                allBookComponents.add(Component.translatable(Constants.MSG_PHASE2_UNLOCKED_GLOBAL).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            }
        } else {
            allBookComponents.add(Component.translatable(Constants.HUD_MSG_PHASE_DISABLED).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        allBookComponents.add(Component.literal(""));

        paginateComponents(allBookComponents);
        System.out.println("Content Prepared (Single Page): Total Pages = " + this.totalPages + ", Lines per page = " + this.linesPerPage);

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
        goalComponents.add(Component.literal(""));

        return goalComponents;
    }

    private void paginateComponents(List<Component> components) {
        this.pages.clear();
        List<FormattedCharSequence> currentSinglePageLines = new ArrayList<>();

        for (Component component : components) {
            // Usa this.textAreaActualWidth para quebrar as linhas
            List<FormattedCharSequence> wrappedLines = this.font.split(component, this.textAreaActualWidth);
            for (FormattedCharSequence line : wrappedLines) {
                if (currentSinglePageLines.size() >= this.linesPerPage) { // Se a página atual está cheia
                    this.pages.add(new ArrayList<>(currentSinglePageLines)); // Adiciona a página completa
                    currentSinglePageLines.clear(); // Começa uma nova página
                }
                currentSinglePageLines.add(line);
            }
        }

        if (!currentSinglePageLines.isEmpty()) { // Adiciona a última página se houver conteúdo restante
            this.pages.add(currentSinglePageLines);
        }

        this.totalPages = this.pages.size();
        if (this.totalPages == 0 && !components.isEmpty()) { // Se havia componentes mas nenhuma linha coube (improvável)
            this.pages.add(new ArrayList<>()); // Garante pelo menos uma página
            this.totalPages = 1;
        } else if (components.isEmpty()){ // Se não havia componentes de todo
            this.pages.add(new ArrayList<>()); // Garante uma página vazia
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

        // Renderiza conteúdo para UMA PÁGINA CENTRALIZADA
        if (this.totalPages > 0 && this.currentPageIndex < this.totalPages) {
            List<FormattedCharSequence> linesOfCurrentPage = this.pages.get(this.currentPageIndex);

            int currentLineY = this.guiTop + PAGE_CONTENT_TOP_MARGIN;
            for (FormattedCharSequence line : linesOfCurrentPage) {
                // Para parar de desenhar se exceder a altura útil da página
                if (currentLineY + this.font.lineHeight > this.guiTop + BOOK_IMAGE_HEIGHT - PAGE_CONTENT_BOTTOM_MARGIN +5) { // +5 de tolerância
                    break;
                }
                guiGraphics.drawString(this.font, line, this.textStartX, currentLineY, ChatFormatting.BLACK.getColor(), false);
                currentLineY += this.font.lineHeight;
            }
        }

        // Debugging page numbers
        // String pageNumText = String.format("Página %d / %d", this.currentPageIndex + 1, this.totalPages);
        // guiGraphics.drawString(this.font, pageNumText, this.guiLeft + 5, this.guiTop + 5, 0xFFFFFF, true);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static class PageButton extends Button {
        private final boolean isForward;
        private final boolean playTurnSound;

        public PageButton(int x, int y, boolean isForward, OnPress onPress, boolean playTurnSound) {
            super(x, y, 23, 13, Component.empty(), onPress, DEFAULT_NARRATION);
            this.isForward = isForward;
            this.playTurnSound = playTurnSound;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (this.visible) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                int u = 0;
                int v = 192;
                if (this.isHoveredOrFocused()) {
                    u += 23;
                }
                if (!this.isForward) {
                    v += 13;
                }
                guiGraphics.blit(ProgressionBookScreen.BOOK_TEXTURE, this.getX(), this.getY(), u, v, 23, 13);
            }
        }

        @Override
        public void playDownSound(net.minecraft.client.sounds.SoundManager soundManager) {
            if (this.playTurnSound) {
                soundManager.play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, 1.0F));
            }
        }
    }
}