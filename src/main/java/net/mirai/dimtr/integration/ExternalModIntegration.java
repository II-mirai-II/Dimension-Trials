package net.mirai.dimtr.integration;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.neoforged.fml.ModList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Integration system for external mod bosses (Mowzie's Mobs, L_Ender's Cataclysm)
 * Integrates bosses as special objectives in existing phases instead of creating custom phases
 */
public class ExternalModIntegration {

    private static final Map<String, Set<String>> SUPPORTED_MODS = new HashMap<>();
    private static final Map<String, Integer> BOSS_PHASE_ASSIGNMENT = new HashMap<>();
    private static final Set<String> detectedBosses = ConcurrentHashMap.newKeySet();
    private static final Map<String, BossInfo> bossInfoMap = new ConcurrentHashMap<>();
    
    // 🎯 NOVO: Flag para controlar inicialização do lado cliente
    private static volatile boolean clientSideInitialized = false;
    
    // 🎯 NOVO: Versão da integração para controle de compatibilidade
    private static final String INTEGRATION_VERSION = "1.3.0";
    
    public static class BossInfo {
        public final String entityId;
        public final String displayName;
        public final String description;
        public final int phase;
        public final boolean required;
        
        public BossInfo(String entityId, String displayName, String description, int phase, boolean required) {
            this.entityId = entityId;
            this.displayName = displayName;
            this.description = description;
            this.phase = phase;
            this.required = required;
        }
    }
    
    static {
        // Mowzie's Mobs bosses - IDs corretos baseados na config
        Set<String> mowzieBosses = new HashSet<>();
        mowzieBosses.add("mowziesmobs:ferrous_wroughtnaut");
        mowzieBosses.add("mowziesmobs:frostmaw");
        mowzieBosses.add("mowziesmobs:umvuthi"); // ✅ CORRIGIDO: Umvuthi substituiu Barako
        mowzieBosses.add("mowziesmobs:naga");
        mowzieBosses.add("mowziesmobs:sculptor"); // ✅ NOVO: Boss adicionado
        SUPPORTED_MODS.put("mowziesmobs", mowzieBosses);
        
        // L_Ender's Cataclysm bosses - IDs corretos baseados na config
        Set<String> cataclysmBosses = new HashSet<>();
        cataclysmBosses.add("cataclysm:ender_guardian");
        cataclysmBosses.add("cataclysm:ender_golem");
        cataclysmBosses.add("cataclysm:netherite_monstrosity");
        cataclysmBosses.add("cataclysm:harbinger"); // ✅ CORRIGIDO: sem "the_"
        cataclysmBosses.add("cataclysm:leviathan"); // ✅ CORRIGIDO: sem "the_"
        cataclysmBosses.add("cataclysm:ancient_remnant");
        cataclysmBosses.add("cataclysm:ignis");
        cataclysmBosses.add("cataclysm:maledictus");
        SUPPORTED_MODS.put("cataclysm", cataclysmBosses);
        
        // Phase assignments based on natural spawn dimensions/biomes
        // Phase 1 (Overworld) bosses - where they naturally spawn
        BOSS_PHASE_ASSIGNMENT.put("mowziesmobs:ferrous_wroughtnaut", 1); // Wroughtnaut Chamber (Overworld)
        BOSS_PHASE_ASSIGNMENT.put("mowziesmobs:frostmaw", 1);           // Cold biomes (Overworld)
        BOSS_PHASE_ASSIGNMENT.put("mowziesmobs:umvuthi", 1);            // Hot biomes/deserts (Overworld) - replaces Barako
        BOSS_PHASE_ASSIGNMENT.put("mowziesmobs:naga", 1);               // Jungle biomes (Overworld)
        BOSS_PHASE_ASSIGNMENT.put("mowziesmobs:sculptor", 1);           // Overworld structures (NEW)
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:ancient_remnant", 1);      // Ancient Factory (Overworld)
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:leviathan", 1);            // Underwater structures (Overworld) - corrigido
        
        // Phase 2 (Nether) bosses - where they naturally spawn
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:ignis", 2);                // Soul Blacksmith (Nether)
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:netherite_monstrosity", 2); // Nether structures
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:harbinger", 2);            // Nether structures - corrigido
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:maledictus", 2);           // Nether structures
        
        // Phase 3 (End) bosses - where they naturally spawn
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:ender_guardian", 3);       // End structures
        BOSS_PHASE_ASSIGNMENT.put("cataclysm:ender_golem", 3);          // End structures
    }

