package net.changed.mixin.server;

import net.changed.Changed;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLifecycleHooks.class)
public abstract class ServerLifecycleHooksMixin {
    @Inject(method = "handleServerAboutToStart", at = @At("HEAD"))
    private static void changed$ensureWorldLocalServerConfig(MinecraftServer server, CallbackInfo ci) {
        if (Changed.config != null) {
            Changed.config.ensureWorldLocalServerConfig(server);
        }
    }
}
