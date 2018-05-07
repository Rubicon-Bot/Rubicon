package fun.rubicon.core;

import java.util.HashMap;

/**
 * @author ForYaSee / Yannick Seeger
 */
public abstract class Cache<T, V> {

    protected HashMap<T, V> cache;

    public Cache() {
        this.cache = new HashMap<>();
    }

    public V get(T key) {
        if (cache.containsKey(key))
            return cache.get(key);
        return null;
    }

    public boolean contains(T key) {
        return cache.containsKey(key);
    }

    public V update(T key, V value) {
        if (cache.containsKey(key))
            cache.replace(key, value);
        else
            cache.put(key, value);
        return get(key);
    }

    public void remove(T key) {
        cache.remove(key);
    }
}
