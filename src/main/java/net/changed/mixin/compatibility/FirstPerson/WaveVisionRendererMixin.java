package net.changed.mixin.compatibility.FirstPerson;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.changed.client.WaveVisionRenderer;
import net.changed.extension.RequiredMods;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WaveVisionRenderer.class, remap = false)
@RequiredMods("firstperson")
public abstract class WaveVisionRendererMixin {
    @Shadow public abstract void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource);

    @Inject(method = "renderEntities", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V"))
    public void andRenderFirstPerson(ClientLevel level, Frustum frustum, Camera camera, PoseStack poseStack, double camX, double camY, double camZ, float partialTicks, MultiBufferSource bufferSource, Runnable submitDrawCall, CallbackInfoReturnable<Integer> cir) {
        if (!camera.isDetached() && FirstPersonModelCore.instance.getLogicHandler().shouldApplyThirdPerson(false)) {
            FirstPersonModelCore.instance.setRenderingPlayer(true);
            this.renderEntity(camera.getEntity(), camX, camY, camZ, partialTicks, poseStack, bufferSource);
            FirstPersonModelCore.instance.setRenderingPlayer(false);
        }
    }
}
