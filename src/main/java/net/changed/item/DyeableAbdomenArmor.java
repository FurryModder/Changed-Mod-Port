package net.changed.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.core.Holder;

public class DyeableAbdomenArmor extends AbdomenArmor {
    public DyeableAbdomenArmor(Holder<ArmorMaterial> material, ArmorItem.Type slot) {
        super(material, slot);
    }
}
