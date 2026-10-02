package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomCoatLayer;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.GasWolfFemaleModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.furrymodder.changed.entity.beast.GasWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GasWolfFemaleRenderer extends AdvancedHumanoidRenderer<GasWolfFemale, GasWolfFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/gas_wolf_female.png");

    public GasWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GasWolfFemaleModel(context.bakeLayer( GasWolfFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomCoatLayer<>(this, this.getModel(), Changed.modResource("textures/gas_wolf_female_coat")));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GasWolfFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}