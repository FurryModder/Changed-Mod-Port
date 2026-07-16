package net.changed.world.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.changed.Changed;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.block.StasisChamber;
import net.changed.block.entity.StasisChamberBlockEntity;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.*;
import net.changed.item.FluidCanister;
import net.changed.network.packet.StasisChamberControlPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StasisChamberMenu extends AbstractContainerMenu implements UpdateableMenu {
    public static final int MAX_WAIT_DURATION = 20 * 120;
    private static final int CUSTOM_SLOT_COUNT = 2;
    private static final int DATA_SLOT_COUNT = 3;

    public final StasisChamberBlockEntity blockEntity;
    public final boolean startOnModificationsScreen;
    public final Container container;
    public final ContainerData data;
    private @NotNull Player accessor;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public boolean hideSlots = false;

    private static final ResourceLocation EMPTY_SYRINGE_SLOT = Changed.modResource("item/empty_slot_syringe");

    public StasisChamberMenu(int id, Inventory inventory, FriendlyByteBuf extra) {
        super(ChangedMenus.STASIS_CHAMBER.get(), id);
        this.accessor = inventory.player;
        this.data = new SimpleContainerData(DATA_SLOT_COUNT);

        if (extra == null) {
            this.blockEntity = null;
            this.startOnModificationsScreen = false;
            this.container = new SimpleContainer(2);
            this.container.startOpen(inventory.player);
            this.addDataSlots(this.data);
            this.createSlots(inventory);
            return;
        }

        this.blockEntity = inventory.player.level().getBlockEntity(extra.readBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);
        this.startOnModificationsScreen = extra.readBoolean();
        this.container = blockEntity != null ? blockEntity : new SimpleContainer(CUSTOM_SLOT_COUNT);
        this.container.startOpen(inventory.player);
        this.addDataSlots(this.data);
        this.createSlots(inventory);
    }

    public StasisChamberMenu(int id, Inventory inventory, @Nullable StasisChamberBlockEntity blockEntity, ContainerData dataAccess) {
        super(ChangedMenus.STASIS_CHAMBER.get(), id);
        this.accessor = inventory.player;
        this.data = dataAccess;
        this.blockEntity = blockEntity;
        this.startOnModificationsScreen = false;
        this.container = blockEntity != null ? blockEntity : new SimpleContainer(2);
        this.container.startOpen(inventory.player);
        this.addDataSlots(this.data);
        this.createSlots(inventory);
    }

    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private static class HideableSlot extends Slot {
        private final Supplier<Boolean> shouldHide;

        public HideableSlot(Container container, int index, int x, int y, Supplier<Boolean> shouldHide) {
            super(container, index, x, y);
            this.shouldHide = shouldHide;
        }

        @Override
        public boolean isActive() {
            return super.isActive() && !shouldHide.get();
        }
    }

    protected void createSlots(Inventory inv) {
        // Transfur variant slot
        this.customSlots.put(0, this.addSlot(new Slot(this.container, 0, 174, 142) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ChangedItems.LATEX_SYRINGE.get() == stack.getItem();
            }

            @Override
            public boolean mayPickup(Player player) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SYRINGE_SLOT);
            }
        }));
        // Chamber fluid slot
        this.customSlots.put(1, this.addSlot(new Slot(this.container, 1, 196, 142) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof FluidCanister fluidCanister && fluidCanister.getFluid() != null;
            }

            @Override
            public boolean mayPickup(Player player) {
                return blockEntity == null || blockEntity.isDrained();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        }));

        for (int slotY = 0; slotY < 3; ++slotY)
            for (int slotX = 0; slotX < 9; ++slotX)
                this.addSlot(new HideableSlot(inv, slotX + (slotY + 1) * 9, 0 + 8 + slotX * 18, 0 + 84 + slotY * 18, () -> hideSlots));
        for (int slotY = 0; slotY < 9; ++slotY)
            this.addSlot(new HideableSlot(inv, slotY, 0 + 8 + slotY * 18, 0 + 142, () -> hideSlots));
    }

    public Slot getCustomSlot(int customSlotIndex) {
        return customSlots.get(customSlotIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < CUSTOM_SLOT_COUNT) {
                if (!this.moveItemStackTo(itemstack1, CUSTOM_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (this.slots.get(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.slots.get(1).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 1, CUSTOM_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (index < CUSTOM_SLOT_COUNT + 27) {
                    if (!this.moveItemStackTo(itemstack1, CUSTOM_SLOT_COUNT + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, CUSTOM_SLOT_COUNT, CUSTOM_SLOT_COUNT + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }
        if (p_38904_.isStackable()) {
            while (!p_38904_.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(p_38904_) && !itemstack.isEmpty() && ItemStack.isSameItemSameComponents(p_38904_, itemstack)) {
                    int j = itemstack.getCount() + p_38904_.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
                    if (j <= maxSize) {
                        p_38904_.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        p_38904_.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!p_38904_.isEmpty()) {
            if (p_38907_) {
                i = p_38906_ - 1;
            } else {
                i = p_38905_;
            }
            while (true) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
                    if (p_38904_.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(p_38904_.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(p_38904_.split(p_38904_.getCount()));
                    }
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    public double getWaitDurationPercent() {
        return ((double)getSyncedWaitDuration()) / (double)MAX_WAIT_DURATION; // Ticks to percent
    }

    public String getWaitDurationSeconds(double percent) {
        return String.format("%.2f", Math.max(Math.round(percent * MAX_WAIT_DURATION), 0) * 0.05);
    }

    public float getFluidLevel(float partialTick) {
        return getSyncedFluidLevel();
    }

    public Optional<LivingEntity> getChamberedEntity() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getChamberedEntity();
    }

    public Optional<IAbstractChangedEntity> getChamberedLatex() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getChamberedLatex();
    }

    public Optional<StasisChamberBlockEntity.ScheduledCommand> getCurrentCommand() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getCurrentCommand();
    }

    public ImmutableList<StasisChamberBlockEntity.ScheduledCommand> getScheduledCommands() {
        if (blockEntity == null)
            return ImmutableList.of();
        return blockEntity.getScheduledCommands();
    }

    public Optional<TransfurVariant<?>> getConfiguredTransfurVariant() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getConfiguredTransfurVariant();
    }

    public boolean isChamberOpen() {
        if (blockEntity == null)
            return false;
        return blockEntity.getBlockState().getValue(StasisChamber.OPEN);
    }

    public float getChamberFillLevel(float partialTick) {
        return getFluidLevel(partialTick);
    }

    private float getSyncedFluidLevel() {
        return Math.max(0.0f, Math.min(1.0f, this.data.get(0) * 0.001f));
    }

    private int getSyncedWaitDuration() {
        return Math.max(0, Math.min(MAX_WAIT_DURATION, this.data.get(1)));
    }

    public boolean isStabilized() {
        return this.data.get(2) != 0;
    }

    public Optional<Fluid> getChamberFillFluid() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getFluidType();
    }

    public boolean openChamber() {
        if (blockEntity == null)
            return false;
        if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
            chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
        }

        return blockEntity.getBlockState().getValue(StasisChamber.OPEN);
    }

    public boolean closeChamber() {
        if (blockEntity == null)
            return false;
        if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
            chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
        }

        return !blockEntity.getBlockState().getValue(StasisChamber.OPEN);
    }

    private void sendControl(CompoundTag tag) {
        if (blockEntity != null && accessor.level().isClientSide) {
            Changed.PACKET_HANDLER.sendToServer(new StasisChamberControlPacket(blockEntity.getBlockPos(), tag));
        } else {
            this.setDirty(tag);
        }
    }

    @Override
    public int getId() {
        return containerId;
    }

    @Override
    public Player getPlayer() {
        return accessor;
    }

    public enum Command {
        NOOP(menu -> {}),
        OPEN_DOOR(StasisChamberMenu::openChamber),
        CLOSE_DOOR(StasisChamberMenu::closeChamber);

        private final Consumer<StasisChamberMenu> handler;

        Command(Consumer<StasisChamberMenu> handler) {
            this.handler = handler;
        }

        public void handle(StasisChamberMenu menu) {
            handler.accept(menu);
        }
    }

    public void sendSimpleCommand(Command command) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "command");
        tag.putInt("command", command.ordinal());
        this.sendControl(tag);
    }

    public void requestUpdate() {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "update");
        this.sendControl(tag);
    }

    public void inputProgram(String program) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "program");
        tag.putString("program", program);
        this.sendControl(tag);
    }

    public void inputModification(String key, Tag value) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "modify");

        CompoundTag modify = new CompoundTag();
        modify.put(key, value);

        tag.put("modify", modify);
        this.sendControl(tag);
    }

    public void inputWaitDuration(double percent) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "waitDuration");
        tag.putInt("waitDuration", (int)(percent * MAX_WAIT_DURATION));
        this.sendControl(tag);
    }

    public static void handleControl(StasisChamberBlockEntity blockEntity, CompoundTag payload, @Nullable ServerPlayer controller) {
        String control = payload.getString("control");
        if ("command".equals(control)) {
            int commandId = payload.getInt("command");
            if (commandId < 0 || commandId >= Command.values().length)
                return;

            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                if (Command.values()[commandId] == Command.OPEN_DOOR)
                    chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
                else if (Command.values()[commandId] == Command.CLOSE_DOOR)
                    chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
                blockEntity.markUpdated();
            }
        } else if ("program".equals(control)) {
            String program = payload.getString("program");
            blockEntity.inputProgram(program, controller);
        } else if ("modify".equals(control)) {
            if (payload.contains("modify"))
                blockEntity.applyModifications(payload.getCompound("modify"));
        } else if ("waitDuration".equals(control)) {
            if (payload.contains("waitDuration"))
                blockEntity.setWaitDuration(payload.getInt("waitDuration"), controller);
        } else if ("update".equals(control)) {
            blockEntity.markUpdated();
        }
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer controller) {
        if (receiver.isServer() && blockEntity != null) {
            handleControl(blockEntity, payload, controller);
        }
    }
}
