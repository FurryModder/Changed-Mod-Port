package net.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.client.FormRenderHandler;
import net.changed.client.renderer.layers.AccessoryLayer;
import net.changed.client.renderer.layers.FirstPersonLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void addChangedLayers(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
        this.addLayer(new AccessoryLayer<>(this));
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"), cancellable = true)
    private void renderHand(PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (FormRenderHandler.maybeRenderHand(((PlayerRenderer)(Object)this), stack, buffer, light, player, arm, sleeve)) {
            ci.cancel(); //cancel the call
        }
    }

    @Inject(method = "renderHand", at = @At("TAIL"), cancellable = true)
    private void renderHandLayers(PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        for (var layer : layers) {
            if (layer instanceof FirstPersonLayer firstPersonLayer)
                firstPersonLayer.renderFirstPersonOnArms(
                        stack, buffer, light, player, getModel().rightArm != arm ? HumanoidArm.LEFT : HumanoidArm.RIGHT,
                        arm.storePose(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        }
    }
}
