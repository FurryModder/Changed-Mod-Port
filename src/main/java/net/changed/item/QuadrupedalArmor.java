package net.changed.item;

import net.changed.Changed;
import net.changed.entity.variant.ClothingShape;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class QuadrupedalArmor extends ArmorItem implements ExtendedItemProperties {
    public static boolean useQuadrupedalModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean useInnerQuadrupedalModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    public QuadrupedalArmor(Holder<ArmorMaterial> material, ArmorItem.Type slot) {
        super(material, slot, ArmorItemProperties.withDurability(material, slot, new Properties()));
    }

    public QuadrupedalArmor(Holder<ArmorMaterial> material, ArmorItem.Type slot, Properties properties) {
        super(material, slot, ArmorItemProperties.withDurability(material, slot, properties));
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return material.equals(ArmorMaterials.LEATHER) && type.getSlot() == EquipmentSlot.FEET;
    }

    @Override
    public void wearTick(ItemStack itemStack, LivingEntity wearer) {}

    @Override
    public boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        var instance = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(wearer));
        if (instance != null && !instance.shouldApplyAbilities())
            return false;

        return ExtendedItemProperties.super.allowedToWear(itemStack, wearer, slot);
    }

    @Override
    public ClothingShape.Legs getExpectedLegShape(ItemStack itemStack) {
        return ClothingShape.Legs.QUADRUPEDAL;
    }

    @Override
    public ClothingShape.Feet getExpectedFeetShape(ItemStack itemStack) {
        return ClothingShape.Feet.QUADRUPEDAL;
    }

    @Nullable
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(Changed.MODID,
                layer.texture(useInnerQuadrupedalModel(slot)).getPath().replace("textures/models/armor/", "textures/models/quadrupedal_armor/"));
    }
}
