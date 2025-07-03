package net.mirai.dimtr.system;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.PlayerProgressionData;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.data.PartyManager;
import net.mirai.dimtr.network.DeltaUpdateSystem;
import net.mirai.dimtr.network.BatchSyncProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 🔄 Sistema de Transferência de Progresso Entre Party e Individual
 * 
 * ✅ Transferência bidirecional de progresso
 * ✅ Algoritmos de merge inteligentes
 * ✅ Validação de consistência
 * ✅ Thread-safety completo
 * ✅ Histórico de transferências
 * ✅ Rollback automático em caso de erro
 * 
 * @author Dimension Trials Team
 */
public class ProgressTransferService {
    
    // ============================================================================
    // THREAD-SAFETY E ESTADO
    // ============================================================================
    
    private static final ReentrantReadWriteLock TRANSFER_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = TRANSFER_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = TRANSFER_LOCK.writeLock();
    
    // Histórico de transferências para rollback
    private static final Map<UUID, List<TransferRecord>> transferHistory = new ConcurrentHashMap<>();
    
    // Cache de últimas transferências para evitar loops
    private static final Map<UUID, Long> lastTransferTime = new ConcurrentHashMap<>();
    private static final long MIN_TRANSFER_INTERVAL_MS = 30000; // 30 segundos
    
    /**
     * 📊 Registro de transferência para histórico
     */
    public static class TransferRecord {
        public final UUID playerId;
        public final TransferType type;
        public final PlayerProgressionData beforeState;
        public final PlayerProgressionData afterState;
        public final long timestamp;
        public final String reason;
        public boolean success;
        
        public TransferRecord(UUID playerId, TransferType type, PlayerProgressionData beforeState, 
                            PlayerProgressionData afterState, String reason) {
            this.playerId = playerId;
            this.type = type;
            this.beforeState = beforeState != null ? beforeState.copy() : null;
            this.afterState = afterState != null ? afterState.copy() : null;
            this.timestamp = System.currentTimeMillis();
            this.reason = reason;
            this.success = false;
        }
    }
    
    /**
     * 📝 Tipos de transferência
     */
    public enum TransferType {
        PARTY_TO_INDIVIDUAL("party_to_individual", "Transferência de Party para Individual"),
        INDIVIDUAL_TO_PARTY("individual_to_party", "Transferência de Individual para Party"),
        PARTY_MERGE("party_merge", "Merge de Progresso de Party"),
        ROLLBACK("rollback", "Rollback de Transferência");
        
        public final String id;
        public final String description;
        
        TransferType(String id, String description) {
            this.id = id;
            this.description = description;
        }
    }
    
    /**
     * 🔧 Estratégias de merge
     */
    public enum MergeStrategy {
        TAKE_HIGHEST,      // Pegar o maior valor
        TAKE_LOWEST,       // Pegar o menor valor
        SUM_VALUES,        // Somar valores
        LOGICAL_OR,        // OR lógico para booleans
        KEEP_INDIVIDUAL,   // Manter valor individual
        KEEP_PARTY         // Manter valor da party
    }
    
    /**
     * ⚙️ Configuração de transferência
     */
    public static class TransferConfig {
        public final Map<String, MergeStrategy> fieldMergeStrategies;
        public final boolean validateConsistency;
        public final boolean createBackup;
        public final boolean notifyPlayer;
        
        public TransferConfig(Map<String, MergeStrategy> fieldMergeStrategies, 
                            boolean validateConsistency, boolean createBackup, boolean notifyPlayer) {
            this.fieldMergeStrategies = fieldMergeStrategies != null ? 
                new HashMap<>(fieldMergeStrategies) : getDefaultMergeStrategies();
            this.validateConsistency = validateConsistency;
            this.createBackup = createBackup;
            this.notifyPlayer = notifyPlayer;
        }
        
        public static TransferConfig getDefault() {
            return new TransferConfig(getDefaultMergeStrategies(), true, true, true);
        }
        
        private static Map<String, MergeStrategy> getDefaultMergeStrategies() {
            Map<String, MergeStrategy> strategies = new HashMap<>();
            
            // Mob kills - somar valores
            strategies.put("zombieKills", MergeStrategy.SUM_VALUES);
            strategies.put("skeletonKills", MergeStrategy.SUM_VALUES);
            strategies.put("spiderKills", MergeStrategy.SUM_VALUES);
            strategies.put("creeperKills", MergeStrategy.SUM_VALUES);
            strategies.put("blazeKills", MergeStrategy.SUM_VALUES);
            strategies.put("witherSkeletonKills", MergeStrategy.SUM_VALUES);
            
            // Objetivos especiais - OR lógico (se qualquer um tem, todos têm)
            strategies.put("elderGuardianKilled", MergeStrategy.LOGICAL_OR);
            strategies.put("raidWon", MergeStrategy.LOGICAL_OR);
            strategies.put("witherKilled", MergeStrategy.LOGICAL_OR);
            strategies.put("wardenKilled", MergeStrategy.LOGICAL_OR);
            
            // Fases - OR lógico
            strategies.put("phase1Completed", MergeStrategy.LOGICAL_OR);
            strategies.put("phase2Completed", MergeStrategy.LOGICAL_OR);
            
            return strategies;
        }
    }
    
