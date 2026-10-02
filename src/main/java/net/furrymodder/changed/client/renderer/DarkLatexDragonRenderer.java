package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.DarkLatexDragonModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.furrymodder.changed.entity.beast.DarkDragon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexDragonRenderer extends AdvancedHumanoidRenderer<DarkDragon, DarkLatexDragonModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/dark_dragon.png");

    public DarkLatexDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexDragonModel(context.bakeLayer(DarkLatexDragonModel.LAYER_LOCATION)), ArmorLatexMaleWingedDragonModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(DarkDragon entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}