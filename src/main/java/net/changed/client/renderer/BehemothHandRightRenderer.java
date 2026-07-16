package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.model.BehemothHandRightModel;
import net.changed.entity.beast.boss.BehemothHandRight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BehemothHandRightRenderer extends MobRenderer<BehemothHandRight, BehemothHandRightModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/behemoth_hand.png");

    public BehemothHandRightRenderer(EntityRendererProvider.Context context) {
        super(context, new BehemothHandRightModel(context.bakeLayer(BehemothHandRightModel.LAYER_LOCATION)), 0.8f);
    }

    @Override
    public ResourceLocation getTextureLocation(BehemothHandRight entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
