package net.mirai.dimtr.network;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.PlayerProgressionData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Sistema otimizado de updates delta para networking
 * 
 * ✅ Reduz largura de banda enviando apenas mudanças
 * ✅ Compressão inteligente de dados
 * ✅ Batching automático de updates
 * ✅ Thread-safety completo
 * ✅ Rate limiting por jogador
 * 
 * @author Dimension Trials Team
 */
public class DeltaUpdateSystem {
    
    // Thread-safety
    private static final ReentrantReadWriteLock DELTA_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = DELTA_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = DELTA_LOCK.writeLock();
    
    // Cache de estados anteriores para calcular deltas
    private static final Map<UUID, PlayerProgressionData> lastKnownStates = new ConcurrentHashMap<>();
    
    // Rate limiting
    private static final Map<UUID, Long> lastUpdateTimes = new ConcurrentHashMap<>();
    private static final long MIN_UPDATE_INTERVAL_MS = 500; // Máximo 2 updates por segundo
    
    // Batching
    private static final Map<UUID, List<ProgressionDelta>> pendingDeltas = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> batchScheduleTime = new ConcurrentHashMap<>();
    private static final long BATCH_DELAY_MS = 1000; // Aguardar 1 segundo antes de enviar batch
    
    /**
     * Classe que representa um delta de progressão
     */
    public static class ProgressionDelta {
        public final UUID playerId;
        public final DeltaType type;
        public final String fieldName;
        public final Object oldValue;
        public final Object newValue;
        public final long timestamp;
        
        public ProgressionDelta(UUID playerId, DeltaType type, String fieldName, Object oldValue, Object newValue) {
            this.playerId = playerId;
            this.type = type;
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.timestamp = System.currentTimeMillis();
        }
        
        /**
         * Verificar se o delta é significativo (mudança real)
         */
        public boolean isSignificant() {
            if (oldValue == null && newValue == null) {
                return false;
            }
            if (oldValue == null || newValue == null) {
                return true;
            }
            
            // Para números, verificar se a mudança é >= 1
            if (oldValue instanceof Number && newValue instanceof Number) {
                double diff = Math.abs(((Number) newValue).doubleValue() - ((Number) oldValue).doubleValue());
                return diff >= 1.0;
            }
            
            return !oldValue.equals(newValue);
        }
        
        /**
         * Calcular prioridade do delta (0 = baixa, 10 = alta)
         */
        public int getPriority() {
            return switch (type) {
                case PHASE_COMPLETION -> 10; // Máxima prioridade
                case BOSS_KILL -> 9;
                case ADVANCEMENT -> 8;
                case MOB_KILL -> {
                    if (newValue instanceof Integer count && count % 10 == 0) {
                        yield 7; // Marcos de 10 kills têm prioridade
                    }
                    yield 3; // Kills individuais têm prioridade baixa
                }
                case MULTIPLIER_CHANGE -> 6;
                case PARTY_UPDATE -> 5;
                default -> 1;
            };
        }
    }
    
    /**
     * Tipos de delta para categorização
     */
    public enum DeltaType {
        MOB_KILL,
        BOSS_KILL,
        ADVANCEMENT,
        PHASE_COMPLETION,
        MULTIPLIER_CHANGE,
        PARTY_UPDATE,
        OTHER
    }
    
