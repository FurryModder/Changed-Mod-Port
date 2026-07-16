package net.changed.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TagUtil {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void replace(CompoundTag from, CompoundTag target) {
        HashSet<String> oldKeys = new HashSet<>(target.getAllKeys());
        oldKeys.forEach(target::remove);
        from.getAllKeys().forEach(key -> {
            target.put(key, Objects.requireNonNull(from.get(key)));
        });
    }

    public static String getStringOrDefault(@Nullable CompoundTag tag, String name, String _default) {
        return tag != null && tag.contains(name) ? tag.getString(name) : _default;
    }

    public static String getStringOrDefault(@Nullable ItemStack item, String name, String _default) {
        return item != null ? getStringOrDefault(getCustomData(item), name, _default) : _default;
    }

    public static boolean getBooleanOrDefault(@Nullable CompoundTag tag, String name, boolean _default) {
        return tag != null && tag.contains(name) ? tag.getBoolean(name) : _default;
    }

    public static boolean getBooleanOrDefault(@Nullable ItemStack item, String name, boolean _default) {
        return item != null ? getBooleanOrDefault(getCustomData(item), name, _default) : _default;
    }

    @Nullable
    public static CompoundTag getCustomData(@Nullable ItemStack stack) {
        if (stack == null)
            return null;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : null;
    }

    public static CompoundTag getOrCreateCustomData(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        return tag != null ? tag : new CompoundTag();
    }

    public static void setCustomData(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    public static void updateCustomData(ItemStack stack, Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public static BlockPos getBlockPos(CompoundTag cTag, String name) {
        Tag tag = cTag.get(name);
        if (tag instanceof IntArrayTag intArrayTag) {
            int[] values = intArrayTag.getAsIntArray();
            if (values.length >= 3)
                return new BlockPos(values[0], values[1], values[2]);
        }

        ListTag listTag = (ListTag)tag;
        return new BlockPos(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2));
    }

    public static ResourceLocation getResourceLocation(CompoundTag cTag, String name) {
        return ResourceLocation.tryParse(cTag.getString(name));
    }

    public static ResourceLocation getResourceLocation(CompoundTag cTag, String name, Function<String, String> fixer) {
        if (cTag.getString(name).isEmpty())
            return null;
        else
            return ResourceLocation.tryParse(fixer.apply(cTag.getString(name)));
    }

    public static void putBlockPos(CompoundTag cTag, String name, BlockPos pos) {
        ListTag tag = new ListTag();
        tag.add(IntTag.valueOf(pos.getX()));
        tag.add(IntTag.valueOf(pos.getY()));
        tag.add(IntTag.valueOf(pos.getZ()));
        cTag.put(name, tag);
    }

    public static void putResourceLocation(CompoundTag cTag, String name, ResourceLocation location) {
        if (location != null)
            cTag.putString(name, location.toString());
    }

    public static <T, V> CompoundTag createMap(Map<T, V> map, TriConsumer<T, V, CompoundTag> consumer) {
        CompoundTag tag = new CompoundTag();
        map.forEach((key, value) -> {
            if (key == null) {
                LOGGER.warn("Encountered null attribute, skipping");
                return;
            }

            consumer.accept(key, value, tag);
        });
        return tag;
    }

    public static void readMap(CompoundTag mapTag, BiConsumer<String, CompoundTag> consumer) {
        mapTag.getAllKeys().forEach(key -> consumer.accept(key, mapTag));
    }
}
