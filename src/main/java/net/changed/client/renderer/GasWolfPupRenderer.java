package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomCoatLayer;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.model.GasWolfPupModel;
import net.changed.client.renderer.model.armor.ArmorNoneModel;
import net.changed.entity.beast.GasWolfPup;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class GasWolfPupRenderer extends AdvancedHumanoidRenderer<GasWolfPup, GasWolfPupModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/gas_wolf_pup.png");

	public GasWolfPupRenderer(EntityRendererProvider.Context context) {
		super(context, new GasWolfPupModel(context.bakeLayer(GasWolfPupModel.LAYER_LOCATION)), ArmorNoneModel.MODEL_SET, 0.4F);
        this.addLayer(new CustomCoatLayer<>(this, this.getModel(), Changed.modResource("textures/gas_wolf_pup_coat")));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .build().setHeadShape(CustomEyesLayer.HeadShape.PUP));
	}

	@Override
	public ResourceLocation getTextureLocation(GasWolfPup entity) {
		return DEFAULT_SKIN_LOCATION;
	}

	@Override
	protected float getFlipDegrees(GasWolfPup entity) {
		return entity.getPose() == Pose.SLEEPING ? 0.0F : super.getFlipDegrees(entity);
	}

	@Override
	protected boolean isEntityUprightType(@NotNull GasWolfPup entity) {
		return false;
	}
}