package net.changed.compat;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface IForgeRegistry<T> extends Registry<T> {
    default T getValue(ResourceLocation key) {
        return get(key);
    }

    default T getValue(int id) {
        return byId(id);
    }

    default int getID(T value) {
        return getId(value);
    }

    default Set<ResourceLocation> getKeys() {
        return keySet();
    }

    default Collection<T> getValues() {
        return stream().toList();
    }

    default Set<Map.Entry<ResourceKey<T>, T>> getEntries() {
        return entrySet();
    }

    default Codec<T> getCodec() {
        return byNameCodec();
    }

    default ResourceKey<? extends Registry<T>> getRegistryKey() {
        return key();
    }

    default Optional<Holder<T>> getHolder(T value) {
        return getResourceKey(value).flatMap(this::getHolder).map(holder -> holder);
    }

    ForgeRegistry.TagManager<T> tags();
}
