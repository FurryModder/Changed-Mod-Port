package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexSnakeModel;
import net.changed.client.renderer.model.armor.ArmorModelPicker;
import net.changed.client.renderer.model.armor.ArmorSnakeAbdomenModel;
import net.changed.client.renderer.model.armor.ArmorSnakeUpperBodyModel;
import net.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.changed.entity.beast.LatexSnake;
import net.changed.item.AbdomenArmor;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnakeRenderer extends AdvancedHumanoidRenderer<LatexSnake, LatexSnakeModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_snake.png");

    public LatexSnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnakeModel(context.bakeLayer(LatexSnakeModel.LAYER_LOCATION)),
                ArmorModelPicker.legless(context.getModelSet(), ArmorSnakeUpperBodyModel.MODEL_SET, ArmorSnakeAbdomenModel.MODEL_SET), 0.5f);
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x6e6e6e)).build());
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnake entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
