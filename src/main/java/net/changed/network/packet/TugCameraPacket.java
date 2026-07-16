package net.changed.network.packet;

import net.changed.entity.PlayerDataExtension;
import net.changed.util.CameraUtil;
import net.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class TugCameraPacket implements ChangedPacket {
    private final CameraUtil.TugData tug;

    public TugCameraPacket(CameraUtil.TugData tug) {
        this.tug = tug;
    }

    public TugCameraPacket(FriendlyByteBuf buffer) {
        this.tug = CameraUtil.TugData.readFromBuffer(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.tug.writeToBuffer(buffer);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(UniversalDist.getLocalPlayer() instanceof PlayerDataExtension ext))
                    return;
                ext.setTugData(this.tug);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
