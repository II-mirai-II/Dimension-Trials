package net.mirai.dimtr.system;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.ConfigurationManager;
import net.mirai.dimtr.network.DeltaUpdateSystem;
import net.mirai.dimtr.network.BatchSyncProcessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 🏆 Sistema de Validação de Boss Kills - Verificação Rigorosa e Anti-Cheat
 * 
 * ✅ Validação de legitimidade de boss kills
 * ✅ Anti-cheat integrado
 * ✅ Verificação de contexto e ambiente
 * ✅ Sistema de reputação de jogadores
 * ✅ Logs detalhados para auditoria
 * ✅ Configuração flexível de regras
 * 
 * @author Dimension Trials Team
 */
public class BossKillValidator {
    
    // ============================================================================
    // THREAD-SAFETY E ESTADO
    // ============================================================================
    
    private static final ReentrantReadWriteLock VALIDATION_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = VALIDATION_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = VALIDATION_LOCK.writeLock();
    
    // Cache de configurações de validação
    private static final Map<String, BossValidationConfig> bossConfigs = new ConcurrentHashMap<>();
    
    // Sistema de reputação de jogadores
    private static final Map<UUID, PlayerReputation> playerReputations = new ConcurrentHashMap<>();
    
    // Histórico de kills recentes para análise de padrões
    private static final Map<UUID, List<BossKillRecord>> recentKills = new ConcurrentHashMap<>();
    
    // Lista negra temporária de jogadores suspeitos
    private static final Set<UUID> suspiciousPlayers = ConcurrentHashMap.newKeySet();
    
    /**
     * 🎯 Configuração de validação por boss
     */
    public static class BossValidationConfig {
        public final String bossId;
        public final String displayName;
        public final boolean requiresDirectDamage;
        public final boolean requiresValidDimension;
        public final Set<String> allowedDimensions;
        public final boolean requiresMinimumDistance;
        public final double minimumDistance;
        public final boolean requiresTimeLimit;
        public final long maximumFightDuration;
        public final boolean allowsAssistance;
        public final int maxAssistants;
        public final boolean requiresHealthThreshold;
        public final double minimumHealthPercentage;
        public final Map<String, Object> customValidations;
        
        public BossValidationConfig(String bossId, String displayName, boolean requiresDirectDamage,
                                  boolean requiresValidDimension, Set<String> allowedDimensions,
                                  boolean requiresMinimumDistance, double minimumDistance,
                                  boolean requiresTimeLimit, long maximumFightDuration,
                                  boolean allowsAssistance, int maxAssistants,
                                  boolean requiresHealthThreshold, double minimumHealthPercentage,
                                  Map<String, Object> customValidations) {
            this.bossId = bossId;
            this.displayName = displayName;
            this.requiresDirectDamage = requiresDirectDamage;
            this.requiresValidDimension = requiresValidDimension;
            this.allowedDimensions = allowedDimensions != null ? new HashSet<>(allowedDimensions) : new HashSet<>();
            this.requiresMinimumDistance = requiresMinimumDistance;
            this.minimumDistance = minimumDistance;
            this.requiresTimeLimit = requiresTimeLimit;
            this.maximumFightDuration = maximumFightDuration;
            this.allowsAssistance = allowsAssistance;
            this.maxAssistants = maxAssistants;
            this.requiresHealthThreshold = requiresHealthThreshold;
            this.minimumHealthPercentage = minimumHealthPercentage;
            this.customValidations = customValidations != null ? new HashMap<>(customValidations) : new HashMap<>();
        }
    }
    
    /**
     * 📊 Reputação de um jogador
     */
    public static class PlayerReputation {
        public final UUID playerId;
        public int legitimateKills;
        public int suspiciousKills;
        public int invalidKills;
        public double reputationScore;
        public long lastKillTime;
        public long firstKillTime;
        public boolean isBlacklisted;
        public String blacklistReason;
        
        public PlayerReputation(UUID playerId) {
            this.playerId = playerId;
            this.legitimateKills = 0;
            this.suspiciousKills = 0;
            this.invalidKills = 0;
            this.reputationScore = 100.0; // Começar com reputação perfeita
            this.lastKillTime = 0;
            this.firstKillTime = 0;
            this.isBlacklisted = false;
            this.blacklistReason = "";
        }
        
