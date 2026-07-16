package net.changed.network.packet;

import net.changed.block.entity.GluBlockEntity;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.Zone;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ServerboundSetGluBlockPacket implements ChangedPacket {
    private final BlockPos pos;
    private final int size;
    private final boolean hasDoor;
    private final Zone zone;
    private final GluBlockEntity.JointType jointType;
    private final String finalState;

    public ServerboundSetGluBlockPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.size = buffer.readInt();
        this.hasDoor = buffer.readBoolean();
        this.zone = ChangedRegistry.FACILITY_ZONES.readRegistryObject(buffer);
        this.jointType = GluBlockEntity.JointType.byName(buffer.readUtf()).orElse(GluBlockEntity.JointType.ENTRANCE);
        this.finalState = buffer.readUtf();
    }

    public ServerboundSetGluBlockPacket(BlockPos pos, int size, boolean hasDoor, Zone zone, GluBlockEntity.JointType jointType, String finalState) {
        this.pos = pos;
        this.size = size;
        this.hasDoor = hasDoor;
        this.zone = zone;
        this.jointType = jointType;
        this.finalState = finalState;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(size);
        buffer.writeBoolean(hasDoor);
        ChangedRegistry.FACILITY_ZONES.writeRegistryObject(buffer, zone);
        buffer.writeUtf(jointType.getSerializedName());
        buffer.writeUtf(finalState);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            if (!context.getSender().canUseGameMasterBlocks()) {
                context.getSender().sendSystemMessage(Component.translatable("advMode.notAllowed"));
                return CompletableFuture.failedFuture(new IllegalArgumentException("Sender cannot use game master blocks"));
            }

            return levelFuture.thenAcceptAsync(level -> {
                var blockState = level.getBlockState(pos);
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof GluBlockEntity gluBlockEntity) {
                    gluBlockEntity.setSize(size);
                    gluBlockEntity.setHasDoor(hasDoor);
                    gluBlockEntity.setZone(zone);
                    gluBlockEntity.setJointType(jointType);
                    gluBlockEntity.setFinalState(finalState);
                    gluBlockEntity.setChanged();
                    level.sendBlockUpdated(pos, blockState, blockState, 3);
                }

                else {
                    throw new IllegalArgumentException("Specified block is not a Glu block");
                }
            }, sidedExecutor);
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.SERVER));
    }
}
