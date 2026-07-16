package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.changed.client.ChangedClient;
import net.changed.client.WallSignTextureManager;
import net.changed.entity.decoration.WallSign;
import net.changed.entity.decoration.WallSignVariant;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.*;

public class WallSignRenderer extends EntityRenderer<WallSign> {
    private interface LightFetcher {
        int applyAsInt(float x, float y, float z);
    }

    private int lerpLight(float alpha, int lerp0, int lerp1) {
        var p00 = lerp0 & '\uffff';
        var p01 = lerp0 >> 16 & '\uffff';
        var p10 = lerp1 & '\uffff';
        var p11 = lerp1 >> 16 & '\uffff';

        return Mth.lerpInt(alpha, p00, p10) | (Mth.lerpInt(alpha, p01, p11) << 16);
    }

    private static float spriteU(TextureAtlasSprite sprite, float pixels) {
        return sprite.getU(pixels / 16.0F);
    }

    private static float spriteV(TextureAtlasSprite sprite, float pixels) {
        return sprite.getV(pixels / 16.0F);
    }

    public WallSignRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(WallSign wallSign, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));
        WallSignVariant variant = wallSign.getVariant();
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid(this.getTextureLocation(wallSign)));
        WallSignTextureManager textureManager = ChangedClient.wallSigns.get();
        this.renderSign(poseStack, vertexconsumer, wallSign, variant.getWidth(), variant.getHeight(),
                textureManager.get(variant), textureManager.getBackSprite(),
                packedLight);
        poseStack.popPose();
        super.render(wallSign, yRot, partialTicks, poseStack, bufferSource, packedLight);
    }

    public ResourceLocation getTextureLocation(WallSign wallSign) {
        return ChangedClient.wallSigns.get().getBackSprite().atlasLocation();
    }

    private void renderFlatSign(PoseStack poseStack, VertexConsumer buffer, WallSign wallSign, int width, int height,
                                TextureAtlasSprite front, TextureAtlasSprite back,
                                int packedLight) {
        PoseStack.Pose poseStackPose = poseStack.last();
        Matrix4f pose = poseStackPose.pose();
        Matrix3f normal = poseStackPose.normal();
        float leftX = (float)(-width) / 2.0F;
        float rightX = (float)width / 2.0F;
        float bottomY = (float)(-height) / 2.0F;
        float topY = (float)height / 2.0F;

        int light = wallSign.isItemRep() || wallSign.level() == null ?
                packedLight :
                LevelRenderer.getLightColor(wallSign.level(), wallSign.blockPosition());
        LightFetcher lightFetcher = (x, y, z) -> light;

        float edgeU0 = back.getU0();
        float edgeU1 = spriteU(back, 1.0F);
        float edgeV0 = back.getV0();
        float edgeV1 = spriteV(back, 1.0F);

        this.vertex(pose, normal, buffer, rightX, bottomY, front.getU1(), front.getV1(), -0.5F, 0, 0, -1, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, bottomY, front.getU0(), front.getV1(), -0.5F, 0, 0, -1, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, topY, front.getU0(), front.getV0(), -0.5F, 0, 0, -1, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, topY, front.getU1(), front.getV0(), -0.5F, 0, 0, -1, lightFetcher);

        this.vertex(pose, normal, buffer, rightX, topY, back.getU1(), back.getV0(), 0.5F, 0, 0, 1, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, topY, back.getU0(), back.getV0(), 0.5F, 0, 0, 1, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, bottomY, back.getU0(), back.getV1(), 0.5F, 0, 0, 1, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, bottomY, back.getU1(), back.getV1(), 0.5F, 0, 0, 1, lightFetcher);

        this.vertex(pose, normal, buffer, rightX, topY, edgeU1, edgeV0, -0.5F, 0, 1, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, topY, edgeU0, edgeV0, -0.5F, 0, 1, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, topY, edgeU0, edgeV1, 0.5F, 0, 1, 0, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, topY, edgeU1, edgeV1, 0.5F, 0, 1, 0, lightFetcher);

        this.vertex(pose, normal, buffer, rightX, bottomY, edgeU1, edgeV0, 0.5F, 0, -1, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, bottomY, edgeU0, edgeV0, 0.5F, 0, -1, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, bottomY, edgeU0, edgeV1, -0.5F, 0, -1, 0, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, bottomY, edgeU1, edgeV1, -0.5F, 0, -1, 0, lightFetcher);

        this.vertex(pose, normal, buffer, rightX, topY, edgeU1, edgeV0, 0.5F, 1, 0, 0, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, bottomY, edgeU1, edgeV1, 0.5F, 1, 0, 0, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, bottomY, edgeU0, edgeV1, -0.5F, 1, 0, 0, lightFetcher);
        this.vertex(pose, normal, buffer, rightX, topY, edgeU0, edgeV0, -0.5F, 1, 0, 0, lightFetcher);

        this.vertex(pose, normal, buffer, leftX, topY, edgeU1, edgeV0, -0.5F, -1, 0, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, bottomY, edgeU1, edgeV1, -0.5F, -1, 0, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, bottomY, edgeU0, edgeV1, 0.5F, -1, 0, 0, lightFetcher);
        this.vertex(pose, normal, buffer, leftX, topY, edgeU0, edgeV0, 0.5F, -1, 0, 0, lightFetcher);
    }

    private void renderSign(PoseStack p_115559_, VertexConsumer buffer, WallSign wallSign, int width, int height,
                            TextureAtlasSprite front, TextureAtlasSprite back,
                            int packedLight) {
        PoseStack.Pose posestack$pose = p_115559_.last();
        Matrix4f pose = posestack$pose.pose();
        Matrix3f normal = posestack$pose.normal();
        float leftMost = (float)(-width) / 2.0F;
        float bottomMost = (float)(-height) / 2.0F;

        float edgeV0 = back.getV0();
        float edgeVSliver = spriteV(back, 1.0F);

        float edgeU0 = back.getU0();
        float edgeUSliver = spriteU(back, 1.0F);

        int blockWidth = width <= 16 ? 1 : (1 + Mth.ceil((width - 16) / 32.0F) * 2);
        int blockHeight = height <= 16 ? 1 : (1 + Mth.ceil((height - 16) / 32.0F) * 2);

        LightFetcher lightFetcher;
        BlockPos.MutableBlockPos globalBlockPos = new BlockPos.MutableBlockPos();
        if (wallSign.isItemRep())
            lightFetcher = (x, y, z) -> packedLight;
        else {
            final Direction direction = wallSign.getDirection();
            final Function<BlockPos, Integer> sampler = blockPos -> LevelRenderer.getLightColor(wallSign.level(), blockPos);
            if (Minecraft.useAmbientOcclusion()) {
                lightFetcher = (x, y, z) -> {
                    int y0 = Mth.floor(wallSign.getY() + (double)((y - 8.0F) / 16.0F));
                    int y1 = Mth.floor(wallSign.getY() + (double)((y + 8.0F) / 16.0F));
                    float yAlpha = (float)Mth.inverseLerp(wallSign.getY() + (double)(y / 16.0F), y0 + 0.5, y1 + 0.5);
                    int x0, x1;
                    float xAlpha;
                    if (direction == Direction.NORTH) {
                        x0 = Mth.floor(wallSign.getX() + (double)((x - 8.0F) / 16.0F));
                        x1 = Mth.floor(wallSign.getX() + (double)((x + 8.0F) / 16.0F));
                        xAlpha = (float)Mth.inverseLerp(wallSign.getX() + (double)(x / 16.0F), x0 + 0.5, x1 + 0.5);
                    } else if (direction == Direction.WEST) {
                        x0 = Mth.floor(wallSign.getZ() - (double)((x - 8.0F) / 16.0F));
                        x1 = Mth.floor(wallSign.getZ() - (double)((x + 8.0F) / 16.0F));
                        xAlpha = (float)Mth.inverseLerp(wallSign.getZ() - (double)(x / 16.0F), x0 + 0.5, x1 + 0.5);
                    } else if (direction == Direction.SOUTH) {
                        x0 = Mth.floor(wallSign.getX() - (double)((x - 8.0F) / 16.0F));
                        x1 = Mth.floor(wallSign.getX() - (double)((x + 8.0F) / 16.0F));
                        xAlpha = (float)Mth.inverseLerp(wallSign.getX() - (double)(x / 16.0F), x0 + 0.5, x1 + 0.5);
                    } else if (direction == Direction.EAST) {
                        x0 = Mth.floor(wallSign.getZ() + (double)((x - 8.0F) / 16.0F));
                        x1 = Mth.floor(wallSign.getZ() + (double)((x + 8.0F) / 16.0F));
                        xAlpha = (float)Mth.inverseLerp(wallSign.getZ() + (double)(x / 16.0F), x0 + 0.5, x1 + 0.5);
                    } else {
                        x0 = globalBlockPos.getZ();
                        x1 = globalBlockPos.getZ();
                        xAlpha = 0.5F;
                    }

                    BlockPos.MutableBlockPos sampleBlockPos = new BlockPos.MutableBlockPos();
                    int l00, l01, l10, l11;
                    if (direction.getAxis() == Direction.Axis.Z) {
                        l00 = sampler.apply(sampleBlockPos.set(x0, y0, globalBlockPos.getZ()));
                        l01 = sampler.apply(sampleBlockPos.set(x0, y1, globalBlockPos.getZ()));
                        l10 = sampler.apply(sampleBlockPos.set(x1, y0, globalBlockPos.getZ()));
                        l11 = sampler.apply(sampleBlockPos.set(x1, y1, globalBlockPos.getZ()));
                    } else {
                        l00 = sampler.apply(sampleBlockPos.set(globalBlockPos.getX(), y0, x0));
                        l01 = sampler.apply(sampleBlockPos.set(globalBlockPos.getX(), y1, x0));
                        l10 = sampler.apply(sampleBlockPos.set(globalBlockPos.getX(), y0, x1));
                        l11 = sampler.apply(sampleBlockPos.set(globalBlockPos.getX(), y1, x1));
                    }

                    return lerpLight(xAlpha,
                            lerpLight(yAlpha, l00, l01),
                            lerpLight(yAlpha, l10, l11));
                };
            } else {
                lightFetcher = (x, y, z) -> {
                    return sampler.apply(globalBlockPos);
                };
            }
        }

        for (int blockX = 0; blockX < blockWidth; ++blockX) {
            float frontU0, frontU1;
            float backU0, backU1;
            float leftX, rightX;
            if (blockX > 0 && blockX < blockWidth - 1) { // Render position is not an edge/corner
                float partial = ((width - 16) % 32) * 0.5F;
                leftX = leftMost + (float)(blockX * 16) - (16.0F - partial);
                rightX = leftMost + (float)((blockX + 1) * 16) - (16.0F - partial);
                backU0 = back.getU0();
                backU1 = back.getU1();
            } else if (blockWidth == 1) { // Sign is small enough to fit in one block
                backU0 = spriteU(back, 8.0F - width * 0.5F);
                backU1 = spriteU(back, 8.0F + width * 0.5F);
                leftX = -width * 0.5F;
                rightX = width * 0.5F;
            } else { // Literal edge case
                float sizeX = ((width - 16) % 32) * 0.5F;
                if (blockX == 0) { // Left edge
                    backU0 = spriteU(back, 16.0F - sizeX);
                    backU1 = back.getU1();
                    leftX = leftMost;
                } else { // Right edge
                    backU0 = back.getU0();
                    backU1 = spriteU(back, sizeX);
                    leftX = leftMost + (float)(blockX * 16) - (16.0F - sizeX);
                }

                rightX = leftX + sizeX;
            }

            frontU0 = spriteU(front, 16.0F - ((leftX - leftMost) / width) * 16.0F);
            frontU1 = spriteU(front, 16.0F - ((rightX - leftMost) / width) * 16.0F);

            for(int blockY = 0; blockY < blockHeight; ++blockY) {
                float frontV0, frontV1;
                float backV0, backV1;
                float bottomY, topY;
                if (blockY > 0 && blockY < blockHeight - 1) { // Render position is not an edge/corner
                    float partial = ((height - 16) % 32) * 0.5F;
                    backV0 = back.getV0();
                    backV1 = back.getV1();
                    bottomY = bottomMost + (float)(blockY * 16) - (16.0F - partial);
                    topY = bottomMost + (float)((blockY + 1) * 16) - (16.0F - partial);
                } else if (blockHeight == 1) { // Sign is small enough to fit in one block
                    backV0 = spriteV(back, 8.0F - height * 0.5F);
                    backV1 = spriteV(back, 8.0F + height * 0.5F);
                    bottomY = -height * 0.5F;
                    topY = height * 0.5F;
                } else { // Literal edge case
                    float sizeY = ((height - 16) % 32) * 0.5F;
                    if (blockY == 0) { // Bottom edge
                        backV0 = back.getV0();
                        backV1 = spriteV(back, sizeY);
                        bottomY = bottomMost;
                    } else { // Top edge
                        backV0 = spriteV(back, 16.0F - sizeY);
                        backV1 = back.getV1();
                        bottomY = bottomMost + (float)(blockY * 16) - (16.0F - sizeY);
                    }

                    topY = bottomY + sizeY;
                }

                frontV0 = spriteV(front, 16.0F - ((bottomY - bottomMost) / height) * 16.0F);
                frontV1 = spriteV(front, 16.0F - ((topY - bottomMost) / height) * 16.0F);

                int globalBlockX = wallSign.getBlockX();
                int globalBlockY = Mth.floor(wallSign.getY() + (double)((topY + bottomY) / 2.0F / 16.0F));
                int globalBlockZ = wallSign.getBlockZ();
                Direction direction = wallSign.getDirection();
                if (direction == Direction.NORTH) {
                    globalBlockX = Mth.floor(wallSign.getX() + (double)((rightX + leftX) / 2.0F / 16.0F));
                }

                if (direction == Direction.WEST) {
                    globalBlockZ = Mth.floor(wallSign.getZ() - (double)((rightX + leftX) / 2.0F / 16.0F));
                }

                if (direction == Direction.SOUTH) {
                    globalBlockX = Mth.floor(wallSign.getX() - (double)((rightX + leftX) / 2.0F / 16.0F));
                }

                if (direction == Direction.EAST) {
                    globalBlockZ = Mth.floor(wallSign.getZ() + (double)((rightX + leftX) / 2.0F / 16.0F));
                }

                globalBlockPos.set(globalBlockX, globalBlockY, globalBlockZ);

                // Render front
                this.vertex(pose, normal, buffer, rightX, bottomY, frontU1, frontV0, -0.5F, 0, 0, -1, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, bottomY, frontU0, frontV0, -0.5F, 0, 0, -1, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, topY, frontU0, frontV1, -0.5F, 0, 0, -1, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, topY, frontU1, frontV1, -0.5F, 0, 0, -1, lightFetcher);

                // Render back
                this.vertex(pose, normal, buffer, rightX, topY, backU1, backV0, 0.5F, 0, 0, 1, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, topY, backU0, backV0, 0.5F, 0, 0, 1, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, bottomY, backU0, backV1, 0.5F, 0, 0, 1, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, bottomY, backU1, backV1, 0.5F, 0, 0, 1, lightFetcher);

                // Render top
                this.vertex(pose, normal, buffer, rightX, topY, backU0, edgeV0, -0.5F, 0, 1, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, topY, backU1, edgeV0, -0.5F, 0, 1, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, topY, backU1, edgeVSliver, 0.5F, 0, 1, 0, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, topY, backU0, edgeVSliver, 0.5F, 0, 1, 0, lightFetcher);

                // Render bottom
                this.vertex(pose, normal, buffer, rightX, bottomY, backU0, edgeV0, 0.5F, 0, -1, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, bottomY, backU1, edgeV0, 0.5F, 0, -1, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, bottomY, backU1, edgeVSliver, -0.5F, 0, -1, 0, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, bottomY, backU0, edgeVSliver, -0.5F, 0, -1, 0, lightFetcher);

                // Render right
                this.vertex(pose, normal, buffer, rightX, topY, edgeUSliver, backV0, 0.5F, -1, 0, 0, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, bottomY, edgeUSliver, backV1, 0.5F, -1, 0, 0, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, bottomY, edgeU0, backV1, -0.5F, -1, 0, 0, lightFetcher);
                this.vertex(pose, normal, buffer, rightX, topY, edgeU0, backV0, -0.5F, -1, 0, 0, lightFetcher);

                // Render left
                this.vertex(pose, normal, buffer, leftX, topY, edgeUSliver, backV0, -0.5F, 1, 0, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, bottomY, edgeUSliver, backV1, -0.5F, 1, 0, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, bottomY, edgeU0, backV1, 0.5F, 1, 0, 0, lightFetcher);
                this.vertex(pose, normal, buffer, leftX, topY, edgeU0, backV0, 0.5F, 1, 0, 0, lightFetcher);
            }
        }
    }

    private void vertex(Matrix4f pose, Matrix3f normal, VertexConsumer buffer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, LightFetcher lightFetcher) {
        buffer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightFetcher.applyAsInt(x, y, z))
                .setNormal((float)normalX, (float)normalY, (float)normalZ);
    }
}
