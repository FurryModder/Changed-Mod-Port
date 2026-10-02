package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexEelModel;
import net.furrymodder.changed.client.renderer.model.LatexSharkModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.furrymodder.changed.entity.beast.LatexEel;
import net.furrymodder.changed.entity.beast.LatexShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexEelRenderer extends AdvancedHumanoidRenderer<LatexEel, LatexEelModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_eel.png");

    public LatexEelRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexEelModel(context.bakeLayer(LatexEelModel.LAYER_LOCATION)), ArmorLatexMaleSharkModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexEel entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}