    /**
     * Initialize external mod integration
     * Creates a custom phase with external mod bosses as special objectives
     */
    public static void initialize() {
        try {
            DimTrMod.LOGGER.info("=== EXTERNAL MOD INTEGRATION DEBUG START ===");
            
            // 🎯 MUDANÇA: Sempre tentar inicializar independente da config
            // Se a config não estiver carregada, usar defaults sensatos
            boolean configLoaded = isConfigLoaded();
            boolean integrationEnabled = true; // Default para habilitado
            
            if (configLoaded) {
                integrationEnabled = DimTrConfig.SERVER.enableExternalModIntegration.get();
                DimTrMod.LOGGER.info("Config loaded - integration enabled: {}", integrationEnabled);
            } else {
                DimTrMod.LOGGER.warn("Config not loaded yet - using default settings (integration enabled)");
            }
            
            if (!integrationEnabled) {
                DimTrMod.LOGGER.info("External mod integration is disabled in config");
                return;
            }
            
            DimTrMod.LOGGER.info("External mod integration is enabled - checking for supported mods...");
            
            // Clear previous data
            detectedBosses.clear();
            bossInfoMap.clear();
            
            // Check which mods are loaded
            boolean anyModFound = false;
            for (String modId : SUPPORTED_MODS.keySet()) {
                boolean isLoaded = ModList.get().isLoaded(modId);
                DimTrMod.LOGGER.info("🔍 Checking mod '{}': loaded = {}", modId, isLoaded);
                if (isLoaded) {
                    anyModFound = true;
                    DimTrMod.LOGGER.info("✅ Processing integration for mod: {}", modId);
                    processModIntegration(modId);
                }
            }
            
            if (!anyModFound) {
                DimTrMod.LOGGER.info("❌ No supported external mods found");
                return;
            }
            
            // Create custom phases if any bosses were detected
            if (!detectedBosses.isEmpty()) {
                DimTrMod.LOGGER.info("📋 Creating integration summary for {} detected bosses", detectedBosses.size());
                createExternalModPhases();
            } else {
                DimTrMod.LOGGER.warn("⚠️ Mods were found but no bosses were detected!");
            }
            
            DimTrMod.LOGGER.info("=== EXTERNAL MOD INTEGRATION DEBUG END ===");
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Failed to initialize external mod integration", e);
        }
    }

    /**
     * 🎯 NOVO: Initialize on client side for HUD display
     * This is a lighter version that only detects mods and creates boss info
     * without creating custom phases (which is server-only)
     */
    public static void initializeClientSide() {
        // Avoid multiple initializations
        if (clientSideInitialized) {
            return;
        }
        
        try {
            DimTrMod.LOGGER.info("=== CLIENT-SIDE EXTERNAL MOD INTEGRATION ===");
            
            // Clear previous data
            detectedBosses.clear();
            bossInfoMap.clear();
            
            // Check which mods are loaded (client-side detection)
            boolean anyModFound = false;
            for (String modId : SUPPORTED_MODS.keySet()) {
                boolean isLoaded = ModList.get().isLoaded(modId);
                DimTrMod.LOGGER.info("🔍 [CLIENT] Checking mod '{}': loaded = {}", modId, isLoaded);
                if (isLoaded) {
                    anyModFound = true;
                    processModIntegrationClientSide(modId);
                }
            }
            
            if (!anyModFound) {
                DimTrMod.LOGGER.info("❌ [CLIENT] No supported external mods found");
            } else {
                DimTrMod.LOGGER.info("✅ [CLIENT] Client-side integration complete! {} bosses available for HUD", bossInfoMap.size());
            }
            
            clientSideInitialized = true;
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("[CLIENT] Failed to initialize client-side external mod integration", e);
        }
    }
    
    /**
     * 🎯 NOVO: Process mod integration on client side
     */
    private static void processModIntegrationClientSide(String modId) {
        try {
            Set<String> bosses = SUPPORTED_MODS.get(modId);
            if (bosses == null) return;
            
            DimTrMod.LOGGER.info("[CLIENT] Processing integration for mod: {} with {} bosses", modId, bosses.size());
            
            // Add all bosses for client-side display
            for (String bossId : bosses) {
                detectedBosses.add(bossId);
                
                // Create boss info (assume required=true for client display)
                int phase = BOSS_PHASE_ASSIGNMENT.getOrDefault(bossId, 1);
                boolean isRequired = true; // Default to true for client display
                String displayName = formatBossName(bossId);
                String description = "Defeat " + displayName;
                
                BossInfo bossInfo = new BossInfo(bossId, displayName, description, phase, isRequired);
                bossInfoMap.put(bossId, bossInfo);
                
                DimTrMod.LOGGER.info("[CLIENT] ✅ Added boss: {} (Phase {})", displayName, phase);
            }
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("[CLIENT] Failed to process integration for mod: {}", modId, e);
        }
    }

