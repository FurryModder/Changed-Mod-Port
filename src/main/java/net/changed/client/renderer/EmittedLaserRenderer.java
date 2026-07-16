package net.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.changed.Changed;
import net.changed.entity.decoration.EmittedLaser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;

public class EmittedLaserRenderer extends EntityRenderer<EmittedLaser> {
    public static final ResourceLocation TEXTURE = Changed.modResource("textures/block/laser_beam.png");
    public static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutout(TEXTURE);
    public static final RenderType BEAM_RENDER_HIGHLIGHT_TYPE = RenderType.eyes(TEXTURE);
    public static final List<RenderType> RENDER_TYPES = List.of(BEAM_RENDER_TYPE, BEAM_RENDER_HIGHLIGHT_TYPE);

    public EmittedLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EmittedLaser laser, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float worldTicks = (laser.tickCount % 10) + partialTicks;
        float rotate = (worldTicks / 10f) * Mth.TWO_PI;

        EmittedLaserRenderer.renderBeam(laser.getBeamStart(), laser.getBeamEnd(), laser.getBeamRadius(), rotate, 0f, poseStack, bufferSource);
    }

    public static void renderBeam(Vec3 laserSource, Vec3 laserTarget, float radius, float rotate, float zOffset, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();

        Vec3 laserDirection = laserTarget.subtract(laserSource);
        float laserLength = (float)(laserDirection.length());
        laserDirection = laserDirection.normalize();
        float pitch = (float)Math.acos(laserDirection.y);
        float yaw = (float)Math.atan2(laserDirection.z, laserDirection.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - yaw) * (180F / (float)Math.PI)));
        poseStack.translate(0.0D, 0.0D, zOffset);
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch * (180F / (float) Math.PI)));
        int i = 1;
        int red = 255;//64 + (int)(f8 * 191.0F);
        int green = 255;//32 + (int)(f8 * 191.0F);
        int blue = 255;//128 - (int)(f8 * 64.0F);
        float beamScaleEnd = radius * 4f;
        float beamScaleStart = radius * 4f;
        float f11 = Mth.cos(rotate + 2.3561945F) * beamScaleStart;
        float f12 = Mth.sin(rotate + 2.3561945F) * beamScaleStart;
        float f13 = Mth.cos(rotate + ((float)Math.PI / 4F)) * beamScaleStart;
        float f14 = Mth.sin(rotate + ((float)Math.PI / 4F)) * beamScaleStart;
        float f15 = Mth.cos(rotate + 3.926991F) * beamScaleStart;
        float f16 = Mth.sin(rotate + 3.926991F) * beamScaleStart;
        float f17 = Mth.cos(rotate + 5.4977875F) * beamScaleStart;
        float f18 = Mth.sin(rotate + 5.4977875F) * beamScaleStart;
        float f19 = Mth.cos(rotate + (float)Math.PI) * beamScaleEnd;
        float f20 = Mth.sin(rotate + (float)Math.PI) * beamScaleEnd;
        float f21 = Mth.cos(rotate + 0.0F) * beamScaleEnd;
        float f22 = Mth.sin(rotate + 0.0F) * beamScaleEnd;
        float f23 = Mth.cos(rotate + ((float)Math.PI / 2F)) * beamScaleEnd;
        float f24 = Mth.sin(rotate + ((float)Math.PI / 2F)) * beamScaleEnd;
        float f25 = Mth.cos(rotate + ((float)Math.PI * 1.5F)) * beamScaleEnd;
        float f26 = Mth.sin(rotate + ((float)Math.PI * 1.5F)) * beamScaleEnd;
        float f27 = 0.0F;
        float f28 = 0.4999F;
        float f29 = 0f;//-1.0F + f2;
        float f30 = 1f;//laserLength * 2.5F + f29;
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f pose = posestack$pose.pose();
        Matrix3f normal = posestack$pose.normal();

        RENDER_TYPES.stream().map(bufferSource::getBuffer).forEach(vertexConsumer -> {
            vertex(vertexConsumer, pose, normal, f19, laserLength, f20, red, green, blue, 1.0F, f30);
            vertex(vertexConsumer, pose, normal, f19, 0.0F, f20, red, green, blue, 1.0F, f29);
            vertex(vertexConsumer, pose, normal, f21, 0.0F, f22, red, green, blue, 0.0F, f29);
            vertex(vertexConsumer, pose, normal, f21, laserLength, f22, red, green, blue, 0.0F, f30);

            vertex(vertexConsumer, pose, normal, f23, laserLength, f24, red, green, blue, 1.0F, f30);
            vertex(vertexConsumer, pose, normal, f23, 0.0F, f24, red, green, blue, 1.0F, f29);
            vertex(vertexConsumer, pose, normal, f25, 0.0F, f26, red, green, blue, 0.0F, f29);
            vertex(vertexConsumer, pose, normal, f25, laserLength, f26, red, green, blue, 0.0F, f30);

            vertex(vertexConsumer, pose, normal, f19, 0.0F, f20, red, green, blue, 1.0F, f29);
            vertex(vertexConsumer, pose, normal, f19, laserLength, f20, red, green, blue, 1.0F, f30);
            vertex(vertexConsumer, pose, normal, f21, laserLength, f22, red, green, blue, 0.0F, f30);
            vertex(vertexConsumer, pose, normal, f21, 0.0F, f22, red, green, blue, 0.0F, f29);

            vertex(vertexConsumer, pose, normal, f23, 0.0F, f24, red, green, blue, 1.0F, f29);
            vertex(vertexConsumer, pose, normal, f23, laserLength, f24, red, green, blue, 1.0F, f30);
            vertex(vertexConsumer, pose, normal, f25, laserLength, f26, red, green, blue, 0.0F, f30);
            vertex(vertexConsumer, pose, normal, f25, 0.0F, f26, red, green, blue, 0.0F, f29);
        });

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, float x, float y, float z, int red, int green, int blue, float u, float v) {
        buffer.addVertex(pose, x, y, z)
                .setColor(red, green, blue, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(EmittedLaser laser) {
        return TEXTURE;
    }
}
