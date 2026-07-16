package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.WhiteLatexKnightModel;
import net.changed.client.renderer.model.armor.ArmorLatexWhiteKnightModel;
import net.changed.entity.beast.WhiteLatexKnight;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexKnightRenderer extends AdvancedHumanoidRenderer<WhiteLatexKnight, WhiteLatexKnightModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/white_latex_knight.png");

    public WhiteLatexKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexKnightModel(context.bakeLayer(WhiteLatexKnightModel.LAYER_LOCATION)), ArmorLatexWhiteKnightModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexKnight entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}