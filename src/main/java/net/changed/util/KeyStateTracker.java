package net.changed.util;

public class KeyStateTracker {
    public interface KeyStateConsumer {
        void accept(boolean isDown, boolean wasDown, boolean unique);
    }

    private boolean isDown, isDownO;
    private int flips;

    public KeyStateTracker(boolean initialState) {
        this.isDown = initialState;
        this.isDownO = initialState;
    }

    public KeyStateTracker() {
        this(false);
    }

    public void copyFrom(KeyStateTracker other) {
        this.isDown = other.isDown;
        this.isDownO = other.isDownO;
        this.flips = other.flips;
    }

    public void reset(boolean initialState) {
        this.isDown = initialState;
        this.isDownO = initialState;
        this.flips = 0;
    }

    /**
     * Gets the current tracked state of the key, without handling flips
     * @return True if the key is down
     */
    public boolean isEffectivelyDown() {
        return (flips + (isDown ? 1 : 0)) % 2 == 1;
    }

    public int getFlipCount() {
        return flips;
    }

    /**
     * Queues a key state flip if isDown is opposite
     * @param isDown State of the key
     * @return True if a flip was queued
     */
    public boolean queueKeyState(boolean isDown) {
        if (isEffectivelyDown() == isDown)
            return false;
        flips++;
        return true;
    }

    /**
     * Invokes the consumer a minimum of once with the current key state and previous key state,
     * as well as if the key is being handled for the first time this function call.
     * @param consumer Invoked at least once, then again for remaining key flips.
     * @return The number of times consumer was invoked, at minimum one.
     */
    public int handleStateUpdates(KeyStateConsumer consumer) {
        int handleCount = 0;

        do {
            if (flips > 0) {
                flips--;
                isDown = !isDown;
            }

            consumer.accept(isDown, isDownO, handleCount == 0);
            handleCount++;

            isDownO = isDown;
        } while (flips > 0);

        return handleCount;
    }
}
