package net.changed.network.packet;

import net.changed.client.gui.InfuserScreen;
import net.changed.util.UniversalDist;
import net.changed.world.inventory.GuiStateProvider;
import net.changed.world.inventory.InfuserMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.changed.network.legacy.NetworkEvent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SyncSwitchPacket implements ChangedPacket {
    private final int containerId;
    private final boolean state;
    private final ResourceLocation name;

    public SyncSwitchPacket(InfuserScreen.Switch switchWidget) {
        containerId = switchWidget.containerScreen.getMenu().containerId;
        state = switchWidget.selected();
        name = switchWidget.getName();
    }

    public SyncSwitchPacket(FriendlyByteBuf buffer) {
        containerId = buffer.readInt();
        state = buffer.readBoolean();
        name = buffer.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(containerId);
        buffer.writeBoolean(state);
        buffer.writeResourceLocation(name);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        final var player = Objects.requireNonNullElseGet(context.getSender(), UniversalDist::getLocalPlayer);
        if (player.containerMenu.containerId == containerId && player.containerMenu instanceof GuiStateProvider menu) {
            menu.getState().put(name.toString(), state);
            if (player.containerMenu instanceof InfuserMenu infuserMenu) {
                infuserMenu.slotsChanged(null);
            }
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.failedFuture(new IllegalStateException());
    }

    public static SyncSwitchPacket of(InfuserScreen.Switch switchWidget) {
        return new SyncSwitchPacket(switchWidget);
    }
}
