package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexRedDragonModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.changed.entity.beast.LatexRedDragon;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedDragonRenderer extends AdvancedHumanoidRenderer<LatexRedDragon, LatexRedDragonModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_red_dragon.png");

    public LatexRedDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedDragonModel(context.bakeLayer(LatexRedDragonModel.LAYER_LOCATION)), ArmorLatexMaleWingedDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xffe93f)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRedDragon entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}