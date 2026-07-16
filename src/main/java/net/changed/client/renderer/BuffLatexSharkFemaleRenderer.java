package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.BuffLatexSharkFemaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexFemaleSharkModel;
import net.changed.entity.beast.BuffLatexSharkFemale;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BuffLatexSharkFemaleRenderer extends AdvancedHumanoidRenderer<BuffLatexSharkFemale, BuffLatexSharkFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_shark_buff_female.png");

    public BuffLatexSharkFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new BuffLatexSharkFemaleModel(context.bakeLayer(BuffLatexSharkFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleSharkModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withEyelashes(Color3.fromInt(0x1a1a1b)).build());
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BuffLatexSharkFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }

    @Override
    protected void scale(BuffLatexSharkFemale entity, PoseStack pose, float partialTick) {
        float modelScale = 1.025F;
        pose.scale(modelScale, modelScale, modelScale);
    }
}