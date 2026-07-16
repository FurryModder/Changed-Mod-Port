package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.PureWhiteLatexCerberusModel;
import net.changed.client.renderer.model.PureWhiteLatexWolfModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.client.renderer.model.armor.ArmorModelSet;
import net.changed.client.renderer.model.armor.ArmorPureWhiteLatexCerberusModel;
import net.changed.entity.beast.PureWhiteLatexCerberus;
import net.changed.entity.beast.PureWhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PureWhiteLatexCerberusRenderer extends AdvancedHumanoidRenderer<PureWhiteLatexCerberus, PureWhiteLatexCerberusModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/pure_white_latex_wolf.png");

	public PureWhiteLatexCerberusRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteLatexCerberusModel(context.bakeLayer(PureWhiteLatexCerberusModel.LAYER_LOCATION)),
				ArmorModelSet.castOf(ArmorPureWhiteLatexCerberusModel.MODEL_SET, ArmorPureWhiteLatexCerberusModel::new), 0.7f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteLatexCerberus entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}