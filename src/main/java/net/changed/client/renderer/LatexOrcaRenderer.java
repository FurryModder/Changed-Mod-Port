package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexOrcaModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.changed.client.renderer.model.armor.ArmorLatexOrcaModel;
import net.changed.entity.beast.LatexOrca;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexOrcaRenderer extends AdvancedHumanoidRenderer<LatexOrca, LatexOrcaModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_orca.png");

    public LatexOrcaRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexOrcaModel(context.bakeLayer(LatexOrcaModel.LAYER_LOCATION)), ArmorLatexOrcaModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexOrca entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}