    /**
     * Enviar delta para um jogador específico
     */
    public static void sendDelta(ServerPlayer player, ProgressionDelta delta) {
        if (player == null || delta == null) {
            return;
        }
        
        writeLock.lock();
        try {
            // Rate limiting
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();
            Long lastUpdate = lastUpdateTimes.get(playerId);
            
            if (lastUpdate != null && (currentTime - lastUpdate) < MIN_UPDATE_INTERVAL_MS) {
                // Agendar para batch se ainda não foi agendado
                scheduleDeltaForBatch(playerId, delta);
                return;
            }
            
            // Verificar se é significativo
            if (!delta.isSignificant()) {
                DimTrMod.LOGGER.debug("Delta não significativo ignorado para {}: {} {} -> {}", 
                    player.getName().getString(), delta.fieldName, delta.oldValue, delta.newValue);
                return;
            }
            
            // Enviar imediatamente se alta prioridade ou sem batch pendente
            if (delta.getPriority() >= 8 || !hasPendingBatch(playerId)) {
                sendDeltaPacket(player, Collections.singletonList(delta));
                lastUpdateTimes.put(playerId, currentTime);
                
                DimTrMod.LOGGER.debug("Delta enviado imediatamente para {}: {} = {}", 
                    player.getName().getString(), delta.fieldName, delta.newValue);
            } else {
                // Agendar para batch
                scheduleDeltaForBatch(playerId, delta);
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Calcular delta entre dois estados de progressão
     */
    public static List<ProgressionDelta> calculateDelta(UUID playerId, PlayerProgressionData oldData, PlayerProgressionData newData) {
        List<ProgressionDelta> deltas = new ArrayList<>();
        
        if (oldData == null) {
            // Primeiro sync - enviar estado completo como deltas
            deltas.addAll(createFullStateDelta(playerId, newData));
            return deltas;
        }
        
        // Comparar mob kills
        compareMobKills(playerId, oldData, newData, deltas);
        
        // Comparar objetivos especiais
        compareSpecialObjectives(playerId, oldData, newData, deltas);
        
        // Comparar fases
        comparePhases(playerId, oldData, newData, deltas);
        
        // Comparar multiplicadores
        compareMultipliers(playerId, oldData, newData, deltas);
        
        return deltas;
    }
    
    /**
     * Enviar batch de deltas acumulados
     */
    public static void processPendingBatches() {
        writeLock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            
            for (Map.Entry<UUID, Long> entry : batchScheduleTime.entrySet()) {
                UUID playerId = entry.getKey();
                Long scheduleTime = entry.getValue();
                
                if (scheduleTime != null && (currentTime - scheduleTime) >= BATCH_DELAY_MS) {
                    List<ProgressionDelta> deltas = pendingDeltas.remove(playerId);
                    batchScheduleTime.remove(playerId);
                    
                    if (deltas != null && !deltas.isEmpty()) {
                        // Encontrar jogador
                        ServerPlayer player = findPlayerById(playerId);
                        if (player != null) {
                            // Otimizar batch - remover deltas redundantes e ordenar por prioridade
                            List<ProgressionDelta> optimizedDeltas = optimizeDeltaBatch(deltas);
                            
                            sendDeltaPacket(player, optimizedDeltas);
                            lastUpdateTimes.put(playerId, currentTime);
                            
                            DimTrMod.LOGGER.debug("Batch de {} deltas enviado para {}", 
                                optimizedDeltas.size(), player.getName().getString());
                        }
                    }
                }
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Atualizar estado conhecido de um jogador
     */
    public static void updateKnownState(UUID playerId, PlayerProgressionData data) {
        writeLock.lock();
        try {
            lastKnownStates.put(playerId, data.copy());
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Obter estado conhecido de um jogador
     */
    public static PlayerProgressionData getKnownState(UUID playerId) {
        readLock.lock();
        try {
            return lastKnownStates.get(playerId);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Limpar dados de um jogador (quando sair do servidor)
     */
    public static void clearPlayerData(UUID playerId) {
        writeLock.lock();
        try {
            lastKnownStates.remove(playerId);
            lastUpdateTimes.remove(playerId);
            pendingDeltas.remove(playerId);
            batchScheduleTime.remove(playerId);
        } finally {
            writeLock.unlock();
        }
    }
    
    // ============================================================================
    // MÉTODOS INTERNOS
    // ============================================================================
    
    private static void scheduleDeltaForBatch(UUID playerId, ProgressionDelta delta) {
        pendingDeltas.computeIfAbsent(playerId, k -> new ArrayList<>()).add(delta);
        batchScheduleTime.putIfAbsent(playerId, System.currentTimeMillis());
        
        DimTrMod.LOGGER.debug("Delta agendado para batch: {} = {}", delta.fieldName, delta.newValue);
    }
    
    private static boolean hasPendingBatch(UUID playerId) {
        return pendingDeltas.containsKey(playerId);
    }
    
    private static void sendDeltaPacket(ServerPlayer player, List<ProgressionDelta> deltas) {
        try {
            ProgressionDeltaPacket packet = new ProgressionDeltaPacket(deltas);
            PacketDistributor.sendToPlayer(player, packet);
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Erro ao enviar delta packet para {}: {}", 
                player.getName().getString(), e.getMessage());
        }
    }
    
    private static List<ProgressionDelta> createFullStateDelta(UUID playerId, PlayerProgressionData data) {
        List<ProgressionDelta> deltas = new ArrayList<>();
        
        // Criar deltas para todos os campos importantes
        deltas.add(new ProgressionDelta(playerId, DeltaType.PHASE_COMPLETION, "phase1Completed", false, data.phase1Completed));
        deltas.add(new ProgressionDelta(playerId, DeltaType.PHASE_COMPLETION, "phase2Completed", false, data.phase2Completed));
        
        // Mob kills principais
        deltas.add(new ProgressionDelta(playerId, DeltaType.MOB_KILL, "zombieKills", 0, data.zombieKills));
        deltas.add(new ProgressionDelta(playerId, DeltaType.MOB_KILL, "skeletonKills", 0, data.skeletonKills));
        
        // Objetivos especiais
        deltas.add(new ProgressionDelta(playerId, DeltaType.BOSS_KILL, "elderGuardianKilled", false, data.elderGuardianKilled));
        deltas.add(new ProgressionDelta(playerId, DeltaType.ADVANCEMENT, "raidWon", false, data.raidWon));
        
        return deltas.stream().filter(ProgressionDelta::isSignificant).toList();
    }
    
    private static void compareMobKills(UUID playerId, PlayerProgressionData oldData, PlayerProgressionData newData, List<ProgressionDelta> deltas) {
        compareIntField(playerId, DeltaType.MOB_KILL, "zombieKills", oldData.zombieKills, newData.zombieKills, deltas);
        compareIntField(playerId, DeltaType.MOB_KILL, "skeletonKills", oldData.skeletonKills, newData.skeletonKills, deltas);
        compareIntField(playerId, DeltaType.MOB_KILL, "ravagerKills", oldData.ravagerKills, newData.ravagerKills, deltas);
        compareIntField(playerId, DeltaType.MOB_KILL, "evokerKills", oldData.evokerKills, newData.evokerKills, deltas);
    }
    
    private static void compareSpecialObjectives(UUID playerId, PlayerProgressionData oldData, PlayerProgressionData newData, List<ProgressionDelta> deltas) {
        compareBooleanField(playerId, DeltaType.BOSS_KILL, "elderGuardianKilled", oldData.elderGuardianKilled, newData.elderGuardianKilled, deltas);
        compareBooleanField(playerId, DeltaType.ADVANCEMENT, "raidWon", oldData.raidWon, newData.raidWon, deltas);
        compareBooleanField(playerId, DeltaType.BOSS_KILL, "witherKilled", oldData.witherKilled, newData.witherKilled, deltas);
        compareBooleanField(playerId, DeltaType.BOSS_KILL, "wardenKilled", oldData.wardenKilled, newData.wardenKilled, deltas);
    }
    
    private static void comparePhases(UUID playerId, PlayerProgressionData oldData, PlayerProgressionData newData, List<ProgressionDelta> deltas) {
        compareBooleanField(playerId, DeltaType.PHASE_COMPLETION, "phase1Completed", oldData.phase1Completed, newData.phase1Completed, deltas);
        compareBooleanField(playerId, DeltaType.PHASE_COMPLETION, "phase2Completed", oldData.phase2Completed, newData.phase2Completed, deltas);
    }
    
    private static void compareMultipliers(UUID playerId, PlayerProgressionData oldData, PlayerProgressionData newData, List<ProgressionDelta> deltas) {
        double oldMultiplier = oldData.getProgressionMultiplier();
        double newMultiplier = newData.getProgressionMultiplier();
        
        if (Math.abs(newMultiplier - oldMultiplier) >= 0.1) {
            deltas.add(new ProgressionDelta(playerId, DeltaType.MULTIPLIER_CHANGE, "progressionMultiplier", oldMultiplier, newMultiplier));
        }
    }
    
    private static void compareIntField(UUID playerId, DeltaType type, String fieldName, int oldValue, int newValue, List<ProgressionDelta> deltas) {
        if (oldValue != newValue) {
            deltas.add(new ProgressionDelta(playerId, type, fieldName, oldValue, newValue));
        }
    }
    
    private static void compareBooleanField(UUID playerId, DeltaType type, String fieldName, boolean oldValue, boolean newValue, List<ProgressionDelta> deltas) {
        if (oldValue != newValue) {
            deltas.add(new ProgressionDelta(playerId, type, fieldName, oldValue, newValue));
        }
    }
    
    private static List<ProgressionDelta> optimizeDeltaBatch(List<ProgressionDelta> deltas) {
        // Remover deltas redundantes (mesmo campo, manter apenas o mais recente)
        Map<String, ProgressionDelta> latestByField = new HashMap<>();
        
        for (ProgressionDelta delta : deltas) {
            ProgressionDelta existing = latestByField.get(delta.fieldName);
            if (existing == null || delta.timestamp > existing.timestamp) {
                latestByField.put(delta.fieldName, delta);
            }
        }
        
        // Ordenar por prioridade (maior primeiro)
        return latestByField.values().stream()
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
            .toList();
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
     * Packet para envio de deltas
     */
    public static class ProgressionDeltaPacket implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ProgressionDeltaPacket> TYPE = 
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimTrMod.MODID, "progression_delta"));
        
        public static final StreamCodec<FriendlyByteBuf, ProgressionDeltaPacket> STREAM_CODEC = 
            StreamCodec.of(
                (buf, packet) -> ProgressionDeltaPacket.write(packet, buf),
                ProgressionDeltaPacket::read
            );
        
        private final List<ProgressionDelta> deltas;
        
        public ProgressionDeltaPacket(List<ProgressionDelta> deltas) {
            this.deltas = deltas;
        }
        
        public static void write(ProgressionDeltaPacket packet, FriendlyByteBuf buf) {
            buf.writeInt(packet.deltas.size());
            for (ProgressionDelta delta : packet.deltas) {
                buf.writeUUID(delta.playerId);
                buf.writeEnum(delta.type);
                buf.writeUtf(delta.fieldName);
                
                // Serializar valores de forma compacta
                writeValue(buf, delta.newValue);
                buf.writeLong(delta.timestamp);
            }
        }
        
        public static ProgressionDeltaPacket read(FriendlyByteBuf buf) {
            int count = buf.readInt();
            List<ProgressionDelta> deltas = new ArrayList<>(count);
            
            for (int i = 0; i < count; i++) {
                UUID playerId = buf.readUUID();
                DeltaType type = buf.readEnum(DeltaType.class);
                String fieldName = buf.readUtf();
                Object newValue = readValue(buf);
                buf.readLong(); // timestamp (preservado para compatibilidade)
                
                ProgressionDelta delta = new ProgressionDelta(playerId, type, fieldName, null, newValue);
                deltas.add(delta);
            }
            
            return new ProgressionDeltaPacket(deltas);
        }
        
        private static void writeValue(FriendlyByteBuf buf, Object value) {
            if (value instanceof Boolean b) {
                buf.writeByte(0);
                buf.writeBoolean(b);
            } else if (value instanceof Integer i) {
                buf.writeByte(1);
                buf.writeVarInt(i);
            } else if (value instanceof Double d) {
                buf.writeByte(2);
                buf.writeDouble(d);
            } else {
                buf.writeByte(3);
                buf.writeUtf(value.toString());
            }
        }
        
        private static Object readValue(FriendlyByteBuf buf) {
            byte type = buf.readByte();
            return switch (type) {
                case 0 -> buf.readBoolean();
                case 1 -> buf.readVarInt();
                case 2 -> buf.readDouble();
                case 3 -> buf.readUtf();
                default -> null;
            };
        }
        
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
        
        public List<ProgressionDelta> getDeltas() {
            return deltas;
        }
    }
}
