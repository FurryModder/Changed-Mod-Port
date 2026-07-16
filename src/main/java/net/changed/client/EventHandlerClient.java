package net.changed.client;

import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.changed.Changed;
import net.changed.ability.AbstractAbility;
import net.changed.ability.GrabEntityAbility;
import net.changed.client.gui.ContentWarningScreen;
import net.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.LatexParticlesLayer;
import net.changed.client.tfanimations.TransfurAnimator;
import net.changed.effect.particle.EmoteParticle;
import net.changed.effect.particle.GasParticle;
import net.changed.effect.particle.LatexDripParticle;
import net.changed.effect.particle.TscSweepParticle;
import net.changed.entity.*;
import net.changed.entity.latex.IClientLatexTypeExtensions;
import net.changed.fluid.AbstractLatexFluid;
import net.changed.init.ChangedAbilities;
import net.changed.init.ChangedGameRules;
import net.changed.init.ChangedLatexTypes;
import net.changed.init.ChangedParticles;
import net.changed.init.ChangedTags;
import net.changed.network.packet.QueryTransfurPacket;
import net.changed.process.ProcessTransfur;
import net.changed.world.LatexCoverGetter;
import net.changed.world.LatexCoverHitResult;
import net.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.changed.compat.ForgeRegistries;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class EventHandlerClient {
    private boolean shouldEntityBeRendered(LivingEntity entity) {
        if (entity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility != null && !grabAbility.shouldRenderGrabbedEntity())
                return false;
        }

        var entityGrabAbility = AbstractAbility.getAbilityInstance(entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (entityGrabAbility != null && !entityGrabAbility.shouldRenderLatex())
            return false;
        if (entity.isDeadOrDying() && entity.getLastDamageSource() != null && entity.getLastDamageSource().is(ChangedTags.DamageTypes.IS_TRANSFUR))
            return false;
        if (entity.getVehicle() instanceof SeatEntity seat && seat.shouldSeatedBeInvisible())
            return false;

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderEntityPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!this.shouldEntityBeRendered(event.getEntity()))
            event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        if (event.isCanceled())
            return;
        if (!this.shouldEntityBeRendered(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (player instanceof PlayerDataExtension ext && ext.isPlayerMover(PlayerMover.LATEX_SWIM.get())) {
            event.setCanceled(true);
            return;
        }

        if (!player.isRemoved() && !player.isSpectator() && !TransfurAnimator.shouldRenderHuman()) {
            if (RenderOverride.renderOverrides(player, null, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick()))
                event.setCanceled(true);
            else if (ProcessTransfur.isPlayerTransfurred(player)) {
                event.setCanceled(true);
                FormRenderHandler.renderForm(player, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.player.isRemoved()) //we need to cache this as the hand may be rendered even in the death screen.
        {
            FormRenderHandler.lastPartialTick = event.getPartialTick();
        }
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ChangedParticles.DRIPPING_LATEX.get(), LatexDripParticle.Provider::new);
        event.registerSpriteSet(ChangedParticles.GAS.get(), GasParticle.Provider::new);
        event.registerSpriteSet(ChangedParticles.EMOTE.get(), EmoteParticle.Provider::new);
        event.registerSpriteSet(ChangedParticles.TSC_SWEEP_ATTACK.get(), TscSweepParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onRegisterModelRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register(Changed.modResource("emissive"), RenderType.cutout(), RenderType.eyes(TextureAtlas.LOCATION_BLOCKS));
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            ChangedShaders.reloadShaders(event.getResourceProvider(), pair -> event.registerShader(pair.getFirst(), pair.getSecond()));
        } catch (Exception exception) {
            throw new RuntimeException("could not reload changed shaders", exception);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderFog(ViewportEvent.RenderFog event) {
        if (!(event.getCamera().getBlockAtCamera().getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)) return;

        event.setNearPlaneDistance(0.25F);
        event.setFarPlaneDistance(1.0F);
        event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onFogColors(ViewportEvent.ComputeFogColor event) {
        if (!(event.getCamera().getBlockAtCamera().getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)) return;

        var color = IClientLatexTypeExtensions.of(abstractLatexFluid.getLatexType()).getColor();
        event.setRed(color.red());
        event.setGreen(color.green());
        event.setBlue(color.blue());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRespawn(ClientPlayerNetworkEvent.Clone event) {
        Changed.PACKET_HANDLER.sendToServer(QueryTransfurPacket.Builder.of(event.getNewPlayer()));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInputEvent(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack() || event.isUseItem()) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;

            ProcessTransfur.ifPlayerTransfurred(localPlayer, variant -> {
                variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), ability -> {
                    if (ability.grabbedEntity != null && !ability.suited) {
                        event.setCanceled(true);
                        event.setSwingHand(false);
                    }
                });
            });

            GrabEntityAbility.getGrabberSafe(localPlayer).flatMap(entity -> entity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                    .ifPresent(ability -> {
                        if (ability.grabbedHasControl) return;

                        event.setCanceled(true);
                        event.setSwingHand(false);
                    });
        }
    }

    /**
     * This function needs to be static
     * @param event
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        ChangedClient.registerReloadListeners(event::registerReloadListener);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSetScreen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen) {
            if (GlUtil.getOpenGLVersion().contains("Mesa")) {
                Changed.LOGGER.warn("Mesa graphics driver detected, certain visual features will be disabled");
                Changed.config.client.renderDripParticlesWithNormal.set(false);
            }

            if (Changed.config.client.showContentWarning.get()) {
                // Comment this line out to disable the content warning screen
                event.setNewScreen(new ContentWarningScreen());
            }
        }
    }

    public static <T extends LivingEntity, M extends EntityModel<T>, R extends LivingEntityRenderer<T, M>> void addLatexParticles(EntityRenderersEvent.AddLayers event, EntityType<T> entityType) {
        R renderer = event.getRenderer(entityType);
        if (renderer != null)
            renderer.addLayer(new LatexParticlesLayer<>(renderer, renderer.getModel()));
        else
            Changed.LOGGER.warn("Renderer not present for {} in AddLayers event", entityType);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void addChangedLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().stream().map(name -> Pair.of(name, event.getSkin(name))).forEach(pair -> {
            if (pair.getSecond() instanceof PlayerRenderer renderer) {
                renderer.addLayer(new DarkLatexMaskLayer<>(renderer, event.getEntityModels()));
                renderer.addLayer(new GasMaskLayer<>(renderer, event.getEntityModels()));
            }
        });
        addLatexParticles(event, EntityType.BEE);
        addLatexParticles(event, EntityType.RABBIT);
    }

    @OnlyIn(Dist.CLIENT)
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ForgeEventHandler {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onNameFormat(RenderNameTagEvent event) {
            if (event.getEntity() instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null) {
                if (!Changed.config.server.showTFNametags.get()) {
                    event.setCanRender(TriState.FALSE);
                    return;
                }

                var variant = ProcessTransfur.getPlayerTransfurVariant(changedEntity.getUnderlyingPlayer());
                if (variant != null && variant.isTransfurring()) {
                    event.setCanRender(TriState.FALSE);
                    return;
                }

            }
        }

        @SubscribeEvent
        public static void onChangedVariant(ProcessTransfur.EntityVariantAssigned.ChangedVariant event) {
            if (event.livingEntity.level().isClientSide)
                return;

            if (event.oldVariant == event.newVariant || event.context == null)
                return;

            final int duration = event.livingEntity.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ?
                    (int)(event.context.cause().getDuration() * 20) : 40;
            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4, false, false));

            if (event.oldVariant != null || event.livingEntity.tickCount < 20)
                return; // Only do effect if player was human

            if (event.livingEntity instanceof Player player && player.isCreative())
                return; // Don't do effect if player is creative mode

            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, false));
            if (!event.newVariant.getEntityType().is(ChangedTags.EntityTypes.LATEX))
                return; // Only do blindness if variant is goo

            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 1, false, false));
        }

        @SubscribeEvent
        public static void addLatexCoverSectionGeometry(AddSectionGeometryEvent event) {
            var level = event.getLevel();
            var sectionOrigin = event.getSectionOrigin();
            var states = new HashMap<BlockPos, LatexCoverState>();

            for (BlockPos blockPos : BlockPos.betweenClosed(sectionOrigin, sectionOrigin.offset(15, 15, 15))) {
                var coverState = LatexCoverState.getAt(level, blockPos);
                if (coverState.isPresent())
                    states.put(blockPos.immutable(), coverState);
            }

            if (states.isEmpty())
                return;

            event.addRenderer(context -> {
                var random = RandomSource.create();
                var getter = LatexCoverGetter.extend(context.getRegion(), blockPos ->
                        states.getOrDefault(blockPos.immutable(), ChangedLatexTypes.NONE.get().defaultCoverState()));

                states.forEach((blockPos, coverState) -> {
                    var renderType = ChangedClient.latexCoveredBlocksRenderer.get().getRenderType(coverState);
                    var buffer = context.getOrCreateChunkBuffer(renderType);
                    ChangedClient.latexCoveredBlocksRenderer.get().tesselate(
                            context.getRegion(),
                            getter,
                            blockPos,
                            buffer,
                            context.getRegion().getBlockState(blockPos),
                            coverState,
                            random);
                });
            });
        }

        @SubscribeEvent
        public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
            if (event.getTarget() instanceof LatexCoverHitResult)
                event.setCanceled(true);

            final var level = Minecraft.getInstance().level;
            final var getter = LatexCoverGetter.wrap(level);
            final var blockPos = event.getTarget().getBlockPos();

            LatexCoverState state = LatexCoverState.getAt(level, blockPos);
            if (!state.isAir() && level.getWorldBorder().isWithinBounds(blockPos)) {
                VertexConsumer bufferBuilder = event.getMultiBufferSource().getBuffer(RenderType.lines());
                Vec3 vec3 = event.getCamera().getPosition();
                double d0 = vec3.x();
                double d1 = vec3.y();
                double d2 = vec3.z();

                LevelRenderer.renderVoxelShape(event.getPoseStack(), bufferBuilder, state.getShape(getter, blockPos, CollisionContext.of(event.getCamera().getEntity())),
                        (double)blockPos.getX() - d0, (double)blockPos.getY() - d1, (double)blockPos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F, false);
            }
        }
    }
}
