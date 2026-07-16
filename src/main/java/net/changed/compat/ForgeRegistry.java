package net.changed.compat;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class ForgeRegistry<T> implements IForgeRegistry<T> {
    private final Registry<T> delegate;
    private final Map<ResourceLocation, Object> slaveMaps = new IdentityHashMap<>();

    public ForgeRegistry(Registry<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> ForgeRegistry<T> of(Registry<T> registry) {
        return registry instanceof ForgeRegistry<T> forgeRegistry ? forgeRegistry : new ForgeRegistry<>(registry);
    }

    public static <T> ForgeRegistry<T> empty(ResourceKey<? extends Registry<T>> key) {
        return of(new MappedRegistry<>(key, Lifecycle.stable()));
    }

    public ForgeRegistry<T> get() {
        return this;
    }

    public <M> M getSlaveMap(ResourceLocation key, Class<M> type) {
        return type.cast(slaveMaps.get(key));
    }

    public void setSlaveMap(ResourceLocation key, Object value) {
        slaveMaps.put(key, value);
    }

    @Override
    public TagManager<T> tags() {
        return new TagManager<>(delegate);
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return delegate.key();
    }

    @Override
    public @Nullable ResourceLocation getKey(T value) {
        return delegate.getKey(value);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T value) {
        return delegate.getResourceKey(value);
    }

    @Override
    public int getId(@Nullable T value) {
        return delegate.getId(value);
    }

    @Override
    public @Nullable T byId(int id) {
        return delegate.byId(id);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public @Nullable T get(@Nullable ResourceKey<T> key) {
        return delegate.get(key);
    }

    @Override
    public @Nullable T get(@Nullable ResourceLocation name) {
        return delegate.get(name);
    }

    @Override
    public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> key) {
        return delegate.registrationInfo(key);
    }

    @Override
    public Lifecycle registryLifecycle() {
        return delegate.registryLifecycle();
    }

    @Override
    public Optional<Holder.Reference<T>> getAny() {
        return delegate.getAny();
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return delegate.keySet();
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return delegate.registryKeySet();
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource random) {
        return delegate.getRandom(random);
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return delegate.containsKey(name);
    }

    @Override
    public boolean containsKey(ResourceKey<T> key) {
        return delegate.containsKey(key);
    }

    @Override
    public Registry<T> freeze() {
        return delegate.freeze();
    }

    @Override
    public Holder.Reference<T> createIntrusiveHolder(T value) {
        return delegate.createIntrusiveHolder(value);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(int id) {
        return delegate.getHolder(id);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceLocation location) {
        return delegate.getHolder(location);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key) {
        return delegate.getHolder(key);
    }

    @Override
    public Holder<T> wrapAsHolder(T value) {
        return delegate.wrapAsHolder(value);
    }

    @Override
    public Stream<Holder.Reference<T>> holders() {
        return delegate.holders();
    }

    @Override
    public Optional<HolderSet.Named<T>> getTag(TagKey<T> key) {
        return delegate.getTag(key);
    }

    @Override
    public HolderSet.Named<T> getOrCreateTag(TagKey<T> key) {
        return delegate.getOrCreateTag(key);
    }

    @Override
    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return delegate.getTags();
    }

    @Override
    public Stream<TagKey<T>> getTagNames() {
        return delegate.getTagNames();
    }

    @Override
    public void resetTags() {
        delegate.resetTags();
    }

    @Override
    public void bindTags(Map<TagKey<T>, List<Holder<T>>> tagMap) {
        delegate.bindTags(tagMap);
    }

    @Override
    public HolderOwner<T> holderOwner() {
        return delegate.holderOwner();
    }

    @Override
    public HolderLookup.RegistryLookup<T> asLookup() {
        return delegate.asLookup();
    }

    @Override
    public boolean doesSync() {
        return delegate.doesSync();
    }

    @Override
    public int getMaxId() {
        return delegate.getMaxId();
    }

    @Override
    public void addCallback(RegistryCallback<T> callback) {
        delegate.addCallback(callback);
    }

    @Override
    public void addAlias(ResourceLocation from, ResourceLocation to) {
        delegate.addAlias(from, to);
    }

    @Override
    public ResourceLocation resolve(ResourceLocation name) {
        return delegate.resolve(name);
    }

    @Override
    public ResourceKey<T> resolve(ResourceKey<T> key) {
        return delegate.resolve(key);
    }

    @Override
    public int getId(ResourceKey<T> key) {
        return delegate.getId(key);
    }

    @Override
    public int getId(ResourceLocation name) {
        return delegate.getId(name);
    }

    @Override
    public boolean containsValue(T value) {
        return delegate.containsValue(value);
    }

    @Override
    public <A> @Nullable A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        return delegate.getData(type, key);
    }

    @Override
    public <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
        return delegate.getDataMap(type);
    }

    public static class TagManager<T> {
        private final Registry<T> registry;

        TagManager(Registry<T> registry) {
            this.registry = registry;
        }

        public Stream<TagKey<T>> getTagNames() {
            return registry.getTagNames();
        }

        public List<T> getTag(TagKey<T> tag) {
            return registry.getTag(tag)
                    .map(named -> named.stream().map(Holder::value).toList())
                    .orElseGet(List::of);
        }
    }
}
