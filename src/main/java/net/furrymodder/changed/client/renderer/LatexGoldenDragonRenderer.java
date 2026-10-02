package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexGoldenDragonModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.furrymodder.changed.entity.beast.LatexGoldenDragon;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexGoldenDragonRenderer extends AdvancedHumanoidRenderer<LatexGoldenDragon, LatexGoldenDragonModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_golden_dragon.png");

    public LatexGoldenDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexGoldenDragonModel(context.bakeLayer(LatexGoldenDragonModel.LAYER_LOCATION)), ArmorLatexMaleWingedDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xff5c42)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexGoldenDragon entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}