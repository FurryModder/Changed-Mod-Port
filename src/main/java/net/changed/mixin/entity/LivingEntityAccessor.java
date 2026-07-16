package net.changed.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("jumping")
    boolean changed$getJumping();

    @Accessor("jumping")
    void changed$setJumping(boolean jumping);

    @Accessor("swimAmount")
    float changed$getSwimAmount();

    @Accessor("swimAmount")
    void changed$setSwimAmount(float swimAmount);

    @Accessor("swimAmountO")
    float changed$getSwimAmountO();

    @Accessor("swimAmountO")
    void changed$setSwimAmountO(float swimAmountO);

    @Accessor("fallFlyTicks")
    int changed$getFallFlyTicks();

    @Accessor("fallFlyTicks")
    void changed$setFallFlyTicks(int fallFlyTicks);

    @Accessor("useItemRemaining")
    int changed$getUseItemRemaining();

    @Accessor("useItemRemaining")
    void changed$setUseItemRemaining(int useItemRemaining);
}
