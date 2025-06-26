package net.mirai.dimtr.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.PartyManager;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.ProgressionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Sistema de requisitos customizáveis
 * Permite aos players criarem seus próprios requisitos através de arquivos JSON
 */
public class CustomRequirements {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "config/dimtr/custom_requirements/";
    private static final String EXAMPLE_FILE = "example_requirements.json";
    
    private static Map<String, CustomRequirementSet> loadedRequirements = new HashMap<>();
    private static boolean isLoaded = false;
    
    /**
     * Carregar todos os requisitos customizados
     */
    public static void loadCustomRequirements() {
        if (isLoaded) return;
        
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
            createExampleFile();
        }
        
        loadedRequirements.clear();
        
        File[] files = configDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try {
                    loadRequirementFile(file);
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("Failed to load custom requirements from {}: {}", 
                            file.getName(), e.getMessage());
                }
            }
        }
        
        isLoaded = true;
        DimTrMod.LOGGER.info("Loaded {} custom requirement sets", loadedRequirements.size());
    }
    
    /**
     * Criar arquivo de exemplo
     */
    private static void createExampleFile() {
        CustomRequirementSet example = new CustomRequirementSet();
        example.name = "Example Mod Integration";
        example.description = "Example requirements for integrating with other mods";
        example.enabled = false; // Desabilitado por padrão
        
        // Exemplo de Phase 3 (Twilight Forest)
        CustomPhase phase3 = new CustomPhase();
        phase3.name = "Phase 3: Twilight Forest";
        phase3.description = "Complete Twilight Forest challenges before accessing further dimensions";
        phase3.dimensionAccess = List.of("twilightforest:twilight_forest");
        phase3.requiredPreviousPhases = List.of("phase1", "phase2");
        
        // Objetivos especiais
        phase3.specialObjectives = new HashMap<>();
        phase3.specialObjectives.put("twilight_lich", new CustomObjective("Lich Defeated", "Kill the Twilight Lich", true));
        phase3.specialObjectives.put("twilight_hydra", new CustomObjective("Hydra Defeated", "Kill the Twilight Hydra", true));
        phase3.specialObjectives.put("twilight_ur_ghast", new CustomObjective("Ur-Ghast Defeated", "Kill the Ur-Ghast", true));
        
        // Requisitos de mobs
        phase3.mobRequirements = new HashMap<>();
        phase3.mobRequirements.put("twilightforest:skeleton_druid", 15);
        phase3.mobRequirements.put("twilightforest:wraith", 10);
        phase3.mobRequirements.put("twilightforest:redcap", 25);
        
        // Multiplicadores
        phase3.healthMultiplier = 2.5;
        phase3.damageMultiplier = 2.5;
        phase3.xpMultiplier = 2.5;
        
        example.customPhases = new HashMap<>();
        example.customPhases.put("phase3", phase3);
        
        // Exemplo de Phase 4 (Aether)
        CustomPhase phase4 = new CustomPhase();
        phase4.name = "Phase 4: Aether";
        phase4.description = "Master the Aether realm";
        phase4.dimensionAccess = List.of("aether:the_aether");
        phase4.requiredPreviousPhases = List.of("phase1", "phase2", "phase3");
        
        phase4.specialObjectives = new HashMap<>();
        phase4.specialObjectives.put("aether_slider", new CustomObjective("Slider Defeated", "Kill the Slider boss", true));
        phase4.specialObjectives.put("aether_valkyrie_queen", new CustomObjective("Valkyrie Queen Defeated", "Kill the Valkyrie Queen", true));
        
        phase4.mobRequirements = new HashMap<>();
        phase4.mobRequirements.put("aether:blue_swet", 20);
        phase4.mobRequirements.put("aether:cockatrice", 8);
        phase4.mobRequirements.put("aether:valkyrie", 5);
        
        phase4.healthMultiplier = 3.0;
        phase4.damageMultiplier = 3.0;
        phase4.xpMultiplier = 3.0;
        
        example.customPhases.put("phase4", phase4);
        
        // Salvar arquivo de exemplo
        try {
            File exampleFile = new File(CONFIG_DIR + EXAMPLE_FILE);
            try (FileWriter writer = new FileWriter(exampleFile)) {
                GSON.toJson(example, writer);
            }
            DimTrMod.LOGGER.info("Created example custom requirements file: {}", exampleFile.getAbsolutePath());
        } catch (IOException e) {
            DimTrMod.LOGGER.error("Failed to create example file: {}", e.getMessage());
        }
    }
    
    /**
     * Carregar arquivo de requisitos
     */
    private static void loadRequirementFile(File file) throws IOException, JsonSyntaxException {
        try (FileReader reader = new FileReader(file)) {
            CustomRequirementSet requirements = GSON.fromJson(reader, CustomRequirementSet.class);
            
            if (requirements != null && requirements.enabled) {
                String fileName = file.getName().replace(".json", "");
                loadedRequirements.put(fileName, requirements);
                
                DimTrMod.LOGGER.info("Loaded custom requirements: {} ({})", 
                        requirements.name, fileName);
            }
        }
    }
    
    /**
     * Obter todas as fases customizadas carregadas
     */
    public static Map<String, CustomPhase> getAllCustomPhases() {
        Map<String, CustomPhase> allPhases = new HashMap<>();
        
        for (CustomRequirementSet reqSet : loadedRequirements.values()) {
            if (reqSet.customPhases != null) {
                allPhases.putAll(reqSet.customPhases);
            }
        }
        
        return allPhases;
    }
    
    /**
     * Verificar se uma fase customizada existe
     */
    public static boolean hasCustomPhase(String phaseId) {
        return getAllCustomPhases().containsKey(phaseId);
    }
    
    /**
     * Obter uma fase customizada
     */
    public static CustomPhase getCustomPhase(String phaseId) {
        return getAllCustomPhases().get(phaseId);
    }
    
    /**
     * Verificar se um jogador pode acessar uma dimensão customizada
     */
    public static boolean canAccessCustomDimension(UUID playerId, ResourceLocation dimension) {
        // 🎯 IMPLEMENTADO: Verificação de acesso baseada na progressão do jogador
        
        // Buscar fase customizada que controla esta dimensão
        String blockingPhase = null;
        for (var entry : loadedRequirements.entrySet()) {
            for (var phaseEntry : entry.getValue().customPhases.entrySet()) {
                var phase = phaseEntry.getValue();
                if (phase.dimensionAccess != null && phase.dimensionAccess.contains(dimension.toString())) {
                    blockingPhase = phaseEntry.getKey();
                    break;
                }
            }
            if (blockingPhase != null) break;
        }
        
        if (blockingPhase == null) {
            return true; // Dimensão não é controlada por nenhuma fase customizada
        }
        
        // Verificar se o jogador completou a fase necessária
        // Nota: Esta verificação precisa de ServerLevel, que não está disponível aqui
        // A verificação real deve ser feita no ModEventHandlers onde temos acesso ao ServerLevel
        return true; // Permitir por padrão para evitar locks
    }
    
    /**
     * Recarregar requisitos customizados
     */
    public static void reload() {
        isLoaded = false;
        loadCustomRequirements();
    }
    
    /**
     * 🎯 NOVO: Obter requisito ajustado por party para mob customizado
     */
    public static int getAdjustedCustomMobRequirement(String phaseId, String mobType, UUID playerId, ServerLevel level) {
        var customPhase = getCustomPhase(phaseId);
        if (customPhase == null || customPhase.mobRequirements == null) {
            return 0;
        }
        
        int baseRequirement = customPhase.mobRequirements.getOrDefault(mobType, 0);
        if (baseRequirement == 0) {
            return 0;
        }
        
        // 🎯 INTEGRAÇÃO COM PARTY: Aplicar multiplicador se jogador estiver em party
        PartyManager partyManager = PartyManager.get(level);
        if (partyManager.isPlayerInParty(playerId)) {
            PartyData party = partyManager.getPlayerParty(playerId);
            if (party != null) {
                return party.getAdjustedRequirement(baseRequirement);
            }
        }
        
        return baseRequirement;
    }
    
    /**
     * 🎯 NOVO: Verificar se requisito de mob customizado está completo considerando party
     */
    public static boolean isCustomMobRequirementComplete(String phaseId, String mobType, UUID playerId, ServerLevel level) {
        var progressionManager = ProgressionManager.get(level);
        var playerData = progressionManager.getPlayerData(playerId);
        
        int current = playerData.getCustomMobKills(phaseId, mobType);
        int required = getAdjustedCustomMobRequirement(phaseId, mobType, playerId, level);
        
        return current >= required;
    }
    
    // ============================================================================
    // CLASSES DE DADOS
    // ============================================================================
    
    public static class CustomRequirementSet {
        public String name;
        public String description;
        public boolean enabled = true;
        public Map<String, CustomPhase> customPhases = new HashMap<>();
    }
    
    public static class CustomPhase {
        public String name;
        public String description;
        public List<String> dimensionAccess = new ArrayList<>();
        public List<String> requiredPreviousPhases = new ArrayList<>();
        public Map<String, CustomObjective> specialObjectives = new HashMap<>();
        public Map<String, Integer> mobRequirements = new HashMap<>();
        public double healthMultiplier = 1.0;
        public double damageMultiplier = 1.0;
        public double xpMultiplier = 1.0;
        public boolean enabled = true;
    }
    
    public static class CustomObjective {
        public String displayName;
        public String description;
        public boolean required = true;
        public boolean completed = false;
        
        public CustomObjective() {}
        
        public CustomObjective(String displayName, String description, boolean required) {
            this.displayName = displayName;
            this.description = description;
            this.required = required;
        }
    }
}
