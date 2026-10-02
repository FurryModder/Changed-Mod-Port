package net.furrymodder.changed.entity;

import net.furrymodder.changed.data.AccessorySlots;
import net.furrymodder.changed.fluid.Gas;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public interface LivingEntityDataExtension {
    int getNoControlTicks();
    void setNoControlTicks(int ticks);

    int getInvertControlTicks();
    void setInvertControlTicks(int ticks);

    @Nullable
    LivingEntity getGrabbedBy();
    void setGrabbedBy(@Nullable LivingEntity holder);

    <T extends Gas> Optional<T> isEyeInGas(Class<T> gas);

    void do_hurtCurrentlyUsedShield(float blocked);
    void do_blockUsingShield(LivingEntity attacker);

    Optional<AccessorySlots> getAccessorySlots();
}
