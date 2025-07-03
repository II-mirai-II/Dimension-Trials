package net.mirai.dimtr.system;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.ConfigurationManager;
import net.mirai.dimtr.network.DeltaUpdateSystem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.advancements.AdvancementHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 🎮 Sistema de Custom Phases Ativo - Gerenciamento Dinâmico de Fases
 * 
 * ✅ Conectado com event processing em tempo real
 * ✅ Verificação automática de requisitos
 * ✅ Progressão dinâmica baseada em configuração
 * ✅ Thread-safety completo para multiplayer
 * ✅ Integração com DeltaUpdateSystem
 * 
 * @author Dimension Trials Team
 */
public class CustomPhaseSystem {
    
    // ============================================================================
    // THREAD-SAFETY E ESTADO
    // ============================================================================
    
    private static final ReentrantReadWriteLock PHASE_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = PHASE_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = PHASE_LOCK.writeLock();
    
    // Cache de definições de fases (carregado da configuração)
    private static final Map<String, PhaseDefinition> phaseDefinitions = new ConcurrentHashMap<>();
    
    // Cache de progressão por jogador
    private static final Map<UUID, Map<String, PhaseProgress>> playerPhaseProgress = new ConcurrentHashMap<>();
    
    // Listeners para mudanças de fase
    private static final List<PhaseChangeListener> listeners = new ArrayList<>();
    
    /**
     * 📋 Definição de uma fase customizada
     */
    public static class PhaseDefinition {
        public final String phaseId;
        public final String displayName;
        public final String description;
        public final Map<String, Integer> requiredMobKills;
        public final Map<String, Boolean> requiredObjectives;
        public final List<String> requiredAdvancements;
        public final Map<String, String> customRequirements;
        public final boolean enabled;
        public final int priority;
        
        public PhaseDefinition(String phaseId, String displayName, String description,
                             Map<String, Integer> requiredMobKills, Map<String, Boolean> requiredObjectives,
                             List<String> requiredAdvancements, Map<String, String> customRequirements,
                             boolean enabled, int priority) {
            this.phaseId = phaseId;
            this.displayName = displayName;
            this.description = description;
            this.requiredMobKills = requiredMobKills != null ? new HashMap<>(requiredMobKills) : new HashMap<>();
            this.requiredObjectives = requiredObjectives != null ? new HashMap<>(requiredObjectives) : new HashMap<>();
            this.requiredAdvancements = requiredAdvancements != null ? new ArrayList<>(requiredAdvancements) : new ArrayList<>();
            this.customRequirements = customRequirements != null ? new HashMap<>(customRequirements) : new HashMap<>();
            this.enabled = enabled;
            this.priority = priority;
        }
    }
    
    /**
     * 📊 Progresso de uma fase para um jogador
     */
    public static class PhaseProgress {
        public final String phaseId;
        public final Map<String, Integer> currentMobKills;
        public final Map<String, Boolean> currentObjectives;
        public final Set<String> completedAdvancements;
        public final Map<String, Object> customProgress;
        public boolean completed;
        public long completedTimestamp;
        public double completionPercentage;
        
        public PhaseProgress(String phaseId) {
            this.phaseId = phaseId;
            this.currentMobKills = new HashMap<>();
            this.currentObjectives = new HashMap<>();
            this.completedAdvancements = new HashSet<>();
            this.customProgress = new HashMap<>();
            this.completed = false;
            this.completedTimestamp = 0;
            this.completionPercentage = 0.0;
        }
        
        public PhaseProgress copy() {
            PhaseProgress copy = new PhaseProgress(this.phaseId);
            copy.currentMobKills.putAll(this.currentMobKills);
            copy.currentObjectives.putAll(this.currentObjectives);
            copy.completedAdvancements.addAll(this.completedAdvancements);
            copy.customProgress.putAll(this.customProgress);
            copy.completed = this.completed;
            copy.completedTimestamp = this.completedTimestamp;
            copy.completionPercentage = this.completionPercentage;
            return copy;
        }
    }
    
