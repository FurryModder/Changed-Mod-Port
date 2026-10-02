package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexSirenModel;
import net.furrymodder.changed.client.renderer.model.armor.*;
import net.furrymodder.changed.entity.beast.LatexSiren;
import net.furrymodder.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSirenRenderer extends AdvancedHumanoidRenderer<LatexSiren, LatexSirenModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_siren.png");

    public LatexSirenRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSirenModel(context.bakeLayer(LatexSirenModel.LAYER_LOCATION)),
                ArmorModelPicker.legless(context.getModelSet(), ArmorSirenUpperBodyModel.MODEL_SET, ArmorSirenAbdomenModel.MODEL_SET), 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSiren entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
