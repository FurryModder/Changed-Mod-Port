package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.BuffLatexSharkMaleModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleBuffSharkModel;
import net.changed.entity.beast.BuffLatexSharkMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BuffLatexSharkMaleRenderer extends AdvancedHumanoidRenderer<BuffLatexSharkMale, BuffLatexSharkMaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_shark_buff_male.png");

    public BuffLatexSharkMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new BuffLatexSharkMaleModel(context.bakeLayer(BuffLatexSharkMaleModel.LAYER_LOCATION)), ArmorLatexMaleBuffSharkModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BuffLatexSharkMale entity) {
        return DEFAULT_SKIN_LOCATION;
    }

    @Override
    protected void scale(BuffLatexSharkMale entity, PoseStack pose, float partialTick) {
        float modelScale = 1.025F;
        pose.scale(modelScale, modelScale, modelScale);
    }
}