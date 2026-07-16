package net.changed.world.inventory;

import net.changed.Changed;
import net.changed.network.packet.MenuUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.changed.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public interface UpdateableMenu {
    default void setDirty(CompoundTag tag) {
        Player player = getPlayer();

        if (player.level().isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new MenuUpdatePacket(this.getId(), tag));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new MenuUpdatePacket(this.getId(), tag));
    }

    int getId();
    Player getPlayer();

    void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin);
}
