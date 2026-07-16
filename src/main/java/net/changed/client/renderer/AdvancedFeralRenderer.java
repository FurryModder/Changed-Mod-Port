package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.client.renderer.model.armor.ArmorModelPicker;
import net.changed.client.renderer.model.armor.ArmorModelSet;
import net.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.changed.entity.ChangedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public abstract class AdvancedFeralRenderer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AdvancedHumanoidRenderer<T, M> {
    public AdvancedFeralRenderer(EntityRendererProvider.Context context, M main, ArmorModelPicker<T, ? extends LatexHumanoidArmorModel<? super T, ?>> modelPicker, float shadowSize) {
        super(context, main, modelPicker, shadowSize);
    }

    public AdvancedFeralRenderer(EntityRendererProvider.Context context, M main, ArmorModelSet<? super T, ? extends LatexHumanoidArmorModel<? super T, ?>> modelSet, float shadowSize) {
        super(context, main, modelSet, shadowSize);
    }

    @Override
    protected void setupRotations(@NotNull T entity, PoseStack poseStack, float bob, float bodyYRot, float partialTicks, float scale) {
        float swimAmount = entity.isVisuallySwimming() ? Math.max(entity.getSwimAmount(partialTicks), 1.0F) : entity.getSwimAmount(partialTicks);
        boolean upright = isEntityUprightType(entity);
        if (!upright && swimAmount > 0.0F) {
            super.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks, scale);
            float f3 = entity.isInWater() ? -entity.getXRot() : 0.0F;
            float f4 = Mth.lerp(swimAmount, 0.0F, f3 * 0.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees(f4));
        } else {
            super.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks, scale);
        }
    }

    @Override
    protected boolean isEntityUprightType(@NotNull T entity) {
        return false;
    }
}
