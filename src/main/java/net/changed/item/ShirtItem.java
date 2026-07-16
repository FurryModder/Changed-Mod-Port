package net.changed.item;

import net.changed.init.ChangedSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public class ShirtItem extends ClothingItem {
    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.SHIRT_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SHIRT_BREAK.get();
    }
}
