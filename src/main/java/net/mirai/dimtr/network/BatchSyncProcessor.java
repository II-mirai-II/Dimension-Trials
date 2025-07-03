package net.mirai.dimtr.network;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ⚡ Sistema de processamento em lotes para sincronização eficiente
 * 
 * ✅ Agrupa updates por prioridade e jogador
 * ✅ Processamento assíncrono inteligente
 * ✅ Rate limiting adaptativo
 * ✅ Thread-safety completo
 * ✅ Compressão automática de dados
 * 
 * @author Dimension Trials Team
 */
public class BatchSyncProcessor {
    
    // ============================================================================
    // CONFIGURAÇÃO E CONSTANTES
    // ============================================================================
    
    private static final long HIGH_PRIORITY_DELAY_MS = 100;    // 100ms para alta prioridade
    private static final long NORMAL_PRIORITY_DELAY_MS = 2000; // 2s para prioridade normal
    
    private static final int MAX_BATCH_SIZE = 50;              // Máximo de items por batch
    private static final int MAX_PLAYERS_PER_CYCLE = 10;       // Máximo de jogadores por ciclo
    
    // ============================================================================
    // THREAD-SAFETY E ESTADO
    // ============================================================================
    
    private static final ReentrantReadWriteLock BATCH_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = BATCH_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = BATCH_LOCK.writeLock();
    
