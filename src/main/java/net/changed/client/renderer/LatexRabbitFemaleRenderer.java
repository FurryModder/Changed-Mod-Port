package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexRabbitFemaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.changed.entity.beast.LatexRabbitFemale;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRabbitFemaleRenderer extends AdvancedHumanoidRenderer<LatexRabbitFemale, LatexRabbitFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_rabbit_female.png");

    public LatexRabbitFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRabbitFemaleModel(context.bakeLayer(LatexRabbitFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0x87e385)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRabbitFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}