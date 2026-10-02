package net.furrymodder.changed.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class LatexRecordItem extends LoopedRecordItem {
    private static final Component NAME = Component.translatable("item.changed.latex_music_disc");

    public LatexRecordItem(Properties builder) {
        super(builder);
    }

    @Override
    public Component getName(ItemStack stack) {
        return NAME;
    }
}
