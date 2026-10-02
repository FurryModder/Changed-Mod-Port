package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexKoboldModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.furrymodder.changed.entity.beast.LatexKobold;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexKoboldRenderer extends AdvancedHumanoidRenderer<LatexKobold, LatexKoboldModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_kobold.png");

    public LatexKoboldRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexKoboldModel(context.bakeLayer(LatexKoboldModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexKobold entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}