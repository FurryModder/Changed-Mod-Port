package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.WhiteLatexWolfMaleModel;
import net.changed.client.renderer.model.WhiteWolfMaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.entity.beast.WhiteWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteWolfMaleRenderer extends AdvancedHumanoidRenderer<WhiteWolfMale, WhiteWolfMaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/white_wolf_male.png");

    public WhiteWolfMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteWolfMaleModel(context.bakeLayer(WhiteLatexWolfMaleModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteWolfMale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}