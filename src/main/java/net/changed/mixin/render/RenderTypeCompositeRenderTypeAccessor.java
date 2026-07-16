package net.changed.mixin.render;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeRenderType.class)
public interface RenderTypeCompositeRenderTypeAccessor {
    @Accessor("state")
    RenderType.CompositeState changed$getState();
}
