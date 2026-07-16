package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.*;
import net.changed.client.renderer.model.WhiteLatexCentaurModel;
import net.changed.client.renderer.model.armor.*;
import net.changed.entity.beast.WhiteLatexCentaur;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexCentaurRenderer extends AdvancedHumanoidRenderer<WhiteLatexCentaur, WhiteLatexCentaurModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/white_latex_centaur.png");

    public WhiteLatexCentaurRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexCentaurModel(context.bakeLayer(WhiteLatexCentaurModel.LAYER_LOCATION)),
                ArmorModelPicker.centaur(context.getModelSet(), ArmorLatexMaleTaurUpperModel.MODEL_SET, ArmorLatexCentaurLowerModel.MODEL_SET_WITH_TORSO), 0.7f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/white_latex_centaur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.shortCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexCentaur entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}