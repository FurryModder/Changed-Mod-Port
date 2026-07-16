package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.*;
import net.changed.client.renderer.model.LatexBeeModel;
import net.changed.client.renderer.model.armor.ArmorLatexBeeModel;
import net.changed.entity.beast.LatexBee;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeeRenderer extends AdvancedHumanoidRenderer<LatexBee, LatexBeeModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_bee.png");

    public LatexBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeeModel(context.bakeLayer(LatexBeeModel.LAYER_LOCATION)), ArmorLatexBeeModel.MODEL_SET, 0.5f);
        var translucent = new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_bee_translucent.png"));
        this.addLayer(translucent);
        this.addLayer(new DoubleItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()).addModel(translucent.getModel(), entity -> translucent.getTexture()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).build());
        this.addLayer(GasMaskLayer.forNormal(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBee entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}