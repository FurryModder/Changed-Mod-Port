package net.changed.mixin.compatibility.Curios;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.changed.client.renderer.layers.PlayerLayerWrapper;
import net.changed.extension.RequiredMods;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.client.render.CuriosLayer;

@Mixin(value = PlayerLayerWrapper.class, remap = false)
@RequiredMods("curios")
public abstract class PlayerLayerWrapperMixin {
    @WrapMethod(method = "isWrappable")
    private static boolean curios$isWrappable(RenderLayer<?, ?> layer, Operation<Boolean> original) {
        if (layer instanceof CuriosLayer<?,?>) return false;

        return original.call(layer);
    }
}
