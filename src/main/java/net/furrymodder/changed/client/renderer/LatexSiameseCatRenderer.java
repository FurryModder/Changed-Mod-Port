package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexSiameseCatModel;
import net.furrymodder.changed.client.renderer.model.LatexSnowLeopardFemaleModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.furrymodder.changed.entity.beast.LatexSiameseCat;
import net.furrymodder.changed.entity.beast.LatexSnowLeopardFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSiameseCatRenderer extends AdvancedHumanoidRenderer<LatexSiameseCat, LatexSiameseCatModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_siamese_cat.png");

    public LatexSiameseCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSiameseCatModel(context.bakeLayer(LatexSiameseCatModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSiameseCat entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}