package net.mirai.dimtr.client.gui.window;

import net.mirai.dimtr.client.ClientProgressionData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.ArrayList;
import java.util.List;

public class WindowManager {
    private final List<ProgressionWindow> windows;
    private int currentWindowIndex = 0;
    private final int windowWidth = 300;
    private final int windowHeight = 200;
    private final int windowSpacing = 10;

    public WindowManager() {
        this.windows = new ArrayList<>();
        initializeWindows();
    }

    private void initializeWindows() {
        // Janela 1: Fase 1 Main (Objetivos Especiais)
        windows.add(new Phase1MainWindow());

        // Janela 2: Fase 1 Goal Kills (Eliminação de Mobs)
        windows.add(new Phase1GoalKillsWindow());

        // Janela 3: Fase 2 Main (Objetivos Especiais)
        windows.add(new Phase2MainWindow());

        // Janela 4: Fase 2 Goal Kills + Reset Overworld
        windows.add(new Phase2GoalKillsWindow());
    }

    public void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight, int mouseX, int mouseY, float partialTick) {
        ClientProgressionData progress = ClientProgressionData.INSTANCE;

        // Calcular posição das janelas (parte superior da tela)
        int totalWidth = (windowWidth * windows.size()) + (windowSpacing * (windows.size() - 1));
        int startX = (screenWidth - totalWidth) / 2;
        int startY = 10; // 10 pixels do topo da tela

        for (int i = 0; i < windows.size(); i++) {
            ProgressionWindow window = windows.get(i);
            int windowX = startX + (i * (windowWidth + windowSpacing));
            boolean isActive = (i == currentWindowIndex);

            window.render(guiGraphics, windowX, startY, windowWidth, windowHeight,
                    mouseX, mouseY, partialTick, isActive, progress);
        }

        // Renderizar indicador de janela ativa
        renderWindowIndicator(guiGraphics, startX, startY + windowHeight + 5, totalWidth);
    }

    private void renderWindowIndicator(GuiGraphics guiGraphics, int x, int y, int totalWidth) {
        // Renderizar indicadores das janelas (pontos)
        int indicatorSize = 6;
        int indicatorSpacing = 12;
        int totalIndicatorWidth = (windows.size() * indicatorSize) + ((windows.size() - 1) * (indicatorSpacing - indicatorSize));
        int indicatorStartX = x + (totalWidth - totalIndicatorWidth) / 2;

        for (int i = 0; i < windows.size(); i++) {
            int indicatorX = indicatorStartX + (i * indicatorSpacing);
            boolean isActive = (i == currentWindowIndex);

            // Cor do indicador
            int color = isActive ? 0xFF00FF00 : 0xFF808080; // Verde para ativo, cinza para inativo

            // Renderizar círculo
            guiGraphics.fill(indicatorX, y, indicatorX + indicatorSize, y + indicatorSize, color);
        }
    }

    public void nextWindow() {
        currentWindowIndex = (currentWindowIndex + 1) % windows.size();
    }

    public void previousWindow() {
        currentWindowIndex = (currentWindowIndex - 1 + windows.size()) % windows.size();
    }

    public void setWindow(int index) {
        if (index >= 0 && index < windows.size()) {
            currentWindowIndex = index;
        }
    }

    public int getCurrentWindowIndex() {
        return currentWindowIndex;
    }

    public int getTotalWindows() {
        return windows.size();
    }

    public ProgressionWindow getCurrentWindow() {
        return windows.get(currentWindowIndex);
    }

    public String getCurrentWindowTitle() {
        return getCurrentWindow().getTitle();
    }
}