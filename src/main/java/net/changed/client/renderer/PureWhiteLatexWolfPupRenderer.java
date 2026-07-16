package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.model.DarkLatexWolfPupModel;
import net.changed.client.renderer.model.PureWhiteLatexWolfPupModel;
import net.changed.client.renderer.model.armor.ArmorNoneModel;
import net.changed.entity.beast.DarkLatexWolfPup;
import net.changed.entity.beast.PureWhiteLatexWolfPup;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class PureWhiteLatexWolfPupRenderer extends AdvancedHumanoidRenderer<PureWhiteLatexWolfPup, PureWhiteLatexWolfPupModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/pure_white_latex_wolf_pup.png");

	public PureWhiteLatexWolfPupRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteLatexWolfPupModel(context.bakeLayer(PureWhiteLatexWolfPupModel.LAYER_LOCATION)), ArmorNoneModel.MODEL_SET, 0.4F);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteLatexWolfPup entity) {
		return DEFAULT_SKIN_LOCATION;
	}

	@Override
	protected float getFlipDegrees(PureWhiteLatexWolfPup entity) {
		return entity.getPose() == Pose.SLEEPING ? 0.0F : super.getFlipDegrees(entity);
	}

	@Override
	protected boolean isEntityUprightType(@NotNull PureWhiteLatexWolfPup entity) {
		return false;
	}
}