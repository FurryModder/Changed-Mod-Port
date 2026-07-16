package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.PureWhiteLatexWolfModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.entity.beast.PureWhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PureWhiteLatexWolfRenderer extends AdvancedHumanoidRenderer<PureWhiteLatexWolf, PureWhiteLatexWolfModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/pure_white_latex_wolf.png");

	public PureWhiteLatexWolfRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteLatexWolfModel(context.bakeLayer(PureWhiteLatexWolfModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteLatexWolf entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}