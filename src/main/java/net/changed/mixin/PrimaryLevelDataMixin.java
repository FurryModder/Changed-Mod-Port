package net.changed.mixin;

import net.changed.Changed;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin {
    @Shadow @Final private int version;

    @Shadow @Final @Nullable private CompoundTag loadedPlayerTag;

    @Inject(method = "createTag", at = @At("HEAD"))
    private void updateChangedTag(RegistryAccess registries, @Nullable CompoundTag hostPlayerNBT, CallbackInfoReturnable<CompoundTag> callback) {
        if (this.version >= SharedConstants.getCurrentVersion().getDataVersion().getVersion() && Changed.dataFixer != null)
            Changed.dataFixer.updateCompoundTag(DataFixTypes.PLAYER, hostPlayerNBT == null ? this.loadedPlayerTag : hostPlayerNBT);
    }

    // FORGE

    @Inject(method = "hasConfirmedExperimentalWarning", at = @At("RETURN"), cancellable = true, remap = false)
    public void hasConfirmedExperimentalWarning(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(true);
    }
}
