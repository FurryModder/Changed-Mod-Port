package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.model.BehemothHeadModel;
import net.furrymodder.changed.entity.beast.boss.BehemothHead;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BehemothHeadRenderer extends MobRenderer<BehemothHead, BehemothHeadModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/behemoth_head.png");
    
    public BehemothHeadRenderer(EntityRendererProvider.Context context) {
        super(context, new BehemothHeadModel(context.bakeLayer(BehemothHeadModel.LAYER_LOCATION)), 3.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(BehemothHead entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}
