package net.changed.network.legacy;

import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;

public final class NetworkHooks {
    private NetworkHooks() {}

    public static void openScreen(ServerPlayer player, MenuProvider provider) {
        player.openMenu(provider);
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
        player.openMenu(provider, buffer -> extraDataWriter.accept((RegistryFriendlyByteBuf)buffer));
    }

    public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
        return new ClientboundAddEntityPacket(entity, 0, entity.blockPosition());
    }

    public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity, net.minecraft.server.level.ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(entity, serverEntity);
    }
}
