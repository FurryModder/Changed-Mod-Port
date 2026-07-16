package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexPinkDeerModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.changed.entity.beast.LatexPinkDeer;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkDeerRenderer extends AdvancedHumanoidRenderer<LatexPinkDeer, LatexPinkDeerModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_pink_deer.png");

    public LatexPinkDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkDeerModel(context.bakeLayer(LatexPinkDeerModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0x7889f3)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkDeer entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}