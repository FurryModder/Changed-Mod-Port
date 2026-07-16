package net.changed.item;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class ClothingState extends StateHolder<ClothingItem, ClothingState> {
    protected ClothingState(ClothingItem item, Reference2ObjectArrayMap<Property<?>, Comparable<?>> properties, MapCodec<ClothingState> codec) {
        super(item, properties, codec);
    }
}
