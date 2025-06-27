package net.mirai.dimtr.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 🎯 PERFORMANCE: Cache LRU (Least Recently Used) para otimizar uso de memória
 * 
 * @param <K> Tipo da chave
 * @param <V> Tipo do valor
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;
    
    public LRUCache(int maxSize) {
        super(16, 0.75f, true); // true = access-order
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
    
    /**
     * Obter valor com computação lazy se não existir
     */
    public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction) {
        V value = get(key);
        if (value == null) {
            value = mappingFunction.apply(key);
            if (value != null) {
                put(key, value);
            }
        }
        return value;
    }
}
