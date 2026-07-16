package net.changed.mixin.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.Util;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.changed.client.ChangedClient;
import net.changed.client.WaveVisionRenderer;
import net.changed.extension.ChangedCompatibility;
import net.changed.util.Cacheable;
import net.changed.util.CameraUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private ClientLevel level;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    @Shadow @Final private RenderBuffers renderBuffers;
    @Shadow @Final private ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;
    @Shadow @Final private Set<BlockEntity> globalBlockEntities;
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;
    @Shadow private int renderedEntities;
    @Shadow private int culledEntities;
    @Shadow @Nullable private RenderTarget itemEntityTarget;
    @Shadow @Nullable private RenderTarget weatherTarget;
    @Shadow @Nullable private RenderTarget entityTarget;
    @Shadow private boolean captureFrustum;
    @Shadow private Frustum cullingFrustum;
    @Shadow @Nullable private Frustum capturedFrustum;
    @Shadow @Final private Vector3d frustumPos;
    @Shadow @Final private SectionOcclusionGraph sectionOcclusionGraph;
    @Shadow @Nullable private ViewArea viewArea;
    @Shadow @Nullable private SectionRenderDispatcher sectionRenderDispatcher;
    @Shadow private int lastViewDistance;
    @Shadow private int lastCameraSectionX;
    @Shadow private int lastCameraSectionY;
    @Shadow private int lastCameraSectionZ;
    @Shadow private double prevCamX;
    @Shadow private double prevCamY;
    @Shadow private double prevCamZ;
    @Shadow private double prevCamRotX;
    @Shadow private double prevCamRotY;
    @Shadow private boolean generateClouds;

    @Shadow protected abstract void setupRender(Camera camera, Frustum frustum, boolean frustumExists, boolean spectator);
    @Shadow protected abstract void compileSections(Camera camera);
    @Shadow protected abstract void captureFrustum(Matrix4f frustumMatrix, Matrix4f projectionMatrix, double camX, double camY, double camZ, Frustum frustum);
    @Shadow protected abstract void applyFrustum(Frustum frustum);
    @Shadow protected abstract boolean shouldShowEntityOutlines();
    @Shadow protected abstract void checkPoseStack(PoseStack poseStack);
    @Shadow protected abstract void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state);
    @Shadow protected abstract void renderDebug(PoseStack poseStack, MultiBufferSource bufferSource, Camera camera);
    @Shadow public abstract void graphicsChanged();
    @Shadow public abstract boolean isSectionCompiled(BlockPos pos);

    @Unique
    private final Cacheable<WaveVisionRenderer> changed$waveVisionRendererCache = Cacheable.of(() -> new WaveVisionRenderer(
            (LevelRenderer)(Object)this,
            this.minecraft,
            this.visibleSections,
            this.entityRenderDispatcher
    ));

    @Unique
    private boolean changed$waveVisionRendererInitialized = false;
    @Unique
    private boolean changed$waveVisionNeedsFrustumUpdate = false;
    @Unique
    private boolean changed$reportedSodiumWaveVisionFailure = false;

    @Inject(method = "prepareCullFrustum", at = @At("TAIL"))
    private void captureInverseMatrix(Vec3 cameraPosition, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Matrix4f viewSpaceToWorldSpace = new Matrix4f();
        viewSpaceToWorldSpace.translate((float)cameraPosition.x, (float)cameraPosition.y, (float)cameraPosition.z);

        float xRot = camera.getXRot() * Mth.DEG_TO_RAD;
        float yRot = (camera.getYRot() + 180.0F) * Mth.DEG_TO_RAD;
        float zRot = camera.getRoll() * Mth.DEG_TO_RAD;
        if (yRot != 0.0F)
            viewSpaceToWorldSpace.rotate(Axis.YN.rotation(yRot));
        if (xRot != 0.0F)
            viewSpaceToWorldSpace.rotate(Axis.XN.rotation(xRot));
        if (zRot != 0.0F)
            viewSpaceToWorldSpace.rotate(Axis.ZN.rotation(zRot));

        CameraUtil.setViewSpaceToWorldSpaceMatrix(viewSpaceToWorldSpace);
    }

    @Inject(method = "renderSectionLayer", at = @At("RETURN"))
    private void andRecordedTranslucent(RenderType renderType, double x, double y, double z, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (renderType == RenderType.translucent())
            ChangedClient.runRecordedTranslucentRender(this.renderBuffers.bufferSource(), renderType);
    }

    @WrapMethod(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Z)V")
    private void changed$ensureWaveVisionChunkIsUpdated(BlockPos blockPos, boolean reRenderOnMainThread, Operation<Void> original) {
        original.call(blockPos, reRenderOnMainThread);

        if (!ChangedClient.shouldBeRenderingWaveVision() || this.viewArea == null) {
            return;
        }

        for (int z = blockPos.getZ() - 1; z <= blockPos.getZ() + 1; ++z) {
            for (int x = blockPos.getX() - 1; x <= blockPos.getX() + 1; ++x) {
                for (int y = blockPos.getY() - 1; y <= blockPos.getY() + 1; ++y) {
                    this.viewArea.setDirty(
                            SectionPos.blockToSectionCoord(x),
                            SectionPos.blockToSectionCoord(y),
                            SectionPos.blockToSectionCoord(z),
                            reRenderOnMainThread);
                }
            }
        }
    }

    @Inject(method = "allChanged", at = @At("HEAD"), cancellable = true)
    private void changed$overrideAllChangedIfWaveVision(CallbackInfo ci) {
        if (ChangedClient.shouldBeRenderingWaveVision()) {
            ci.cancel();
            boolean wasRenderingWaveVision = ChangedClient.isRenderingWaveVision();
            ChangedClient.setRenderingWaveVision(true);
            try {
                this.changed$waveVisionAllChanged();
            } finally {
                ChangedClient.setRenderingWaveVision(wasRenderingWaveVision);
            }
        }
    }

    @Unique
    private void changed$waveVisionAllChanged() {
        if (this.level == null)
            return;

        this.graphicsChanged();
        this.level.clearTintCaches();
        if (this.sectionRenderDispatcher == null || ChangedCompatibility.shouldIgnoreWaveVisionRenderTypesOutsideOfWaveVision()) {
            this.sectionRenderDispatcher = new SectionRenderDispatcher(
                    this.level,
                    (LevelRenderer)(Object)this,
                    Util.backgroundExecutor(),
                    this.renderBuffers,
                    this.minecraft.getBlockRenderer(),
                    this.minecraft.getBlockEntityRenderDispatcher()
            );
        } else {
            this.sectionRenderDispatcher.setLevel(this.level);
        }

        this.generateClouds = true;
        ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
        this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
        if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
        }

        this.sectionRenderDispatcher.blockUntilClear();
        synchronized (this.globalBlockEntities) {
            this.globalBlockEntities.clear();
        }

        this.viewArea = new ViewArea(this.sectionRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), (LevelRenderer)(Object)this);
        this.sectionOcclusionGraph.waitAndReset(this.viewArea);
        this.visibleSections.clear();
        this.changed$waveVisionNeedsFrustumUpdate = true;
        this.prevCamRotX = Double.NaN;
        this.prevCamRotY = Double.NaN;
        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null) {
            this.viewArea.repositionCamera(entity.getX(), entity.getZ());
        }
    }

    @Unique
    private void changed$waveVisionSetupRender(Camera camera, Frustum frustum, boolean frustumExists, boolean spectator) {
        Vec3 camPos = camera.getPosition();
        if (this.sectionRenderDispatcher == null || this.viewArea == null || this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
            this.changed$waveVisionAllChanged();
        }

        this.level.getProfiler().push("camera");
        Entity cameraEntity = this.minecraft.getCameraEntity();
        double playerX = cameraEntity != null ? cameraEntity.getX() : camPos.x();
        double playerY = cameraEntity != null ? cameraEntity.getY() : camPos.y();
        double playerZ = cameraEntity != null ? cameraEntity.getZ() : camPos.z();
        int chunkX = SectionPos.posToSectionCoord(playerX);
        int chunkY = SectionPos.posToSectionCoord(playerY);
        int chunkZ = SectionPos.posToSectionCoord(playerZ);
        if (this.lastCameraSectionX != chunkX || this.lastCameraSectionY != chunkY || this.lastCameraSectionZ != chunkZ) {
            this.lastCameraSectionX = chunkX;
            this.lastCameraSectionY = chunkY;
            this.lastCameraSectionZ = chunkZ;
            this.viewArea.repositionCamera(playerX, playerZ);
        }

        this.sectionRenderDispatcher.setCamera(camPos);
        this.level.getProfiler().popPush("cull");
        this.minecraft.getProfiler().popPush("culling");
        BlockPos blockPos = camera.getBlockPosition();
        double camSectionX = Math.floor(camPos.x / 8.0);
        double camSectionY = Math.floor(camPos.y / 8.0);
        double camSectionZ = Math.floor(camPos.z / 8.0);
        if (camSectionX != this.prevCamX || camSectionY != this.prevCamY || camSectionZ != this.prevCamZ) {
            this.sectionOcclusionGraph.invalidate();
        }

        this.prevCamX = camSectionX;
        this.prevCamY = camSectionY;
        this.prevCamZ = camSectionZ;
        this.minecraft.getProfiler().popPush("update");

        boolean smartCull = this.minecraft.smartCull;
        if (spectator && this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos)) {
            smartCull = false;
        }

        Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) *
                this.minecraft.options.entityDistanceScaling().get());
        this.minecraft.getProfiler().push("section_occlusion_graph");
        this.sectionOcclusionGraph.update(smartCull, camera, frustum, this.visibleSections);
        this.minecraft.getProfiler().pop();
        double rotX = Math.floor(camera.getXRot() / 2.0F);
        double rotY = Math.floor(camera.getYRot() / 2.0F);
        if (this.changed$waveVisionNeedsFrustumUpdate || this.visibleSections.isEmpty() || this.sectionOcclusionGraph.consumeFrustumUpdate() || rotX != this.prevCamRotX || rotY != this.prevCamRotY) {
            this.applyFrustum(LevelRenderer.offsetFrustum(frustum));
            this.changed$waveVisionNeedsFrustumUpdate = false;
            this.prevCamRotX = rotX;
            this.prevCamRotY = rotY;
        }

        this.minecraft.getProfiler().pop();
    }

    @Unique
    private void changed$compileWaveVisionSectionsSync() {
        if (this.sectionRenderDispatcher == null || this.visibleSections.isEmpty()) {
            return;
        }

        RenderRegionCache renderRegionCache = new RenderRegionCache();
        for (SectionRenderDispatcher.RenderSection renderSection : this.visibleSections) {
            if (!renderSection.isDirty()) {
                continue;
            }

            this.sectionRenderDispatcher.rebuildSectionSync(renderSection, renderRegionCache);
            renderSection.setNotDirty();
        }

        this.sectionRenderDispatcher.uploadAllPendingUploads();
    }

    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    private void renderWaveVisionLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.level == null) {
            this.changed$waveVisionRendererInitialized = false;
            return;
        }

        if (!ChangedClient.shouldBeRenderingWaveVision()) {
            this.changed$waveVisionRendererInitialized = false;
            return;
        }

        ci.cancel();
        ChangedClient.setRenderingWaveVision(true);

        try {
            if (!this.changed$waveVisionRendererInitialized && ChangedCompatibility.shouldIgnoreWaveVisionRenderTypesOutsideOfWaveVision()) {
                this.changed$waveVisionAllChanged();
                this.changed$waveVisionRendererInitialized = true;
            }

            float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(frustumMatrix);

            var waveVisionRenderer = changed$waveVisionRendererCache.get();
            waveVisionRenderer.prepare(ChangedClient.setupWaveVisionEffect(partialTicks));

            RenderSystem.setShaderGameTime(this.level.getGameTime(), partialTicks);
            this.blockEntityRenderDispatcher.prepare(this.level, camera, this.minecraft.hitResult);
            this.entityRenderDispatcher.prepare(this.level, camera, this.minecraft.crosshairPickEntity);

            ProfilerFiller profiler = this.level.getProfiler();
            profiler.popPush("light_update_queue");
            this.level.pollLightUpdates();
            profiler.popPush("light_updates");
            this.level.getChunkSource().getLightEngine().runLightUpdates();

            Vec3 camPos = camera.getPosition();
            double camX = camPos.x();
            double camY = camPos.y();
            double camZ = camPos.z();

            profiler.popPush("culling");
            boolean frustumExists = this.capturedFrustum != null;
            Frustum frustum = frustumExists ? this.capturedFrustum : this.cullingFrustum;
            if (frustum == null) {
                frustum = new Frustum(frustumMatrix, projectionMatrix);
            }
            if (frustumExists) {
                frustum.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
            }

            this.minecraft.getProfiler().popPush("captureFrustum");
            if (this.captureFrustum) {
                this.captureFrustum(frustumMatrix, projectionMatrix, camX, camY, camZ, frustumExists ? new Frustum(frustumMatrix, projectionMatrix) : frustum);
                this.captureFrustum = false;
            }

            waveVisionRenderer.renderAndSetupFog(profiler);

            profiler.popPush("terrain_setup");
            this.changed$waveVisionSetupRender(camera, frustum, frustumExists, this.minecraft.player != null && this.minecraft.player.isSpectator());
            profiler.popPush("compile_sections");
            this.compileSections(camera);
            if (ChangedCompatibility.shouldIgnoreWaveVisionRenderTypesOutsideOfWaveVision()) {
                this.changed$compileWaveVisionSectionsSync();
            }

            profiler.popPush("terrain");
            ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.TERRAIN);
            ChangedClient.setWaveResonance(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL);
            if (!this.changed$renderSodiumWaveVisionTerrain(camera, frustum, this.minecraft.player != null && this.minecraft.player.isSpectator(), frustumMatrix, projectionMatrix, camX, camY, camZ)) {
                waveVisionRenderer.renderTerrain(profiler, poseStack, camX, camY, camZ, projectionMatrix);
            }
            ChangedClient.resetWaveResonance();

            if (this.level.effects().constantAmbientLight()) {
                Lighting.setupNetherLevel();
            } else {
                Lighting.setupLevel();
            }

            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.mul(frustumMatrix);
            RenderSystem.applyModelViewMatrix();

            try {
                PoseStack entityPoseStack = new PoseStack();
                profiler.popPush("entities");
                ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.ENTITIES);
                this.renderedEntities = 0;
                this.culledEntities = 0;
                if (this.itemEntityTarget != null) {
                    this.itemEntityTarget.clear(Minecraft.ON_OSX);
                    this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
                    this.minecraft.getMainRenderTarget().bindWrite(false);
                }
                if (this.weatherTarget != null) {
                    this.weatherTarget.clear(Minecraft.ON_OSX);
                }
                if (this.entityTarget != null && this.shouldShowEntityOutlines()) {
                    this.entityTarget.clear(Minecraft.ON_OSX);
                    this.minecraft.getMainRenderTarget().bindWrite(false);
                }

                MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
                WaveVisionRenderer.WaveVisionBufferSource overwrittenSource = new WaveVisionRenderer.WaveVisionBufferSource(bufferSource);
                this.renderedEntities += waveVisionRenderer.renderEntities(this.level, frustum, camera, entityPoseStack, camX, camY, camZ, partialTicks, overwrittenSource, bufferSource::endLastBatch);
                this.checkPoseStack(entityPoseStack);

                bufferSource.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
                bufferSource.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
                bufferSource.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
                bufferSource.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));

                profiler.popPush("blockentities");
                ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.BLOCK_ENTITIES);
                this.changed$renderBlockEntities(partialTicks, frustum, entityPoseStack, camX, camY, camZ, overwrittenSource);

                if (renderBlockOutline) {
                    this.changed$renderHitOutline(camera, entityPoseStack, bufferSource, camX, camY, camZ);
                }

                this.minecraft.debugRenderer.render(entityPoseStack, bufferSource, camX, camY, camZ);
                bufferSource.endLastBatch();
                bufferSource.endBatch(Sheets.translucentCullBlockSheet());
                bufferSource.endBatch(Sheets.bannerSheet());
                bufferSource.endBatch(Sheets.shieldSheet());
                bufferSource.endBatch(RenderType.armorEntityGlint());
                bufferSource.endBatch(RenderType.glint());
                bufferSource.endBatch(RenderType.glintTranslucent());
                bufferSource.endBatch(RenderType.entityGlint());
                bufferSource.endBatch(RenderType.entityGlintDirect());
                bufferSource.endBatch(RenderType.waterMask());
                this.renderBuffers.crumblingBufferSource().endBatch();
                bufferSource.endBatch(RenderType.lines());
                bufferSource.endBatch();

                this.renderDebug(entityPoseStack, bufferSource, camera);
                bufferSource.endLastBatch();
            } finally {
                modelViewStack.popMatrix();
                RenderSystem.applyModelViewMatrix();
            }

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        } finally {
            ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.TERRAIN);
            ChangedClient.setRenderingWaveVision(false);
            FogRenderer.setupNoFog();
        }
    }

    @Unique
    private boolean changed$renderSodiumWaveVisionTerrain(Camera camera, Frustum frustum, boolean spectator, Matrix4f frustumMatrix, Matrix4f projectionMatrix, double camX, double camY, double camZ) {
        if (!ChangedCompatibility.isModPresent("sodium")) {
            return false;
        }

        try {
            Object sodiumWorldRenderer = this.changed$getSodiumWorldRenderer();
            if (sodiumWorldRenderer == null) {
                return false;
            }

            Object sodiumViewport = this.changed$createSodiumViewport(frustum);
            if (sodiumViewport == null) {
                return false;
            }

            Class<?> renderDeviceClass = Class.forName("net.caffeinemc.mods.sodium.client.gl.device.RenderDevice");
            Class<?> viewportClass = Class.forName("net.caffeinemc.mods.sodium.client.render.viewport.Viewport");
            Class<?> matricesClass = Class.forName("net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices");
            Class<?> matrix4fcClass = Class.forName("org.joml.Matrix4fc");

            Object chunkMatrices = matricesClass.getConstructor(matrix4fcClass, matrix4fcClass).newInstance(projectionMatrix, frustumMatrix);
            boolean flawlessFrames = this.changed$isSodiumFlawlessFramesActive();

            this.changed$enterSodiumManagedCode(renderDeviceClass);
            try {
                sodiumWorldRenderer.getClass()
                        .getMethod("setupTerrain", Camera.class, viewportClass, boolean.class, boolean.class)
                        .invoke(sodiumWorldRenderer, camera, sodiumViewport, spectator, flawlessFrames);
            } finally {
                this.changed$exitSodiumManagedCode(renderDeviceClass);
            }

            this.changed$enterSodiumManagedCode(renderDeviceClass);
            try {
                this.changed$drawSodiumChunkLayer(sodiumWorldRenderer, matricesClass, chunkMatrices, RenderType.solid(), camX, camY, camZ);
                this.changed$drawSodiumChunkLayer(sodiumWorldRenderer, matricesClass, chunkMatrices, RenderType.cutoutMipped(), camX, camY, camZ);
                this.changed$drawSodiumChunkLayer(sodiumWorldRenderer, matricesClass, chunkMatrices, RenderType.cutout(), camX, camY, camZ);
            } finally {
                this.changed$exitSodiumManagedCode(renderDeviceClass);
            }

            return true;
        } catch (Throwable throwable) {
            if (!this.changed$reportedSodiumWaveVisionFailure) {
                WaveVisionRenderer.LOGGER.warn("Failed to render wave vision terrain through Sodium, falling back to vanilla chunk buffers", throwable);
                this.changed$reportedSodiumWaveVisionFailure = true;
            }

            return false;
        }
    }

    @Unique
    private Object changed$getSodiumWorldRenderer() throws ReflectiveOperationException {
        Class<?> extensionClass = Class.forName("net.caffeinemc.mods.sodium.client.world.LevelRendererExtension");
        Object levelRenderer = (LevelRenderer)(Object)this;
        if (!extensionClass.isInstance(levelRenderer)) {
            return null;
        }

        return extensionClass.getMethod("sodium$getWorldRenderer").invoke(levelRenderer);
    }

    @Unique
    private Object changed$createSodiumViewport(Frustum frustum) throws ReflectiveOperationException {
        Class<?> viewportProviderClass = Class.forName("net.caffeinemc.mods.sodium.client.render.viewport.ViewportProvider");
        if (!viewportProviderClass.isInstance(frustum)) {
            return null;
        }

        return viewportProviderClass.getMethod("sodium$createViewport").invoke(frustum);
    }

    @Unique
    private boolean changed$isSodiumFlawlessFramesActive() {
        try {
            Class<?> flawlessFramesClass = Class.forName("net.caffeinemc.mods.sodium.client.util.FlawlessFrames");
            return Boolean.TRUE.equals(flawlessFramesClass.getMethod("isActive").invoke(null));
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Unique
    private void changed$enterSodiumManagedCode(Class<?> renderDeviceClass) throws ReflectiveOperationException {
        renderDeviceClass.getMethod("enterManagedCode").invoke(null);
    }

    @Unique
    private void changed$exitSodiumManagedCode(Class<?> renderDeviceClass) throws ReflectiveOperationException {
        renderDeviceClass.getMethod("exitManagedCode").invoke(null);
    }

    @Unique
    private void changed$drawSodiumChunkLayer(Object sodiumWorldRenderer, Class<?> matricesClass, Object chunkMatrices, RenderType renderType, double camX, double camY, double camZ) throws ReflectiveOperationException {
        sodiumWorldRenderer.getClass()
                .getMethod("drawChunkLayer", RenderType.class, matricesClass, double.class, double.class, double.class)
                .invoke(sodiumWorldRenderer, renderType, chunkMatrices, camX, camY, camZ);
    }

    @Unique
    private void changed$renderBlockEntities(float partialTicks, Frustum frustum, PoseStack poseStack, double camX, double camY, double camZ, MultiBufferSource bufferSource) {
        for (SectionRenderDispatcher.RenderSection section : this.visibleSections) {
            List<BlockEntity> blockEntities = section.getCompiled().getRenderableBlockEntities();
            if (blockEntities.isEmpty())
                continue;

            for (BlockEntity blockEntity : blockEntities) {
                if (!this.changed$shouldRenderBlockEntity(blockEntity, camX, camY, camZ))
                    continue;

                BlockPos blockPos = blockEntity.getBlockPos();
                poseStack.pushPose();
                poseStack.translate((double)blockPos.getX() - camX, (double)blockPos.getY() - camY, (double)blockPos.getZ() - camZ);
                this.blockEntityRenderDispatcher.render(blockEntity, partialTicks, poseStack, bufferSource);
                poseStack.popPose();
            }
        }

        synchronized (this.globalBlockEntities) {
            for (BlockEntity blockEntity : this.globalBlockEntities) {
                if (!this.changed$shouldRenderBlockEntity(blockEntity, camX, camY, camZ))
                    continue;

                BlockPos blockPos = blockEntity.getBlockPos();
                poseStack.pushPose();
                poseStack.translate((double)blockPos.getX() - camX, (double)blockPos.getY() - camY, (double)blockPos.getZ() - camZ);
                this.blockEntityRenderDispatcher.render(blockEntity, partialTicks, poseStack, bufferSource);
                poseStack.popPose();
            }
        }
    }

    @Unique
    private boolean changed$shouldRenderBlockEntity(BlockEntity blockEntity, double camX, double camY, double camZ) {
        BlockEntityRenderer<BlockEntity> renderer = this.blockEntityRenderDispatcher.getRenderer(blockEntity);
        return renderer != null && renderer.shouldRender(blockEntity, new Vec3(camX, camY, camZ));
    }

    @Unique
    private void changed$renderHitOutline(Camera camera, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ) {
        HitResult hitResult = this.minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHitResult) || hitResult.getType() != HitResult.Type.BLOCK)
            return;

        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.isAir() || !this.level.getWorldBorder().isWithinBounds(blockPos))
            return;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        this.renderHitOutline(poseStack, vertexConsumer, camera.getEntity(), camX, camY, camZ, blockPos, blockState);
    }
}
