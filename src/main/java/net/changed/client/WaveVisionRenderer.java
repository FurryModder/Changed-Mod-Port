package net.changed.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.changed.Changed;
import net.changed.data.MixedTexture;
import net.changed.init.ChangedTextures;
import net.changed.init.ChangedTags;
import net.changed.mixin.render.RenderStateShardEmptyTextureStateShardAccessor;
import net.changed.mixin.render.RenderTypeCompositeRenderTypeAccessor;
import net.changed.mixin.render.RenderTypeCompositeStateAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WaveVisionRenderer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEXTURE_SUFFIX = "_wave_resonance";
    private final LevelRenderer levelRenderer;
    private final Minecraft minecraft;
    private final ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private float waveEffect = 0.0f;

    private static DynamicTexture WAVE_RESONANCE_BLOCK_MASK_TEXTURE = null;
    private static DynamicTexture WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE = null;
    public static final ResourceLocation WAVE_RESONANCE_BLOCK_MASK = Changed.modResource("wave_resonance_block_mask");
    public static final ResourceLocation WAVE_RESONANCE_BLOCK_MASK_STRICT = Changed.modResource("wave_resonance_block_mask_strict");

    public static final Vector3f LATEX_RESONANCE_NEUTRAL = new Vector3f(1.0f, 1.0f, 1.0f);

    public static class WrappedModel extends BakedModelWrapper<BakedModel> {
        public WrappedModel(BakedModel wrapped) {
            super(wrapped);
        }

        public RenderType convertRenderType(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data, RenderType renderType) {
            if (renderType == RenderType.solid())
                return ChangedShaders.waveVisionResonantSolidFixed();
            if (renderType == RenderType.cutout())
                return ChangedShaders.waveVisionResonantCutoutFixed();
            if (renderType == RenderType.cutoutMipped())
                return ChangedShaders.waveVisionResonantCutoutMippedFixed();
            return renderType;
        }

        public RenderType backConvertRenderType(@NotNull BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, RenderType renderType) {
            if (renderType == ChangedShaders.waveVisionResonantSolidFixed())
                return RenderType.solid();
            if (renderType == ChangedShaders.waveVisionResonantCutoutFixed())
                return RenderType.cutout();
            if (renderType == ChangedShaders.waveVisionResonantCutoutMippedFixed())
                return RenderType.cutoutMipped();
            return renderType;
        }

        @Override
        public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            return ChangedClient.createRenderTypeSetWithOverride(super.getRenderTypes(state, rand, data), renderType -> {
                return this.convertRenderType(state, rand, data, renderType);
            });
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
            return super.getQuads(state, side, rand, extraData, this.backConvertRenderType(state, side, rand, extraData, renderType));
        }
    }

    public static class WaveVisionBufferSource extends RenderTypeOverride {
        public WaveVisionBufferSource(MultiBufferSource actualSource) {
            super(actualSource, WaveVisionBufferSource::overwriteRenderType);
        }

        public static RenderType overwriteRenderType(RenderType renderType) {
            if (renderType.format() != DefaultVertexFormat.NEW_ENTITY)
                return renderType;

            if (renderType instanceof RenderType.CompositeRenderType composite) {
                var state = ((RenderTypeCompositeRenderTypeAccessor)(Object)composite).changed$getState();
                var textureState = ((RenderTypeCompositeStateAccessor)(Object)state).changed$getTextureState();
                return ((RenderStateShardEmptyTextureStateShardAccessor)(Object)textureState).changed$cutoutTexture().map(texture -> {
                    if (texture.getPath().contains("dark_latex") || texture.getPath().contains("phage_latex"))
                        return ChangedShaders.waveVisionEntityResonant(texture, WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL);
                    else
                        return ChangedShaders.waveVisionEntity(texture);
                }).orElse(renderType);
            }

            return renderType;
        }
    }

    public WaveVisionRenderer(LevelRenderer levelRenderer, Minecraft minecraft, ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections, EntityRenderDispatcher entityRenderDispatcher) {
        this.levelRenderer = levelRenderer;
        this.minecraft = minecraft;
        this.visibleSections = visibleSections;
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    public void prepare(float waveEffect) {
        this.waveEffect = waveEffect;
    }

    public void renderAndSetupFog(ProfilerFiller profiler) {
        profiler.popPush("clear");
        RenderSystem.setShaderFogColor(0.0f, 0.0f, 0.0f);
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        profiler.popPush("fog");
        RenderSystem.setShaderFogStart(12.0f);
        RenderSystem.setShaderFogEnd(48.0f);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }

    private void renderChunkLayer(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        this.renderChunkLayer(renderType, renderType, poseStack, camX, camY, camZ, projectionMatrix);
    }

    private void renderChunkLayer(RenderType renderType, RenderType actualRenderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        RenderSystem.assertOnRenderThread();
        actualRenderType.setupRenderState();

        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush(() -> {
            return "render_" + renderType;
        });
        ShaderInstance shader = RenderSystem.getShader();

        for(int k = 0; k < 12; ++k) {
            int i = RenderSystem.getShaderTexture(k);
            shader.setSampler("Sampler" + k, i);
        }

        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(poseStack.last().pose());
        }

        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projectionMatrix);
        }

        if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }

        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }

        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }

        if (shader.FOG_SHAPE != null) {
            shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }

        if (shader.TEXTURE_MATRIX != null) {
            shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shader.GAME_TIME != null) {
            shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        ChangedShaders.getUniform(shader, "WaveEffect").ifPresent(uniform -> uniform.set(this.waveEffect));
        ChangedShaders.getUniform(shader, "WaveResonance").ifPresent(uniform -> uniform.set(ChangedClient.getWaveResonance()));

        RenderSystem.setupShaderLights(shader);
        shader.apply();
        Uniform chunkOffset = shader.CHUNK_OFFSET;

        for (SectionRenderDispatcher.RenderSection renderSection : this.visibleSections) {
            SectionRenderDispatcher.CompiledSection compiledSection = renderSection.getCompiled();
            if (!compiledSection.isEmpty(renderType)) {
                VertexBuffer vertexbuffer = renderSection.getBuffer(renderType);
                Objects.requireNonNull(vertexbuffer, () -> "Compiled section is missing layer buffer, but is non-empty, for render type " + renderType);
                BlockPos blockpos = renderSection.getOrigin();
                if (chunkOffset != null) {
                    chunkOffset.set((float)((double)blockpos.getX() - camX), (float)((double)blockpos.getY() - camY), (float)((double)blockpos.getZ() - camZ));
                    chunkOffset.upload();
                }

                vertexbuffer.bind();
                vertexbuffer.draw();
            }
        }

        if (chunkOffset != null) {
            chunkOffset.set(0.0F, 0.0F, 0.0F);
        }

        shader.clear();
        VertexBuffer.unbind();
        this.minecraft.getProfiler().pop();
        actualRenderType.clearRenderState();
    }

    public void renderTerrain(ProfilerFiller profiler, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        ChangedClient.resetWaveResonance();
        this.renderChunkLayer(RenderType.solid(), ChangedShaders.waveVisionSolid(), poseStack, camX, camY, camZ, projectionMatrix);
        ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);
        this.renderChunkLayer(ChangedShaders.waveVisionResonantSolidFixed(), poseStack, camX, camY, camZ, projectionMatrix);

        ChangedClient.resetWaveResonance();
        this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, this.minecraft.options.mipmapLevels().get() > 0);
        this.renderChunkLayer(RenderType.cutoutMipped(), ChangedShaders.waveVisionCutoutMipped(), poseStack, camX, camY, camZ, projectionMatrix);
        ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);
        this.renderChunkLayer(ChangedShaders.waveVisionResonantCutoutMippedFixed(), poseStack, camX, camY, camZ, projectionMatrix);

        ChangedClient.resetWaveResonance();
        this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
        this.renderChunkLayer(RenderType.cutout(), ChangedShaders.waveVisionCutout(), poseStack, camX, camY, camZ, projectionMatrix);
        ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);
        this.renderChunkLayer(ChangedShaders.waveVisionResonantCutoutFixed(), poseStack, camX, camY, camZ, projectionMatrix);

        ChangedClient.resetWaveResonance();
    }

    public void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        float bodyRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        this.entityRenderDispatcher.render(entity, x - camX, y - camY, z - camZ, bodyRot, partialTicks, poseStack, bufferSource, this.entityRenderDispatcher.getPackedLightCoords(entity, partialTicks));
    }

    public int renderEntities(ClientLevel level, Frustum frustum, Camera camera, PoseStack poseStack, double camX, double camY, double camZ, float partialTicks, MultiBufferSource bufferSource, Runnable submitDrawCall) {
        int totalEntities = 0;

        ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);

        for (Entity entity : level.entitiesForRendering()) {
            if (this.entityRenderDispatcher.shouldRender(entity, frustum, camX, camY, camZ) || entity.hasIndirectPassenger(this.minecraft.player)) {
                BlockPos blockpos = entity.blockPosition();
                if ((level.isOutsideBuildHeight(blockpos.getY()) || levelRenderer.isSectionCompiled(blockpos)) && (entity != camera.getEntity() || camera.isDetached() || camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).isSleeping()) && (!(entity instanceof LocalPlayer) || camera.getEntity() == entity || (entity == minecraft.player && !minecraft.player.isSpectator()))) {
                    ++totalEntities;
                    if (entity.tickCount == 0) {
                        entity.xOld = entity.getX();
                        entity.yOld = entity.getY();
                        entity.zOld = entity.getZ();
                    }

                    this.renderEntity(entity, camX, camY, camZ, partialTicks, poseStack, bufferSource);
                }
            }
        }

        submitDrawCall.run();

        ChangedClient.resetWaveResonance();

        return totalEntities;
    }

    private static ResourceLocation resolveResonanceTexture(ResourceLocation originalTexture) {
        return ResourceLocation.fromNamespaceAndPath(originalTexture.getNamespace(), originalTexture.getPath() + TEXTURE_SUFFIX);
    }

    private static void loadImageIntoMask(NativeImage mask, TextureAtlasSprite originalSprite, Resource resource) throws IOException {
        SimpleTexture.TextureImage image;

        try (InputStream stream = resource.open()) {
            NativeImage nativeimage = NativeImage.read(stream);
            image = new SimpleTexture.TextureImage(resource.metadata().getSection(TextureMetadataSection.SERIALIZER).orElse(null), nativeimage);
        }

        AnimationMetadataSection animationMetadata = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(null);
        final var contents = originalSprite.contents();
        for (int y = 0; y < contents.height(); ++y) {
            for (int x = 0; x < contents.width(); ++x){
                mask.setPixelRGBA(x + originalSprite.getX(), y + originalSprite.getY(), MixedTexture.sampleNearest(
                        image.getImage(),
                        animationMetadata,
                        (float)x / contents.width(),
                        (float)y / contents.height()).toInt());
            }
        }
    }

    private static void fillMaskWithDefault(NativeImage mask, TextureAtlasSprite originalSprite) {
        mask.fillRect(originalSprite.getX(), originalSprite.getY(), originalSprite.contents().width(), originalSprite.contents().height(), 0xFFFFFFFF);
    }

    private static void fillBlockStateSprites(NativeImage mask, ModelManager modelManager, BlockState state) {
        RandomSource random = RandomSource.create();
        BakedModel model = modelManager.getBlockModelShaper().getBlockModel(state);
        fillMaskWithDefault(mask, model.getParticleIcon());
        model.getQuads(state, null, random).forEach(quad -> fillMaskWithDefault(mask, quad.getSprite()));
        for (Direction direction : Direction.values()) {
            model.getQuads(state, direction, random).forEach(quad -> fillMaskWithDefault(mask, quad.getSprite()));
        }
    }

    private static void fillCrystallineBlockSprites(NativeImage mask, ModelManager modelManager) {
        BuiltInRegistries.BLOCK.stream().forEach(block -> block.getStateDefinition().getPossibleStates().forEach(state -> {
            if (!state.is(ChangedTags.Blocks.CRYSTALLINE)) {
                return;
            }

            fillBlockStateSprites(mask, modelManager, state);
        }));
    }

    private static void fillBlockSprites(NativeImage mask, ModelManager modelManager, ResourceLocation blockId) {
        BuiltInRegistries.BLOCK.getOptional(blockId).ifPresent(block ->
                block.getStateDefinition().getPossibleStates().forEach(state -> fillBlockStateSprites(mask, modelManager, state)));
    }

    private static void fillBlockTagSprites(NativeImage mask, ModelManager modelManager, ResourceManager resources, ResourceLocation tagId) {
        fillBlockTagSprites(mask, modelManager, resources, tagId, Set.of());
    }

    private static void fillBlockTagSprites(NativeImage mask, ModelManager modelManager, ResourceManager resources, ResourceLocation tagId, Set<ResourceLocation> seen) {
        if (seen.contains(tagId)) {
            return;
        }

        Set<ResourceLocation> nextSeen = new java.util.HashSet<>(seen);
        nextSeen.add(tagId);

        loadBlockTag(mask, modelManager, resources, tagId, "tags/block/", nextSeen);
        loadBlockTag(mask, modelManager, resources, tagId, "tags/blocks/", nextSeen);
    }

    private static void loadBlockTag(NativeImage mask, ModelManager modelManager, ResourceManager resources, ResourceLocation tagId, String folder, Set<ResourceLocation> seen) {
        ResourceLocation tagFile = ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(), folder + tagId.getPath() + ".json");

        resources.getResource(tagFile).ifPresent(resource -> {
            try (InputStream stream = resource.open(); InputStreamReader reader = new InputStreamReader(stream)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray values = json.getAsJsonArray("values");
                if (values == null) {
                    return;
                }

                for (JsonElement valueElement : values) {
                    String value = null;
                    if (valueElement.isJsonPrimitive()) {
                        value = valueElement.getAsString();
                    } else if (valueElement.isJsonObject()) {
                        value = valueElement.getAsJsonObject().get("id").getAsString();
                    }

                    if (value == null) {
                        continue;
                    }

                    if (value.startsWith("#")) {
                        fillBlockTagSprites(mask, modelManager, resources, ResourceLocation.parse(value.substring(1)), seen);
                    } else {
                        fillBlockSprites(mask, modelManager, ResourceLocation.parse(value));
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load block tag {} for wave resonance mask", tagFile, e);
            }
        });
    }

    private static boolean isKnownResonantSprite(ResourceLocation texture) {
        String path = texture.getPath();
        if (!path.startsWith("block/")) {
            return false;
        }

        return path.contains("crystal") ||
                path.contains("amethyst") ||
                path.contains("diamond_ore") ||
                path.contains("diamond_block") ||
                path.contains("emerald_ore") ||
                path.contains("emerald_block") ||
                path.contains("dark_latex_block") ||
                path.contains("dark_latex_ice");
    }

    private static void uploadResonanceMask() {
        if (WAVE_RESONANCE_BLOCK_MASK_TEXTURE != null) {
            WAVE_RESONANCE_BLOCK_MASK_TEXTURE.upload();
            ChangedTextures.lateRegisterTexture(WAVE_RESONANCE_BLOCK_MASK, () -> WAVE_RESONANCE_BLOCK_MASK_TEXTURE);
        }

        if (WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE != null) {
            WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE.upload();
            ChangedTextures.lateRegisterTexture(WAVE_RESONANCE_BLOCK_MASK_STRICT, () -> WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE);
        }
    }

    public static void shutdown() {
        if (WAVE_RESONANCE_BLOCK_MASK_TEXTURE != null) {
            WAVE_RESONANCE_BLOCK_MASK_TEXTURE.releaseId();
            WAVE_RESONANCE_BLOCK_MASK_TEXTURE = null;
        }

        if (WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE != null) {
            WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE.releaseId();
            WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE = null;
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onModelBake(ModelEvent.BakingCompleted event) {
            LOGGER.info("Creating resonance block mask");

            ResourceManager resources = Minecraft.getInstance().getResourceManager();
            TextureAtlas blockAtlas = event.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
            if (!(blockAtlas instanceof TextureAtlasExtender ext)) return;

            NativeImage maskBuilder = new NativeImage(ext.getWidth(), ext.getHeight(), false);
            NativeImage strictMaskBuilder = new NativeImage(ext.getWidth(), ext.getHeight(), false);
            strictMaskBuilder.fillRect(0, 0, ext.getWidth(), ext.getHeight(), 0xFF000000);
            fillCrystallineBlockSprites(strictMaskBuilder, event.getModelManager());
            fillBlockTagSprites(strictMaskBuilder, event.getModelManager(), resources, Changed.modResource("crystalline"));

            ext.getSprites().forEach(sprite -> {
                if (isKnownResonantSprite(sprite.contents().name())) {
                    fillMaskWithDefault(strictMaskBuilder, sprite);
                }

                ResourceLocation resonanceMask = MixedTexture.getResourceLocation(resolveResonanceTexture(sprite.contents().name()));
                resources.getResource(resonanceMask).ifPresentOrElse(resource -> {
                    try {
                        loadImageIntoMask(maskBuilder, sprite, resource);
                        loadImageIntoMask(strictMaskBuilder, sprite, resource);
                        LOGGER.debug("Loaded {} into resonance mask", resonanceMask);
                    } catch (IOException e) {
                        fillMaskWithDefault(maskBuilder, sprite);
                    } catch (NullPointerException e) {
                        LOGGER.warn("Resource at {} gave a null InputStream", resonanceMask);
                        fillMaskWithDefault(maskBuilder, sprite);
                    }
                }, () -> {
                    fillMaskWithDefault(maskBuilder, sprite);
                });
            });

            LOGGER.info("Resonance block mask created");

            if (WAVE_RESONANCE_BLOCK_MASK_TEXTURE != null)
                WAVE_RESONANCE_BLOCK_MASK_TEXTURE.releaseId();
            WAVE_RESONANCE_BLOCK_MASK_TEXTURE = new DynamicTexture(maskBuilder);

            if (WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE != null)
                WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE.releaseId();
            WAVE_RESONANCE_BLOCK_MASK_STRICT_TEXTURE = new DynamicTexture(strictMaskBuilder);

            if (!RenderSystem.isOnRenderThreadOrInit()) {
                RenderSystem.recordRenderCall(WaveVisionRenderer::uploadResonanceMask);
            } else {
                uploadResonanceMask();
            }
        }
    }
}
