package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.GasSkunkModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.furrymodder.changed.entity.beast.GasSkunk;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GasSkunkRenderer extends AdvancedHumanoidRenderer<GasSkunk, GasSkunkModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/gas_skunk.png");

    public GasSkunkRenderer(EntityRendererProvider.Context context) {
        super(context, new GasSkunkModel(context.bakeLayer(GasSkunkModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GasSkunk entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}