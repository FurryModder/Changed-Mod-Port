package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexTigerSharkModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.changed.entity.beast.LatexTigerShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTigerSharkRenderer extends AdvancedHumanoidRenderer<LatexTigerShark, LatexTigerSharkModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_tiger_shark.png");

    public LatexTigerSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTigerSharkModel(context.bakeLayer(LatexTigerSharkModel.LAYER_LOCATION)), ArmorLatexMaleSharkModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTigerShark entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}