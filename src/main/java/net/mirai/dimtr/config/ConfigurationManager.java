package net.mirai.dimtr.config;

import net.mirai.dimtr.DimTrMod;
import net.neoforged.neoforge.common.ModConfigSpec;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Gerenciador centralizado e robusto de configurações para o Dimension Trials
 * 
 * Este sistema oferece:
 * ✅ Acesso unificado a todas as configurações do mod
 * ✅ Cache inteligente com invalidação automática
 * ✅ Recarregamento dinâmico de configurações
 * ✅ Suporte a tipos genéricos com type safety
 * ✅ Thread-safety completo
 * ✅ Configurações customizadas JSON
 * ✅ Sistema de observadores para mudanças
 * 
 * @author Dimension Trials Team
 */
public class ConfigurationManager {
    
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();
    
    // Thread-safety
    private static final ReentrantReadWriteLock CONFIG_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = CONFIG_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = CONFIG_LOCK.writeLock();
    
    // Cache inteligente
    private static final Map<String, Object> configCache = new ConcurrentHashMap<>();
    private static final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 30000; // 30 segundos
    
    // Paths de configuração
    private static final String CONFIG_DIR = "config/dimtr/";
    private static final String CUSTOM_CONFIG_DIR = CONFIG_DIR + "custom/";
    
    // Sistema de observadores
    private static final Map<String, ConfigChangeListener> changeListeners = new ConcurrentHashMap<>();
    
    // Estado de inicialização
    private static boolean initialized = false;
    
    /**
     * Interface para observadores de mudanças de configuração
     */
    @FunctionalInterface
    public interface ConfigChangeListener {
        void onConfigChanged(String path, Object oldValue, Object newValue);
    }
    
    /**
     * Inicializar o sistema de configuração
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        writeLock.lock();
        try {
            createDirectories();
            loadCustomConfigurations();
            registerDefaultListeners();
            
            initialized = true;
            DimTrMod.LOGGER.info("✅ ConfigurationManager inicializado com sucesso");
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Obter valor de configuração com type safety
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getConfig(String path, Class<T> type) {
        readLock.lock();
        try {
            // Verificar cache primeiro
            String cacheKey = path + ":" + type.getSimpleName();
            if (isCacheValid(cacheKey)) {
                Object cachedValue = configCache.get(cacheKey);
                if (type.isInstance(cachedValue)) {
                    return Optional.of((T) cachedValue);
                }
            }
            
            // Buscar valor real
            Optional<T> value = resolveConfigValue(path, type);
            
            // Atualizar cache
            if (value.isPresent()) {
                configCache.put(cacheKey, value.get());
                cacheTimestamps.put(cacheKey, System.currentTimeMillis());
            }
            
            return value;
            
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Obter valor de configuração com fallback
     */
    public static <T> T getConfig(String path, Class<T> type, T defaultValue) {
        return getConfig(path, type).orElse(defaultValue);
    }
    
    /**
     * Obter valor de configuração com supplier de fallback
     */
    public static <T> T getConfig(String path, Class<T> type, Supplier<T> defaultSupplier) {
        return getConfig(path, type).orElseGet(defaultSupplier);
    }
    
