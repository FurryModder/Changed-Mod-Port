package net.changed;

import net.changed.ability.tree.AbilityTrees;
import net.changed.client.*;
import net.changed.client.latexparticles.LatexParticleType;
import net.changed.data.BuiltinRepositorySource;
import net.changed.entity.AccessoryEntities;
import net.changed.entity.HairStyle;
import net.changed.entity.PlayerMover;
import net.changed.extension.ChangedCompatibility;
import net.changed.init.*;
import net.changed.network.ChangedPackets;
import net.changed.network.packet.ChangedPacket;
import net.changed.network.syncher.ChangedEntityDataSerializers;
import net.changed.process.ProcessTransfur;
import net.changed.world.ChangedDataFixer;
import net.changed.world.features.structures.FacilityPieces;
import net.changed.world.features.structures.facility.FacilityZoneEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.changed.compat.DistExecutor;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.changed.network.legacy.NetworkEvent;
import net.changed.network.legacy.NetworkRegistry;
import net.changed.network.legacy.SimpleChannel;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Changed.MODID)
public class Changed {
    private static Changed instance;
    public static Changed getInstance() { return instance; }

    public static final String MODID = "changed";

    public static final Logger LOGGER = LogManager.getLogger(Changed.class);
    public static EventHandlerClient eventHandlerClient;
    public static ChangedConfig config;
    public static ChangedDataFixer dataFixer;
    //private static IEventBus modEventBus;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(modResource("network"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static final ChangedPackets PACKETS = new ChangedPackets(PACKET_HANDLER);

    /**
     * This function is split out of the main function as a request by mod extension devs
     */
    private void registerLoadingEventListeners(IEventBus eventBus) {
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::customPacks);
        eventBus.addListener(this::registerPayloadHandlers);

