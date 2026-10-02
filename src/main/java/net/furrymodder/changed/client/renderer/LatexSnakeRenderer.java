package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexSnakeModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorModelPicker;
import net.furrymodder.changed.client.renderer.model.armor.ArmorSnakeAbdomenModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorSnakeUpperBodyModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.furrymodder.changed.entity.beast.LatexSnake;
import net.furrymodder.changed.item.AbdomenArmor;
import net.furrymodder.changed.util.Color3;
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
