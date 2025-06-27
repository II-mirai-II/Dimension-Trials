package net.mirai.dimtr.util;

import net.mirai.dimtr.config.DimTrConfig;

/**
 * 🎯 PERFORMANCE: Cache de configurações para evitar múltiplas chamadas .get()
 * 
 * Este cache é atualizado automaticamente quando as configurações mudam
 * e melhora a performance em loops críticos.
 */
public class ConfigCache {
    
    // Cache de configurações booleanas críticas
    private static volatile boolean enablePartySystem;
    private static volatile boolean enableDebugLogging;
    private static volatile boolean enablePhase1;
    private static volatile boolean enablePhase2;
    private static volatile boolean enableMobKillsPhase1;
    private static volatile boolean enableMobKillsPhase2;
    
    // Cache de configurações numéricas críticas
    private static volatile int maxPartySize;
    private static volatile double partyProgressionMultiplier;
    private static volatile double partyProximityRadius;
    
    // Flag para indicar se o cache foi inicializado
    private static volatile boolean initialized = false;
    
    /**
     * Inicializar ou atualizar o cache de configurações
     */
    public static void refreshCache() {
        enablePartySystem = DimTrConfig.SERVER.enablePartySystem.get();
        enableDebugLogging = DimTrConfig.SERVER.enableDebugLogging.get();
        enablePhase1 = DimTrConfig.SERVER.enablePhase1.get();
        enablePhase2 = DimTrConfig.SERVER.enablePhase2.get();
        enableMobKillsPhase1 = DimTrConfig.SERVER.enableMobKillsPhase1.get();
        enableMobKillsPhase2 = DimTrConfig.SERVER.enableMobKillsPhase2.get();
        
        maxPartySize = DimTrConfig.SERVER.maxPartySize.get();
        partyProgressionMultiplier = DimTrConfig.SERVER.partyProgressionMultiplier.get();
        partyProximityRadius = DimTrConfig.SERVER.partyProximityRadius.get();
        
        initialized = true;
    }
    
    /**
     * Garantir que o cache foi inicializado
     */
    private static void ensureInitialized() {
        if (!initialized) {
            refreshCache();
        }
    }
    
    // Getters para configurações críticas com cache
    public static boolean isPartySystemEnabled() {
        ensureInitialized();
        return enablePartySystem;
    }
    
    public static boolean isDebugLoggingEnabled() {
        ensureInitialized();
        return enableDebugLogging;
    }
    
    public static boolean isPhase1Enabled() {
        ensureInitialized();
        return enablePhase1;
    }
    
    public static boolean isPhase2Enabled() {
        ensureInitialized();
        return enablePhase2;
    }
    
    public static boolean isMobKillsPhase1Enabled() {
        ensureInitialized();
        return enableMobKillsPhase1;
    }
    
    public static boolean isMobKillsPhase2Enabled() {
        ensureInitialized();
        return enableMobKillsPhase2;
    }
    
    public static int getMaxPartySize() {
        ensureInitialized();
        return maxPartySize;
    }
    
    public static double getPartyProgressionMultiplier() {
        ensureInitialized();
        return partyProgressionMultiplier;
    }
    
    public static double getPartyProximityRadius() {
        ensureInitialized();
        return partyProximityRadius;
    }
}
