package net.changed.init;

import net.changed.Changed;
import net.changed.data.AccessorySlotType;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedAccessorySlots {
    public static final DeferredRegister<AccessorySlotType> REGISTRY = ChangedRegistry.ACCESSORY_SLOTS.createDeferred(Changed.MODID);
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> BODY = REGISTRY.register("body", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> FACE = REGISTRY.register("face", () -> new AccessorySlotType(EquipmentSlot.HEAD));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> FULL_BODY = REGISTRY.register("full_body", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> HANDS = REGISTRY.register("hands", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> HEAD = REGISTRY.register("head", () -> new AccessorySlotType(EquipmentSlot.HEAD));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> LEGS = REGISTRY.register("legs", () -> new AccessorySlotType(EquipmentSlot.LEGS));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> LOWER_BODY = REGISTRY.register("lower_body", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> LOWER_BODY_SIDE = REGISTRY.register("lower_body_side", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final DeferredHolder<AccessorySlotType, AccessorySlotType> NECK = REGISTRY.register("neck", () -> new AccessorySlotType(EquipmentSlot.CHEST));
}
