package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexYuinModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexDeerModel;
import net.furrymodder.changed.entity.beast.LatexYuin;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexYuinRenderer extends AdvancedHumanoidRenderer<LatexYuin, LatexYuinModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_yuin.png");

    public LatexYuinRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexYuinModel(context.bakeLayer(LatexYuinModel.LAYER_LOCATION)), ArmorLatexDeerModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xffc301)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexYuin entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}