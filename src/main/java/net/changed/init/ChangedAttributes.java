package net.changed.init;

import net.changed.Changed;
import net.changed.ability.GrabEntityAbilityInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Changed.MODID);

    public static final DeferredHolder<Attribute, Attribute> TRANSFUR_TOLERANCE = REGISTRY.register("transfur_tolerance",
            () -> new RangedAttribute("attribute.name.changed.transfur_tolerance", 20.0D, 1.0D, 1024.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> TRANSFUR_DAMAGE = REGISTRY.register("transfur_damage",
            () -> new RangedAttribute("attribute.name.changed.transfur_damage", 3.0D, 0.0D, 2048.0D));

    /**
     * How Easily an entity will break free from a grab
     */
    public static final DeferredHolder<Attribute, Attribute> GRAB_STRUGGLE_STRENGTH = REGISTRY.register("grab_struggle_strength",
            () -> new RangedAttribute("attribute.name.changed.grab_struggle_strength", 0.005f, 0.0D, 2048.0D).setSyncable(true));

    /**
     * Multiplies the player's speed difference sprinting vs not. One is vanilla. Zero disables sprint. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final DeferredHolder<Attribute, Attribute> SPRINT_SPEED = REGISTRY.register("sprint_speed",
            () -> new RangedAttribute("attribute.name.changed.sprint_speed", 1.0D, 0.0D, 512.0D).setSyncable(true));
    /**
     * Multiplies the player's speed when sneaking. A higher value is faster. One is vanilla. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final DeferredHolder<Attribute, Attribute> SNEAK_SPEED = REGISTRY.register("sneak_speed",
            () -> new RangedAttribute("attribute.name.changed.sneak_speed", 1.0D, 0.0D, 512.0D).setSyncable(true));
    /**
     * Replaces player's base air capacity. In seconds. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final DeferredHolder<Attribute, Attribute> AIR_CAPACITY = REGISTRY.register("air_capacity",
            () -> new RangedAttribute("attribute.name.changed.air_capacity", 15.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Multiplies the entity's jump height.
     */
    public static final DeferredHolder<Attribute, Attribute> JUMP_STRENGTH = REGISTRY.register("jump_strength",
            () -> new RangedAttribute("attribute.name.changed.jump_strength", 1.0D, 0.0D, 256.0D).setSyncable(true));
    /**
     * Divides the entity's fall distance.
     */
    public static final DeferredHolder<Attribute, Attribute> FALL_RESISTANCE = REGISTRY.register("fall_resistance",
            () -> new RangedAttribute("attribute.name.changed.fall_resistance", 1.0D, 0.5D, 1024.0D).setSyncable(true));

    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> {
            event.add(entityType, ChangedAttributes.TRANSFUR_TOLERANCE);
            event.add(entityType, ChangedAttributes.GRAB_STRUGGLE_STRENGTH, GrabEntityAbilityInstance.GRAB_STRENGTH_DECAY);
        });

        event.add(EntityType.PLAYER, ChangedAttributes.TRANSFUR_TOLERANCE);
        event.add(EntityType.PLAYER, ChangedAttributes.TRANSFUR_DAMAGE, 3.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.GRAB_STRUGGLE_STRENGTH, GrabEntityAbilityInstance.GRAB_STRENGTH_DECAY_PLAYER);
        event.add(EntityType.PLAYER, ChangedAttributes.SPRINT_SPEED, 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.SNEAK_SPEED, 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.AIR_CAPACITY, 15.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.JUMP_STRENGTH, 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.FALL_RESISTANCE, 1.0D);
    }
}
