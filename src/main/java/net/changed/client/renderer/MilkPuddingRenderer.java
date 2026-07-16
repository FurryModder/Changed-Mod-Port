package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.model.MilkPuddingModel;
import net.changed.entity.beast.MilkPudding;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MilkPuddingRenderer extends MobRenderer<MilkPudding, MilkPuddingModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/milk_pudding.png");

    public MilkPuddingRenderer(EntityRendererProvider.Context context) {
        super(context, new MilkPuddingModel(context.bakeLayer(MilkPuddingModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(MilkPudding entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
