package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomCoatLayer;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.GasTigerModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.changed.entity.beast.GasTiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GasTigerRenderer extends AdvancedHumanoidRenderer<GasTiger, GasTigerModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/gas_tiger.png");

    public GasTigerRenderer(EntityRendererProvider.Context context) {
        super(context, new GasTigerModel(context.bakeLayer(GasTigerModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomCoatLayer<>(this, this.getModel(), Changed.modResource("textures/gas_tiger_hair")));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GasTiger entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}