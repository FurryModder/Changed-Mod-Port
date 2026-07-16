package net.changed.mixin.compatibility.JourneyMap;

import journeymap.client.model.entity.EntityDTO;
import journeymap.client.render.draw.MobIconCache;
import net.changed.entity.ChangedEntity;
import net.changed.extension.RequiredMods;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobIconCache.class, remap = false)
@RequiredMods("journeymap")
public abstract class MobIconCacheMixin {
    @Inject(method = "getMobIcon", at = @At("HEAD"), cancellable = true)
    private static void useJourneyMapMarkerForChangedEntities(EntityDTO entityDTO, boolean outlined,
                                                             CallbackInfoReturnable<Object> callback) {
        Entity entity = entityDTO.getEntityRef() != null ? entityDTO.getEntityRef().get() : null;
        if (entity instanceof ChangedEntity) {
            callback.setReturnValue(null);
        }
    }
}
