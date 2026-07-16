package net.changed.client.renderer.model;

import net.changed.block.StasisChamber;
import net.changed.client.ClientLivingEntityExtender;
import net.changed.util.EntityUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public interface LeglessModel {
    ModelPart getAbdomen();

    public static boolean shouldLeglessSit(LivingEntity entity) {
        return StasisChamber.isEntityCaptured(EntityUtil.maybeGetUnderlying(entity));
    }
}
