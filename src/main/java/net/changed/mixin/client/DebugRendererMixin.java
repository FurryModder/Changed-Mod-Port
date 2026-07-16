package net.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.client.ChangedClient;
import net.changed.client.debug.ChangedDebugRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin {
    @WrapMethod(method = "clear")
    public void clearChangedDebugRenderers(Operation<Void> original) {
        original.call();

        ChangedClient.debugRenderer.get().clear();
    }

    @WrapMethod(method = "render")
    public void renderChangedDebugRenderers(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ, Operation<Void> original) {
        original.call(poseStack, bufferSource, camX, camY, camZ);

        ChangedClient.debugRenderer.get().render(poseStack, bufferSource, camX, camY, camZ);
    }
}
