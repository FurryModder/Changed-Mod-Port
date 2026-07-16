package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.*;
import net.changed.client.renderer.model.LatexStigerModel;
import net.changed.client.renderer.model.armor.ArmorLatexStigerModel;
import net.changed.entity.beast.LatexStiger;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexStigerRenderer extends AdvancedHumanoidRenderer<LatexStiger, LatexStigerModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_stiger.png");

    public LatexStigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexStigerModel(context.bakeLayer(LatexStigerModel.LAYER_LOCATION)), ArmorLatexStigerModel.MODEL_SET, 0.5f);
        this.addLayer(new DoubleItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).build());
        this.addLayer(AdditionalEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).build(Changed.modResource("latex_stiger")));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexStiger entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}