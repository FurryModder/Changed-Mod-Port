package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexDeerModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexDeerModel;
import net.furrymodder.changed.entity.beast.LatexDeer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexDeerRenderer extends AdvancedHumanoidRenderer<LatexDeer, LatexDeerModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_deer.png");

    public LatexDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexDeerModel(context.bakeLayer(LatexDeerModel.LAYER_LOCATION)), ArmorLatexDeerModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexDeer entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}