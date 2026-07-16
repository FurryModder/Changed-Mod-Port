package net.changed.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.changed.Changed;
import net.changed.ability.AbstractAbility;
import net.changed.ability.tree.AbilityTree;
import net.changed.ability.tree.condition.AbstractCondition;
import net.changed.client.latexparticles.LatexParticleType;
import net.changed.data.AccessorySlotType;
import net.changed.entity.HairStyle;
import net.changed.entity.PlayerMover;
import net.changed.entity.decoration.WallSignVariant;
import net.changed.entity.latex.LatexType;
import net.changed.entity.variant.TransfurVariant;
import net.changed.entity.animation.AnimationEvent;
import net.changed.compat.ForgeRegistry;
import net.changed.compat.IForgeRegistry;
import net.changed.world.LatexCoverState;
import net.changed.world.features.structures.facility.FacilityPieceEvent;
import net.changed.world.features.structures.facility.types.PieceType;
import net.changed.world.features.structures.facility.Zone;
import net.minecraft.core.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public abstract class ChangedRegistry<T> implements Registry<T> {
    private static final Logger LOGGER = LogManager.getLogger(ChangedRegistry.class);

    private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
    private static final HashMap<ResourceKey<Registry<?>>, Supplier<IForgeRegistry<?>>> REGISTRY_HOLDERS = new HashMap<>();

    public static class RegistryHolder<T> implements Supplier<IForgeRegistry<T>> {
        protected final ResourceKey<Registry<T>> key;
        protected final IdMap<T> idMap = new IdMap<>() {
            @Override
            public int getId(T value) {
                return RegistryHolder.this.getID(value);
            }

            @Override
            public @org.jetbrains.annotations.Nullable T byId(int id) {
                return RegistryHolder.this.getValue(id);
            }

            @Override
            public int size() {
                return RegistryHolder.this.get().getValues().size();
            }

            @Override
            public @NotNull Iterator<T> iterator() {
                return RegistryHolder.this.get().getValues().iterator();
            }
        };
        protected final HolderLookup.RegistryLookup<T> lookup = new HolderLookup.RegistryLookup<>() {
            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return RegistryHolder.this.key;
            }

            @Override
            public Lifecycle registryLifecycle() {
                return Lifecycle.stable();
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                final var raw = getRaw();
                return raw.getKeys().stream()
                        .map(raw::getHolder)
                        .flatMap(Optional::stream)
                        .map(holder -> (Holder.Reference<T>)(Object)holder);
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return Stream.empty();
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> resourceKey) {
                return getRaw().getHolder(resourceKey).map(holder -> (Holder.Reference<T>)(Object)holder);
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
                return Optional.empty();
            }
        };

        public RegistryHolder(ResourceKey<Registry<T>> key) {
            this.key = key;
        }

        public ResourceLocation getKey(T value) {
            return get().getKey(value);
        }

        public Optional<ResourceLocation> getKeySafe(T value) {
            return Optional.ofNullable(get().getKey(value));
        }

        public @Nullable T getValue(ResourceLocation key) {
            return get().getValue(key);
        }

        public Set<ResourceLocation> getKeys() {
            return get().getKeys();
        }

        public int getID(T value) {
            return getRaw().getID(value);
        }

        public T getValue(int id) {
            return getRaw().getValue(id);
        }

        public Optional<Holder<T>> getHolder(T value) {
            return get().getHolder(value);
        }

        public Optional<Holder.Reference<T>> getHolder(ResourceLocation name) {
            return get().getHolder(name);
        }

        public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> name) {
            return get().getHolder(name);
        }

        public void writeRegistryObject(FriendlyByteBuf buffer, T value) {
            buffer.writeInt(getRaw().getID(value));
        }

        public T readRegistryObject(FriendlyByteBuf buffer) {
            return getRaw().getValue(buffer.readInt());
        }

        @Override
        public IForgeRegistry<T> get() {
            if (REGISTRY_HOLDERS.isEmpty())
                throw new IllegalStateException("Cannot access registries before creation");
            return (IForgeRegistry<T>) REGISTRY_HOLDERS.get(key).get();
        }

        public ForgeRegistry<T> getRaw() {
            if (REGISTRY_HOLDERS.isEmpty())
                throw new IllegalStateException("Cannot access registries before creation");
            return (ForgeRegistry<T>) REGISTRY_HOLDERS.get(key).get();
        }

        public DeferredRegister<T> createDeferred(String modId) {
            return DeferredRegister.create(key, modId);
        }

        public ResourceKey<T> createResourceKey(ResourceLocation resourceLocation) {
            return ResourceKey.create(key, resourceLocation);
        }

        public IdMap<T> asIdMap() {
            return idMap;
        }

        public HolderLookup<T> asLookup() {
            return lookup;
        }
    }

    // TODO rename registeries to be plural, and have modern names
    public static final RegistryHolder<TransfurVariant<?>> TRANSFUR_VARIANT = new RegistryHolder<TransfurVariant<?>>(registryKey("latex_variant"));
    public static final RegistryHolder<AbstractAbility<?>> ABILITY = new RegistryHolder<AbstractAbility<?>>(registryKey("ability"));
    public static final RegistryHolder<HairStyle> HAIR_STYLE = new RegistryHolder<HairStyle>(registryKey("hair_style"));
    public static final RegistryHolder<PlayerMover<?>> PLAYER_MOVER = new RegistryHolder<PlayerMover<?>>(registryKey("player_mover"));
    public static final RegistryHolder<LatexParticleType<?>> LATEX_PARTICLE_TYPE = new RegistryHolder<LatexParticleType<?>>(registryKey("latex_particle_type"));
    public static final RegistryHolder<AnimationEvent<?>> ANIMATION_EVENTS = new RegistryHolder<AnimationEvent<?>>(registryKey("animation_events"));
    public static final RegistryHolder<AccessorySlotType> ACCESSORY_SLOTS = new RegistryHolder<AccessorySlotType>(registryKey("accessory_slots"));
    public static final RegistryHolder<LatexType> LATEX_TYPE = new RegistryHolder<>(registryKey("latex_type"));
    public static final RegistryHolder<WallSignVariant> WALL_SIGN_VARIANT = new RegistryHolder<>(registryKey("wall_sign_variant"));

    public static final RegistryHolder<PieceType<?>> FACILITY_PIECE_TYPES = new RegistryHolder<>(registryKey("facility/piece_types"));
    public static final RegistryHolder<Zone> FACILITY_ZONES = new RegistryHolder<>(registryKey("facility/zones"));
    public static final RegistryHolder<FacilityPieceEvent> FACILITY_EVENTS = new RegistryHolder<>(registryKey("facility/events"));

    public static final RegistryHolder<Codec<? extends AbilityTree.NodeEffect>> ABILITY_NODE_EFFECTS = new RegistryHolder<>(registryKey("ability/node_effects"));
    public static final RegistryHolder<Codec<? extends AbstractCondition>> ABILITY_EFFECT_CONDITIONS = new RegistryHolder<>(registryKey("ability/effect_conditions"));

    private static class ClearableObjectIntIdentityMap<I> extends IdMapper<I> {
        void clear()
        {
            this.tToId.clear();
            this.idToT.clear();
            this.nextId = 0;
        }

        @SuppressWarnings("unused")
        void remove(I key)
        {
            boolean hadId = this.tToId.containsKey(key);
            int prev = this.tToId.removeInt(key);
            if (hadId) {
                this.idToT.set(prev, null);
            }
        }
    }

    @SubscribeEvent
    public static void onCreateRegistries(NewRegistryEvent event) {
        createRegistry(event, TRANSFUR_VARIANT.key, builder -> {
        }, null);
        createRegistry(event, ABILITY.key);
        createRegistry(event, HAIR_STYLE.key, builder -> {
        }, null);
        createRegistry(event, PLAYER_MOVER.key, builder -> {
        }, null);
        createRegistry(event, LATEX_PARTICLE_TYPE.key);
        createRegistry(event, ANIMATION_EVENTS.key);
        createRegistry(event, ACCESSORY_SLOTS.key);
        createRegistry(event, LATEX_TYPE.key, builder -> {
            builder.onClear((owner, stage) -> {
                ChangedLatexTypes.LATEXCOVERSTATE_BY_ID.clear();
            });
            builder.onBake(owner -> {
                ChangedLatexTypes.LATEXCOVERSTATE_BY_ID.clear();

                for (LatexType type : owner) {
                    for (LatexCoverState state : type.getStateDefinition().getPossibleStates()) {
                        ChangedLatexTypes.LATEXCOVERSTATE_BY_ID.add(state);
                        state.initCache();
                    }
                }
                DebugLevelSource.initValidStates();
            });
        }, null);
        createRegistry(event, WALL_SIGN_VARIANT.key);
        createRegistry(event, FACILITY_PIECE_TYPES.key);
        createRegistry(event, FACILITY_ZONES.key);
        createRegistry(event, FACILITY_EVENTS.key);
        createRegistry(event, ABILITY_NODE_EFFECTS.key);
        createRegistry(event, ABILITY_EFFECT_CONDITIONS.key);
    }

    private static <T> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key) {
        createRegistry(event, key, null, null);
    }

    private static <T> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key,
                                                                          @Nullable Consumer<RegistryBuilder<T>> additionalBuilder,
                                                                          @Nullable Consumer<IForgeRegistry<T>> onFill) {
        var builder = makeRegistry(key);
        if (additionalBuilder != null)
            additionalBuilder.accept(builder);
        ForgeRegistry<T> holder = ForgeRegistry.of(event.create(builder));
        if (onFill != null)
            onFill.accept(holder);
        REGISTRY_HOLDERS.put((ResourceKey)key, () -> holder);
        LOGGER.info("Created registry {}", key);
    }

    static <T> Class<T> c(Class<?> cls) { return (Class<T>)cls; }
    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key) {
        return new RegistryBuilder<T>(key).maxId(MAX_VARINT);
    }

    private ChangedRegistry() {}

    private static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(Changed.modResource(name));
    }
}
