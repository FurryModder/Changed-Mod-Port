package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomCoatLayer;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.GreenLizardModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.furrymodder.changed.entity.beast.GreenLizard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GreenLizardRenderer extends AdvancedHumanoidRenderer<GreenLizard, GreenLizardModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/green_lizard.png");

    public GreenLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new GreenLizardModel(context.bakeLayer(GreenLizardModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomCoatLayer<>(this, this.getModel(), Changed.modResource("textures/green_lizard_hair")));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GreenLizard entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}