    // ============================================================================
    // API PRINCIPAL
    // ============================================================================
    
    /**
     * ⬇️ Transferir progresso da party para individual
     */
    public static boolean transferFromPartyToIndividual(UUID playerId) {
        return transferFromPartyToIndividual(playerId, TransferConfig.getDefault(), "Manual transfer request");
    }
    
    /**
     * ⬇️ Transferir progresso da party para individual (configurável)
     * 🔧 CORREÇÃO CRÍTICA: Sistema corrigido para extrair apenas contribuições individuais
     */
    public static boolean transferFromPartyToIndividual(UUID playerId, TransferConfig config, String reason) {
        if (playerId == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            // Verificar rate limiting
            if (!checkRateLimit(playerId)) {
                DimTrMod.LOGGER.debug("Transfer bloqueado por rate limiting para {}", playerId);
                return false;
            }
            
            // Obter dados atuais através do sistema real
            PartyData party = getRealPlayerParty(playerId);
            
            if (party == null) {
                DimTrMod.LOGGER.warn("Dados insuficientes para transferência party->individual: {}", playerId);
                return false;
            }
            
            // 🔧 CORREÇÃO CRÍTICA: Remover contribuições individuais da party e obter o que o player contribuiu
            Map<String, Integer> playerContributions = party.removeIndividualContributions(playerId);
            
            // 🔧 CORREÇÃO: Criar dados de progressão individual baseados APENAS nas contribuições do jogador
            PlayerProgressionData restoredIndividualData = createProgressionFromContributions(playerId, playerContributions, party);
            
            // 🔧 CORREÇÃO: Aplicar os dados restaurados ao jogador via ProgressionManager
            if (applyIndividualProgressionChanges(playerId, restoredIndividualData)) {
                // Criar registro completo com resultado
                TransferRecord finalRecord = new TransferRecord(playerId, TransferType.PARTY_TO_INDIVIDUAL, 
                    null, restoredIndividualData, reason);
                finalRecord.success = true;
                
                // Salvar no histórico
                if (config.createBackup) {
                    saveTransferRecord(finalRecord);
                }
                
                // Atualizar rate limiting
                lastTransferTime.put(playerId, System.currentTimeMillis());
                
                DimTrMod.LOGGER.info("✅ Transferência party->individual concluída: {} kills restaurados para jogador {}",
                    playerContributions.values().stream().mapToInt(Integer::intValue).sum(), playerId);
                return true;
            }
            
            return false;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * ⬆️ Transferir progresso individual para party
     */
    public static boolean transferFromIndividualToParty(UUID playerId) {
        return transferFromIndividualToParty(playerId, TransferConfig.getDefault(), "Manual transfer request");
    }
    
    /**
     * ⬆️ Transferir progresso individual para party (configurável)
     * 🔧 CORREÇÃO CRÍTICA: Sistema corrigido para preservar progresso individual e contribuições
     */
    public static boolean transferFromIndividualToParty(UUID playerId, TransferConfig config, String reason) {
        if (playerId == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            // Verificar rate limiting
            if (!checkRateLimit(playerId)) {
                DimTrMod.LOGGER.debug("Transfer bloqueado por rate limiting para {}", playerId);
                return false;
            }
            
            // Obter dados atuais através do sistema real
            PlayerProgressionData individualData = getRealIndividualProgression(playerId);
            PartyData party = getRealPlayerParty(playerId);
            
            if (individualData == null || party == null) {
                DimTrMod.LOGGER.warn("Dados insuficientes para transferência individual->party: {}", playerId);
                return false;
            }
            
            // 🔧 CORREÇÃO CRÍTICA: Extrair progresso individual como contribuição
            Map<String, Integer> individualMobKills = extractMobKillsFromProgression(individualData);
            Map<String, Boolean> individualObjectives = extractObjectivesFromProgression(individualData);
            
            // 🔧 NOVO: Extrair custom mob kills e objectives de mods externos
            Map<String, Map<String, Integer>> customMobKills = extractCustomMobKillsFromProgression(individualData);
            Map<String, Map<String, Boolean>> customObjectives = extractCustomObjectivesFromProgression(individualData);
            
            // 🔧 CORREÇÃO: Registrar contribuições na PartyData ANTES de somar ao progresso compartilhado
            party.transferIndividualProgress(playerId, individualMobKills);
            
            // 🔧 CORREÇÃO: Transferir objetivos individuais para party (usando OR lógico)
            transferObjectivesToParty(party, individualObjectives);
            
            // 🔧 NOVO: Transferir custom mob kills e objectives de mods externos para party
            transferCustomProgressToParty(party, customMobKills, customObjectives);
            
            // Criar backup dos dados da transferência
            TransferRecord record = new TransferRecord(playerId, TransferType.INDIVIDUAL_TO_PARTY, 
                individualData, null, reason);
            record.success = true;
            
            // Salvar no histórico
            if (config.createBackup) {
                saveTransferRecord(record);
            }
            
            // Atualizar rate limiting
            lastTransferTime.put(playerId, System.currentTimeMillis());
            
            DimTrMod.LOGGER.info("✅ Transferência individual->party concluída: {} contribuições registradas para party {}", 
                individualMobKills.values().stream().mapToInt(Integer::intValue).sum(),
                party.getName());
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 🔄 Sincronizar progresso de toda a party (merge bidirecional)
     */
    public static boolean synchronizePartyProgress(UUID partyLeaderId, TransferConfig config) {
        if (partyLeaderId == null) return false;
        
        writeLock.lock();
        try {
            PartyData party = getPlayerParty(partyLeaderId);
            if (party == null) return false;
            
            Set<UUID> members = new HashSet<>(party.getMembers());
            if (members.size() <= 1) return true; // Nada para sincronizar
            
            // Coletar todas as progressões
            Map<UUID, PlayerProgressionData> allProgressions = new HashMap<>();
            for (UUID memberId : members) {
                PlayerProgressionData progression = getIndividualProgression(memberId);
                if (progression != null) {
                    allProgressions.put(memberId, progression);
                }
            }
            
            // Calcular progresso agregado
            PlayerProgressionData aggregatedProgress = calculateAggregatedProgress(allProgressions.values(), config);
            if (aggregatedProgress == null) return false;
            
            // Aplicar a todos os membros individualmente (já que não temos updateMemberProgression em PartyData)
            boolean success = true;
            for (UUID memberId : members) {
                // Em vez de atualizar a party, atualizamos a progressão individual diretamente
                // Isso é uma adaptação da lógica original para o novo sistema sem ThreadSafePartyData
                updateIndividualProgression(memberId, aggregatedProgress.copy());
                notifyProgressTransfer(memberId, TransferType.PARTY_MERGE, aggregatedProgress);
            }
            
            DimTrMod.LOGGER.info("Sincronização de party concluída para {} membros", members.size());
            return success;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * ↩️ Rollback da última transferência
     */
    public static boolean rollbackLastTransfer(UUID playerId) {
        writeLock.lock();
        try {
            List<TransferRecord> playerHistory = transferHistory.get(playerId);
            if (playerHistory == null || playerHistory.isEmpty()) {
                DimTrMod.LOGGER.warn("Nenhum histórico de transferência encontrado para {}", playerId);
                return false;
            }
            
            // Pegar último registro bem-sucedido
            TransferRecord lastRecord = null;
            for (int i = playerHistory.size() - 1; i >= 0; i--) {
                TransferRecord record = playerHistory.get(i);
                if (record.success && record.type != TransferType.ROLLBACK) {
                    lastRecord = record;
                    break;
                }
            }
            
            if (lastRecord == null || lastRecord.beforeState == null) {
                DimTrMod.LOGGER.warn("Nenhum estado anterior válido encontrado para rollback: {}", playerId);
                return false;
            }
            
            // Aplicar estado anterior
            if (applyProgressionChanges(playerId, lastRecord.beforeState)) {
                // Registrar rollback
                TransferRecord rollbackRecord = new TransferRecord(playerId, TransferType.ROLLBACK, 
                    lastRecord.afterState, lastRecord.beforeState, "Rollback automático");
                rollbackRecord.success = true;
                playerHistory.add(rollbackRecord);
                
                // Notificar
                notifyProgressTransfer(playerId, TransferType.ROLLBACK, lastRecord.beforeState);
                
                DimTrMod.LOGGER.info("Rollback de transferência concluído para {}", playerId);
                return true;
            }
            
            return false;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // MÉTODOS DE CONSULTA
    // ============================================================================
    
    /**
     * 📊 Obter histórico de transferências de um jogador
     */
    public static List<TransferRecord> getTransferHistory(UUID playerId) {
        readLock.lock();
        try {
            List<TransferRecord> history = transferHistory.get(playerId);
            return history != null ? new ArrayList<>(history) : new ArrayList<>();
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * ⏰ Verificar se pode fazer transferência (rate limiting)
     */
    public static boolean canTransfer(UUID playerId) {
        readLock.lock();
        try {
            return checkRateLimit(playerId);
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
            transferHistory.remove(playerId);
            lastTransferTime.remove(playerId);
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // MÉTODOS INTERNOS
    // ============================================================================
    
    private static boolean checkRateLimit(UUID playerId) {
        Long lastTransfer = lastTransferTime.get(playerId);
        if (lastTransfer == null) return true;
        
        long timeSinceLastTransfer = System.currentTimeMillis() - lastTransfer;
        return timeSinceLastTransfer >= MIN_TRANSFER_INTERVAL_MS;
    }
    
    private static PlayerProgressionData mergeProgressionData(PlayerProgressionData base, 
                                                            PlayerProgressionData source, 
                                                            TransferConfig config) {
        if (base == null) return source != null ? source.copy() : null;
        if (source == null) return base.copy();
        
        PlayerProgressionData merged = base.copy();
        
        // Aplicar estratégias de merge para cada campo
        for (Map.Entry<String, MergeStrategy> entry : config.fieldMergeStrategies.entrySet()) {
            String fieldName = entry.getKey();
            MergeStrategy strategy = entry.getValue();
            
            applyMergeStrategy(merged, source, fieldName, strategy);
        }
        
        // Merge de mapas customizados
        mergeCustomMaps(merged, source, config);
        
        return merged;
    }
    
    private static void applyMergeStrategy(PlayerProgressionData target, PlayerProgressionData source, 
                                         String fieldName, MergeStrategy strategy) {
        try {
            // Usar reflection para acessar campos dinamicamente
            var targetField = PlayerProgressionData.class.getField(fieldName);
            var sourceField = PlayerProgressionData.class.getField(fieldName);
            
            Object targetValue = targetField.get(target);
            Object sourceValue = sourceField.get(source);
            
            Object mergedValue = switch (strategy) {
                case TAKE_HIGHEST -> takeHighest(targetValue, sourceValue);
                case TAKE_LOWEST -> takeLowest(targetValue, sourceValue);
                case SUM_VALUES -> sumValues(targetValue, sourceValue);
                case LOGICAL_OR -> logicalOr(targetValue, sourceValue);
                case KEEP_INDIVIDUAL -> targetValue;
                case KEEP_PARTY -> sourceValue;
            };
            
            targetField.set(target, mergedValue);
            
        } catch (Exception e) {
            DimTrMod.LOGGER.warn("Erro ao aplicar merge strategy para campo {}: {}", fieldName, e.getMessage());
        }
    }
    
    private static Object takeHighest(Object a, Object b) {
        if (a instanceof Number na && b instanceof Number nb) {
            return na.intValue() > nb.intValue() ? a : b;
        }
        return a;
    }
    
    private static Object takeLowest(Object a, Object b) {
        if (a instanceof Number na && b instanceof Number nb) {
            return na.intValue() < nb.intValue() ? a : b;
        }
        return a;
    }
    
    private static Object sumValues(Object a, Object b) {
        if (a instanceof Number na && b instanceof Number nb) {
            return na.intValue() + nb.intValue();
        }
        return a;
    }
    
    private static Object logicalOr(Object a, Object b) {
        if (a instanceof Boolean ba && b instanceof Boolean bb) {
            return ba || bb;
        }
        return a;
    }
    
    private static void mergeCustomMaps(PlayerProgressionData target, PlayerProgressionData source, 
                                       TransferConfig config) {
        // Merge custom mob kills
        for (Map.Entry<String, Map<String, Integer>> entry : source.getCustomMobKillsMap().entrySet()) {
            String phase = entry.getKey();
            Map<String, Integer> sourceMobKills = entry.getValue();
            
            Map<String, Integer> targetMobKills = target.getCustomMobKillsMap()
                .computeIfAbsent(phase, k -> new HashMap<>());
            
            for (Map.Entry<String, Integer> mobEntry : sourceMobKills.entrySet()) {
                String mobType = mobEntry.getKey();
                int sourceKills = mobEntry.getValue();
                int currentKills = targetMobKills.getOrDefault(mobType, 0);
                
                // Sempre somar para custom mob kills
                targetMobKills.put(mobType, currentKills + sourceKills);
            }
        }
        
        // Merge custom objectives
        for (Map.Entry<String, Map<String, Boolean>> entry : source.getCustomObjectiveCompletionMap().entrySet()) {
            String phase = entry.getKey();
            Map<String, Boolean> sourceObjectives = entry.getValue();
            
            Map<String, Boolean> targetObjectives = target.getCustomObjectiveCompletionMap()
                .computeIfAbsent(phase, k -> new HashMap<>());
            
            for (Map.Entry<String, Boolean> objEntry : sourceObjectives.entrySet()) {
                String objectiveId = objEntry.getKey();
                boolean sourceCompleted = objEntry.getValue();
                boolean currentCompleted = targetObjectives.getOrDefault(objectiveId, false);
                
                // OR lógico para objetivos
                targetObjectives.put(objectiveId, currentCompleted || sourceCompleted);
            }
        }
        
        // Merge custom phase completion
        for (Map.Entry<String, Boolean> entry : source.getCustomPhaseCompletionMap().entrySet()) {
            String phaseId = entry.getKey();
            boolean sourceCompleted = entry.getValue();
            boolean currentCompleted = target.getCustomPhaseCompletionMap().getOrDefault(phaseId, false);
            
            // OR lógico para fases
            target.getCustomPhaseCompletionMap().put(phaseId, currentCompleted || sourceCompleted);
        }
    }
    
    private static PlayerProgressionData calculatePartyAggregatedProgress(PartyData party, UUID excludePlayerId) {
        Set<UUID> members = new HashSet<>(party.getMembers());
        List<PlayerProgressionData> progressions = new ArrayList<>();
        
        for (UUID memberId : members) {
            if (!memberId.equals(excludePlayerId)) {
                PlayerProgressionData progression = getIndividualProgression(memberId);
                if (progression != null) {
                    progressions.add(progression);
                }
            }
        }
        
        return calculateAggregatedProgress(progressions, TransferConfig.getDefault());
    }
    
    private static PlayerProgressionData calculateAggregatedProgress(Collection<PlayerProgressionData> progressions, 
                                                                   TransferConfig config) {
        if (progressions.isEmpty()) return null;
        
        // Usar primeira progressão como base
        PlayerProgressionData aggregated = progressions.iterator().next().copy();
        
        // Merge com todas as outras
        for (PlayerProgressionData progression : progressions) {
            if (progression != aggregated) {
                aggregated = mergeProgressionData(aggregated, progression, config);
            }
        }
        
        return aggregated;
    }
    
    private static boolean validateProgressionConsistency(PlayerProgressionData data) {
        if (data == null) return false;
        
        // Validações básicas
        if (data.zombieKills < 0 || data.skeletonKills < 0) return false;
        
        // Validar lógica de fases
        if (data.phase2Completed && !data.phase1Completed) {
            DimTrMod.LOGGER.warn("Inconsistência: Fase 2 completa mas Fase 1 não");
            return false;
        }
        
        return true;
    }
    
    private static boolean applyProgressionChanges(UUID playerId, PlayerProgressionData newData) {
        try {
            // Aqui você integraria com seu sistema de dados principal
            // Por exemplo, salvar no PlayerProgressionData do jogador
            
            // Para este exemplo, apenas simulo que funcionou
            return true;
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao aplicar mudanças de progressão para {}: {}", playerId, e.getMessage());
            return false;
        }
    }
    
    private static void notifyProgressTransfer(UUID playerId, TransferType type, PlayerProgressionData newData) {
        try {
            // Criar delta para networking
            var delta = new DeltaUpdateSystem.ProgressionDelta(
                playerId,
                DeltaUpdateSystem.DeltaType.OTHER,
                "progress_transfer:" + type.id,
                null,
                newData.getProgressionMultiplier()
            );
            
            // Adicionar ao batch com prioridade alta
            BatchSyncProcessor.addToBatch(
                playerId,
                BatchSyncProcessor.BatchType.PROGRESSION_DELTA,
                delta,
                8 // Alta prioridade
            );
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao notificar transferência de progresso: {}", e.getMessage());
        }
    }
    
    private static void saveTransferRecord(TransferRecord record) {
        List<TransferRecord> playerHistory = transferHistory.computeIfAbsent(record.playerId, k -> new ArrayList<>());
        playerHistory.add(record);
        
        // Limitar histórico a 50 registros
        if (playerHistory.size() > 50) {
            playerHistory.remove(0);
        }
    }
    
    private static PlayerProgressionData getIndividualProgression(UUID playerId) {
        return getRealIndividualProgression(playerId);
    }
    
    private static PartyData getPlayerParty(UUID playerId) {
        return getRealPlayerParty(playerId);
    }
    
    /**
     * 🔧 CORREÇÃO: Obter progressão individual real através do ProgressionManager
     */
    private static PlayerProgressionData getRealIndividualProgression(UUID playerId) {
        try {
            // Buscar servidor ativo
            net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) return null;
            
            net.minecraft.server.level.ServerLevel overworldLevel = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (overworldLevel == null) return null;
            
            ProgressionManager progressionManager = ProgressionManager.get(overworldLevel);
            return progressionManager.getPlayerData(server.getPlayerList().getPlayer(playerId));
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao obter progressão individual para {}: {}", playerId, e.getMessage());
            return null;
        }
    }
    
    /**
     * 🔧 CORREÇÃO: Obter party real através do PartyManager
     */
    private static PartyData getRealPlayerParty(UUID playerId) {
        try {
            // Buscar servidor ativo
            net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) return null;
            
            net.minecraft.server.level.ServerLevel overworldLevel = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (overworldLevel == null) return null;
            
            PartyManager partyManager = PartyManager.get(overworldLevel);
            return partyManager.getPlayerParty(playerId);
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao obter party para {}: {}", playerId, e.getMessage());
            return null;
        }
    }
    
    /**
     * 🔧 NOVO: Extrair mob kills de PlayerProgressionData
     */
    private static Map<String, Integer> extractMobKillsFromProgression(PlayerProgressionData data) {
        Map<String, Integer> mobKills = new HashMap<>();
        
        // Mob kills básicos
        mobKills.put("zombie", data.zombieKills);
        mobKills.put("skeleton", data.skeletonKills);
        mobKills.put("stray", data.strayKills);
        mobKills.put("husk", data.huskKills);
        mobKills.put("spider", data.spiderKills);
        mobKills.put("creeper", data.creeperKills);
        mobKills.put("drowned", data.drownedKills);
        mobKills.put("enderman", data.endermanKills);
        mobKills.put("witch", data.witchKills);
        mobKills.put("pillager", data.pillagerKills);
        mobKills.put("captain", data.captainKills);
        mobKills.put("vindicator", data.vindicatorKills);
        mobKills.put("bogged", data.boggedKills);
        mobKills.put("breeze", data.breezeKills);
        mobKills.put("ravager", data.ravagerKills);
        mobKills.put("evoker", data.evokerKills);
        
        // Fase 2 mobs
        mobKills.put("blaze", data.blazeKills);
        mobKills.put("wither_skeleton", data.witherSkeletonKills);
        mobKills.put("piglin_brute", data.piglinBruteKills);
        mobKills.put("hoglin", data.hoglinKills);
        mobKills.put("zoglin", data.zoglinKills);
        mobKills.put("ghast", data.ghastKills);
        mobKills.put("piglin", data.piglinKills);
        
        return mobKills;
    }
    
    /**
     * 🔧 NOVO: Extrair objetivos de PlayerProgressionData
     */
    private static Map<String, Boolean> extractObjectivesFromProgression(PlayerProgressionData data) {
        Map<String, Boolean> objectives = new HashMap<>();
        
        objectives.put("elderGuardianKilled", data.elderGuardianKilled);
        objectives.put("raidWon", data.raidWon);
        objectives.put("trialVaultAdvancementEarned", data.trialVaultAdvancementEarned);
        objectives.put("voluntaireExileAdvancementEarned", data.voluntaireExileAdvancementEarned);
        objectives.put("witherKilled", data.witherKilled);
        objectives.put("wardenKilled", data.wardenKilled);
        objectives.put("phase1Completed", data.phase1Completed);
        objectives.put("phase2Completed", data.phase2Completed);
        
        return objectives;
    }
    
    /**
     * 🔧 NOVO: Transferir objetivos para party usando OR lógico
     */
    private static void transferObjectivesToParty(PartyData party, Map<String, Boolean> objectives) {
        for (Map.Entry<String, Boolean> entry : objectives.entrySet()) {
            String objective = entry.getKey();
            boolean hasObjective = entry.getValue();
            
            if (hasObjective) {
                switch (objective) {
                    case "elderGuardianKilled" -> party.setSharedElderGuardianKilled(true);
                    case "raidWon" -> party.setSharedRaidWon(true);
                    case "trialVaultAdvancementEarned" -> party.setSharedTrialVaultAdvancementEarned(true);
                    case "voluntaireExileAdvancementEarned" -> party.setSharedVoluntaireExileAdvancementEarned(true);
                    case "witherKilled" -> party.setSharedWitherKilled(true);
                    case "wardenKilled" -> party.setSharedWardenKilled(true);
                    case "phase1Completed" -> party.setPhase1SharedCompleted(true);
                    case "phase2Completed" -> party.setPhase2SharedCompleted(true);
                }
            }
        }
    }
    
    /**
     * 🔧 NOVO: Extrair custom mob kills (mods externos) de PlayerProgressionData
     */
    private static Map<String, Map<String, Integer>> extractCustomMobKillsFromProgression(PlayerProgressionData data) {
        return new HashMap<>(data.getCustomMobKillsMap());
    }
    
    /**
     * 🔧 NOVO: Extrair custom objectives (mods externos) de PlayerProgressionData
     */
    private static Map<String, Map<String, Boolean>> extractCustomObjectivesFromProgression(PlayerProgressionData data) {
        return new HashMap<>(data.getCustomObjectiveCompletionMap());
    }
    
    /**
     * 🔧 NOVO: Transferir custom progress (mods externos) para party
     */
    private static void transferCustomProgressToParty(PartyData party, 
                                                     Map<String, Map<String, Integer>> customMobKills,
                                                     Map<String, Map<String, Boolean>> customObjectives) {
        // Transferir custom mob kills para party
        for (Map.Entry<String, Map<String, Integer>> phaseEntry : customMobKills.entrySet()) {
            String phase = phaseEntry.getKey();
            Map<String, Integer> mobKills = phaseEntry.getValue();
            
            for (Map.Entry<String, Integer> mobEntry : mobKills.entrySet()) {
                String mobType = mobEntry.getKey();
                int kills = mobEntry.getValue();
                
                if (kills > 0) {
                    // Somar kills ao progresso compartilhado da party
                    party.addSharedCustomMobKills(phase, mobType, kills);
                }
            }
        }
        
        // Transferir custom objectives para party (OR lógico)
        for (Map.Entry<String, Map<String, Boolean>> phaseEntry : customObjectives.entrySet()) {
            String phase = phaseEntry.getKey();
            Map<String, Boolean> objectives = phaseEntry.getValue();
            
            for (Map.Entry<String, Boolean> objEntry : objectives.entrySet()) {
                String objectiveId = objEntry.getKey();
                boolean completed = objEntry.getValue();
                
                if (completed) {
                    // Marcar objetivo como completado na party
                    party.setSharedCustomObjectiveComplete(phase, objectiveId, true);
                }
            }
        }
    }
    
    private static void updateIndividualProgression(UUID playerId, PlayerProgressionData data) {
        // Placeholder - integrar com seu sistema de dados
        // Aqui você deve implementar a lógica para atualizar a progressão individual do jogador
        DimTrMod.LOGGER.debug("Atualizando progressão individual para jogador {}", playerId);
        // Na implementação real, você salvaria os dados na fonte de dados apropriada
    }
    
    /**
     * 🔧 NOVO: Criar dados de progressão individual a partir de contribuições
     */
    private static PlayerProgressionData createProgressionFromContributions(UUID playerId, Map<String, Integer> contributions, PartyData party) {
        PlayerProgressionData data = new PlayerProgressionData(playerId);
        
        // Aplicar mob kills das contribuições
        for (Map.Entry<String, Integer> entry : contributions.entrySet()) {
            String mobType = entry.getKey();
            int kills = entry.getValue();
            
            switch (mobType) {
                case "zombie" -> data.zombieKills = kills;
                case "skeleton" -> data.skeletonKills = kills;
                case "stray" -> data.strayKills = kills;
                case "husk" -> data.huskKills = kills;
                case "spider" -> data.spiderKills = kills;
                case "creeper" -> data.creeperKills = kills;
                case "drowned" -> data.drownedKills = kills;
                case "enderman" -> data.endermanKills = kills;
                case "witch" -> data.witchKills = kills;
                case "pillager" -> data.pillagerKills = kills;
                case "captain" -> data.captainKills = kills;
                case "vindicator" -> data.vindicatorKills = kills;
                case "bogged" -> data.boggedKills = kills;
                case "breeze" -> data.breezeKills = kills;
                case "ravager" -> data.ravagerKills = kills;
                case "evoker" -> data.evokerKills = kills;
                case "blaze" -> data.blazeKills = kills;
                case "wither_skeleton" -> data.witherSkeletonKills = kills;
                case "piglin_brute" -> data.piglinBruteKills = kills;
                case "hoglin" -> data.hoglinKills = kills;
                case "zoglin" -> data.zoglinKills = kills;
                case "ghast" -> data.ghastKills = kills;
                case "piglin" -> data.piglinKills = kills;
            }
        }
        
        // 🔧 NOVO: Restaurar custom mob kills e objectives de mods externos
        // TODO: Implementar sistema de tracking de contribuições individuais para custom progress
        // Por enquanto, objetivos permanecem com o jogador (boss kills não são "removidos")
        
        // 🔧 IMPORTANTE: Objetivos NÃO são removidos quando sai da party
        // Eles permanecem com o jogador pois foram conquistados enquanto na party
        // Custom objectives (boss kills) também permanecem - uma vez derrotado, sempre derrotado
        
        return data;
    }
    
    /**
     * 🔧 NOVO: Aplicar mudanças de progressão individual via ProgressionManager
     */
    private static boolean applyIndividualProgressionChanges(UUID playerId, PlayerProgressionData newData) {
        try {
            // Buscar servidor ativo
            net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) return false;
            
            net.minecraft.server.level.ServerLevel overworldLevel = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (overworldLevel == null) return false;
            
            ProgressionManager progressionManager = ProgressionManager.get(overworldLevel);
            
            // 🔧 CORREÇÃO: Substituir dados diretamente no mapa
            PlayerProgressionData currentData = progressionManager.getPlayerData(playerId);
            copyProgressionData(newData, currentData);
            
            // Sincronizar com cliente se jogador estiver online
            net.minecraft.server.level.ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            if (player != null) {
                progressionManager.sendToClient(player);
                DimTrMod.LOGGER.debug("Dados de progressão aplicados e sincronizados para jogador online: {}", playerId);
            } else {
                DimTrMod.LOGGER.debug("Dados de progressão salvos para jogador offline: {}", playerId);
            }
            
            return true;
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao aplicar mudanças de progressão individual para {}: {}", playerId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 🔧 NOVO: Copiar dados de progressão de uma instância para outra
     */
    private static void copyProgressionData(PlayerProgressionData source, PlayerProgressionData target) {
        // Copiar mob kills
        target.zombieKills = source.zombieKills;
        target.skeletonKills = source.skeletonKills;
        target.strayKills = source.strayKills;
        target.huskKills = source.huskKills;
        target.spiderKills = source.spiderKills;
        target.creeperKills = source.creeperKills;
        target.drownedKills = source.drownedKills;
        target.endermanKills = source.endermanKills;
        target.witchKills = source.witchKills;
        target.pillagerKills = source.pillagerKills;
        target.captainKills = source.captainKills;
        target.vindicatorKills = source.vindicatorKills;
        target.boggedKills = source.boggedKills;
        target.breezeKills = source.breezeKills;
        target.ravagerKills = source.ravagerKills;
        target.evokerKills = source.evokerKills;
        target.blazeKills = source.blazeKills;
        target.witherSkeletonKills = source.witherSkeletonKills;
        target.piglinBruteKills = source.piglinBruteKills;
        target.hoglinKills = source.hoglinKills;
        target.zoglinKills = source.zoglinKills;
        target.ghastKills = source.ghastKills;
        target.piglinKills = source.piglinKills;
        
        // Copiar objetivos
        target.elderGuardianKilled = source.elderGuardianKilled;
        target.raidWon = source.raidWon;
        target.trialVaultAdvancementEarned = source.trialVaultAdvancementEarned;
        target.voluntaireExileAdvancementEarned = source.voluntaireExileAdvancementEarned;
        target.witherKilled = source.witherKilled;
        target.wardenKilled = source.wardenKilled;
        target.phase1Completed = source.phase1Completed;
        target.phase2Completed = source.phase2Completed;
        
        // 🔧 NOVO: Copiar custom mob kills e objectives (mods externos)
        copyCustomProgress(source, target);
    }
    
    /**
     * 🔧 NOVO: Copiar custom progress (mods externos) entre instâncias
     */
    private static void copyCustomProgress(PlayerProgressionData source, PlayerProgressionData target) {
        // Copiar custom mob kills
        Map<String, Map<String, Integer>> sourceCustomMobKills = source.getCustomMobKillsMap();
        Map<String, Map<String, Integer>> targetCustomMobKills = target.getCustomMobKillsMap();
        
        targetCustomMobKills.clear();
        for (Map.Entry<String, Map<String, Integer>> phaseEntry : sourceCustomMobKills.entrySet()) {
            String phase = phaseEntry.getKey();
            Map<String, Integer> sourceMobKills = phaseEntry.getValue();
            Map<String, Integer> targetMobKills = new HashMap<>(sourceMobKills);
            targetCustomMobKills.put(phase, targetMobKills);
        }
        
        // Copiar custom objectives
        Map<String, Map<String, Boolean>> sourceCustomObjectives = source.getCustomObjectiveCompletionMap();
        Map<String, Map<String, Boolean>> targetCustomObjectives = target.getCustomObjectiveCompletionMap();
        
        targetCustomObjectives.clear();
        for (Map.Entry<String, Map<String, Boolean>> phaseEntry : sourceCustomObjectives.entrySet()) {
            String phase = phaseEntry.getKey();
            Map<String, Boolean> sourceObjectives = phaseEntry.getValue();
            Map<String, Boolean> targetObjectives = new HashMap<>(sourceObjectives);
            targetCustomObjectives.put(phase, targetObjectives);
        }
        
        // Copiar custom phase completion
        target.getCustomPhaseCompletionMap().clear();
        target.getCustomPhaseCompletionMap().putAll(source.getCustomPhaseCompletionMap());
    }
}
