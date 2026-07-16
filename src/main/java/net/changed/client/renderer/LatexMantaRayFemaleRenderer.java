package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexMantaRayFemaleModel;
import net.changed.client.renderer.model.armor.ArmorFemaleMantaRayAbdomenModel;
import net.changed.client.renderer.model.armor.ArmorFemaleMantaRayUpperBodyModel;
import net.changed.client.renderer.model.armor.ArmorModelPicker;
import net.changed.entity.beast.LatexMantaRayFemale;
import net.changed.item.AbdomenArmor;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayFemaleRenderer extends AdvancedHumanoidRenderer<LatexMantaRayFemale, LatexMantaRayFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_manta_ray_female.png");

    public LatexMantaRayFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayFemaleModel(context.bakeLayer(LatexMantaRayFemaleModel.LAYER_LOCATION)),
                ArmorModelPicker.legless(context.getModelSet(), ArmorFemaleMantaRayUpperBodyModel.MODEL_SET, ArmorFemaleMantaRayAbdomenModel.MODEL_SET), 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withEyelashes(Color3.fromInt(0x1a1a1b)).build());
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
