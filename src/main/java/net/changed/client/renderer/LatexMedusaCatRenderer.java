package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexMedusaCatModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.changed.entity.beast.LatexMedusaCat;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMedusaCatRenderer extends AdvancedHumanoidRenderer<LatexMedusaCat, LatexMedusaCatModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_medusa_cat.png");

    public LatexMedusaCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMedusaCatModel(context.bakeLayer(LatexMedusaCatModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xf64967)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMedusaCat entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}