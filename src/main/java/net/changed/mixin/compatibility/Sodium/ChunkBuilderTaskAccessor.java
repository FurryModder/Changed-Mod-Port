package net.changed.mixin.compatibility.Sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.changed.extension.RequiredMods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChunkBuilderTask.class, remap = false)
@RequiredMods("sodium")
public interface ChunkBuilderTaskAccessor {
    @Accessor("render")
    RenderSection changed$getRenderSection();
}
