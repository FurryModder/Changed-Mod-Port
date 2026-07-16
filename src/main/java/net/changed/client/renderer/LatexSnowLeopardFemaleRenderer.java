package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexSnowLeopardFemaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.changed.entity.beast.LatexSnowLeopardFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnowLeopardFemaleRenderer extends AdvancedHumanoidRenderer<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_snow_leopard_female.png");

    public LatexSnowLeopardFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowLeopardFemaleModel(context.bakeLayer(LatexSnowLeopardFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnowLeopardFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}