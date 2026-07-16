package net.changed.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.animations.AnimationAssociations;
import net.changed.client.animations.AnimationDefinitions;
import net.changed.client.debug.ChangedDebugRenderer;
import net.changed.client.latexparticles.LatexParticleEngine;
import net.changed.client.latexparticles.SetupContext;
import net.changed.client.renderer.ChangedEntityWithOutLevelRenderer;
import net.changed.client.renderer.blockentity.ChangedBlockEntityWithoutLevelRenderer;
import net.changed.client.renderer.layers.FirstPersonLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.sound.GasSFX;
import net.changed.compat.ForgeSpawnEggItem;
import net.changed.entity.ChangedEntity;
import net.changed.entity.VisionType;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.init.ChangedBlocks;
import net.changed.init.ChangedEntities;
import net.changed.init.ChangedFluids;
import net.changed.init.ChangedItems;
import net.changed.item.Syringe;
import net.changed.process.ProcessTransfur;
import net.changed.util.Cacheable;
import net.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ChangedClient {
    public static long clientTicks = 0;
    public static final Cacheable<LatexParticleEngine> particleSystem = Cacheable.of(() -> new LatexParticleEngine(Minecraft.getInstance()));
    public static final Cacheable<ChangedBlockEntityWithoutLevelRenderer> itemRenderer =
            Cacheable.of(() -> new ChangedBlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));
    public static final Cacheable<ChangedEntityWithOutLevelRenderer> itemEntityRenderer =
            Cacheable.of(() -> new ChangedEntityWithOutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), Minecraft.getInstance().getEntityRenderDispatcher()));
    public static final Cacheable<AbilityColors> abilityColors = Cacheable.of(AbilityColors::createDefault);
    public static final Cacheable<AbilityRenderer> abilityRenderer = Cacheable.of(() -> new AbilityRenderer(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getModelManager(), abilityColors.getOrThrow()));
    public static final Cacheable<LatexCoveredBlocksRenderer> latexCoveredBlocksRenderer = Cacheable.of(() -> new LatexCoveredBlocksRenderer(Minecraft.getInstance()));
    public static final Cacheable<WallSignTextureManager> wallSigns = Cacheable.of(() -> new WallSignTextureManager(Minecraft.getInstance().getTextureManager()));
    public static final Cacheable<ChangedDebugRenderer> debugRenderer = Cacheable.of(() -> new ChangedDebugRenderer(Minecraft.getInstance()));

    private static final ThreadLocal<Function<RenderType, RenderType>> CHUNK_RENDER_TYPE_SET_OVERRIDE = ThreadLocal.withInitial(() -> null);

    public static ChunkRenderTypeSet createRenderTypeSetWithOverride(
            ChunkRenderTypeSet toWrap,
            Function<RenderType, RenderType> function) {

        CHUNK_RENDER_TYPE_SET_OVERRIDE.set(function);

        return ChunkRenderTypeSet.of(toWrap.asList());
    }

    public static Function<RenderType, RenderType> acceptNextRenderTypeSetOverride() {
        var next = CHUNK_RENDER_TYPE_SET_OVERRIDE.get();
        CHUNK_RENDER_TYPE_SET_OVERRIDE.remove();
        return next;
    }

    public static void registerEventListeners() {
        Changed.addEventListener(ChangedClient::afterRenderStage);
        Changed.addEventListener(ChangedClient::onClientTick);
    }

    public static void onClientFinishSetup(FMLLoadCompleteEvent event) {
        ChangedFluids.APPLY_RENDER_LAYERS.forEach(Runnable::run);
    }

    public static void registerReloadListeners(Consumer<PreparableReloadListener> resourceManager) {
        resourceManager.accept(particleSystem.getOrThrow());
        resourceManager.accept(abilityRenderer.getOrThrow());
        resourceManager.accept(latexCoveredBlocksRenderer.getOrThrow());
        resourceManager.accept(wallSigns.getOrThrow());
        resourceManager.accept(AnimationDefinitions.INSTANCE);
        resourceManager.accept(AnimationAssociations.INSTANCE);
    }

    public static void afterRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            particleSystem.get().render(event.getPoseStack(), Minecraft.getInstance().gameRenderer.lightTexture(), event.getCamera(), partialTick, event.getFrustum(), SetupContext.THIRD_PERSON);
            FirstPersonLayer.renderFirstPersonLayersOnFace(event.getPoseStack(), event.getCamera(), partialTick);
        }
    }

    public static double getAcceptableParticleDistanceSqr() {
        return switch (Minecraft.getInstance().options.particles().get()) {
            case ALL -> 9999999999999999.0;
            case DECREASED -> 4096.0;
            case MINIMAL -> 256.0;
            default -> 16384.0; // In case of a mixin
        };
    }

    protected static void addLatexParticleToChangedEntity(ChangedEntity entity) {
        if (particleSystem.getOrThrow().pauseForReload())
            return;
        if (entity.getRandom().nextFloat() > entity.getDripRate(1.0f - entity.computeHealthRatio()))
            return;
        if (Minecraft.getInstance().cameraEntity != null && entity.distanceToSqr(Minecraft.getInstance().cameraEntity) > getAcceptableParticleDistanceSqr())
            return;
        var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        if (!(renderer instanceof LivingEntityRenderer<?,?> livingEntityRenderer))
            return;
        for (var layer : livingEntityRenderer.layers) {
            if (layer instanceof LatexParticlesLayer<?,?> latexParticlesLayer) {
                latexParticlesLayer.createNewDripParticle(entity);
                break;
            }
        }
    }

    protected static void addLatexParticleToAssimilatedEntity(PathfinderMob entity) {
        if (particleSystem.getOrThrow().pauseForReload())
            return;
        if (!ProcessTransfur.isMobAssimilated(entity))
            return;
        if (entity.getRandom().nextFloat() > 0.05f)
            return;
        if (Minecraft.getInstance().cameraEntity != null && entity.distanceToSqr(Minecraft.getInstance().cameraEntity) > getAcceptableParticleDistanceSqr())
            return;
        var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        if (!(renderer instanceof LivingEntityRenderer<?,?> livingEntityRenderer))
            return;
        for (var layer : livingEntityRenderer.layers) {
            if (layer instanceof LatexParticlesLayer<?,?> latexParticlesLayer) {
                latexParticlesLayer.createNewDripParticle(entity);
                break;
            }
        }
    }

    protected static void addLatexParticleToPlayer(Player entity) {
        ProcessTransfur.ifPlayerTransfurred(entity, variant -> {
            addLatexParticleToChangedEntity(variant.getChangedEntity());
        });
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level != null && particleSystem.getOrThrow().tick()) {
            var cameraPos = minecraft.gameRenderer.getMainCamera().getBlockPosition();
            var aabb = AABB.of(BoundingBox.fromCorners(cameraPos.offset(-64, -64, -64), cameraPos.offset(64, 64, 64)));
            minecraft.level.getEntitiesOfClass(ChangedEntity.class, aabb).forEach(ChangedClient::addLatexParticleToChangedEntity);
            minecraft.level.getEntitiesOfClass(Player.class, aabb).forEach(ChangedClient::addLatexParticleToPlayer);
            minecraft.level.getEntitiesOfClass(PathfinderMob.class, aabb).forEach(ChangedClient::addLatexParticleToAssimilatedEntity);
        }

        clientTicks++;

        GasSFX.ensureGasSfx();
    }

    private static List<Consumer<VertexConsumer>> TRANSLUCENT_CONSUMERS = new ArrayList<>();

    public static void runRecordedTranslucentRender(MultiBufferSource buffers, RenderType renderType) {
        final VertexConsumer buffer = buffers.getBuffer(renderType);
        TRANSLUCENT_CONSUMERS.forEach(consumer -> consumer.accept(buffer));
        TRANSLUCENT_CONSUMERS.clear();
    }

    public static void recordTranslucentRender(MultiBufferSource buffers, RenderType renderType, Consumer<VertexConsumer> consumer) {
        if (renderType == RenderType.translucent()) {
            TRANSLUCENT_CONSUMERS.add(consumer);
        } else {
            consumer.accept(buffers.getBuffer(renderType));
        }
    }

    public enum WaveVisionRenderPhase {
        TERRAIN,
        ENTITIES,
        BLOCK_ENTITIES
    }

    private static WaveVisionRenderPhase phase = WaveVisionRenderPhase.TERRAIN;

    public static WaveVisionRenderPhase getWaveRenderPhase() {
        return phase;
    }

    public static void setWaveRenderPhase(WaveVisionRenderPhase phase) {
        ChangedClient.phase = phase;
    }

    public static boolean shouldBeRenderingWaveVision() {
        final var minecraft = Minecraft.getInstance();
        if (minecraft == null)
            return false;
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(minecraft.cameraEntity))
                .map(variant -> variant.visionType == VisionType.WAVE_VISION)
                .orElse(false);
    }

    private static boolean renderingWaveVision = false;
    private static float waveEffect = 0.0f;
    private static Vector3f waveResonance = new Vector3f(0f);
    private static long waveVisionStartGameTime = Long.MIN_VALUE;
    private static int waveVisionCameraEntityId = Integer.MIN_VALUE;
    public static float setupWaveVisionEffect(float partialTicks) {
        final var minecraft = Minecraft.getInstance();
        final var cameraEntity = EntityUtil.playerOrNull(minecraft.cameraEntity);
        final var variant = ProcessTransfur.getPlayerTransfurVariantSafe(cameraEntity)
                .filter(instance -> instance.visionType == VisionType.WAVE_VISION);

        if (minecraft.level == null || cameraEntity == null || variant.isEmpty()) {
            waveVisionStartGameTime = Long.MIN_VALUE;
            waveVisionCameraEntityId = Integer.MIN_VALUE;
            waveEffect = 0.0f;
            return waveEffect;
        }

        if (waveVisionStartGameTime == Long.MIN_VALUE || waveVisionCameraEntityId != cameraEntity.getId()) {
            waveVisionStartGameTime = minecraft.level.getGameTime();
            waveVisionCameraEntityId = cameraEntity.getId();
        }

        float localTicks = minecraft.level.getGameTime() - waveVisionStartGameTime + partialTicks;
        float variantTicks = variant.map(TransfurVariantInstance::getTicksInWaveVision).orElse(0) + partialTicks;
        float effect = Math.max(variantTicks, localTicks);

        waveEffect = effect * 0.5f;
        return waveEffect;
    }

    public static void setRenderingWaveVision(boolean renderingWaveVision) {
        ChangedClient.renderingWaveVision = renderingWaveVision;
    }

    public static boolean isRenderingWaveVision() {
        return ChangedClient.renderingWaveVision;
    }

    public static float getWaveEffect() {
        return waveEffect;
    }

    public static void setWaveResonance(Vector3f resonance) {
        ChangedClient.waveResonance = resonance;
    }

    public static void resetWaveResonance() {
        ChangedClient.waveResonance = new Vector3f(0f);
    }

    public static Vector3f getWaveResonance() {
        return waveResonance;
    }

    public static void onBlockColorsInit(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, layer) ->
                        switch (layer) {
                            case 0 -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
                            case 1 -> 0xFFFFFF;
                            default -> -1;
                        },
                ChangedBlocks.ORANGE_TREE_LEAVES.get());
    }

    private static int opaqueItemTint(int rgb) {
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    private static int latexContainerTint(net.minecraft.world.item.ItemStack stack, int layer) {
        if (layer >= 2)
            return -1;

        var variant = Syringe.getVariant(stack);
        if (variant == null)
            return opaqueItemTint(0xF0F0F0);

        var colors = variant.getColors();
        return opaqueItemTint((layer == 0 ? colors.getFirst() : colors.getSecond()).toInt());
    }

    public static void onItemColorsInit(RegisterColorHandlersEvent.Item event) {
        event.register(ChangedClient::latexContainerTint,
                ChangedItems.LATEX_SYRINGE.get(), ChangedItems.LATEX_TIPPED_ARROW.get(), ChangedItems.LATEX_FLASK.get());

        Item[] spawnEggItems = ChangedEntities.SPAWN_EGGS.values().stream()
                .map(holder -> (Item)holder.get())
                .toArray(Item[]::new);
        event.register((stack, layer) -> {
            if (layer > 1)
                return -1;
            return stack.getItem() instanceof ForgeSpawnEggItem spawnEggItem ? opaqueItemTint(spawnEggItem.getColor(layer)) : -1;
        }, spawnEggItems);

        event.register((stack, layer) -> layer > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR),
                ChangedItems.LEATHER_LOWER_ABDOMEN_ARMOR.get(), ChangedItems.LEATHER_UPPER_ABDOMEN_ARMOR.get());
        event.register((stack, layer) -> layer > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR),
                ChangedItems.LEATHER_QUADRUPEDAL_BOOTS.get(), ChangedItems.LEATHER_QUADRUPEDAL_LEGGINGS.get());

        event.register((stack, layer) ->
                        switch (layer) {
                            case 0 -> opaqueItemTint(FoliageColor.getDefaultColor());
                            case 1 -> opaqueItemTint(0xFFFFFF);
                            default -> -1;
                        },
                ChangedBlocks.ORANGE_TREE_LEAVES.get());
    }
}
