package net.changed.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.changed.entity.ChangedEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @WrapOperation(method = "dropLeash(Lnet/minecraft/world/entity/Entity;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;broadcast(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/protocol/Packet;)V"))
    private static void changed$dropLeashForUnderlying(ServerChunkCache instance, Entity entity, Packet<?> packet, Operation<Void> original) {
        changed$broadcastForUnderlying(instance, entity, packet, original);
    }

    @WrapOperation(method = "setLeashedTo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;broadcast(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/protocol/Packet;)V"))
    private static void changed$setLeashedForUnderlying(ServerChunkCache instance, Entity entity, Packet<?> packet, Operation<Void> original) {
        changed$broadcastForUnderlying(instance, entity, packet, original);
    }

    private static void changed$broadcastForUnderlying(ServerChunkCache instance, Entity entity, Packet<?> packet, Operation<Void> original) {
        if (entity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null) {
            instance.broadcastAndSend(changedEntity.getUnderlyingPlayer(), packet);
        } else {
            original.call(instance, entity, packet);
        }
    }
}
