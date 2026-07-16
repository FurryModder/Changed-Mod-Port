package net.changed.network.packet;

import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SetTransfurVariantDataPacket implements ChangedPacket {
    private final int id;
    @Nullable
    private final List<SynchedEntityData.DataValue<?>> packedItems;

    private static RegistryFriendlyByteBuf registryBuffer(FriendlyByteBuf buffer) {
        if (buffer instanceof RegistryFriendlyByteBuf registryFriendlyByteBuf)
            return registryFriendlyByteBuf;
        throw new IllegalArgumentException("Transfur variant entity data requires a registry-aware network buffer");
    }

    private static void pack(List<SynchedEntityData.DataValue<?>> p_253940_, RegistryFriendlyByteBuf p_253901_) {
        for(SynchedEntityData.DataValue<?> datavalue : p_253940_) {
            datavalue.write(p_253901_);
        }

        p_253901_.writeByte(255);
    }

    private static List<SynchedEntityData.DataValue<?>> unpack(RegistryFriendlyByteBuf p_253726_) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

        int i;
        while((i = p_253726_.readUnsignedByte()) != 255) {
            list.add(SynchedEntityData.DataValue.read(p_253726_, i));
        }

        return list;
    }

    public SetTransfurVariantDataPacket(int id, List<SynchedEntityData.DataValue<?>> data) {
        this.id = id;
        this.packedItems = data;
    }

    public SetTransfurVariantDataPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.packedItems = unpack(registryBuffer(buffer));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.id);
        pack(this.packedItems, registryBuffer(buffer));
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var player = level.getEntity(this.id);

                ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(player), variant -> {
                    if (packedItems == null)
                        return;
                    variant.getChangedEntity().getEntityData().assignValues(packedItems);
                });
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }

    public int getId() {
        return this.id;
    }
}
