package net.changed.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class LoopedRecordItem extends Item {
    private final int comparatorValue;
    private final Supplier<SoundEvent> soundSupplier;

    public LoopedRecordItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Properties builder) {
        super(builder);
        this.comparatorValue = comparatorValue;
        this.soundSupplier = soundSupplier;
    }

    public int getAnalogOutput() {
        return this.comparatorValue;
    }

    public SoundEvent getSound() {
        return this.soundSupplier.get();
    }

    public int getLengthInTicks() {
        return Integer.MAX_VALUE;
    }
}
