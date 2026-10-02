package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexTrafficConeDragonModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexBigTailDragonModel;
import net.furrymodder.changed.entity.beast.LatexTrafficConeDragon;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTrafficConeDragonRenderer extends AdvancedHumanoidRenderer<LatexTrafficConeDragon, LatexTrafficConeDragonModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_traffic_cone_dragon.png");

	public LatexTrafficConeDragonRenderer(EntityRendererProvider.Context context) {
		super(context, new LatexTrafficConeDragonModel(context.bakeLayer(LatexTrafficConeDragonModel.LAYER_LOCATION)), ArmorLatexBigTailDragonModel.MODEL_SET, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, this.model));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
				.withSclera(Color3.fromInt(0xffea86)).withIris(Color3.BLACK).build());
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(LatexTrafficConeDragon entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}