    /**
     * Process integration for a specific mod
     */
    private static void processModIntegration(String modId) {
        try {
            // Check if this mod is enabled in config
            if (!isModEnabled(modId)) {
                DimTrMod.LOGGER.info("Integration for mod '{}' is disabled in config", modId);
                return;
            }
            
            Set<String> bosses = SUPPORTED_MODS.get(modId);
            if (bosses == null) return;
            
            DimTrMod.LOGGER.info("Processing integration for mod: {} with {} bosses", modId, bosses.size());
            
            // For testing: add all bosses without entity verification (trust our curated list)
            for (String bossId : bosses) {
                DimTrMod.LOGGER.info("🔄 Processing boss: {}", bossId);
                detectedBosses.add(bossId);
                
                // Create boss info
                int phase = BOSS_PHASE_ASSIGNMENT.getOrDefault(bossId, 1);
                boolean isRequired = isConfigLoaded() ? DimTrConfig.SERVER.requireExternalModBosses.get() : true;
                String displayName = formatBossName(bossId);
                String description = "Defeat " + displayName;
                
                BossInfo bossInfo = new BossInfo(bossId, displayName, description, phase, isRequired);
                bossInfoMap.put(bossId, bossInfo);
                
                DimTrMod.LOGGER.info("✅ Integrated boss: {} (Phase {}) - Required: {} - Map size: {}", 
                    displayName, phase, isRequired, bossInfoMap.size());
            }
            
            DimTrMod.LOGGER.info("🎯 Mod '{}' integration complete. Total detected bosses: {}, Total in map: {}", 
                modId, detectedBosses.size(), bossInfoMap.size());
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Failed to process integration for mod: {}", modId, e);
        }
    }

    /**
     * Create custom phases for external mod bosses
     */
    private static void createExternalModPhases() {
        try {
            // 🎯 NOVO SISTEMA: Não criar fases customizadas separadas
            // Os bosses agora são integrados diretamente nas fases principais como objetivos especiais
            DimTrMod.LOGGER.info("✅ External mod integration complete! {} bosses integrated into main phases", bossInfoMap.size());
            
            // Log final da integração por fase
            Map<Integer, List<BossInfo>> integrationSummary = new HashMap<>();
            for (BossInfo boss : bossInfoMap.values()) {
                int targetPhase = boss.phase;
                if (boss.phase == 3 && isConfigLoaded() && !DimTrConfig.SERVER.createPhase3ForEndBosses.get()) {
                    targetPhase = 2;
                    DimTrMod.LOGGER.info("⚠️ Moving End boss '{}' to Phase 2 (Phase 3 creation disabled)", boss.displayName);
                }
                integrationSummary.computeIfAbsent(targetPhase, k -> new ArrayList<>()).add(boss);
            }
            
            for (Map.Entry<Integer, List<BossInfo>> entry : integrationSummary.entrySet()) {
                int phase = entry.getKey();
                List<BossInfo> bosses = entry.getValue();
                DimTrMod.LOGGER.info("📋 Phase {}: {} external bosses integrated", phase, bosses.size());
                for (BossInfo boss : bosses) {
                    DimTrMod.LOGGER.info("  • {} (Required: {})", boss.displayName, boss.required);
                }
            }
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Failed to create external mod phases", e);
        }
    }

    /**
     * Format boss ID into a readable display name
     */
    private static String formatBossName(String bossId) {
        String name = bossId.substring(bossId.indexOf(':') + 1);
        return Arrays.stream(name.split("_"))
                     .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                     .reduce((a, b) -> a + " " + b)
                     .orElse(name);
    }

    /**
     * Check if a specific mod integration is enabled
     */
    private static boolean isModEnabled(String modId) {
        if (!isConfigLoaded()) return true; // Default to enabled if config not loaded
        
        switch (modId) {
            case "mowziesmobs":
                return DimTrConfig.SERVER.enableMowziesModsIntegration.get();
            case "cataclysm":
                return DimTrConfig.SERVER.enableCataclysmIntegration.get();
            default:
                return true;
        }
    }

