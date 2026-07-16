package net.changed.entity;

import net.changed.init.ChangedAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;

public abstract class AttributePresets {
    /*
    Note for setting attributes
    - Movement speed is automatically adjusted to fit players with a 10:1 ratio
    - Net swim speed is (movement speed * swim speed)
     */

    public static void playerLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        map.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(1.0);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0);
        map.getInstance(net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT).setBaseValue(0.0);
    }

    public static void wolfLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075);
        map.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.95);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }

    public static void catLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.15);
        map.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.9);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(22.0);
        map.getInstance(ChangedAttributes.AIR_CAPACITY).setBaseValue(7.5);
        map.getInstance(ChangedAttributes.JUMP_STRENGTH).setBaseValue(1.25);
        map.getInstance(ChangedAttributes.FALL_RESISTANCE).setBaseValue(2.5);
    }

    public static void sharkLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.875);
        map.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(1.48);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }

    public static void dragonLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        map.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.85);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }
}
