package net.changed.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.data.AccessorySlotContext;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public interface AccessoryRenderer {
    <T extends LivingEntity, M extends EntityModel<T>> void render(AccessorySlotContext<T> slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                   int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);

    default <T extends LivingEntity, M extends EntityModel<T>> void renderFirstPersonOnArms(AccessorySlotContext<T> slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                                            int light, HumanoidArm arm, PartPose armPose, float partialTick) {}
}
