package net.changed.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.item.SpecializedItemRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.model.ItemLayerModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    @Shadow public abstract ItemModelShaper getItemModelShaper();

    @Shadow @Final private Minecraft minecraft;

    @Unique private ItemStack cachedStack;
    @Unique private Level cachedLevel;
    @Unique private LivingEntity cachedEntity;
    @Unique private int cachedSeed;

    @WrapMethod(method = "getModel")
    public BakedModel cacheOverrideParams(ItemStack stack, Level level, LivingEntity entity, int seed, Operation<BakedModel> original) {
        cachedStack = stack;
        cachedLevel = level;
        cachedEntity = entity;
        cachedSeed = seed;
        return original.call(stack, level, entity, seed);
    }

    @WrapOperation(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/ClientHooks;handleCameraTransforms(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemDisplayContext;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            remap = false))
    public BakedModel getOverriddenModel(PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean applyLeftHandTransform, Operation<BakedModel> original,
                                        @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof SpecializedItemRendering special) {
            var modelName = special.getModelLocation(stack, cameraTransformType);
            if (modelName != null) {
                var newModel = this.getItemModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(modelName));
                if (newModel != model && cachedStack == stack) {
                    ClientLevel clientlevel = cachedLevel instanceof ClientLevel ? (ClientLevel) cachedLevel : null;
                    newModel = newModel.getOverrides().resolve(newModel, stack, clientlevel, cachedEntity, cachedSeed);
                }

                model = newModel;
            }
        }

        return original.call(poseStack, model, cameraTransformType, applyLeftHandTransform);
    }
}
