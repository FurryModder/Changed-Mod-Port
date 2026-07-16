package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexRedPandaModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.changed.entity.beast.LatexRedPanda;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedPandaRenderer extends AdvancedHumanoidRenderer<LatexRedPanda, LatexRedPandaModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_red_panda.png");

    public LatexRedPandaRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedPandaModel(context.bakeLayer(LatexRedPandaModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRedPanda entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}