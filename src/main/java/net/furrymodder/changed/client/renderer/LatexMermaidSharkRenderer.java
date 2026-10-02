package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexMermaidSharkModel;
import net.furrymodder.changed.client.renderer.model.armor.*;
import net.furrymodder.changed.entity.beast.LatexMermaidShark;
import net.furrymodder.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMermaidSharkRenderer extends AdvancedHumanoidRenderer<LatexMermaidShark, LatexMermaidSharkModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_mermaid_shark.png");

    public LatexMermaidSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMermaidSharkModel(context.bakeLayer(LatexMermaidSharkModel.LAYER_LOCATION)),
                ArmorModelPicker.legless(context.getModelSet(), ArmorMermaidSharkUpperBodyModel.MODEL_SET, ArmorMermaidSharkAbdomenModel.MODEL_SET), 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMermaidShark entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
