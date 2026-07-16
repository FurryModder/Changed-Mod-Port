package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.EmissiveBodyLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.model.LatexAlienModel;
import net.changed.client.renderer.model.armor.ArmorLatexAlienModel;
import net.changed.entity.beast.LatexAlien;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexAlienRenderer extends AdvancedHumanoidRenderer<LatexAlien, LatexAlienModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_alien.png");

    public LatexAlienRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexAlienModel(context.bakeLayer(LatexAlienModel.LAYER_LOCATION)), ArmorLatexAlienModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new EmissiveBodyLayer<>(this, Changed.modResource("textures/latex_alien_emissive.png")));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexAlien entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}