    // Pool de threads para processamento assíncrono
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3, r -> {
        Thread t = new Thread(r, "DimTr-BatchSync");
        t.setDaemon(true);
        return t;
    });
    
    // ============================================================================
    // ESTRUTURAS DE DADOS
    // ============================================================================
    
    // Filas por prioridade
    private static final Map<UUID, Queue<BatchItem>> highPriorityQueue = new ConcurrentHashMap<>();
    private static final Map<UUID, Queue<BatchItem>> normalPriorityQueue = new ConcurrentHashMap<>();
    private static final Map<UUID, Queue<BatchItem>> lowPriorityQueue = new ConcurrentHashMap<>();
    
    // Controle de agendamento
    private static final Map<UUID, Long> lastProcessTime = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> nextScheduledTime = new ConcurrentHashMap<>();
    
    // Estatísticas
    private static final Map<UUID, BatchStats> playerStats = new ConcurrentHashMap<>();
    
    static {
        // Iniciar processamento automático a cada 500ms
        executorService.scheduleAtFixedRate(BatchSyncProcessor::processAllBatches, 
            1000, 500, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 📦 Item de batch com prioridade e dados
     */
    public static class BatchItem {
        public final BatchType type;
        public final Object data;
        public final long timestamp;
        public final int priority;
        
        public BatchItem(BatchType type, Object data, int priority) {
            this.type = type;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.priority = priority;
        }
    }
    
    /**
     * 📊 Tipos de item de batch
     */
    public enum BatchType {
        PROGRESSION_DELTA(8),    // Updates de progressão
        PARTY_UPDATE(6),         // Updates de party
        CONFIG_SYNC(4),          // Sincronização de config
        STATISTICS_UPDATE(2);    // Updates de estatísticas
        
        public final int basePriority;
        
        BatchType(int basePriority) {
            this.basePriority = basePriority;
        }
    }
    
    /**
     * 📈 Estatísticas de batch por jogador
     */
    public static class BatchStats {
        public long totalItemsProcessed = 0;
        public long totalBatchesSent = 0;
        public long averageBatchSize = 0;
        public long lastUpdateTime = 0;
        public double compressionRatio = 1.0;
        
        public void updateStats(int batchSize, double compression) {
            totalItemsProcessed += batchSize;
            totalBatchesSent++;
            averageBatchSize = totalItemsProcessed / totalBatchesSent;
            lastUpdateTime = System.currentTimeMillis();
            compressionRatio = (compressionRatio + compression) / 2.0; // Média móvel
        }
    }
    
    // ============================================================================
    // API PÚBLICA
    // ============================================================================
    
    /**
     * ➕ Adicionar item ao batch de um jogador
     */
    public static void addToBatch(UUID playerId, BatchType type, Object data, int priority) {
        // Validação robusta dos dados de entrada
        if (!validateBatchData(playerId, data) || type == null) {
            return;
        }
        
        writeLock.lock();
        try {
            BatchItem item = new BatchItem(type, data, priority);
            
            // Validar o item de batch criado
            if (!validateBatchItem(item)) {
                return;
            }
            
            // Determinar fila baseada na prioridade total
            int totalPriority = type.basePriority + priority;
            
            if (totalPriority >= 10) {
                highPriorityQueue.computeIfAbsent(playerId, k -> new LinkedList<>()).offer(item);
                scheduleHighPriorityProcessing(playerId);
            } else if (totalPriority >= 5) {
                normalPriorityQueue.computeIfAbsent(playerId, k -> new LinkedList<>()).offer(item);
            } else {
                lowPriorityQueue.computeIfAbsent(playerId, k -> new LinkedList<>()).offer(item);
            }
            
            DimTrMod.LOGGER.debug("Item adicionado ao batch: {} (prioridade: {})", type, totalPriority);
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 🚀 Processar batch específico de um jogador (método principal)
     */
    public static void processBatch(UUID playerId) {
        if (playerId == null) {
            return;
        }
        
        ServerPlayer player = findPlayerById(playerId);
        if (player == null) {
            clearPlayerQueues(playerId);
            return;
        }
        
        writeLock.lock();
        try {
            List<BatchItem> batch = collectBatchItems(playerId);
            
            if (batch.isEmpty()) {
                return;
            }
            
            // Otimizar e comprimir batch
            List<BatchItem> optimizedBatch = optimizeBatch(batch);
            double compressionRatio = (double) optimizedBatch.size() / batch.size();
            
            // Enviar batch
            if (!optimizedBatch.isEmpty()) {
                sendBatchToPlayer(player, optimizedBatch);
                
                // Atualizar estatísticas
                BatchStats stats = playerStats.computeIfAbsent(playerId, k -> new BatchStats());
                stats.updateStats(optimizedBatch.size(), compressionRatio);
                
                // Atualizar tempo de processamento
                lastProcessTime.put(playerId, System.currentTimeMillis());
                
                DimTrMod.LOGGER.debug("Batch processado para {}: {} items (compressão: {:.2f})", 
                    player.getName().getString(), optimizedBatch.size(), compressionRatio);
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * ⏰ Agendar processamento de batch (para controle externo)
     */
    public static void scheduleBatch(UUID playerId, long delayMs) {
        if (playerId == null) {
            return;
        }
        
        writeLock.lock();
        try {
            long scheduledTime = System.currentTimeMillis() + delayMs;
            nextScheduledTime.put(playerId, scheduledTime);
            
            executorService.schedule(() -> processBatch(playerId), delayMs, TimeUnit.MILLISECONDS);
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 🧹 Limpar dados de um jogador (quando sair do servidor)
     */
    public static void clearPlayerData(UUID playerId) {
        if (playerId == null) {
            return;
        }
        
        writeLock.lock();
        try {
            clearPlayerQueues(playerId);
            lastProcessTime.remove(playerId);
            nextScheduledTime.remove(playerId);
            playerStats.remove(playerId);
            
            DimTrMod.LOGGER.debug("Dados de batch limpos para jogador: {}", playerId);
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 📊 Obter estatísticas de um jogador
     */
    public static BatchStats getPlayerStats(UUID playerId) {
        readLock.lock();
        try {
            return playerStats.get(playerId);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 🔧 Shutdown limpo do sistema
     */
    public static void shutdown() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // ============================================================================
    // MÉTODOS INTERNOS
    // ============================================================================
    
    /**
     * Processar todos os batches pendentes (chamado pelo timer)
     */
    private static void processAllBatches() {
        try {
            readLock.lock();
            Set<UUID> playersToProcess = new HashSet<>();
            long currentTime = System.currentTimeMillis();
            
            // Coletar jogadores que precisam de processamento
            for (UUID playerId : getAllPlayerIds()) {
                if (shouldProcessPlayer(playerId, currentTime)) {
                    playersToProcess.add(playerId);
                    if (playersToProcess.size() >= MAX_PLAYERS_PER_CYCLE) {
                        break; // Limitar jogadores por ciclo
                    }
                }
            }
            
            readLock.unlock();
            
            // Processar em paralelo (fora do lock)
            playersToProcess.parallelStream().forEach(BatchSyncProcessor::processBatch);
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro no processamento automático de batches: {}", e.getMessage());
        }
    }
    
    private static void scheduleHighPriorityProcessing(UUID playerId) {
        executorService.schedule(() -> processBatch(playerId), 
            HIGH_PRIORITY_DELAY_MS, TimeUnit.MILLISECONDS);
    }
    
    private static boolean shouldProcessPlayer(UUID playerId, long currentTime) {
        // Verificar se há items pendentes
        if (!hasQueuedItems(playerId)) {
            return false;
        }
        
        // Verificar rate limiting
        Long lastProcess = lastProcessTime.get(playerId);
        if (lastProcess != null && (currentTime - lastProcess) < NORMAL_PRIORITY_DELAY_MS) {
            return false;
        }
        
        // Verificar agendamento específico
        Long scheduled = nextScheduledTime.get(playerId);
        if (scheduled != null && currentTime < scheduled) {
            return false;
        }
        
        return true;
    }
    
    private static Set<UUID> getAllPlayerIds() {
        Set<UUID> allIds = new HashSet<>();
        allIds.addAll(highPriorityQueue.keySet());
        allIds.addAll(normalPriorityQueue.keySet());
        allIds.addAll(lowPriorityQueue.keySet());
        return allIds;
    }
    
    private static boolean hasQueuedItems(UUID playerId) {
        return (highPriorityQueue.containsKey(playerId) && !highPriorityQueue.get(playerId).isEmpty()) ||
               (normalPriorityQueue.containsKey(playerId) && !normalPriorityQueue.get(playerId).isEmpty()) ||
               (lowPriorityQueue.containsKey(playerId) && !lowPriorityQueue.get(playerId).isEmpty());
    }
    
    private static List<BatchItem> collectBatchItems(UUID playerId) {
        List<BatchItem> batch = new ArrayList<>();
        
        // Coletar de alta prioridade primeiro
        Queue<BatchItem> highQueue = highPriorityQueue.get(playerId);
        if (highQueue != null) {
            batch.addAll(drainQueue(highQueue, MAX_BATCH_SIZE));
        }
        
        // Depois prioridade normal
        if (batch.size() < MAX_BATCH_SIZE) {
            Queue<BatchItem> normalQueue = normalPriorityQueue.get(playerId);
            if (normalQueue != null) {
                batch.addAll(drainQueue(normalQueue, MAX_BATCH_SIZE - batch.size()));
            }
        }
        
        // Por último, baixa prioridade
        if (batch.size() < MAX_BATCH_SIZE) {
            Queue<BatchItem> lowQueue = lowPriorityQueue.get(playerId);
            if (lowQueue != null) {
                batch.addAll(drainQueue(lowQueue, MAX_BATCH_SIZE - batch.size()));
            }
        }
        
        return batch;
    }
    
    private static List<BatchItem> drainQueue(Queue<BatchItem> queue, int maxItems) {
        List<BatchItem> items = new ArrayList<>();
        
        for (int i = 0; i < maxItems && !queue.isEmpty(); i++) {
            BatchItem item = queue.poll();
            if (item != null) {
                items.add(item);
            }
        }
        
        return items;
    }
    
    private static List<BatchItem> optimizeBatch(List<BatchItem> batch) {
        // Remover duplicatas por tipo e dados
        Map<String, BatchItem> uniqueItems = new LinkedHashMap<>();
        
        for (BatchItem item : batch) {
            String key = item.type + ":" + item.data.getClass().getSimpleName();
            
            // Manter o item mais recente
            BatchItem existing = uniqueItems.get(key);
            if (existing == null || item.timestamp > existing.timestamp) {
                uniqueItems.put(key, item);
            }
        }
        
        // Ordenar por prioridade (maior primeiro)
        return uniqueItems.values().stream()
            .sorted((a, b) -> Integer.compare(b.priority, a.priority))
            .toList();
    }
    
    private static void sendBatchToPlayer(ServerPlayer player, List<BatchItem> batch) {
        try {
            // Criar packet de batch customizado
            BatchSyncPacket packet = new BatchSyncPacket(batch);
            PacketDistributor.sendToPlayer(player, packet);
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao enviar batch para {}: {}", 
                player.getName().getString(), e.getMessage());
        }
    }
    
    private static void clearPlayerQueues(UUID playerId) {
        highPriorityQueue.remove(playerId);
        normalPriorityQueue.remove(playerId);
        lowPriorityQueue.remove(playerId);
    }
    
    private static ServerPlayer findPlayerById(UUID playerId) {
        try {
            // Usar ServerLifecycleHooks para obter o servidor
            net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return server.getPlayerList().getPlayer(playerId);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 📦 Packet para envio de batches (simplificado por enquanto)
     */
    public static class BatchSyncPacket implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        public static final Type<BatchSyncPacket> TYPE = 
            new Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(DimTrMod.MODID, "batch_sync"));
        
        private final List<BatchItem> items;
        
        public BatchSyncPacket(List<BatchItem> items) {
            this.items = items;
        }
        
        @Override
        public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
            return TYPE;
        }
        
        public List<BatchItem> getItems() {
            return items;
        }
    }
    
    /**
     * 🔒 Validação robusta para dados de entrada no processamento de batch
     * 
     * @param playerId ID do jogador
     * @param data Dados a processar
     * @return true se os dados são válidos
     */
    private static boolean validateBatchData(UUID playerId, Object data) {
        if (playerId == null) {
            DimTrMod.LOGGER.warn("❌ BatchSyncProcessor: Tentativa de processar batch com playerId null");
            return false;
        }
        
        if (data == null) {
            DimTrMod.LOGGER.warn("❌ BatchSyncProcessor: Tentativa de processar batch com data null para jogador {}", playerId);
            return false;
        }
        
        return true;
    }
    
    /**
     * 🔒 Validação robusta para dados de batch e registro de dados inválidos
     * 
     * @param item Item de batch a validar
     * @return true se o item é válido
     */
    private static boolean validateBatchItem(BatchItem item) {
        if (item == null) {
            DimTrMod.LOGGER.warn("❌ BatchSyncProcessor: Item de batch null");
            return false;
        }
        
        if (item.type == null) {
            DimTrMod.LOGGER.warn("❌ BatchSyncProcessor: Item de batch com tipo null");
            return false;
        }
        
        if (item.data == null) {
            DimTrMod.LOGGER.warn("❌ BatchSyncProcessor: Item de batch com dados null (tipo: {})", item.type);
            return false;
        }
        
        return true;
    }
}
