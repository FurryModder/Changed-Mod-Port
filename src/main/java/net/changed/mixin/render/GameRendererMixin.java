package net.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.ability.AbstractAbility;
import net.changed.client.ChangedClient;
import net.changed.client.latexparticles.SetupContext;
import net.changed.entity.LivingEntityDataExtension;
import net.changed.init.ChangedAbilities;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Camera mainCamera;

    @Shadow public abstract LightTexture lightTexture();

    @Shadow public abstract boolean isPanoramicMode();

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void getNightVisionScale(LivingEntity livingEntity, float p_109110_, CallbackInfoReturnable<Float> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(livingEntity), variant -> {
            if (variant.visionType.test(MobEffects.NIGHT_VISION.value())) {
                callback.setReturnValue(1.0f);
            }

            if (variant.getParent().getBreatheMode().canBreatheWater() && livingEntity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                callback.setReturnValue(0.85f);
            }
        });
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobView(PoseStack pose, float partialTicks, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(Minecraft.getInstance().getCameraEntity()), variant -> {
            if (variant.getEntityShape().isLegless())
                callback.cancel();
        });
    }

    @WrapOperation(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"),
            require = 0
    )
    public Entity overrideGrabbedEntity(Minecraft instance, Operation<Entity> original) {
        final var entity = original.call(instance);

        if (entity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            return AbstractAbility.getAbilityInstanceSafe(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                    .map(ability -> ability.grabbedHasControl ? ability.grabbedEntity : null)
                    .orElseGet(ext::getGrabbedBy);
        }
        else if (entity instanceof LivingEntity livingEntity) {
            return AbstractAbility.getAbilityInstanceSafe(livingEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                    .<Entity>map(ability -> ability.grabbedHasControl ? ability.grabbedEntity : null)
                    .orElse(entity);
        }

        return entity;
    }

    @Inject(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;turnOffLightLayer()V"))
    public void hookFirstPersonParticles(Camera camera, float partialTicks, Matrix4f projectionMatrix, CallbackInfo ci,
                                         @Local PoseStack pose) {
        ChangedClient.particleSystem.getOrThrow().render(pose, this.lightTexture(), camera, partialTicks, null, SetupContext.FIRST_PERSON);
    }
}
