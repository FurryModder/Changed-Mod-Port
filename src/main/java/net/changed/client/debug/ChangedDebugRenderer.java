package net.changed.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.Changed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

public class ChangedDebugRenderer {
    public final FacilityDebugRenderer facilityDebugRenderer;

    public ChangedDebugRenderer(Minecraft minecraft) {
        this.facilityDebugRenderer = new FacilityDebugRenderer(minecraft);
    }

    public void clear() {
        this.facilityDebugRenderer.clear();
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ) {
        if (Changed.config.server.debugFacilitiesEnabled.get())
            this.facilityDebugRenderer.render(poseStack, bufferSource, camX, camY, camZ);
    }
}
