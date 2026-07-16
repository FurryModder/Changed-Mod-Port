package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexWatermelonCatModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.changed.entity.beast.LatexWatermelonCat;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWatermelonCatRenderer extends AdvancedHumanoidRenderer<LatexWatermelonCat, LatexWatermelonCatModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_watermelon_cat.png");

    public LatexWatermelonCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWatermelonCatModel(context.bakeLayer(LatexWatermelonCatModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.BLACK).withIris(Color3.fromInt(0x67fd2a)).withEyebrows(Color3.fromInt(0x91ad3f)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWatermelonCat entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}