package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexBlueWolfModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.furrymodder.changed.entity.beast.LatexBlueWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueWolfRenderer extends AdvancedHumanoidRenderer<LatexBlueWolf, LatexBlueWolfModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_blue_wolf.png");

    public LatexBlueWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBlueWolfModel(context.bakeLayer(LatexBlueWolfModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBlueWolf entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}