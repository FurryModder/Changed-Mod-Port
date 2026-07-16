package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.DarkLatexWolfFemaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.entity.beast.DarkLatexWolfFemale;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfFemaleRenderer extends AdvancedHumanoidRenderer<DarkLatexWolfFemale, DarkLatexWolfFemaleModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/dark_latex_wolf_female.png");

	public DarkLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfFemaleModel(context.bakeLayer(DarkLatexWolfFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotMask));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
				.withSclera(Color3.fromInt(0x242424))
				.withIris(CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
						CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE))
				.build());
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfFemale entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}