        eventBus.addListener(ChangedAttributes::modifyEntityAttributes);
    }

    public Changed(IEventBus modEventBus, ModContainer modContainer) {
        //modEventBus = context.getModEventBus();
        config = new ChangedConfig(modContainer);
        ChangedGameRules.bootstrap();

        registerLoadingEventListeners(modEventBus);

        addEventListener(this::dataListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registerClientEventListeners(modEventBus));

        PACKETS.registerPackets();

        instance = this;

        HairStyle.REGISTRY.register(modEventBus);
        ChangedAbilities.REGISTRY.register(modEventBus);
        PlayerMover.REGISTRY.register(modEventBus);
        LatexParticleType.REGISTRY.register(modEventBus);
        ChangedLatexTypes.REGISTRY.register(modEventBus);

        ChangedAttributes.REGISTRY.register(modEventBus);
        ChangedRecipeSerializers.REGISTRY.register(modEventBus);
        ChangedEntityDataSerializers.REGISTRY.register(modEventBus);
        ChangedStructureTypes.REGISTRY.register(modEventBus);
        ChangedStructurePieceTypes.REGISTRY.register(modEventBus);
        ChangedLootItemFunctions.REGISTRY.register(modEventBus);
        ChangedCriteriaTriggers.REGISTRY.register(modEventBus);
        ChangedRecipeTypes.REGISTRY.register(modEventBus);
        ChangedTabs.REGISTRY.register(modEventBus);
        ChangedSounds.REGISTRY.register(modEventBus);
        ChangedParticles.REGISTRY.register(modEventBus);
        ChangedFeatures.REGISTRY_FEATURE.register(modEventBus);
        ChangedFeatures.REGISTRY_PROCESSOR.register(modEventBus);
        ChangedMenus.REGISTRY.register(modEventBus);
        ChangedEffects.REGISTRY.register(modEventBus);
        ChangedBlockEntities.REGISTRY.register(modEventBus);
        ChangedFluids.REGISTRY_TYPES.register(modEventBus);
        ChangedFluids.REGISTRY_FLUIDS.register(modEventBus);
        ChangedItems.REGISTRY.register(modEventBus);
        ChangedBlockStateProviders.REGISTRY.register(modEventBus);
        ChangedBlocks.REGISTRY.register(modEventBus);
        ChangedTransfurVariants.REGISTRY.register(modEventBus);
        ChangedEntities.REGISTRY.register(modEventBus);
        modEventBus.addListener(ChangedEntities::registerSpawnPlacements);
        modEventBus.addListener(ChangedEntities::registerAttributes);
        ChangedAnimationEvents.REGISTRY.register(modEventBus);
        ChangedAccessorySlots.REGISTRY.register(modEventBus);
        ChangedWallSigns.REGISTRY.register(modEventBus);
        ChangedFacilityPieceTypes.REGISTRY.register(modEventBus);
        ChangedFacilityZones.REGISTRY.register(modEventBus);
        ChangedAbilityTreeCodecs.NODE_EFFECT_REGISTRY.register(modEventBus);
        ChangedAbilityTreeCodecs.EFFECT_CONDITION_REGISTRY.register(modEventBus);

        // Our DFU references the above registries, so they need to be initialized before the DFU is created
        dataFixer = new ChangedDataFixer();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(ChangedBlocks.ORANGE_TREE_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ChangedBlocks.ORANGE_TREE_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ChangedItems.ORANGE.get(), 0.65F);
        });
        event.enqueueWork(ProcessTransfur::gatherMobAssimilations);
    }

    private void registerClientEventListeners(IEventBus eventBus) {
        eventHandlerClient = new EventHandlerClient();
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onRenderEntityPre);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onRenderHand);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onRenderFog);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onFogColors);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onRespawn);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onInputEvent);
        NeoForge.EVENT_BUS.addListener(eventHandlerClient::onSetScreen);
        eventBus.addListener(EventHandlerClient::onRegisterParticles);
        eventBus.addListener(EventHandlerClient::onRegisterModelRenderTypes);
        eventBus.addListener(EventHandlerClient::onRegisterShaders);
        eventBus.addListener(EventHandlerClient::onRegisterReloadListenerEvent);
        eventBus.addListener(EventHandlerClient::addChangedLayers);
        eventBus.addListener(RecipeCategories::registerCategories);
        eventBus.addListener(ChangedOverlays::registerOverlays);
        eventBus.addListener(ChangedClient::onBlockColorsInit);
        eventBus.addListener(ChangedClient::onItemColorsInit);
        eventBus.addListener(ChangedClient::onClientFinishSetup);
        eventBus.addListener(AbilityRenderer::onRegisterModels);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ChangedClient.registerEventListeners();
    }

    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        PACKET_HANDLER.registerPayloadHandlers(event);
    }

    private void dataListeners(final AddReloadListenerEvent event) {
        event.addListener(ChangedFusions.INSTANCE);
        event.addListener(AccessoryEntities.INSTANCE);
        event.addListener(FacilityPieces.INSTANCE);
        event.addListener(FacilityZoneEntities.INSTANCE);
        event.addListener(AbilityTrees.INSTANCE);
        ChangedCompatibility.addDataListeners(event);
    }

    private void customPacks(final AddPackFindersEvent event) {
        try {
            switch (event.getPackType()) {
                case CLIENT_RESOURCES, SERVER_DATA ->
                        event.addRepositorySource(new BuiltinRepositorySource(event.getPackType(), MODID));
                default -> {}
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public static ResourceLocation modResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static String modResourceStr(String path) {
        return MODID + ":" + path;
    }

    public static <T extends Event & IModBusEvent> void postModLoadingEvent(T event) {
        ModLoader.postEvent(event);
    }

    public static <T extends Event> void addEventListener(Consumer<T> listener) {
        NeoForge.EVENT_BUS.addListener(listener);
    }

    public static <T extends Event> boolean postModEvent(T event) {
        NeoForge.EVENT_BUS.post(event);
        return event instanceof ICancellableEvent cancellableEvent && cancellableEvent.isCanceled();
    }
}
