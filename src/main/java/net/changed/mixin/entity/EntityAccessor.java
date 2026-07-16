package net.changed.mixin.entity;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("wasTouchingWater")
    boolean changed$getWasTouchingWater();

    @Accessor("wasTouchingWater")
    void changed$setWasTouchingWater(boolean wasTouchingWater);

    @Accessor("vehicle")
    Entity changed$getVehicle();

    @Accessor("vehicle")
    void changed$setVehicle(Entity vehicle);
}
