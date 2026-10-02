package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.LatexTranslucentLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.PhageLatexWolfMaleModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.furrymodder.changed.entity.beast.PhageLatexWolfMale;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PhageLatexWolfMaleRenderer extends AdvancedHumanoidRenderer<PhageLatexWolfMale, PhageLatexWolfMaleModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/phage_latex_wolf_male.png");

	public PhageLatexWolfMaleRenderer(EntityRendererProvider.Context context) {
		super(context, new PhageLatexWolfMaleModel(context.bakeLayer(PhageLatexWolfMaleModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotMask));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
				.withSclera(Color3.fromInt(0x242424))
				.withIris(CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
						CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE))
				.build());
		this.addLayer(new LatexTranslucentLayer<>(this, model, Changed.modResource("textures/phage_latex_wolf_male_translucent.png")));
	}

	@Override
	public ResourceLocation getTextureLocation(PhageLatexWolfMale entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}