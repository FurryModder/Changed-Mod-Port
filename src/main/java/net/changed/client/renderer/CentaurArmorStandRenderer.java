package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.changed.Changed;
import net.changed.client.renderer.layers.ArmorStandArmorLayer;
import net.changed.client.renderer.model.CentaurArmorStandModel;
import net.changed.client.renderer.model.LeglessArmorStandModel;
import net.changed.client.renderer.model.armor.ArmorModelPicker;
import net.changed.entity.decoration.CentaurArmorStand;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class CentaurArmorStandRenderer extends LivingEntityRenderer<CentaurArmorStand, CentaurArmorStandModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/entity/armor_stands/centaur_armor_stand.png");

    public CentaurArmorStandRenderer(EntityRendererProvider.Context context) {
        super(context, new CentaurArmorStandModel(context.bakeLayer(CentaurArmorStandModel.ARMOR_STAND)), 0.0F);
        this.addLayer(new ArmorStandArmorLayer<>(this,
                ArmorModelPicker.centaur(context.getModelSet(), CentaurArmorStandModel.MODEL_SET_UPPER, CentaurArmorStandModel.MODEL_SET_LOWER),
                context.getModelManager()));
        //this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        //this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
        //this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(CentaurArmorStand entity) {
        return DEFAULT_SKIN_LOCATION;
    }

    protected void setupRotations(CentaurArmorStand entity, PoseStack poseStack, float p_113802_, float p_113803_, float p_113804_) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - p_113803_));
        float f = (float)(entity.level().getGameTime() - entity.lastHit) + p_113804_;
        if (f < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(f / 1.5F * (float)Math.PI) * 3.0F));
        }

    }

    protected boolean shouldShowName(CentaurArmorStand entity) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(entity);
        float f = entity.isCrouching() ? 32.0F : 64.0F;
        return d0 >= (double)(f * f) ? false : entity.isCustomNameVisible();
    }

    @Nullable
    protected RenderType getRenderType(CentaurArmorStand entity, boolean p_113807_, boolean p_113808_, boolean p_113809_) {
        if (!entity.isMarker()) {
            return super.getRenderType(entity, p_113807_, p_113808_, p_113809_);
        } else {
            ResourceLocation resourcelocation = this.getTextureLocation(entity);
            if (p_113808_) {
                return RenderType.entityTranslucent(resourcelocation, false);
            } else {
                return p_113807_ ? RenderType.entityCutoutNoCull(resourcelocation, false) : null;
            }
        }
    }
}