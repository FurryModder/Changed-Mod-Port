package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexBenignWolfModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.furrymodder.changed.entity.beast.LatexBenignWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBenignWolfRenderer extends AdvancedHumanoidRenderer<LatexBenignWolf, LatexBenignWolfModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_benign_wolf.png");

    public LatexBenignWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBenignWolfModel(context.bakeLayer(LatexBenignWolfModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBenignWolf entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}