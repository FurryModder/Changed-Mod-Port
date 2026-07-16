package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.entity.decoration.WallSign;
import net.changed.init.ChangedEntities;
import net.changed.item.WallSignItem;
import net.changed.util.Cacheable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ChangedEntityWithOutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    private final Cacheable<WallSign> wallSign = Cacheable.of(() -> new WallSign(ChangedEntities.WALL_SIGN.get(), Minecraft.getInstance().level).markAsItemRep());
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ChangedEntityWithOutLevelRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet,
                                             EntityRenderDispatcher entityRenderDispatcher) {
        super(dispatcher, modelSet);
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    protected <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
        return entityRenderDispatcher.getRenderer(entity);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(itemStack, transformType, poseStack, bufferSource, packedLight, packedOverlay);

        if (itemStack.getItem() instanceof WallSignItem wallSignItem) {
            wallSignItem.getVariant().ifPresent(variant -> {
                wallSign.get().setVariant(variant);

                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);
                getRenderer(wallSign.get()).render(wallSign.get(), 0, 0, poseStack, bufferSource, packedLight);
                poseStack.popPose();
            });
        }
    }
}
