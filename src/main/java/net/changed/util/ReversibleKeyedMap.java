package net.changed.util;

import com.mojang.datafixers.util.Pair;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Map that has two keys for each value, and the keys map to the same object regardless of order
 * @param <K> key type
 * @param <V> value type
 */
public class ReversibleKeyedMap<K, V> implements BiFunction<K, K, Pair<V, V>> {
    private final Map<K, Map<K, Pair<V, V>>> internal;

    public ReversibleKeyedMap() {
        this.internal = new HashMap<>();
    }

    public ReversibleKeyedMap(Map<K, Map<K, Pair<V, V>>> technology) {
        this.internal = technology;
    }

    @Override
    public Pair<V, V> apply(K l, K r) {
        return get(l, r);
    }

    public Pair<V, V> get(Pair<K, K> keys) {
        return get(keys.getFirst(), keys.getSecond());
    }

    public Pair<V, V> get(K l, K r) {
        if (internal.containsKey(l)) {
            return internal.get(l).get(r);
        } else if (internal.containsKey(r)) {
            return internal.get(r).get(l).swap();
        }

        return null;
    }

    public Pair<V, V> put(K l, K r, Pair<V, V> value) {
        if (internal.containsKey(l))
            return internal.get(l).put(r, value);
        else if (internal.containsKey(r))
            return internal.get(r).put(l, value.swap());
        return internal.computeIfAbsent(l, (key) -> new HashMap<>()).put(r, value);
    }

    public Pair<V, V> computeIfAbsent(K l, K r, BiFunction<K, K, Pair<V, V>> ifAbsent) {
        Pair<V, V> existing = get(l, r);
        if (existing != null)
            return existing;
        existing = ifAbsent.apply(l, r);
        if (existing != null) {
            put(l, r, existing);
            return existing;
        }
        return null;
    }
}