        public void updateReputation(ValidationResult result) {
            switch (result.status) {
                case VALID -> {
                    legitimateKills++;
                    reputationScore = Math.min(100.0, reputationScore + 1.0);
                }
                case SUSPICIOUS -> {
                    suspiciousKills++;
                    reputationScore = Math.max(0.0, reputationScore - 5.0);
                }
                case INVALID -> {
                    invalidKills++;
                    reputationScore = Math.max(0.0, reputationScore - 10.0);
                }
            }
            
            lastKillTime = System.currentTimeMillis();
            if (firstKillTime == 0) {
                firstKillTime = lastKillTime;
            }
            
            // Auto-blacklist se reputação muito baixa
            if (reputationScore <= 20.0 && (invalidKills + suspiciousKills) >= 5) {
                isBlacklisted = true;
                blacklistReason = "Múltiplas tentativas de kill inválido/suspeito";
            }
        }
    }
    
    /**
     * 📝 Registro de boss kill para histórico
     */
    public static class BossKillRecord {
        public final UUID playerId;
        public final String bossId;
        public final String bossType;
        public final long timestamp;
        public final BlockPos killLocation;
        public final String dimension;
        public final DamageSource damageSource;
        public final ValidationResult validationResult;
        public final Map<String, Object> metadata;
        
        public BossKillRecord(UUID playerId, String bossId, String bossType, BlockPos killLocation,
                            String dimension, DamageSource damageSource, ValidationResult validationResult) {
            this.playerId = playerId;
            this.bossId = bossId;
            this.bossType = bossType;
            this.timestamp = System.currentTimeMillis();
            this.killLocation = killLocation;
            this.dimension = dimension;
            this.damageSource = damageSource;
            this.validationResult = validationResult;
            this.metadata = new HashMap<>();
        }
    }
    
    /**
     * ✅ Resultado de validação
     */
    public static class ValidationResult {
        public final ValidationStatus status;
        public final String reason;
        public final double confidence;
        public final Map<String, Object> details;
        
        public ValidationResult(ValidationStatus status, String reason, double confidence) {
            this.status = status;
            this.reason = reason;
            this.confidence = confidence;
            this.details = new HashMap<>();
        }
        
        public ValidationResult(ValidationStatus status, String reason, double confidence, Map<String, Object> details) {
            this.status = status;
            this.reason = reason;
            this.confidence = confidence;
            this.details = details != null ? new HashMap<>(details) : new HashMap<>();
        }
    }
    
    /**
     * 🚦 Status de validação
     */
    public enum ValidationStatus {
        VALID("Válido", "Boss kill legitimamente validado"),
        SUSPICIOUS("Suspeito", "Boss kill com características suspeitas"),
        INVALID("Inválido", "Boss kill não atende aos critérios de validação");
        
        public final String displayName;
        public final String description;
        
        ValidationStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }
    
    // ============================================================================
    // INICIALIZAÇÃO
    // ============================================================================
    
