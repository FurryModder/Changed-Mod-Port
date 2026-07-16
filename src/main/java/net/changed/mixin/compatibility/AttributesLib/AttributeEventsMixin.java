package net.changed.mixin.compatibility.AttributesLib;

import dev.shadowsoffire.apothic_attributes.impl.AttributeEvents;
import net.changed.extension.RequiredMods;
import net.changed.process.ProcessTransfur;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttributeEvents.class, remap = false)
@RequiredMods("attributeslib|apothic_attributes")
public class AttributeEventsMixin {
    @Inject(method = "fixChangedAttributes", at = @At("TAIL"), require = 0)
    public void fixAttributesForChanged(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), variant -> {
            // Let TransfurVariantInstance handle attributes
            variant.refreshAttributes();
        });
    }
}
