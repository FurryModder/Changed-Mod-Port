package net.changed.util;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.*;

public class KeyStatesTracker<K> {
    public interface KeyStateConsumer<K> {
        void accept(K key, boolean isDown, boolean wasDown, boolean unique);
    }

    private static class KeyState {
        boolean isDown, isDownO;

        KeyState(boolean initialState) {
            this.isDown = initialState;
            this.isDownO = initialState;
        }
    }

    private static class KeyFlips<K> {
        final K key;
        int flips;

        private KeyFlips(KeyFlips<K> copyFrom) {
            this.key = copyFrom.key;
            this.flips = copyFrom.flips;
        }

        private KeyFlips(K key, int flips) {
            this.key = key;
            this.flips = flips;
        }

        private KeyFlips(K key) {
            this(key, 0);
        }

        int getFlips() {
            return flips;
        }
    }

    private final Map<K, KeyState> keyStates;
    private final List<KeyFlips<K>> keyFlips = new ArrayList<>();

    public KeyStatesTracker(Set<K> keys, boolean initialState) {
        var mapBuilder = ImmutableMap.<K, KeyState>builder();
        for (var key : keys)
            mapBuilder.put(key, new KeyState(initialState));
        keyStates = mapBuilder.build();
    }

    public KeyStatesTracker(Set<K> keys) {
        this(keys, false);
    }

    public void copyFrom(KeyStatesTracker<K> otherKeyStates) {
        this.keyStates.forEach((key, keyState) -> {
            var copyState = otherKeyStates.keyStates.get(key);
            keyState.isDown = copyState.isDown;
            keyState.isDownO = copyState.isDownO;
        });
        this.keyFlips.clear();
        otherKeyStates.keyFlips.forEach(keyFlips -> {
            this.keyFlips.add(new KeyFlips<>(keyFlips));
        });
    }

    public void reset(boolean initialState) {
        this.keyStates.forEach((key, keyState) -> {
            keyState.isDown = initialState;
            keyState.isDownO = initialState;
        });
        this.keyFlips.clear();
    }

    /**
     * Gets the current tracked state of the key, without handling each key
     * @param key Key to check
     * @return True if the key is down
     */
    public boolean isEffectivelyDown(K key) {
        int flips = getFlipCount(key);
        return (flips + (keyStates.get(key).isDown ? 1 : 0)) % 2 == 1;
    }

    public int getFlipCount(K key) {
        return keyFlips.stream()
                .filter(keyFlips -> keyFlips.key.equals(key))
                .mapToInt(KeyFlips::getFlips)
                .sum();
    }

    /**
     * Queues a key state flip if isDown is opposite
     * @param key Key that is queuing a change for
     * @param isDown State of the key
     * @return True if a flip was queued
     */
    public boolean queueKeyState(K key, boolean isDown) {
        if (isEffectivelyDown(key) == isDown)
            return false;
        if (keyFlips.isEmpty() || !keyFlips.get(keyFlips.size() - 1).key.equals(key))
            keyFlips.add(new KeyFlips<>(key));
        keyFlips.get(keyFlips.size() - 1).flips++;
        return true;
    }

    /**
     * Invokes the consumer a minimum of once per each key, with their current key state and previous key state,
     * as well as if the key is being handled for the first time this function call.
     * @param consumer Invoked at least once, then again for remaining key flips. Invokes are in the order they are queued,
     *                 then by the order presented in the internal map.
     * @return The number of times consumer was invoked, at minimum the number of keys tracked.
     */
    public int handleStateUpdates(KeyStateConsumer<K> consumer) {
        if (keyFlips.isEmpty()) { // Case 1 - no flips queued, process in order of map
            keyStates.forEach((key, keyState) -> {
                consumer.accept(key, keyState.isDown, keyState.isDownO, true);
            });
            return keyStates.size();
        }

        else { // Case 2 - flips are queued, process in order of queue, then remaining in order of map
            Object2IntArrayMap<K> handleCounts = new Object2IntArrayMap<>(keyStates.size());
            keyStates.keySet().forEach(key -> handleCounts.put(key, 0));

            keyFlips.forEach(keyFlip -> {
                var keyState = keyStates.get(keyFlip.key);

                do {
                    if (keyFlip.flips > 0) {
                        keyFlip.flips--;
                        keyState.isDown = !keyState.isDown;
                    }

                    int handleCount = handleCounts.getInt(keyFlip.key);
                    consumer.accept(keyFlip.key, keyState.isDown, keyState.isDownO, handleCount == 0);
                    handleCounts.put(keyFlip.key, handleCount + 1);

                    keyState.isDownO = keyState.isDown;
                } while (keyFlip.flips > 0);
            });

            keyStates.forEach((key, keyState) -> {
                if (handleCounts.getInt(key) > 0)
                    return;
                consumer.accept(key, keyState.isDown, keyState.isDownO, true);
                handleCounts.put(key, 1);
            });

            keyFlips.clear();
            return handleCounts.values().intStream().sum();
        }
    }
}
