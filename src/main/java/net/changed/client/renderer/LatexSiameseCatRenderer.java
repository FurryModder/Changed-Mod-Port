package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexSiameseCatModel;
import net.changed.client.renderer.model.LatexSnowLeopardFemaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.changed.entity.beast.LatexSiameseCat;
import net.changed.entity.beast.LatexSnowLeopardFemale;
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