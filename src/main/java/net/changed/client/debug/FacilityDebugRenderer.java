package net.changed.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.world.data.ActiveFacilityInstance;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    public final Minecraft minecraft;
    private final Map<DimensionType, Map<String, ActiveFacilityInstance.PieceGenerationInfo>> genInfos = new HashMap<>();

    private static final int YELLOW = FastColor.ARGB32.color(255, 255, 255, 0);
    private static final int GREEN = FastColor.ARGB32.color(255, 0, 255, 0);

    public FacilityDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void clear() {
        genInfos.clear();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, double camX, double camY, double camZ) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        LevelAccessor level = this.minecraft.level;
        DimensionType dimensiontype = level.dimensionType();
        BlockPos blockpos = BlockPos.containing(camera.getPosition().x, 0.0D, camera.getPosition().z);
        if (this.genInfos.containsKey(dimensiontype)) {
            for (ActiveFacilityInstance.PieceGenerationInfo genInfo : this.genInfos.get(dimensiontype).values()) {
                BoundingBox boundingBox = genInfo.region();
                var center = boundingBox.getCenter();
                if (blockpos.closerThan(center, 500.0D)) {
                    LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()),
                            (double)boundingBox.minX() - camX,
                            (double)boundingBox.minY() - camY,
                            (double)boundingBox.minZ() - camZ,
                            (double)(boundingBox.maxX() + 1) - camX,
                            (double)(boundingBox.maxY() + 1) - camY,
                            (double)(boundingBox.maxZ() + 1) - camZ,
                            1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);

                    DebugRenderer.renderFloatingText(poseStack, bufferSource, genInfo.pieceName().toString(),
                            center.getX(), center.getY(), center.getZ(), YELLOW);
                    DebugRenderer.renderFloatingText(poseStack, bufferSource, genInfo.zone().toString(),
                            center.getX(), center.getY() - 1, center.getZ(), GREEN);
                }
            }
        }
    }

    public void addPieces(DimensionType dimensionType, List<ActiveFacilityInstance.PieceGenerationInfo> pieces) {
        if (pieces.isEmpty())
            return;

        var map = genInfos.computeIfAbsent(dimensionType, key -> new HashMap<>());
        pieces.forEach(genInfo -> {
            map.put(genInfo.region().toString(), genInfo);
        });
    }
}
