package net.changed.block.entity;

import com.google.common.collect.ImmutableList;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.block.StasisChamber;
import net.changed.entity.*;
import net.changed.entity.ai.ImmediateTransfurDecision;
import net.changed.entity.animation.StasisAnimationParameters;
import net.changed.entity.beast.CustomLatexEntity;
import net.changed.entity.variant.TransfurVariant;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.init.*;
import net.changed.item.FluidCanister;
import net.changed.item.Syringe;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.changed.world.inventory.StasisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.AABB;
import net.changed.network.legacy.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

public class StasisChamberBlockEntity extends BaseContainerBlockEntity implements SeatableBlockEntity, StackedContentsCompatible {
    private SeatEntity entityHolder; // Track single entity when active
    private float fluidLevel = 0.0f; // Allows chamber to fill up with fluid
    private float fluidLevelO = 0.0f;
    private final List<ScheduledCommand> scheduledCommands = new ArrayList<>();
    private @Nullable ScheduledCommand currentCommand = null;
    private LivingEntity cachedEntity;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
            //ChestBlockEntity.playSound(level, blockPos, blockState, SoundEvents.CHEST_OPEN);
        }

        protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
            //ChestBlockEntity.playSound(level, blockPos, blockState, SoundEvents.CHEST_CLOSE);
        }

        protected void openerCountChanged(Level level, BlockPos blockPos, BlockState blockState, int p_155364_, int count) {
            //ChestBlockEntity.this.signalOpenCount(level, blockPos, blockState, p_155364_, count);
        }

        protected boolean isOwnContainer(Player player) {
            if (!(player.containerMenu instanceof StasisChamberMenu)) {
                return false;
            } else {
                if (player.containerMenu instanceof StasisChamberMenu stasisMenu)
                    return stasisMenu.blockEntity == StasisChamberBlockEntity.this;

                if (((StasisChamberMenu)player.containerMenu).container instanceof CompoundContainer compoundContainer)
                    compoundContainer.contains(StasisChamberBlockEntity.this);

                return false;
            }
        }
    };

    public NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int waitDuration = 0;
    private boolean stabilized = false;
    private boolean skipModify = false;
    private boolean onetimeMenuOpen = true;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataSlot) {
            return switch (dataSlot) {
                case 0 -> (int)(StasisChamberBlockEntity.this.fluidLevel * 1000);
                case 1 -> StasisChamberBlockEntity.this.waitDuration;
                case 2 -> StasisChamberBlockEntity.this.stabilized ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int dataSlot, int dataValue) {
            switch (dataSlot) {
                case 0 -> StasisChamberBlockEntity.this.fluidLevel = ((float)dataValue) * 0.001f;
                case 1 -> StasisChamberBlockEntity.this.waitDuration = dataValue;
                case 2 -> StasisChamberBlockEntity.this.stabilized = dataValue != 0;
            };
        }

        public int getCount() {
            return 3;
        }
    };

    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.changed.stasis_chamber");
    }

    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new StasisChamberMenu(id, inventory, this, this.dataAccess);
    }

    public StasisChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.STASIS_CHAMBER.get(), pos, state);
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public ItemStack getItem(int p_58328_) {
        return this.items.get(p_58328_);
    }

    public ItemStack removeItem(int p_58330_, int p_58331_) {
        ItemStack itemStack = ContainerHelper.removeItem(this.items, p_58330_, p_58331_);
        if (!itemStack.isEmpty())
            this.markUpdated();
        return itemStack;
    }

    public ItemStack removeItemNoUpdate(int p_58387_) {
        ItemStack itemStack = ContainerHelper.takeItem(this.items, p_58387_);
        if (!itemStack.isEmpty())
            this.markUpdated();
        return itemStack;
    }

    public void setItem(int slotId, ItemStack stack) {
        ItemStack existingItem = this.items.get(slotId);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, existingItem);
        this.items.set(slotId, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (!flag)
            this.markUpdated();
    }

    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean canPlaceItem(int slotId, ItemStack stack) {
        if (slotId == 0)
            return stack.is(ChangedItems.LATEX_SYRINGE.get());
        else if (slotId == 1)
            return stack.getItem() instanceof FluidCanister fluidCanister && fluidCanister.getFluid() != null;
        else
            return false;
    }

    public void clearContent() {
        this.items.clear();
        this.markUpdated();
    }

    public void fillStackedContents(StackedContents contents) {
        for(ItemStack itemstack : this.items) {
            contents.accountStack(itemstack);
        }
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("fluidLevel", fluidLevel);
        tag.putFloat("fluidLevelO", fluidLevelO);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("waitDuration", waitDuration);
        tag.putBoolean("stabilized", stabilized);
        if (entityHolder != null)
            tag.putInt("entityHolderId", entityHolder.getId());

        var commandTag = new ListTag();
        scheduledCommands.stream().map(command -> StringTag.valueOf(command.name())).forEach(commandTag::add);
        tag.put("scheduledCommands", commandTag);
        if (currentCommand != null)
            tag.putString("currentCommand", currentCommand.name());
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidLevel = tag.getFloat("fluidLevel");
        fluidLevelO = tag.getFloat("fluidLevelO");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        waitDuration = tag.getInt("waitDuration");
        stabilized = tag.getBoolean("stabilized");
        if (tag.contains("entityHolderId") && level != null && level.isClientSide) {
            Entity entity = level.getEntity(tag.getInt("entityHolderId"));
            if (entity instanceof SeatEntity seat)
                entityHolder = seat;
        }

        scheduledCommands.clear();
        var commandTag = tag.getList("scheduledCommands", 8);
        for (int idx = 0; idx < commandTag.size(); ++idx)
            ScheduledCommand.bySerializedName(commandTag.getString(idx)).ifPresent(scheduledCommands::add);
        currentCommand = null;
        if (tag.contains("currentCommand")) {
            currentCommand = ScheduledCommand.bySerializedName(tag.getString("currentCommand")).orElse(null);
        }
    }

    public void markUpdated() {
        this.setChanged();
        if (this.getLevel() != null)
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    private @Nullable SeatEntity findEntityHolderInLevel() {
        if (entityHolder != null && !entityHolder.isRemoved())
            return entityHolder;

        entityHolder = null;
        Level level = getLevel();
        if (level == null)
            return null;

        entityHolder = level.getEntitiesOfClass(SeatEntity.class, new AABB(this.worldPosition).inflate(2.0D),
                seat -> this.worldPosition.equals(seat.getAttachedBlockPos())).stream().findFirst().orElse(null);
        return entityHolder;
    }

    private void discardEmptyEntityHolder() {
        SeatEntity holder = findEntityHolderInLevel();
        if (holder != null && holder.getFirstPassenger() == null) {
            holder.discard();
            entityHolder = null;
        }
    }

    public void releaseHeldEntityAndDiscardSeat() {
        SeatEntity holder = findEntityHolderInLevel();
        if (holder != null) {
            holder.getPassengers().forEach(Entity::stopRiding);
            holder.discard();
        }

        entityHolder = null;
        cachedEntity = null;
        if (stabilized) {
            stabilized = false;
            if (level instanceof ServerLevel serverLevel)
                serverLevel.updateSleepingPlayerList();
        }
        markUpdated();
    }

    @Override
    public SeatEntity getEntityHolder() {
        return findEntityHolderInLevel();
    }

    @Override
    public void setEntityHolder(SeatEntity entityHolder) {
        this.entityHolder = entityHolder;
        this.markUpdated();
    }

    public boolean chamberEntity(LivingEntity entity) {
        if (entity.isRemoved() || entity.isDeadOrDying())
            return false;

        SeatEntity holder = findEntityHolderInLevel();
        if (holder == null) {
            holder = SeatEntity.createFor(entity.level(), this.getBlockState(), this.getBlockPos(), false, false, false);
            entityHolder = holder;
            if (holder != null)
                this.markUpdated();
        }

        if (holder == null)
            return false;

        Entity seated = holder.getFirstPassenger();
        if (seated != null)
            return seated == entity && entity.getVehicle() == holder;

        if (entityHolder != null) {
            if (level != null && !level.isClientSide && getFluidType().orElse(null) instanceof WaterFluid) {
                if (entity.getVehicle() != null && entity.getVehicle() != holder)
                    entity.stopRiding();
                entity.startRiding(holder);
                ChangedAnimationEvents.broadcastEntityAnimation(entity, ChangedAnimationEvents.STASIS_IDLE.get(), StasisAnimationParameters.INSTANCE);
                this.cachedEntity = entity;
                this.markUpdated();
                return entity.getVehicle() == holder;
            }

            return level != null && level.isClientSide;
        }

        return false;
    }

    public Optional<LivingEntity> getChamberedEntity() {
        SeatEntity holder = findEntityHolderInLevel();
        if (holder == null)
            return Optional.empty();
        if (holder.isRemoved()) {
            entityHolder = null;
            cachedEntity = null;
            return Optional.empty();
        }
        if (this.getBlockState().getValue(StasisChamber.OPEN))
            return Optional.empty();
        return Optional.ofNullable(holder.getFirstPassenger()).map(entity -> {
            if (entity instanceof LivingEntity livingEntity)
                return livingEntity;
            else
                return null;
        });
    }

    public Optional<IAbstractChangedEntity> getChamberedLatex() {
        return getChamberedEntity().map(IAbstractChangedEntity::forEither);
    }

    public Optional<ScheduledCommand> getCurrentCommand() {
        return Optional.ofNullable(currentCommand);
    }

    private @Nullable TransfurVariant<?> findVariantFromSlots() {
        return getSyringe().is(ChangedItems.LATEX_SYRINGE.get()) ? Syringe.getVariant(getSyringe()) : null;
    }

    private ItemStack getSyringe() {
        return items.get(0);
    }

    public Optional<TransfurVariant<?>> getConfiguredTransfurVariant() {
        return Optional.ofNullable(findVariantFromSlots());
    }

    public ImmutableList<ScheduledCommand> getScheduledCommands() {
        return ImmutableList.copyOf(scheduledCommands);
    }

    public float getFluidLevel() {
        return fluidLevel;
    }

    public float getFluidYHeight() {
        return (getFluidLevel() * 2.75f + 0.125f) + this.getBlockPos().below().getY();
    }

    public float getFluidLevel(float partialTick) {
        return Mth.lerp(partialTick, fluidLevelO, fluidLevel);
    }

    public Optional<Fluid> getFluidType() {
        ItemStack canisterStack = items.get(1);
        if (canisterStack.getCount() > 0 && canisterStack.getItem() instanceof FluidCanister canisterItem) {
            return Optional.ofNullable(canisterItem.getFluid());
        }
        return Optional.empty();
    }

    public void setFluidLevel(float fluidLevel) {
        this.fluidLevelO = fluidLevel;
        this.fluidLevel = fluidLevel;
        this.markUpdated();
    }

    public boolean shouldChamberIdle(LivingEntity entity) {
        return openersCounter.getOpenerCount() > 0;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, StasisChamberBlockEntity blockEntity) {
        blockEntity.openersCounter.recheckOpeners(level, blockPos, blockState);

        var commands = blockEntity.scheduledCommands;
        if (commands.isEmpty() && !blockEntity.getEntitiesWithin().isEmpty()) {
            // No scheduled work, ensure entities within can leave
            commands.add(ScheduledCommand.DRAIN);
            commands.add(ScheduledCommand.RELEASE);
            commands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
        }

        if (!commands.isEmpty() && blockEntity.currentCommand == null) {
            blockEntity.currentCommand = commands.get(0);
            commands.remove(0);

            if (blockEntity.currentCommand == null || !blockEntity.currentCommand.canStart(blockEntity)) {
                blockEntity.currentCommand = null; // Cannot start command
                blockEntity.markUpdated();
                return;
            } else {
                blockEntity.markUpdated();
            }
        }

        if (blockEntity.currentCommand != null && !blockEntity.currentCommand.tick(blockEntity)) {
            blockEntity.currentCommand = null; // Command finished
            blockEntity.markUpdated();
        }
    }

    public boolean isOpen() {
        return this.getBlockState().getValue(StasisChamber.OPEN);
    }

    public boolean isClosed() {
        return !isOpen();
    }

    public boolean isFilled() {
        return fluidLevel >= 1.0f;
    }

    public boolean isPartiallyFilled() {
        return fluidLevel > 0.0f;
    }

    public boolean isClosedAndDrained() {
        return isClosed() && isDrained();
    }

    public boolean isClosedAndNotFullAndHasFluid() {
        return isClosed() && !isFilled() && getFluidType().isPresent();
    }

    public boolean isDrained() {
        return fluidLevel <= 0.0f;
    }

    public boolean isFilledAndHasNoEntity() {
        return isFilled() && getChamberedEntity().isEmpty() && getEntitiesWithin().isEmpty();
    }

    public boolean isFilledAndHasEntity() {
        return isFilled() && getChamberedEntity().isPresent();
    }

    public boolean isFilledAndHasLatex() {
        return isFilled() && getChamberedLatex().isPresent();
    }

    public <T extends Entity> List<T> getEntitiesWithin(Class<T> clazz) {
        if (!(this.getBlockState().getBlock() instanceof StasisChamber chamber)) {
            return List.of();
        }

        Level level = getLevel();
        if (level == null)
            return List.of();

        AABB detectionBox = chamber.getDetectionSize(this.getBlockState(), level, this.getBlockPos());
        var entities = level.getEntitiesOfClass(clazz, detectionBox);
        {
            var iterator = entities.iterator();
            while (iterator.hasNext()) {
                var entity = iterator.next();
                if (entity.isRemoved() || entity.isSpectator()) {
                    iterator.remove();
                    continue;
                }
                if (entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()) {
                    iterator.remove();
                    continue;
                }

                AABB entityBox = entity.getBoundingBox();
                double centerX = (entityBox.minX + entityBox.maxX) * 0.5;
                double centerZ = (entityBox.minZ + entityBox.maxZ) * 0.5;
                boolean centerInsideFootprint = centerX >= detectionBox.minX && centerX <= detectionBox.maxX &&
                        centerZ >= detectionBox.minZ && centerZ <= detectionBox.maxZ;
                boolean overlapsHeight = entityBox.maxY > detectionBox.minY && entityBox.minY < detectionBox.maxY;

                if (!(centerInsideFootprint && overlapsHeight))
                    iterator.remove();
            }
        }
        return entities;
    }

    public List<LivingEntity> getEntitiesWithin() {
        return getEntitiesWithin(LivingEntity.class);
    }

    public List<Player> getPlayersWithin() {
        return getEntitiesWithin(Player.class).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player)entity)
                .toList();
    }

    public boolean ensureCapturedIsStillInside() {
        var entities = getEntitiesWithin();

        if (cachedEntity == null) {
            cachedEntity = entities.stream().findAny().orElse(null);
        }

        if (cachedEntity != null) {
            if (cachedEntity.isRemoved() || cachedEntity.isDeadOrDying()) {
                cachedEntity = null;
                markUpdated();
                return false; // Entity is dead
            }

            if (!entities.contains(cachedEntity)) {
                cachedEntity = null;
                markUpdated();
                return false; // Entity is no longer inside the chamber
            }

            if (!chamberEntity(cachedEntity)) {
                cachedEntity = null;
                markUpdated();
                return false;
            }
        }

        return cachedEntity != null;
    }

    // This method is to prevent griefing from locking a player in a chamber indefinitely.
    public boolean isPlayerAllowedToConfigure(@Nullable Player controller) {
        if (controller == null)
            return true; // Unknown packet origin (shouldn't be possible on server-side)
        var players = getPlayersWithin();
        if (players.isEmpty())
            return true; // No players within the chamber

        return players.contains(controller); // Controller is inside the chamber
    }

    public void applyModifications(CompoundTag modifications) {
        if (currentCommand == ScheduledCommand.MODIFY_ENTITY) {
            getChamberedLatex().ifPresent(entity -> {
                this.skipModify = true;

                if (entity.getChangedEntity() instanceof ModifiableEntity modifiable) {
                    var vectors = modifiable.getModificationVectors();

                    AtomicBoolean anyMatch = new AtomicBoolean(false);
                    modifications.getAllKeys().forEach(key -> {
                        if (!vectors.containsKey(key))
                            return;

                        if (vectors.get(key).readFromTag(modifications.get(key)))
                            anyMatch.set(true);
                    });

                    if (anyMatch.getAcquire())
                        ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.STASIS_CHAMBER_MODIFY_LATEX, 1.0f, 1.0f);
                } else ChangedTransfurVariants.Gendered.getOpposite(entity.getSelfVariant()).ifPresent(otherVariant -> {
                    entity.replaceVariant(otherVariant);
                    ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.STASIS_CHAMBER_MODIFY_LATEX, 1.0f, 1.0f);
                });
            });

            markUpdated();
        }
    }

    public int getWaitDuration() {
        return waitDuration;
    }

    public void setWaitDuration(int waitDuration, @Nullable ServerPlayer controller) {
        waitDuration = Mth.clamp(waitDuration, 0, StasisChamberMenu.MAX_WAIT_DURATION);

        if (waitDuration > this.waitDuration && !this.isPlayerAllowedToConfigure(controller))
            return;

        this.waitDuration = waitDuration;
        markUpdated();
    }

    public boolean isStabilized() {
        return stabilized;
    }

    public void trimSchedule() {
        boolean changed = false;
        if (currentCommand != null) {
            var trimmed = switch (currentCommand) {
                case CLOSE_WHEN_EMPTY, RELEASE, DRAIN, WAIT -> null;
                default -> currentCommand;
            };
            if (trimmed != currentCommand) {
                currentCommand = trimmed;
                changed = true;
            }
        }
        while (!scheduledCommands.isEmpty()) {
            var lastCommand = scheduledCommands.get(scheduledCommands.size() - 1);
            switch (lastCommand) {
                case CLOSE_WHEN_EMPTY, RELEASE, DRAIN, WAIT -> {
                    scheduledCommands.remove(scheduledCommands.size() - 1);
                    changed = true;
                }
                default -> {
                    if (changed)
                        markUpdated();
                    return;
                }
            }
        }
        if (changed)
            markUpdated();
    }

    private boolean hasCommand(ScheduledCommand command) {
        return currentCommand == command || scheduledCommands.contains(command);
    }

    public void inputProgram(String program, @Nullable ServerPlayer controller) {
        if ("transfur".equals(program) && !hasCommand(ScheduledCommand.TRANSFUR_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.OPEN);
            scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
            scheduledCommands.add(ScheduledCommand.FILL);
            scheduledCommands.add(ScheduledCommand.TRANSFUR_ENTITY);

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("modify".equals(program) && !hasCommand(ScheduledCommand.MODIFY_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.OPEN);
            scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
            scheduledCommands.add(ScheduledCommand.FILL);
            scheduledCommands.add(ScheduledCommand.MODIFY_ENTITY);

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("captureNextEntity".equals(program) && !hasCommand(ScheduledCommand.CAPTURE_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.OPEN);
            scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
            scheduledCommands.add(ScheduledCommand.WAIT);
            if (waitDuration < 200 && this.isPlayerAllowedToConfigure(controller))
                waitDuration = 200; // Default 10 seconds

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("toggleStasis".equals(program)) {
            if (stabilized || scheduledCommands.contains(ScheduledCommand.STABILIZE_ENTITY) || currentCommand == ScheduledCommand.STABILIZE_ENTITY) {
                trimSchedule();

                currentCommand = ScheduledCommand.WAKE_ENTITY;
                scheduledCommands.add(ScheduledCommand.WAIT);
                if (waitDuration < 400 && this.isPlayerAllowedToConfigure(controller))
                    waitDuration = 400; // Default 20 seconds

                scheduledCommands.add(ScheduledCommand.DRAIN);
                scheduledCommands.add(ScheduledCommand.RELEASE);
                scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
                markUpdated();
            } else {
                trimSchedule();

                scheduledCommands.add(ScheduledCommand.OPEN);
                scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
                scheduledCommands.add(ScheduledCommand.FILL);
                scheduledCommands.add(ScheduledCommand.STABILIZE_ENTITY);
                scheduledCommands.add(ScheduledCommand.WAIT);
                if (waitDuration < 400 && this.isPlayerAllowedToConfigure(controller))
                    waitDuration = 400; // Default 20 seconds

                scheduledCommands.add(ScheduledCommand.DRAIN);
                scheduledCommands.add(ScheduledCommand.RELEASE);
                scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
                markUpdated();
            }
        }

        else if ("createEntity".equals(program) && !hasCommand(ScheduledCommand.CREATE_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            scheduledCommands.add(ScheduledCommand.FILL);
            scheduledCommands.add(ScheduledCommand.CREATE_ENTITY);
            scheduledCommands.add(ScheduledCommand.STABILIZE_ENTITY);
            scheduledCommands.add(ScheduledCommand.WAIT);
            if (waitDuration < 600 && this.isPlayerAllowedToConfigure(controller))
                waitDuration = 600; // Default 30 seconds

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("discardEntity".equals(program) && !hasCommand(ScheduledCommand.DISCARD_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.DISCARD_ENTITY);

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("abort".equals(program)) {
            currentCommand = ScheduledCommand.DRAIN;
            scheduledCommands.clear();
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);

            markUpdated();
        }
    }

    public enum ScheduledCommand {
        /**
         * Opens the chamber door.
         * Requires: Chamber door is closed, and the chamber is drained.
         */
        OPEN("open", StasisChamberBlockEntity::isClosedAndDrained, blockEntity -> {
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Waits until only one entity is inside the chamber to close the door.
         * Requires: Chamber door is open.
         */
        CAPTURE_ENTITY("capture_entity", StasisChamberBlockEntity::isOpen, blockEntity -> {
            var entities = blockEntity.getEntitiesWithin();
            if (entities.size() != 1)
                return true; // Execute again
            blockEntity.cachedEntity = entities.get(0);

            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Closes the chamber door.
         * Requires: Chamber door is open.
         */
        CLOSE("close", StasisChamberBlockEntity::isOpen, blockEntity -> {
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Fills the chamber with the configured fluid.
         * Requires: Chamber door is closed, the chamber is not full, and a fluid is configured.
         */
        FILL("fill", StasisChamberBlockEntity::isClosedAndNotFullAndHasFluid, blockEntity -> {
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.markAsActive(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            blockEntity.fluidLevelO = blockEntity.fluidLevel;
            blockEntity.fluidLevel += (0.05f / 12.0f); // Take 12 seconds to fill

            if (blockEntity.fluidLevel > 0.6f)
                blockEntity.ensureCapturedIsStillInside();

            if (blockEntity.isFilled()) {
                blockEntity.fluidLevelO = 1f;
                blockEntity.fluidLevel = 1f;
            }

            blockEntity.markUpdated();
            return !blockEntity.isFilled();
        }),
        /**
         * Sets the chamber in stasis mode. Treats chambered entity as "sleeping", and freezes their AI.
         * Requires: Chamber is filled and an entity is chambered.
         */
        STABILIZE_ENTITY("stabilize_entity", StasisChamberBlockEntity::isFilledAndHasEntity, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.stabilized = true;
            blockEntity.getChamberedEntity().map(EntityUtil::playerOrNull).map(Player::level).ifPresent(level -> {
                if (level instanceof ServerLevel serverLevel)
                    serverLevel.updateSleepingPlayerList();
            });
            blockEntity.markUpdated();

            return false;
        }),
        /**
         * Sets the chamber out of stasis mode. "Wakes" the entity.
         * Requires: Chamber is filled, an entity is chambered, and the chamber is in stasis mode.
         */
        WAKE_ENTITY("wake_entity", blockEntity -> blockEntity.isFilledAndHasEntity() && blockEntity.stabilized, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.stabilized = false;
            blockEntity.getChamberedEntity().map(EntityUtil::playerOrNull).map(Player::level).ifPresent(level -> {
                if (level instanceof ServerLevel serverLevel)
                    serverLevel.updateSleepingPlayerList();
            });
            blockEntity.markUpdated();

            return false;
        }),
        /**
         * Modifies the configured properties of a chambered latex, or waits while any player has the chamber panel open.
         * If the chambered latex is CustomLatex entity: applies the configuredCustomLatex form flags.
         * If the chambered latex is part of a gendered pair: switches to the opposite gender.
         * Requires: Chamber is filled and a latex entity (or player) is chambered.
         */
        MODIFY_ENTITY("modify_entity", StasisChamberBlockEntity::isFilledAndHasLatex, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            if (blockEntity.getChamberedEntity().map(blockEntity::shouldChamberIdle).orElse(false)) {
                blockEntity.onetimeMenuOpen = false;
                return true; // Idle while captured entity has panel open
            }

            if (blockEntity.onetimeMenuOpen && blockEntity.openersCounter.getOpenerCount() <= 0) {
                blockEntity.onetimeMenuOpen = false;

                boolean playerOpened = blockEntity.getChamberedEntity().map(entity -> {
                    if (!(entity instanceof ServerPlayer serverPlayer))
                        return false;
                    var provider = blockEntity.getBlockState().getMenuProvider(blockEntity.level, blockEntity.worldPosition);
                    if (provider == null)
                        return false;
                    NetworkHooks.openScreen(serverPlayer, provider, extra -> {
                        extra.writeBlockPos(blockEntity.worldPosition);
                        extra.writeBoolean(true);
                    });
                    return true;
                }).orElse(false);

                if (playerOpened)
                    return true;
            }

            if (blockEntity.skipModify) {
                blockEntity.skipModify = false;
                blockEntity.onetimeMenuOpen = true;
                blockEntity.markUpdated();
                return false;
            }

            blockEntity.getChamberedLatex().ifPresent(entity -> {
                ChangedTransfurVariants.Gendered.getOpposite(entity.getSelfVariant()).ifPresent(otherVariant -> {
                    entity.replaceVariant(otherVariant);
                    ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.STASIS_CHAMBER_MODIFY_LATEX, 1.0f, 1.0f);
                });
            });

            blockEntity.onetimeMenuOpen = true;
            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Safely transfurs the chambered entity, and waits for the transfur to complete.
         * If the process killed the chambered entity, automatically switches to the new npc entity.
         * Requires: Chamber is filled, a transfur variant is configured, and a non-latex entity is chambered.
         */
        TRANSFUR_ENTITY("transfur_entity", blockEntity -> {
            return blockEntity.findVariantFromSlots() != null && blockEntity.isFilledAndHasEntity() && !blockEntity.isFilledAndHasLatex();
        }, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.getChamberedEntity().ifPresent(entity -> {
                if (TransfurVariant.getEntityVariant(entity) != null) return;
                if (!entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) return;

                ProcessTransfur.transfur(entity, ImmediateTransfurDecision.safe(blockEntity.findVariantFromSlots(), TransfurCause.STASIS_CHAMBER, newEntity -> {
                    blockEntity.cachedEntity = newEntity.getEntity();
                    blockEntity.ensureCapturedIsStillInside();
                    blockEntity.markUpdated();
                }));

                blockEntity.setItem(0, new ItemStack(ChangedItems.SYRINGE.get()));
            });

            return blockEntity.getChamberedLatex().map(IAbstractChangedEntity::getTransfurVariantInstance)
                    .map(TransfurVariantInstance::isTransfurring)
                    .orElse(false);
        }),
        /**
         * Creates a new CustomLatex entity to modify, release, and/or discard.
         * Requires: Chamber is filled and the chamber has no entity within.
         */
        CREATE_ENTITY("create_entity", StasisChamberBlockEntity::isFilledAndHasNoEntity, blockEntity -> {
            ChangedEntity newEntity = ChangedEntities.CUSTOM_LATEX.get().create(blockEntity.level);
            if (newEntity == null)
                return false;
            var blockPos = blockEntity.getBlockPos();
            var facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            newEntity.moveTo(blockPos.getX() + 0.5, blockPos.getY() - 1.0, blockPos.getZ() + 0.5, facing.toYRot(), 0f);
            newEntity.finalizeSpawn((ServerLevelAccessor) blockEntity.level, blockEntity.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null);
            blockEntity.level.addFreshEntity(newEntity);
            blockEntity.chamberEntity(newEntity);
            ChangedSounds.broadcastSound(newEntity, ChangedSounds.STASIS_CHAMBER_CREATE_LATEX, 1.0f, 1.0f);
            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Discards the chambered latex entity (no loot/experience).
         * If the latex entity is actually a player, nothing happens.
         * Requires: Chamber is filled and a latex entity (or player) is chambered.
         */
        DISCARD_ENTITY("discard_entity", StasisChamberBlockEntity::isFilledAndHasLatex, blockEntity -> {
            var entity = blockEntity.getChamberedEntity().orElse(null);
            if (entity == null || entity instanceof Player)
                return false;

            ChangedSounds.broadcastSound(entity, ChangedSounds.STASIS_CHAMBER_DISCARD_LATEX, 1.0f, 1.0f);
            entity.stopRiding();
            entity.discard();
            blockEntity.cachedEntity = null;
            blockEntity.discardEmptyEntityHolder();
            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Drains the chamber of fluid, and exits stasis mode.
         * Requires: Chamber is not empty.
         */
        DRAIN("drain", StasisChamberBlockEntity::isPartiallyFilled, blockEntity -> {
            blockEntity.fluidLevelO = blockEntity.fluidLevel;
            blockEntity.fluidLevel -= (0.05f / 8f); // Take 8 seconds to drain

            if (blockEntity.stabilized) {
                blockEntity.stabilized = false;
                blockEntity.getChamberedEntity().map(EntityUtil::playerOrNull).map(Player::level).ifPresent(level -> {
                    if (level instanceof ServerLevel serverLevel)
                        serverLevel.updateSleepingPlayerList();
                });
            }

            if (blockEntity.fluidLevel > 0.6f)
                blockEntity.ensureCapturedIsStillInside();
            else {
                SeatEntity seatEntity = blockEntity.getEntityHolder();
                if (seatEntity != null) {
                    seatEntity.getPassengers().forEach(Entity::stopRiding);
                }
            }

            if (blockEntity.isDrained()) {
                blockEntity.fluidLevelO = 0f;
                blockEntity.fluidLevel = 0f;
                if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber)
                    chamber.markAsInactive(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            blockEntity.markUpdated();
            return !blockEntity.isDrained();
        }),
        /**
         * Opens the chamber door if there are entities within the chamber.
         * Requires: Chamber door is closed and the chamber is drained.
         */
        RELEASE("release", StasisChamberBlockEntity::isClosedAndDrained, blockEntity -> {
            boolean hasEntity = blockEntity.getChamberedEntity().isPresent() ||
                    blockEntity.cachedEntity != null && !blockEntity.cachedEntity.isRemoved() && !blockEntity.cachedEntity.isDeadOrDying() ||
                    !blockEntity.getEntitiesWithin().isEmpty();
            if (!hasEntity)
                return false; // No entities inside, no need to open
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
                chamber.markAsInactive(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }
            blockEntity.cachedEntity = null;
            blockEntity.discardEmptyEntityHolder();
            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Closes the chamber door if there are no entities within the chamber.
         * Requires: Chamber door is open.
         */
        CLOSE_WHEN_EMPTY("close_when_empty", StasisChamberBlockEntity::isOpen, blockEntity -> {
            if (!blockEntity.getEntitiesWithin().isEmpty())
                return true;
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
                chamber.markAsInactive(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            blockEntity.cachedEntity = null;
            blockEntity.discardEmptyEntityHolder();
            blockEntity.markUpdated();
            return false;
        }),
        /**
         * Waits the configured amount of ticks before proceeding to the next command.
         */
        WAIT("wait", blockEntity -> true, blockEntity -> {
            if (blockEntity.waitDuration <= 0)
                return false;

            blockEntity.waitDuration = Math.max(0, blockEntity.waitDuration - 1);
            blockEntity.markUpdated();
            return blockEntity.waitDuration > 0;
        });

        private final String serialName;
        private final Predicate<StasisChamberBlockEntity> predicateCanStart;
        private final Function<StasisChamberBlockEntity, Boolean> functionTick;

        ScheduledCommand(String serialName, Predicate<StasisChamberBlockEntity> predicateCanStart, Function<StasisChamberBlockEntity, Boolean> functionTick) {
            this.serialName = serialName;
            this.predicateCanStart = predicateCanStart;
            this.functionTick = functionTick;
        }

        public boolean canStart(StasisChamberBlockEntity blockEntity) {
            return this.predicateCanStart.test(blockEntity);
        }

        public boolean tick(StasisChamberBlockEntity blockEntity) {
            return this.functionTick.apply(blockEntity);
        }

        public Component getDisplayText() {
            return Component.translatable("changed.stasis.command." + serialName);
        }

        public Component getActiveDisplayText() {
            return Component.translatable("changed.stasis.command._active", getDisplayText());
        }

        public static Optional<ScheduledCommand> bySerializedName(String name) {
            try {
                return Optional.of(ScheduledCommand.valueOf(name));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }
    }
}