    /**
     * Check if config is loaded and accessible
     */
    private static boolean isConfigLoaded() {
        try {
            // Try to access a config value to see if it's loaded
            DimTrConfig.SERVER.enableExternalModIntegration.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ✅ MELHORADO: Check if a boss is from an external mod and integrated
     */
    public static boolean isExternalBoss(String entityId) {
        return bossInfoMap.containsKey(entityId) || detectedBosses.contains(entityId);
    }

    /**
     * ✅ MELHORADO: Get the phase assignment for a boss
     */
    public static int getBossPhase(String bossId) {
        BossInfo info = bossInfoMap.get(bossId);
        if (info != null) {
            return info.phase;
        }
        return BOSS_PHASE_ASSIGNMENT.getOrDefault(bossId, 1);
    }

    /**
     * Get all detected external bosses
     */
    public static Set<String> getDetectedBosses() {
        return new HashSet<>(detectedBosses);
    }

    /**
     * Get list of detected bosses for a specific phase
     */
    public static List<BossInfo> getBossesForPhase(int phase) {
        List<BossInfo> phaseBosses = new ArrayList<>();
        
        // 🎯 CORREÇÃO: Reduzir debug logging para evitar spam
        // Só logar em chamadas diretas, não quando cache é usado
        boolean shouldLog = false; // Definir como false para reduzir spam
        
        if (shouldLog) {
            DimTrMod.LOGGER.debug("🔍 getBossesForPhase({}) called. Detected bosses: {}, BossInfoMap size: {}", 
                phase, detectedBosses.size(), bossInfoMap.size());
        }
        
        for (String bossId : detectedBosses) {
            BossInfo info = getBossInfo(bossId);
            if (info != null) {
                // Check if Phase 3 creation is disabled and this is a Phase 3 boss
                int targetPhase = info.phase;
                if (info.phase == 3 && isConfigLoaded() && !DimTrConfig.SERVER.createPhase3ForEndBosses.get()) {
                    targetPhase = 2; // Move to Phase 2
                }
                
                if (targetPhase == phase) {
                    phaseBosses.add(info);
                    if (shouldLog) {
                        DimTrMod.LOGGER.debug("✅ Added boss {} to phase {} (original phase: {})", 
                            info.displayName, phase, info.phase);
                    }
                }
            } else if (shouldLog) {
                DimTrMod.LOGGER.warn("⚠️ BossInfo not found for detected boss: {}", bossId);
            }
        }
        
        if (shouldLog) {
            DimTrMod.LOGGER.debug("🎯 Phase {} has {} bosses", phase, phaseBosses.size());
        }
        return phaseBosses;
    }
    
    /**
     * Get all detected boss infos
     */
    public static List<BossInfo> getAllDetectedBosses() {
        List<BossInfo> allBosses = new ArrayList<>();
        
        for (String bossId : detectedBosses) {
            BossInfo info = getBossInfo(bossId);
            if (info != null) {
                allBosses.add(info);
            }
        }
        
        return allBosses;
    }
    
    /**
     * Check if there are any Phase 3 bosses available
     */
    public static boolean hasPhase3Bosses() {
        return !getBossesForPhase(3).isEmpty();
    }

    /**
     * Check if any external mods are loaded and integration is enabled
     */
    public static boolean isIntegrationActive() {
        if (!isConfigLoaded() || !DimTrConfig.SERVER.enableExternalModIntegration.get()) {
            return false;
        }
        
        for (String modId : SUPPORTED_MODS.keySet()) {
            if (ModList.get().isLoaded(modId) && isModEnabled(modId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get display name for a boss
     */
    public static String getBossDisplayName(String bossId) {
        if (!detectedBosses.contains(bossId)) {
            return bossId;
        }
        return formatBossName(bossId);
    }

    /**
     * Get boss info for event handling
     */
    public static BossInfo getBossInfo(String entityId) {
        return bossInfoMap.get(entityId);
    }
    
    /**
     * Obtém a versão atual da integração com mods externos
     * @return String da versão
     */
    public static String getIntegrationVersion() {
        return INTEGRATION_VERSION;
    }
    
    /**
     * Atribui uma fase a um boss específico
     * @param bossEntityId ID da entidade do boss (ex: "cataclysm:ignis")
     * @param phase Fase (1 = Overworld, 2 = Nether, 3 = End)
     * @return Verdadeiro se a atribuição foi bem-sucedida
     */
    public static boolean assignBossToPhase(String bossEntityId, int phase) {
        if (phase < 1 || phase > 3) {
            DimTrMod.LOGGER.warn("Fase inválida para boss {}: {}. Use 1, 2 ou 3.", bossEntityId, phase);
            return false;
        }
        
        // Verificar se o boss está registrado como suportado
        boolean isSupported = false;
        for (Set<String> bosses : SUPPORTED_MODS.values()) {
            if (bosses.contains(bossEntityId)) {
                isSupported = true;
                break;
            }
        }
        
        if (!isSupported) {
            DimTrMod.LOGGER.warn("Boss não suportado para atribuição de fase: {}", bossEntityId);
            return false;
        }
        
        // Atribuir fase
        BOSS_PHASE_ASSIGNMENT.put(bossEntityId, phase);
        DimTrMod.LOGGER.info("Boss {} atribuído à fase {}", bossEntityId, phase);
        
        // Atualizar info do boss se já existir
        BossInfo existingInfo = bossInfoMap.get(bossEntityId);
        if (existingInfo != null) {
            BossInfo updatedInfo = new BossInfo(
                existingInfo.entityId,
                existingInfo.displayName,
                existingInfo.description,
                phase,
                existingInfo.required
            );
            bossInfoMap.put(bossEntityId, updatedInfo);
        }
        
        return true;
    }
}
