package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexOtterModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexOtterModel;
import net.furrymodder.changed.entity.beast.LatexOtter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexOtterRenderer extends AdvancedHumanoidRenderer<LatexOtter, LatexOtterModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_otter.png");

    public LatexOtterRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexOtterModel(context.bakeLayer(LatexOtterModel.LAYER_LOCATION)), ArmorLatexOtterModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexOtter entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}