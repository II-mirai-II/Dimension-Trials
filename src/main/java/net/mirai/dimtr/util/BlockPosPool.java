package net.mirai.dimtr.util;

import net.minecraft.core.BlockPos;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 🎯 PERFORMANCE: Pool de objetos BlockPos para reduzir GC overhead
 * 
 * BlockPos é criado frequentemente em verificações de portal, 
 * este pool reutiliza instâncias para melhorar performance.
 */
public class BlockPosPool {
    
    private static final int POOL_SIZE = 100;
    private static final BlockingQueue<BlockPos.MutableBlockPos> POOL = new ArrayBlockingQueue<>(POOL_SIZE);
    
    static {
        // Inicializar pool com instâncias reutilizáveis
        for (int i = 0; i < POOL_SIZE; i++) {
            POOL.offer(new BlockPos.MutableBlockPos());
        }
    }
    
    /**
     * Obter BlockPos do pool (ou criar novo se pool vazio)
     */
    public static BlockPos.MutableBlockPos acquire() {
        BlockPos.MutableBlockPos pos = POOL.poll();
        return pos != null ? pos : new BlockPos.MutableBlockPos();
    }
    
    /**
     * Obter BlockPos configurado com coordenadas
     */
    public static BlockPos.MutableBlockPos acquire(int x, int y, int z) {
        BlockPos.MutableBlockPos pos = acquire();
        pos.set(x, y, z);
        return pos;
    }
    
    /**
     * Obter BlockPos baseado em outro BlockPos
     */
    public static BlockPos.MutableBlockPos acquire(BlockPos original) {
        BlockPos.MutableBlockPos pos = acquire();
        pos.set(original);
        return pos;
    }
    
    /**
     * Retornar BlockPos para o pool para reutilização
     */
    public static void release(BlockPos.MutableBlockPos pos) {
        if (pos != null && POOL.size() < POOL_SIZE) {
            // Reset para estado limpo antes de retornar ao pool
            pos.set(0, 0, 0);
            POOL.offer(pos);
        }
    }
    
    /**
     * Obter estatísticas do pool para debugging
     */
    public static int getAvailablePositions() {
        return POOL.size();
    }
}
