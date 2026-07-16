package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexRaccoonModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.changed.entity.beast.LatexRaccoon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRaccoonRenderer extends AdvancedHumanoidRenderer<LatexRaccoon, LatexRaccoonModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_raccoon.png");

    public LatexRaccoonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRaccoonModel(context.bakeLayer(LatexRaccoonModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRaccoon entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}