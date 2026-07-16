package net.changed.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

final class ArmorItemProperties {
    private ArmorItemProperties() {}

    static Item.Properties withDurability(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        return properties.durability(type.getDurability(durabilityFactor(material)));
    }

    private static int durabilityFactor(Holder<ArmorMaterial> material) {
        if (material.equals(ArmorMaterials.LEATHER))
            return 5;
        if (material.equals(ArmorMaterials.GOLD))
            return 7;
        if (material.equals(ArmorMaterials.DIAMOND))
            return 33;
        if (material.equals(ArmorMaterials.NETHERITE))
            return 37;
        return 15;
    }
}
