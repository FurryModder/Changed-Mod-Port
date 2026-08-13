package net.changed.init;

import net.changed.Changed;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public final class ChangedJukeboxSongs {
    private ChangedJukeboxSongs() {
    }

    public static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Changed.modResource(name));
    }

    public static boolean isLooped(Holder<JukeboxSong> song) {
        return song.unwrapKey()
                .map(ResourceKey::location)
                .map(location -> location.getNamespace().equals(Changed.MODID))
                .orElse(false);
    }
}
