package net.furrymodder.changed.item;

import net.furrymodder.changed.init.ChangedBlocks;
import net.furrymodder.changed.init.ChangedTabs;
import net.minecraft.world.item.ItemNameBlockItem;

public class LabBook extends ItemNameBlockItem {
    public LabBook() {
        super(ChangedBlocks.BOOK_STACK.get(), new Properties());
    }
}
