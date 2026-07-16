package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.*;
import net.changed.client.renderer.model.LatexTranslucentLizardModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.changed.entity.beast.LatexTranslucentLizard;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLizardRenderer extends AdvancedHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_translucent_lizard_inner.png");

    public LatexTranslucentLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5f);
        var translucent = new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_translucent_lizard_outer.png"));
        this.addLayer(translucent);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()).addModel(translucent.getModel(), entity -> translucent.getTexture()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer.fixedColor(Color3.fromInt(0xffb84c), 0.5f)).withIris(CustomEyesLayer.fixedColor(Color3.fromInt(0xa24b42), 0.75f)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTranslucentLizard entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}