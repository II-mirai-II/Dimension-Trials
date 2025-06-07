package net.mirai.dimtr.client.gui.window;

import net.mirai.dimtr.client.ClientProgressionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;

public abstract class ProgressionWindow {
    protected static final int BORDER_COLOR = 0xFF404040;
    protected static final int BACKGROUND_COLOR = 0xE0000000;
    protected static final int ACTIVE_BORDER_COLOR = 0xFF00FF00;
    protected static final int TITLE_COLOR = 0xFFFFFFFF;
    protected static final int HEADER_SEPARATOR_COLOR = 0xFF808080;

    public abstract String getTitle();
    public abstract List<Component> getContent(ClientProgressionData progress);

    public void render(GuiGraphics guiGraphics, int x, int y, int width, int height,
                       int mouseX, int mouseY, float partialTick, boolean isActive,
                       ClientProgressionData progress) {

        Minecraft minecraft = Minecraft.getInstance();

        // Renderizar fundo da janela
        guiGraphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Renderizar borda (verde se ativa, cinza se inativa)
        int borderColor = isActive ? ACTIVE_BORDER_COLOR : BORDER_COLOR;
        renderBorder(guiGraphics, x, y, width, height, borderColor);

        // Renderizar título
        Component title = Component.literal(getTitle()).withStyle(ChatFormatting.BOLD);
        int titleWidth = minecraft.font.width(title); // CORREÇÃO: Usar minecraft.font.width()
        int titleX = x + (width - titleWidth) / 2; // CORREÇÃO: Centralizar corretamente
        int titleY = y + 8;
        guiGraphics.drawString(minecraft.font, title, titleX, titleY, TITLE_COLOR);

        // Linha separadora abaixo do título
        guiGraphics.fill(x + 5, titleY + 12, x + width - 5, titleY + 13, HEADER_SEPARATOR_COLOR);

        // Renderizar conteúdo
        List<Component> content = getContent(progress);
        int contentStartY = titleY + 20;
        int lineHeight = 10;

        for (int i = 0; i < content.size() && contentStartY + (i * lineHeight) < y + height - 10; i++) {
            Component line = content.get(i);
            guiGraphics.drawString(minecraft.font, line, x + 8, contentStartY + (i * lineHeight), 0xFFFFFFFF);
        }
    }

    private void renderBorder(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        // Borda superior
        guiGraphics.fill(x, y, x + width, y + 2, color);
        // Borda inferior
        guiGraphics.fill(x, y + height - 2, x + width, y + height, color);
        // Borda esquerda
        guiGraphics.fill(x, y, x + 2, y + height, color);
        // Borda direita
        guiGraphics.fill(x + width - 2, y, x + width, y + height, color);
    }

    // Método helper para criar linha de objetivo
    protected Component createGoalLine(String text, boolean completed) {
        ChatFormatting statusColor = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        String statusIcon = completed ? "✔" : "❌";

        return Component.literal(statusIcon + " ").withStyle(statusColor)
                .append(Component.literal(text).withStyle(ChatFormatting.WHITE));
    }

    // Método helper para criar linha de mob counter
    protected Component createMobCounterLine(String mobName, int current, int required) {
        boolean completed = current >= required;
        ChatFormatting countColor = completed ? ChatFormatting.GREEN :
                (current > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
        String statusIcon = completed ? "✔" : "⚔";

        return Component.literal(statusIcon + " ").withStyle(completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED)
                .append(Component.literal(mobName).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(current + "/" + required).withStyle(countColor));
    }
}