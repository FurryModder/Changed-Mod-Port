package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexWhiteTigerModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.furrymodder.changed.entity.beast.LatexWhiteTiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWhiteTigerRenderer extends AdvancedHumanoidRenderer<LatexWhiteTiger, LatexWhiteTigerModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_white_tiger.png");

    public LatexWhiteTigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWhiteTigerModel(context.bakeLayer(LatexWhiteTigerModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWhiteTiger entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}