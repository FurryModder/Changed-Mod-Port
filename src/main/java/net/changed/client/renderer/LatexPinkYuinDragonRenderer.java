package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexPinkYuinDragonModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.changed.entity.beast.LatexPinkYuinDragon;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkYuinDragonRenderer extends AdvancedHumanoidRenderer<LatexPinkYuinDragon, LatexPinkYuinDragonModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_pink_yuin_dragon.png");

    public LatexPinkYuinDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkYuinDragonModel(context.bakeLayer(LatexPinkYuinDragonModel.LAYER_LOCATION)), ArmorLatexMaleWingedDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0x7889f3)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkYuinDragon entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}