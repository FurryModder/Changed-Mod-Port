package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexMantaRayMaleModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexOrcaModel;
import net.furrymodder.changed.entity.beast.LatexMantaRayMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayMaleRenderer extends AdvancedHumanoidRenderer<LatexMantaRayMale, LatexMantaRayMaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_manta_ray_male.png");

    public LatexMantaRayMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayMaleModel(context.bakeLayer(LatexMantaRayMaleModel.LAYER_LOCATION)), ArmorLatexMaleSharkModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayMale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
