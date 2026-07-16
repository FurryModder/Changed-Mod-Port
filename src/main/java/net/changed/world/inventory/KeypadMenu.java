package net.changed.world.inventory;

import net.changed.Changed;
import net.changed.block.entity.KeypadBlockEntity;
import net.changed.init.ChangedMenus;
import net.changed.network.packet.KeypadCodePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KeypadMenu extends AbstractContainerMenu implements UpdateableMenu {
    private final BlockPos blockPos;
    private final BlockState blockState;
    private final KeypadBlockEntity blockEntity;
    private final Level level;
    private final Player player;

    public KeypadMenu(int id, Inventory inventory, BlockPos pos, BlockState state, KeypadBlockEntity blockEntity) {
        super(ChangedMenus.KEYPAD.get(), id);
        this.level = inventory.player.level();
        this.player = inventory.player;
        this.blockPos = pos;
        this.blockState = state;
        this.blockEntity = blockEntity;
    }

    public KeypadMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.KEYPAD.get(), id);
        this.level = inventory.player.level();
        this.player = inventory.player;
        this.blockPos = extraData.readBlockPos();
        this.blockState = this.level.getBlockState(this.blockPos);
        this.blockEntity = this.level.getBlockEntity(this.blockPos, net.changed.init.ChangedBlockEntities.KEYPAD.get()).orElse(null);
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.isSpectator())
            return false;
        return blockEntity != null && !blockEntity.isRemoved();
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void useCode(List<Byte> attemptedCode) {
        if (this.level.isClientSide) {
            Changed.PACKET_HANDLER.sendToServer(new KeypadCodePacket(this.blockPos, attemptedCode));
        } else {
            if (blockEntity == null)
                return;
            blockEntity.useCode(attemptedCode);
        }
    }

    @Override
    public int getId() {
        return containerId;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver.isServer()) {
            try {
                var code = payload.getByteArray("Code");
                List<Byte> codeList = new ArrayList<>();
                for (byte b : code)
                    codeList.add(b);

                useCode(codeList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
