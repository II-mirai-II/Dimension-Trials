package net.mirai.dimtr.client.gui.sections;

import net.mirai.dimtr.client.ClientProgressionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerenciador centralizado de seções do HUD
 */
public class SectionManager {

    private static final Map<HUDSection.SectionType, HUDSection> sections = new HashMap<>();

    static {
        // Registrar todas as seções
        register(new Phase1MainSection());
        register(new Phase1GoalsSection()); // ✅ ADICIONADO
        register(new Phase2MainSection());  // ✅ ADICIONADO
        register(new Phase2GoalsSection()); // ✅ ADICIONADO
        register(new PartiesSection());     // 🎯 NOVA SEÇÃO
    }

    private static void register(HUDSection section) {
        sections.put(section.getType(), section);
    }

    /**
     * Obter seção por tipo
     */
    public static HUDSection getSection(HUDSection.SectionType type) {
        return sections.get(type);
    }

    /**
     * Obter todas as seções
     */
    public static List<HUDSection> getAllSections() {
        return new ArrayList<>(sections.values());
    }

    /**
     * Obter seções acessíveis
     */
    public static List<HUDSection> getAccessibleSections(ClientProgressionData progress) {
        List<HUDSection> accessibleSections = new ArrayList<>();

        for (HUDSection section : sections.values()) {
            if (section.isAccessible(progress)) {
                accessibleSections.add(section);
            }
        }

        return accessibleSections;
    }
}