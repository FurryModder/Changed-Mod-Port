package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.model.SharkModel;
import net.changed.client.renderer.model.armor.ArmorNoneModel;
import net.changed.entity.beast.FeralShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SharkRenderer extends AdvancedFeralRenderer<FeralShark, SharkModel<FeralShark>> {
    private static final ResourceLocation SHARK_LOCATION = Changed.modResource("textures/shark.png");

    public SharkRenderer(EntityRendererProvider.Context context) {
        super(context, new SharkModel<>(context.bakeLayer(SharkModel.LAYER_LOCATION)), ArmorNoneModel.MODEL_SET, 0.7F);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
    }

    public ResourceLocation getTextureLocation(FeralShark shark) {
        return SHARK_LOCATION;
    }
}