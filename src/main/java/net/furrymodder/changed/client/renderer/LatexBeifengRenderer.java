package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexBeifengModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.furrymodder.changed.entity.beast.Beifeng;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeifengRenderer extends AdvancedHumanoidRenderer<Beifeng, LatexBeifengModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/beifeng.png");

    public LatexBeifengRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeifengModel(context.bakeLayer(LatexBeifengModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xffe852)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Beifeng entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}