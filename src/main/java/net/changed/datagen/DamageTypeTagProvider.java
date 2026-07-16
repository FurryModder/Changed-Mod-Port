package net.changed.datagen;

import net.changed.Changed;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagProvider extends DamageTypeTagsProvider {
    public DamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookup, Changed.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider lookup) {
    }
}