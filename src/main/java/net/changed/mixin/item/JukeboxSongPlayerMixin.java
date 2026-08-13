package net.changed.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.changed.init.ChangedJukeboxSongs;
import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(JukeboxSongPlayer.class)
public abstract class JukeboxSongPlayerMixin {
    @Shadow @Nullable private Holder<JukeboxSong> song;

    @WrapOperation(method = "setSongWithoutPlaying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSong;hasFinished(J)Z"))
    private boolean changed$loadLoopedSong(JukeboxSong instance, long ticksSinceSongStarted, Operation<Boolean> original,
                                           @Local(argsOnly = true) Holder<JukeboxSong> song) {
        return !ChangedJukeboxSongs.isLooped(song) && original.call(instance, ticksSinceSongStarted);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSong;hasFinished(J)Z"))
    private boolean changed$keepLoopedSongPlaying(JukeboxSong instance, long ticksSinceSongStarted, Operation<Boolean> original) {
        return !ChangedJukeboxSongs.isLooped(this.song) && original.call(instance, ticksSinceSongStarted);
    }
}
