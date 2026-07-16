package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexKeonWolfModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.entity.beast.LatexKeonWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexKeonWolfRenderer extends AdvancedHumanoidRenderer<LatexKeonWolf, LatexKeonWolfModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_keon.png");

    public LatexKeonWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexKeonWolfModel(context.bakeLayer(LatexKeonWolfModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexKeonWolf entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}