package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.WhiteLatexWolfFemaleModel;
import net.furrymodder.changed.client.renderer.model.WhiteWolfFemaleModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.furrymodder.changed.entity.beast.WhiteWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteWolfFemaleRenderer extends AdvancedHumanoidRenderer<WhiteWolfFemale, WhiteWolfFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/white_wolf_female.png");

    public WhiteWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteWolfFemaleModel(context.bakeLayer(WhiteLatexWolfFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteWolfFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}