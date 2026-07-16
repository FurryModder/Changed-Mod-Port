package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexSirenModel;
import net.changed.client.renderer.model.armor.*;
import net.changed.entity.beast.LatexSiren;
import net.changed.item.AbdomenArmor;
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