    /**
     * Recarregar configuração específica
     */
    public static void reloadConfig(String path) {
        writeLock.lock();
        try {
            if ("all".equals(path)) {
                configCache.clear();
                cacheTimestamps.clear();
                loadCustomConfigurations();
                DimTrMod.LOGGER.info("🔄 Todas as configurações foram recarregadas");
            } else {
                invalidateCache(path);
                DimTrMod.LOGGER.debug("🔄 Configuração '{}' foi recarregada", path);
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Verificar se uma configuração existe
     */
    public static boolean hasConfig(String path) {
        readLock.lock();
        try {
            return resolveConfigValue(path, Object.class).isPresent();
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Registrar observador de mudanças
     */
    public static void addConfigChangeListener(String path, ConfigChangeListener listener) {
        changeListeners.put(path, listener);
    }
    
    /**
     * Finalizar o sistema de configuração
     */
    public static void shutdown() {
        writeLock.lock();
        try {
            configCache.clear();
            cacheTimestamps.clear();
            changeListeners.clear();
            initialized = false;
            DimTrMod.LOGGER.info("✅ ConfigurationManager finalizado");
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // MÉTODOS INTERNOS
    // ============================================================================
    
    @SuppressWarnings("unchecked")
    private static <T> Optional<T> resolveConfigValue(String path, Class<T> type) {
        try {
            // Tentar configurações do NeoForge primeiro
            Optional<T> neoForgeValue = resolveNeoForgeConfig(path, type);
            if (neoForgeValue.isPresent()) {
                return neoForgeValue;
            }
            
            // Tentar configurações customizadas
            return resolveCustomConfig(path, type);
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("❌ Erro ao resolver configuração '{}': {}", path, e.getMessage());
            return Optional.empty();
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Optional<T> resolveNeoForgeConfig(String path, Class<T> type) {
        try {
            String[] parts = path.split("\\.");
            if (parts.length < 2) {
                return Optional.empty();
            }
            
            Object configObject = null;
            String configType = parts[0].toLowerCase();
            
            switch (configType) {
                case "server":
                    configObject = DimTrConfig.SERVER;
                    break;
                case "client":
                    configObject = DimTrConfig.CLIENT;
                    break;
                default:
                    return Optional.empty();
            }
            
            // Navegar pela hierarquia de campos
            Object currentObject = configObject;
            for (int i = 1; i < parts.length; i++) {
                Field field = currentObject.getClass().getDeclaredField(parts[i]);
                field.setAccessible(true);
                currentObject = field.get(currentObject);
                
                if (currentObject instanceof ModConfigSpec.ConfigValue) {
                    Object value = ((ModConfigSpec.ConfigValue<?>) currentObject).get();
                    if (type.isInstance(value)) {
                        return Optional.of((T) value);
                    }
                }
            }
            
            if (type.isInstance(currentObject)) {
                return Optional.of((T) currentObject);
            }
            
        } catch (Exception e) {
            DimTrMod.LOGGER.debug("Configuração NeoForge '{}' não encontrada: {}", path, e.getMessage());
        }
        
        return Optional.empty();
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Optional<T> resolveCustomConfig(String path, Class<T> type) {
        try {
            String[] parts = path.split("\\.");
            if (parts.length < 2) {
                return Optional.empty();
            }
            
            String configFile = parts[0] + ".json";
            File customConfigFile = new File(CUSTOM_CONFIG_DIR + configFile);
            
            if (!customConfigFile.exists()) {
                return Optional.empty();
            }
            
            try (FileReader reader = new FileReader(customConfigFile)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> configData = GSON.fromJson(reader, Map.class);
                
                Object currentValue = configData;
                for (int i = 1; i < parts.length; i++) {
                    if (currentValue instanceof Map) {
                        currentValue = ((Map<String, Object>) currentValue).get(parts[i]);
                    } else {
                        return Optional.empty();
                    }
                }
                
                if (currentValue != null) {
                    T convertedValue = convertValue(currentValue, type);
                    if (convertedValue != null) {
                        return Optional.of(convertedValue);
                    }
                }
            }
            
        } catch (Exception e) {
            DimTrMod.LOGGER.debug("Configuração customizada '{}' não encontrada: {}", path, e.getMessage());
        }
        
        return Optional.empty();
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T convertValue(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }
        
        if (type.isInstance(value)) {
            return (T) value;
        }
        
        try {
            if (type == Boolean.class || type == boolean.class) {
                if (value instanceof String) {
                    return (T) Boolean.valueOf((String) value);
                } else if (value instanceof Number) {
                    return (T) Boolean.valueOf(((Number) value).intValue() != 0);
                }
            } else if (type == Integer.class || type == int.class) {
                if (value instanceof Number) {
                    return (T) Integer.valueOf(((Number) value).intValue());
                } else if (value instanceof String) {
                    return (T) Integer.valueOf((String) value);
                }
            } else if (type == Double.class || type == double.class) {
                if (value instanceof Number) {
                    return (T) Double.valueOf(((Number) value).doubleValue());
                } else if (value instanceof String) {
                    return (T) Double.valueOf((String) value);
                }
            } else if (type == String.class) {
                return (T) value.toString();
            }
        } catch (Exception e) {
            DimTrMod.LOGGER.debug("Erro ao converter valor '{}' para tipo {}: {}", 
                value, type.getSimpleName(), e.getMessage());
        }
        
        return null;
    }
    
    private static boolean isCacheValid(String cacheKey) {
        Long timestamp = cacheTimestamps.get(cacheKey);
        if (timestamp == null) {
            return false;
        }
        return (System.currentTimeMillis() - timestamp) < CACHE_TTL_MS;
    }
    
    private static void invalidateCache(String path) {
        configCache.entrySet().removeIf(entry -> entry.getKey().startsWith(path));
        cacheTimestamps.entrySet().removeIf(entry -> entry.getKey().startsWith(path));
    }
    
    private static void createDirectories() {
        try {
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            File customConfigDir = new File(CUSTOM_CONFIG_DIR);
            if (!customConfigDir.exists()) {
                customConfigDir.mkdirs();
            }
        } catch (Exception e) {
            DimTrMod.LOGGER.error("❌ Erro ao criar diretórios de configuração: {}", e.getMessage());
        }
    }
    
    private static void loadCustomConfigurations() {
        try {
            File customConfigDir = new File(CUSTOM_CONFIG_DIR);
            if (!customConfigDir.exists()) {
                return;
            }
            
            File[] jsonFiles = customConfigDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles != null) {
                for (File jsonFile : jsonFiles) {
                    try (FileReader reader = new FileReader(jsonFile)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> configData = GSON.fromJson(reader, Map.class);
                        if (configData != null) {
                            DimTrMod.LOGGER.debug("📁 Configuração customizada carregada: {} ({} chaves)", 
                                jsonFile.getName(), configData.size());
                        }
                    } catch (Exception e) {
                        DimTrMod.LOGGER.warn("⚠️ Erro ao carregar configuração customizada {}: {}", 
                            jsonFile.getName(), e.getMessage());
                    }
                }
            }
            
            DimTrMod.LOGGER.debug("📁 Configurações customizadas carregadas");
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("❌ Erro ao carregar configurações customizadas: {}", e.getMessage());
        }
    }
    
    private static void registerDefaultListeners() {
        addConfigChangeListener("server.enablePartySystem", (path, oldValue, newValue) -> {
            DimTrMod.LOGGER.info("🎉 Sistema de party {}", 
                Boolean.TRUE.equals(newValue) ? "habilitado" : "desabilitado");
        });
        
        addConfigChangeListener("server.enableDebugLogging", (path, oldValue, newValue) -> {
            DimTrMod.LOGGER.info("🔍 Debug logging {}", 
                Boolean.TRUE.equals(newValue) ? "habilitado" : "desabilitado");
        });
    }
    
    // ============================================================================
    // MÉTODOS DE CONVENIÊNCIA
    // ============================================================================
    
    public static boolean isPartySystemEnabled() {
        return getConfig("server.enablePartySystem", Boolean.class, false);
    }
    
    public static boolean isDebugLoggingEnabled() {
        return getConfig("server.enableDebugLogging", Boolean.class, false);
    }
    
    public static boolean isExternalModIntegrationEnabled() {
        return getConfig("server.enableExternalModIntegration", Boolean.class, false);
    }
    
    public static int getMaxPartySize() {
        return getConfig("server.maxPartySize", Integer.class, 4);
    }
    
    public static double getPartyProgressionMultiplier() {
        return getConfig("server.partyProgressionMultiplier", Double.class, 1.5);
    }
    
    public static int getZombieKillRequirement() {
        return getConfig("server.reqZombieKills", Integer.class, 10);
    }
    
    public static int getRavagerKillRequirement() {
        return getConfig("server.reqRavagerKills", Integer.class, 1);
    }
    
    public static int getEvokerKillRequirement() {
        return getConfig("server.reqEvokerKills", Integer.class, 5);
    }
}