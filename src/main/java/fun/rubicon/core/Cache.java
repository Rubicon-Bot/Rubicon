package fun.rubicon.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class Cache<K, V> {

    private Map<K, V> storage;

    public Cache() {
        storage = new HashMap<>();
    }

    public V get(K key) {
        return storage.get(key);
    }

    public void put(K key, V value) {
        if (containsKey(key))
            replace(key, value);
        else
            storage.put(key, value);
    }

    public void replace(K key, V value) {
        storage.replace(key, value);
    }

    public boolean containsKey(K key) {
        return storage.containsKey(key);
    }

    public void remove(K key) {
        storage.remove(key);
    }
}