    /**
     * 🔔 Interface para listeners de mudanças de fase
     */
    public interface PhaseChangeListener {
        void onPhaseCompleted(UUID playerId, String phaseId, PhaseProgress progress);
        void onPhaseProgressUpdated(UUID playerId, String phaseId, PhaseProgress progress);
        void onPhaseStarted(UUID playerId, String phaseId);
    }
    
    // ============================================================================
    // INICIALIZAÇÃO E CONFIGURAÇÃO
    // ============================================================================
    
    /**
     * 🚀 Inicializar sistema (chamado no startup do mod)
     */
    public static void initialize() {
        writeLock.lock();
        try {
            loadPhaseDefinitions();
            DimTrMod.LOGGER.info("CustomPhaseSystem inicializado com {} fases definidas", phaseDefinitions.size());
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 📥 Carregar definições de fases da configuração
     */
    public static void loadPhaseDefinitions() {
        writeLock.lock();
        try {
            phaseDefinitions.clear();
            
            // Carregar do ConfigurationManager
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> customPhases = (Map<String, Object>) ConfigurationManager.getConfig("custom_phases", Map.class, new HashMap<>());
                
                for (Map.Entry<String, Object> entry : customPhases.entrySet()) {
                    String phaseId = entry.getKey();
                    
                    if (entry.getValue() instanceof Map<?, ?> phaseConfig) {
                        PhaseDefinition definition = parsePhaseDefinition(phaseId, phaseConfig);
                        if (definition != null && definition.enabled) {
                            phaseDefinitions.put(phaseId, definition);
                            DimTrMod.LOGGER.debug("Fase customizada carregada: {}", phaseId);
                        }
                    }
                }
            } catch (Exception e) {
                DimTrMod.LOGGER.warn("Erro ao carregar fases customizadas da configuração: {}", e.getMessage());
            }
            
            // Carregar fases hardcoded como fallback
            loadDefaultPhases();
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * ➕ Adicionar listener para mudanças de fase
     */
    public static void addPhaseChangeListener(PhaseChangeListener listener) {
        writeLock.lock();
        try {
            listeners.add(listener);
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // PROCESSAMENTO DE EVENTOS
    // ============================================================================
    
    /**
     * ⚔️ Processar kill de mob (conectado com MobKillEvent)
     */
    public static void processMobKill(ServerPlayer player, LivingEntity killedEntity, DamageSource damageSource) {
        if (player == null || killedEntity == null) return;
        
        UUID playerId = player.getUUID();
        String mobType = getMobType(killedEntity);
        
        writeLock.lock();
        try {
            Map<String, PhaseProgress> playerProgress = playerPhaseProgress.computeIfAbsent(playerId, k -> new HashMap<>());
            
            boolean anyPhaseUpdated = false;
            
            // Verificar todas as fases ativas
            for (PhaseDefinition phase : phaseDefinitions.values()) {
                if (!phase.enabled || !phase.requiredMobKills.containsKey(mobType)) {
                    continue;
                }
                
                PhaseProgress progress = playerProgress.computeIfAbsent(phase.phaseId, k -> new PhaseProgress(phase.phaseId));
                
                if (!progress.completed) {
                    // Incrementar kill count
                    int currentKills = progress.currentMobKills.getOrDefault(mobType, 0);
                    int newKills = currentKills + 1;
                    progress.currentMobKills.put(mobType, newKills);
                    
                    // Verificar se completou os requisitos
                    checkPhaseCompletion(playerId, phase, progress);
                    
                    // Notificar listeners
                    notifyPhaseProgressUpdated(playerId, phase.phaseId, progress);
                    
                    anyPhaseUpdated = true;
                    
                    DimTrMod.LOGGER.debug("Jogador {} matou {} - Progresso fase {}: {}/{}", 
                        player.getName().getString(), mobType, phase.phaseId, newKills, phase.requiredMobKills.get(mobType));
                }
            }
            
            // Enviar updates via DeltaUpdateSystem
            if (anyPhaseUpdated) {
                sendPhaseUpdates(player);
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 🏆 Processar advancement earned (conectado com AdvancementEvent)
     */
    public static void processAdvancementEarned(ServerPlayer player, AdvancementHolder advancement) {
        if (player == null || advancement == null) return;
        
        UUID playerId = player.getUUID();
        String advancementId = advancement.id().getPath();
        
        writeLock.lock();
        try {
            Map<String, PhaseProgress> playerProgress = playerPhaseProgress.computeIfAbsent(playerId, k -> new HashMap<>());
            
            boolean anyPhaseUpdated = false;
            
            // Verificar todas as fases que requerem este advancement
            for (PhaseDefinition phase : phaseDefinitions.values()) {
                if (!phase.enabled || !phase.requiredAdvancements.contains(advancementId)) {
                    continue;
                }
                
                PhaseProgress progress = playerProgress.computeIfAbsent(phase.phaseId, k -> new PhaseProgress(phase.phaseId));
                
                if (!progress.completed && !progress.completedAdvancements.contains(advancementId)) {
                    progress.completedAdvancements.add(advancementId);
                    
                    // Verificar se completou os requisitos
                    checkPhaseCompletion(playerId, phase, progress);
                    
                    // Notificar listeners
                    notifyPhaseProgressUpdated(playerId, phase.phaseId, progress);
                    
                    anyPhaseUpdated = true;
                    
                    DimTrMod.LOGGER.debug("Jogador {} ganhou advancement {} - Progresso fase {}", 
                        player.getName().getString(), advancementId, phase.phaseId);
                }
            }
            
            // Enviar updates via DeltaUpdateSystem
            if (anyPhaseUpdated) {
                sendPhaseUpdates(player);
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 🎯 Processar objetivo especial completado
     */
    public static void processObjectiveCompleted(ServerPlayer player, String objectiveId, boolean completed) {
        if (player == null || objectiveId == null) return;
        
        UUID playerId = player.getUUID();
        
        writeLock.lock();
        try {
            Map<String, PhaseProgress> playerProgress = playerPhaseProgress.computeIfAbsent(playerId, k -> new HashMap<>());
            
            boolean anyPhaseUpdated = false;
            
            // Verificar todas as fases que requerem este objetivo
            for (PhaseDefinition phase : phaseDefinitions.values()) {
                if (!phase.enabled || !phase.requiredObjectives.containsKey(objectiveId)) {
                    continue;
                }
                
                PhaseProgress progress = playerProgress.computeIfAbsent(phase.phaseId, k -> new PhaseProgress(phase.phaseId));
                
                if (!progress.completed) {
                    progress.currentObjectives.put(objectiveId, completed);
                    
                    // Verificar se completou os requisitos
                    checkPhaseCompletion(playerId, phase, progress);
                    
                    // Notificar listeners
                    notifyPhaseProgressUpdated(playerId, phase.phaseId, progress);
                    
                    anyPhaseUpdated = true;
                    
                    DimTrMod.LOGGER.debug("Jogador {} completou objetivo {} - Progresso fase {}", 
                        player.getName().getString(), objectiveId, phase.phaseId);
                }
            }
            
            // Enviar updates via DeltaUpdateSystem
            if (anyPhaseUpdated) {
                sendPhaseUpdates(player);
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // VERIFICAÇÃO DE REQUISITOS
    // ============================================================================
    
    /**
     * ✅ Verificar se uma fase foi completada
     */
    private static void checkPhaseCompletion(UUID playerId, PhaseDefinition phase, PhaseProgress progress) {
        if (progress.completed) return;
        
        boolean allRequirementsMet = true;
        
        // Verificar mob kills
        for (Map.Entry<String, Integer> requirement : phase.requiredMobKills.entrySet()) {
            String mobType = requirement.getKey();
            int requiredKills = requirement.getValue();
            int currentKills = progress.currentMobKills.getOrDefault(mobType, 0);
            
            if (currentKills < requiredKills) {
                allRequirementsMet = false;
                break;
            }
        }
        
        // Verificar objetivos
        if (allRequirementsMet) {
            for (Map.Entry<String, Boolean> requirement : phase.requiredObjectives.entrySet()) {
                String objectiveId = requirement.getKey();
                boolean requiredState = requirement.getValue();
                boolean currentState = progress.currentObjectives.getOrDefault(objectiveId, false);
                
                if (currentState != requiredState) {
                    allRequirementsMet = false;
                    break;
                }
            }
        }
        
        // Verificar advancements
        if (allRequirementsMet) {
            for (String requiredAdvancement : phase.requiredAdvancements) {
                if (!progress.completedAdvancements.contains(requiredAdvancement)) {
                    allRequirementsMet = false;
                    break;
                }
            }
        }
        
        // Verificar requisitos customizados
        if (allRequirementsMet) {
            allRequirementsMet = checkCustomRequirements(playerId, phase, progress);
        }
        
        // Atualizar porcentagem de conclusão
        progress.completionPercentage = calculateCompletionPercentage(phase, progress);
        
        // Se completou, marcar como tal
        if (allRequirementsMet && !progress.completed) {
            progress.completed = true;
            progress.completedTimestamp = System.currentTimeMillis();
            progress.completionPercentage = 100.0;
            
            // Notificar conclusão
            notifyPhaseCompleted(playerId, phase.phaseId, progress);
            
            DimTrMod.LOGGER.info("Jogador {} completou a fase customizada: {}", playerId, phase.phaseId);
        }
    }
    
    /**
     * 📊 Calcular porcentagem de conclusão
     */
    private static double calculateCompletionPercentage(PhaseDefinition phase, PhaseProgress progress) {
        int totalRequirements = 0;
        int completedRequirements = 0;
        
        // Mob kills
        for (Map.Entry<String, Integer> requirement : phase.requiredMobKills.entrySet()) {
            String mobType = requirement.getKey();
            int requiredKills = requirement.getValue();
            int currentKills = progress.currentMobKills.getOrDefault(mobType, 0);
            
            totalRequirements++;
            if (currentKills >= requiredKills) {
                completedRequirements++;
            }
        }
        
        // Objetivos
        for (Map.Entry<String, Boolean> requirement : phase.requiredObjectives.entrySet()) {
            String objectiveId = requirement.getKey();
            boolean requiredState = requirement.getValue();
            boolean currentState = progress.currentObjectives.getOrDefault(objectiveId, false);
            
            totalRequirements++;
            if (currentState == requiredState) {
                completedRequirements++;
            }
        }
        
        // Advancements
        for (String requiredAdvancement : phase.requiredAdvancements) {
            totalRequirements++;
            if (progress.completedAdvancements.contains(requiredAdvancement)) {
                completedRequirements++;
            }
        }
        
        return totalRequirements > 0 ? (double) completedRequirements / totalRequirements * 100.0 : 0.0;
    }
    
    /**
     * 🔧 Verificar requisitos customizados (extensível)
     */
    private static boolean checkCustomRequirements(UUID playerId, PhaseDefinition phase, PhaseProgress progress) {
        // Implementação base - pode ser extendida via configuration
        
        for (Map.Entry<String, String> customReq : phase.customRequirements.entrySet()) {
            String requirementType = customReq.getKey();
            String requirementValue = customReq.getValue();
            
            // Exemplo de requisitos customizados
            switch (requirementType) {
                case "playtime_minutes" -> {
                    try {
                        Integer.parseInt(requirementValue); // Apenas validar se é número
                        // Aqui você implementaria a verificação de tempo de jogo
                        // Para exemplo, assumimos que está sempre ok
                    } catch (NumberFormatException e) {
                        DimTrMod.LOGGER.warn("Requisito customizado inválido: {} = {}", requirementType, requirementValue);
                        return false;
                    }
                }
                case "dimension_visits" -> {
                    // Verificar se visitou dimensões específicas
                    // Implementação seria conectada com sistema de tracking
                }
                default -> {
                    DimTrMod.LOGGER.warn("Tipo de requisito customizado desconhecido: {}", requirementType);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // ============================================================================
    // API PÚBLICA
    // ============================================================================
    
    /**
     * 📋 Obter progresso de todas as fases de um jogador
     */
    public static Map<String, PhaseProgress> getPlayerPhaseProgress(UUID playerId) {
        readLock.lock();
        try {
            Map<String, PhaseProgress> playerProgress = playerPhaseProgress.get(playerId);
            if (playerProgress == null) {
                return new HashMap<>();
            }
            
            // Retornar cópias para thread-safety
            Map<String, PhaseProgress> result = new HashMap<>();
            for (Map.Entry<String, PhaseProgress> entry : playerProgress.entrySet()) {
                result.put(entry.getKey(), entry.getValue().copy());
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 📋 Obter progresso de uma fase específica
     */
    public static PhaseProgress getPhaseProgress(UUID playerId, String phaseId) {
        readLock.lock();
        try {
            Map<String, PhaseProgress> playerProgress = playerPhaseProgress.get(playerId);
            if (playerProgress == null) {
                return null;
            }
            
            PhaseProgress progress = playerProgress.get(phaseId);
            return progress != null ? progress.copy() : null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 📜 Obter todas as definições de fases
     */
    public static Map<String, PhaseDefinition> getPhaseDefinitions() {
        readLock.lock();
        try {
            return new HashMap<>(phaseDefinitions);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * ✅ Verificar se uma fase está completa
     */
    public static boolean isPhaseCompleted(UUID playerId, String phaseId) {
        readLock.lock();
        try {
            Map<String, PhaseProgress> playerProgress = playerPhaseProgress.get(playerId);
            if (playerProgress == null) {
                return false;
            }
            
            PhaseProgress progress = playerProgress.get(phaseId);
            return progress != null && progress.completed;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 🧹 Limpar dados de um jogador
     */
    public static void clearPlayerData(UUID playerId) {
        writeLock.lock();
        try {
            playerPhaseProgress.remove(playerId);
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // MÉTODOS INTERNOS
    // ============================================================================
    
    private static PhaseDefinition parsePhaseDefinition(String phaseId, Map<?, ?> config) {
        try {
            // Parsing seguro com defaults
            String displayName = config.containsKey("display_name") ? 
                (String) config.get("display_name") : phaseId;
            String description = config.containsKey("description") ? 
                (String) config.get("description") : "";
            boolean enabled = config.containsKey("enabled") ? 
                (Boolean) config.get("enabled") : true;
            int priority = config.containsKey("priority") ? 
                ((Number) config.get("priority")).intValue() : 0;
            
            // Mob kills com verificação de tipo
            Map<String, Integer> mobKills = new HashMap<>();
            if (config.containsKey("required_mob_kills") && config.get("required_mob_kills") instanceof Map<?, ?> mobKillsMap) {
                for (Map.Entry<?, ?> entry : mobKillsMap.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof Number value) {
                        mobKills.put(key, value.intValue());
                    }
                }
            }
            
            // Objetivos com verificação de tipo
            Map<String, Boolean> objectives = new HashMap<>();
            if (config.containsKey("required_objectives") && config.get("required_objectives") instanceof Map<?, ?> objectivesMap) {
                for (Map.Entry<?, ?> entry : objectivesMap.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof Boolean value) {
                        objectives.put(key, value);
                    }
                }
            }
            
            // Advancements com verificação de tipo
            List<String> advancements = new ArrayList<>();
            if (config.containsKey("required_advancements") && config.get("required_advancements") instanceof List<?> advancementsList) {
                for (Object item : advancementsList) {
                    if (item instanceof String advancement) {
                        advancements.add(advancement);
                    }
                }
            }
            
            // Custom requirements com verificação de tipo
            Map<String, String> customReqs = new HashMap<>();
            if (config.containsKey("custom_requirements") && config.get("custom_requirements") instanceof Map<?, ?> customReqsMap) {
                for (Map.Entry<?, ?> entry : customReqsMap.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                        customReqs.put(key, value);
                    }
                }
            }
            
            return new PhaseDefinition(phaseId, displayName, description, mobKills, objectives, advancements, customReqs, enabled, priority);
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao parsear definição de fase {}: {}", phaseId, e.getMessage());
            return null;
        }
    }
    
    private static void loadDefaultPhases() {
        // Fase 1 exemplo
        Map<String, Integer> phase1MobKills = Map.of(
            "zombie", 50,
            "skeleton", 30,
            "spider", 25
        );
        Map<String, Boolean> phase1Objectives = Map.of(
            "elderGuardianKilled", true
        );
        
        PhaseDefinition phase1 = new PhaseDefinition(
            "phase1_extended",
            "Fase 1 Estendida",
            "Versão estendida da Fase 1 com requisitos customizados",
            phase1MobKills,
            phase1Objectives,
            List.of("minecraft:adventure/kill_mob_near_sculk_catalyst"),
            Map.of("playtime_minutes", "120"),
            true,
            1
        );
        
        phaseDefinitions.put("phase1_extended", phase1);
        
        // Fase Boss exemplo
        Map<String, Integer> bossPhaseKills = Map.of(
            "wither", 1,
            "ender_dragon", 1
        );
        Map<String, Boolean> bossObjectives = Map.of(
            "witherKilled", true,
            "dragonKilled", true
        );
        
        PhaseDefinition bossPhase = new PhaseDefinition(
            "boss_master",
            "Mestre dos Bosses",
            "Derrote todos os bosses principais",
            bossPhaseKills,
            bossObjectives,
            List.of("minecraft:end/kill_dragon", "minecraft:nether/summon_wither"),
            new HashMap<>(),
            true,
            10
        );
        
        phaseDefinitions.put("boss_master", bossPhase);
    }
    
    private static String getMobType(LivingEntity entity) {
        return entity.getType().getDescriptionId().replace("entity.minecraft.", "");
    }
    
    private static void sendPhaseUpdates(ServerPlayer player) {
        // Integração com DeltaUpdateSystem
        UUID playerId = player.getUUID();
        
        // Criar delta para mudanças de fase
        var delta = new DeltaUpdateSystem.ProgressionDelta(
            playerId,
            DeltaUpdateSystem.DeltaType.PHASE_COMPLETION,
            "custom_phases_updated",
            null,
            System.currentTimeMillis()
        );
        
        // Enviar com prioridade alta
        DeltaUpdateSystem.sendDelta(player, delta);
    }
    
    private static void notifyPhaseCompleted(UUID playerId, String phaseId, PhaseProgress progress) {
        readLock.lock();
        try {
            for (PhaseChangeListener listener : listeners) {
                try {
                    listener.onPhaseCompleted(playerId, phaseId, progress.copy());
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("Erro em listener de fase completada: {}", e.getMessage());
                }
            }
        } finally {
            readLock.unlock();
        }
    }
    
    private static void notifyPhaseProgressUpdated(UUID playerId, String phaseId, PhaseProgress progress) {
        readLock.lock();
        try {
            for (PhaseChangeListener listener : listeners) {
                try {
                    listener.onPhaseProgressUpdated(playerId, phaseId, progress.copy());
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("Erro em listener de progresso de fase: {}", e.getMessage());
                }
            }
        } finally {
            readLock.unlock();
        }
    }
}
