package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexBlueDragonModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.furrymodder.changed.entity.beast.LatexBlueDragon;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueDragonRenderer extends AdvancedHumanoidRenderer<LatexBlueDragon, LatexBlueDragonModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_blue_dragon.png");

    public LatexBlueDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBlueDragonModel(context.bakeLayer(LatexBlueDragonModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0x26c841)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBlueDragon entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}