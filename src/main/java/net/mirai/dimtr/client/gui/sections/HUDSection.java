package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Interface base para seções do HUD de progressão
 */
public interface HUDSection {

    /**
     * Tipo único da seção
     */
    SectionType getType();

    /**
     * Título exibido na seção
     */
    Component getTitle();

    /**
     * Descrição exibida no sumário
     */
    Component getDescription();

    /**
     * Ícone da seção
     */
    String getIcon();

    /**
     * Verifica se a seção está acessível
     */
    boolean isAccessible(ClientProgressionData progress);

    /**
     * Gera o conteúdo da seção
     */
    List<Component> generateContent(ClientProgressionData progress);

    /**
     * Tipos de seção disponíveis na interface
     */
    enum SectionType {
        PHASE1_MAIN(Constants.WINDOW_PHASE1_MAIN_TITLE, "📊"),
        PHASE1_GOALS(Constants.WINDOW_PHASE1_GOALS_TITLE, "⚔"),
        PHASE2_MAIN(Constants.WINDOW_PHASE2_MAIN_TITLE, "🌌"),
        PHASE2_GOALS(Constants.WINDOW_PHASE2_GOALS_TITLE, "👹"),
        PHASE3_MAIN("Phase 3 - End Bosses", "🌟"),
        PARTIES(Constants.WINDOW_PARTIES_TITLE, "👥");

        private final String titleKey;
        private final String icon;

        SectionType(String titleKey, String icon) {
            this.titleKey = titleKey;
            this.icon = icon;
        }

        public String getTitleKey() {
            return titleKey;
        }

        public String getIcon() {
            return icon;
        }
    }
}