    /**
     * 🚀 Inicializar sistema de validação
     */
    public static void initialize() {
        writeLock.lock();
        try {
            loadBossConfigurations();
            DimTrMod.LOGGER.info("BossKillValidator inicializado com {} configurações de boss", bossConfigs.size());
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 📥 Carregar configurações de boss
     */
    private static void loadBossConfigurations() {
        bossConfigs.clear();
        
        // Carregar configurações customizadas
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> bossValidationConfig = (Map<String, Object>) ConfigurationManager.getConfig("boss_validation", Map.class, new HashMap<>());
            
            for (Map.Entry<String, Object> entry : bossValidationConfig.entrySet()) {
                String bossId = entry.getKey();
                if (entry.getValue() instanceof Map<?, ?> config) {
                    BossValidationConfig bossConfig = parseBossConfig(bossId, config);
                    if (bossConfig != null) {
                        bossConfigs.put(bossId, bossConfig);
                    }
                }
            }
        } catch (Exception e) {
            DimTrMod.LOGGER.warn("Erro ao carregar configurações de validação de boss: {}", e.getMessage());
        }
        
        // Carregar configurações padrão
        loadDefaultBossConfigurations();
    }
    
    /**
     * 📋 Carregar configurações padrão dos bosses
     */
    private static void loadDefaultBossConfigurations() {
        // Ender Dragon
        bossConfigs.put("ender_dragon", new BossValidationConfig(
            "ender_dragon", "Ender Dragon",
            true, true, Set.of("minecraft:the_end"),
            true, 10.0,
            true, 600000, // 10 minutos
            true, 3,
            true, 0.9,
            Map.of("requires_end_crystals_destroyed", true)
        ));
        
        // Wither
        bossConfigs.put("wither", new BossValidationConfig(
            "wither", "Wither",
            true, true, Set.of("minecraft:overworld", "minecraft:the_nether"),
            true, 5.0,
            true, 300000, // 5 minutos
            true, 2,
            true, 0.8,
            Map.of("requires_natural_spawn", false)
        ));
        
        // Elder Guardian
        bossConfigs.put("elder_guardian", new BossValidationConfig(
            "elder_guardian", "Elder Guardian",
            true, true, Set.of("minecraft:overworld"),
            true, 8.0,
            true, 180000, // 3 minutos
            true, 4,
            true, 0.7,
            Map.of("requires_ocean_monument", true)
        ));
        
        // Warden
        bossConfigs.put("warden", new BossValidationConfig(
            "warden", "Warden",
            true, true, Set.of("minecraft:overworld"),
            true, 6.0,
            true, 240000, // 4 minutos
            true, 1,
            true, 0.9,
            Map.of("requires_deep_dark", true, "max_light_level", 0)
        ));
    }
    
    // ============================================================================
    // API PRINCIPAL DE VALIDAÇÃO
    // ============================================================================
    
    /**
     * ✅ Validar boss kill (método principal)
     */
    public static boolean validateKill(UUID playerId, String bossId, DamageSource source) {
        ValidationResult result = validateKillDetailed(playerId, bossId, source);
        return result.status == ValidationStatus.VALID;
    }
    
    /**
     * 🔍 Validar boss kill com detalhes completos
     */
    public static ValidationResult validateKillDetailed(UUID playerId, String bossId, DamageSource source) {
        if (playerId == null || bossId == null || source == null) {
            return new ValidationResult(ValidationStatus.INVALID, "Parâmetros nulos", 0.0);
        }
        
        readLock.lock();
        try {
            // Verificar se jogador está na lista negra
            PlayerReputation reputation = playerReputations.get(playerId);
            if (reputation != null && reputation.isBlacklisted) {
                return new ValidationResult(ValidationStatus.INVALID, 
                    "Jogador na lista negra: " + reputation.blacklistReason, 0.0);
            }
            
            // Verificar se é um jogador suspeito
            if (suspiciousPlayers.contains(playerId)) {
                return new ValidationResult(ValidationStatus.SUSPICIOUS, 
                    "Jogador sob observação por atividade suspeita", 0.5);
            }
            
            // Obter configuração do boss
            BossValidationConfig config = bossConfigs.get(bossId);
            if (config == null) {
                return new ValidationResult(ValidationStatus.INVALID, 
                    "Configuração de validação não encontrada para boss: " + bossId, 0.0);
            }
            
            // Validar kill usando context
            return validateWithContext(playerId, bossId, source, config);
            
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 🔍 Validar boss kill com contexto completo
     */
    public static ValidationResult validateBossKillEvent(ServerPlayer player, LivingEntity boss, DamageSource source) {
        if (player == null || boss == null || source == null) {
            return new ValidationResult(ValidationStatus.INVALID, "Entidades nulas", 0.0);
        }
        
        UUID playerId = player.getUUID();
        String bossId = getBossId(boss);
        String bossType = boss.getType().getDescriptionId();
        
        writeLock.lock();
        try {
            // Executar validação detalhada
            ValidationResult result = validateKillDetailed(playerId, bossId, source);
            
            // Criar registro detalhado
            BossKillRecord record = new BossKillRecord(
                playerId, bossId, bossType, boss.blockPosition(), 
                getDimensionId(boss.level()), source, result
            );
            
            // Adicionar metadados contextuais
            record.metadata.put("player_health", player.getHealth());
            record.metadata.put("player_armor", player.getArmorValue());
            record.metadata.put("boss_max_health", boss.getMaxHealth());
            record.metadata.put("damage_amount", source.getEntity() != null && source.getEntity() instanceof LivingEntity livingEntity ? 
                livingEntity.getHealth() : 0.0);
            record.metadata.put("kill_time", System.currentTimeMillis());
            
            // Salvar no histórico
            saveKillRecord(record);
            
            // Atualizar reputação do jogador
            updatePlayerReputation(playerId, result);
            
            // Notificar sistema se válido
            if (result.status == ValidationStatus.VALID) {
                notifyValidBossKill(player, bossId, result);
            } else {
                notifyInvalidBossKill(player, bossId, result);
            }
            
            return result;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // VALIDAÇÕES ESPECÍFICAS
    // ============================================================================
    
    private static ValidationResult validateWithContext(UUID playerId, String bossId, 
                                                       DamageSource source, BossValidationConfig config) {
        double confidence = 1.0;
        List<String> issues = new ArrayList<>();
        Map<String, Object> details = new HashMap<>();
        
        // Validação 1: Fonte de dano
        if (config.requiresDirectDamage) {
            var damageEntity = source.getEntity();
            if (damageEntity == null || !damageEntity.getUUID().equals(playerId)) {
                confidence -= 0.3;
                issues.add("Dano não foi direto do jogador");
            }
        }
        
        // Validação 2: Dimensão
        if (config.requiresValidDimension && source.getEntity() instanceof LivingEntity entity) {
            String currentDimension = getDimensionId(entity.level());
            if (!config.allowedDimensions.contains(currentDimension)) {
                confidence -= 0.4;
                issues.add("Boss morto em dimensão não permitida: " + currentDimension);
            }
            details.put("dimension", currentDimension);
        }
        
        // Validação 3: Distância mínima
        if (config.requiresMinimumDistance && source.getEntity() instanceof LivingEntity attacker) {
            if (source.getDirectEntity() instanceof LivingEntity target) {
                double distance = attacker.distanceTo(target);
                if (distance > config.minimumDistance) {
                    confidence -= 0.2;
                    issues.add(String.format("Distância muito grande: %.2f > %.2f", distance, config.minimumDistance));
                }
                details.put("kill_distance", distance);
            }
        }
        
        // Validação 4: Verificação de padrões suspeitos
        ValidationResult patternCheck = checkSuspiciousPatterns(playerId, bossId);
        if (patternCheck.status == ValidationStatus.SUSPICIOUS) {
            confidence -= 0.3;
            issues.add("Padrão suspeito detectado: " + patternCheck.reason);
        }
        
        // Validação 5: Reputação do jogador
        PlayerReputation reputation = playerReputations.get(playerId);
        if (reputation != null) {
            double reputationFactor = reputation.reputationScore / 100.0;
            confidence *= reputationFactor;
            details.put("player_reputation", reputation.reputationScore);
            
            if (reputation.reputationScore < 50.0) {
                issues.add("Baixa reputação do jogador: " + reputation.reputationScore);
            }
        }
        
        // Validação 6: Validações customizadas por boss
        ValidationResult customResult = validateCustomRequirements(playerId, bossId, source, config);
        confidence *= customResult.confidence;
        if (!customResult.reason.isEmpty()) {
            issues.add("Validação customizada: " + customResult.reason);
        }
        
        // Determinar status final
        ValidationStatus status;
        if (confidence >= 0.8) {
            status = ValidationStatus.VALID;
        } else if (confidence >= 0.4) {
            status = ValidationStatus.SUSPICIOUS;
        } else {
            status = ValidationStatus.INVALID;
        }
        
        String reason = issues.isEmpty() ? "Validação passou em todos os testes" : 
                       String.join("; ", issues);
        
        details.put("confidence_score", confidence);
        details.put("validation_issues", issues);
        
        return new ValidationResult(status, reason, confidence, details);
    }
    
    private static ValidationResult checkSuspiciousPatterns(UUID playerId, String bossId) {
        List<BossKillRecord> playerKills = recentKills.get(playerId);
        if (playerKills == null || playerKills.isEmpty()) {
            return new ValidationResult(ValidationStatus.VALID, "", 1.0);
        }
        
        long currentTime = System.currentTimeMillis();
        long oneHour = 3600000; // 1 hora em milissegundos
        
        // Contar kills recentes do mesmo boss
        long recentSameBossKills = playerKills.stream()
            .filter(record -> record.bossId.equals(bossId))
            .filter(record -> (currentTime - record.timestamp) <= oneHour)
            .count();
        
        // Suspeito se muitos kills do mesmo boss em pouco tempo
        if (recentSameBossKills >= 3) {
            return new ValidationResult(ValidationStatus.SUSPICIOUS, 
                "Muitos kills do mesmo boss em 1 hora: " + recentSameBossKills, 0.3);
        }
        
        // Verificar velocidade de kills
        List<BossKillRecord> recentKills = playerKills.stream()
            .filter(record -> (currentTime - record.timestamp) <= oneHour)
            .sorted((a, b) -> Long.compare(b.timestamp, a.timestamp))
            .toList();
        
        if (recentKills.size() >= 2) {
            long timeBetweenKills = recentKills.get(0).timestamp - recentKills.get(1).timestamp;
            if (timeBetweenKills < 60000) { // Menos de 1 minuto
                return new ValidationResult(ValidationStatus.SUSPICIOUS, 
                    "Intervalo muito curto entre boss kills: " + (timeBetweenKills / 1000) + "s", 0.4);
            }
        }
        
        return new ValidationResult(ValidationStatus.VALID, "", 1.0);
    }
    
    private static ValidationResult validateCustomRequirements(UUID playerId, String bossId, 
                                                             DamageSource source, BossValidationConfig config) {
        double confidence = 1.0;
        StringBuilder issues = new StringBuilder();
        
        for (Map.Entry<String, Object> requirement : config.customValidations.entrySet()) {
            String requirementType = requirement.getKey();
            Object requirementValue = requirement.getValue();
            
            switch (requirementType) {
                case "requires_end_crystals_destroyed" -> {
                    if ((Boolean) requirementValue && bossId.equals("ender_dragon")) {
                        // Aqui você verificaria se os cristais do End foram destruídos
                        // Para este exemplo, assumimos que está ok
                    }
                }
                case "requires_ocean_monument" -> {
                    if ((Boolean) requirementValue && bossId.equals("elder_guardian")) {
                        // Verificar se está em um monumento oceânico
                        // Implementação específica seria necessária
                    }
                }
                case "requires_deep_dark" -> {
                    if ((Boolean) requirementValue && bossId.equals("warden")) {
                        // Verificar se está no bioma Deep Dark
                        // Implementação específica seria necessária
                    }
                }
                case "max_light_level" -> {
                    if (requirementValue instanceof Number && bossId.equals("warden")) {
                        // Verificar nível de luz
                        // Implementação específica seria necessária
                    }
                }
                default -> {
                    DimTrMod.LOGGER.warn("Requisito customizado desconhecido: {}", requirementType);
                }
            }
        }
        
        return new ValidationResult(ValidationStatus.VALID, issues.toString(), confidence);
    }
    
    // ============================================================================
    // SISTEMA DE REPUTAÇÃO
    // ============================================================================
    
    private static void updatePlayerReputation(UUID playerId, ValidationResult result) {
        PlayerReputation reputation = playerReputations.computeIfAbsent(playerId, PlayerReputation::new);
        reputation.updateReputation(result);
        
        // Adicionar à lista de suspeitos se necessário
        if (reputation.reputationScore < 60.0 && result.status == ValidationStatus.SUSPICIOUS) {
            suspiciousPlayers.add(playerId);
            DimTrMod.LOGGER.warn("Jogador {} adicionado à lista de suspeitos. Reputação: {}", 
                playerId, reputation.reputationScore);
        }
        
        // Remover da lista de suspeitos se reputação melhorar
        if (reputation.reputationScore >= 80.0 && result.status == ValidationStatus.VALID) {
            suspiciousPlayers.remove(playerId);
        }
    }
    
    // ============================================================================
    // NOTIFICAÇÕES E INTEGRAÇÃO
    // ============================================================================
    
    private static void notifyValidBossKill(ServerPlayer player, String bossId, ValidationResult result) {
        try {
            // Criar delta para DeltaUpdateSystem
            var delta = new DeltaUpdateSystem.ProgressionDelta(
                player.getUUID(),
                DeltaUpdateSystem.DeltaType.BOSS_KILL,
                "boss_kill_validated:" + bossId,
                false,
                true
            );
            
            // Enviar com alta prioridade
            DeltaUpdateSystem.sendDelta(player, delta);
            
            // Também adicionar ao batch
            BatchSyncProcessor.addToBatch(
                player.getUUID(),
                BatchSyncProcessor.BatchType.PROGRESSION_DELTA,
                result,
                9 // Prioridade muito alta para boss kills
            );
            
            DimTrMod.LOGGER.info("Boss kill válido confirmado: {} matou {} (confiança: {:.2f})", 
                player.getName().getString(), bossId, result.confidence);
                
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao notificar boss kill válido: {}", e.getMessage());
        }
    }
    
    private static void notifyInvalidBossKill(ServerPlayer player, String bossId, ValidationResult result) {
        DimTrMod.LOGGER.warn("Boss kill inválido detectado: {} tentou matar {} - Razão: {} (confiança: {:.2f})", 
            player.getName().getString(), bossId, result.reason, result.confidence);
        
        // Aqui você poderia implementar penalidades ou notificações para admins
    }
    
    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================
    
    private static String getBossId(LivingEntity entity) {
        if (entity instanceof EnderDragon) return "ender_dragon";
        if (entity instanceof WitherBoss) return "wither";
        if (entity instanceof ElderGuardian) return "elder_guardian";
        if (entity instanceof Warden) return "warden";
        
        // Fallback para entidades customizadas
        return entity.getType().getDescriptionId().replace("entity.minecraft.", "");
    }
    
    private static String getDimensionId(Level level) {
        return level.dimension().location().toString();
    }
    
    private static void saveKillRecord(BossKillRecord record) {
        List<BossKillRecord> playerKills = recentKills.computeIfAbsent(record.playerId, k -> new ArrayList<>());
        playerKills.add(record);
        
        // Limitar histórico a 50 registros por jogador
        if (playerKills.size() > 50) {
            playerKills.remove(0);
        }
        
        // Limpar registros antigos (mais de 24 horas)
        long oneDayAgo = System.currentTimeMillis() - 86400000;
        playerKills.removeIf(killRecord -> killRecord.timestamp < oneDayAgo);
    }
    
    private static BossValidationConfig parseBossConfig(String bossId, Map<?, ?> config) {
        try {
            // Parsing similar ao CustomPhaseSystem, mas para boss configs
            String displayName = config.containsKey("display_name") ? 
                (String) config.get("display_name") : bossId;
            
            boolean requiresDirectDamage = config.containsKey("requires_direct_damage") ? 
                (Boolean) config.get("requires_direct_damage") : true;
            
            // ... outros campos similar ao pattern usado no CustomPhaseSystem
            
            return new BossValidationConfig(bossId, displayName, requiresDirectDamage, 
                true, Set.of("minecraft:overworld"), true, 5.0, true, 300000, 
                true, 2, true, 0.8, new HashMap<>());
                
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao parsear configuração de boss {}: {}", bossId, e.getMessage());
            return null;
        }
    }
    
    // ============================================================================
    // API PÚBLICA DE CONSULTA
    // ============================================================================
    
    /**
     * 📊 Obter reputação de um jogador
     */
    public static PlayerReputation getPlayerReputation(UUID playerId) {
        readLock.lock();
        try {
            return playerReputations.get(playerId);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 📝 Obter histórico de boss kills de um jogador
     */
    public static List<BossKillRecord> getPlayerKillHistory(UUID playerId) {
        readLock.lock();
        try {
            List<BossKillRecord> history = recentKills.get(playerId);
            return history != null ? new ArrayList<>(history) : new ArrayList<>();
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
            playerReputations.remove(playerId);
            recentKills.remove(playerId);
            suspiciousPlayers.remove(playerId);
        } finally {
            writeLock.unlock();
        }
    }
}
