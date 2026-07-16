package net.changed.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.changed.Changed;
import net.changed.ability.AbstractAbility;
import net.changed.ability.GrabEntityAbility;
import net.changed.block.StasisChamber;
import net.changed.block.ThreeXThreeSection;
import net.changed.block.WearableBlock;
import net.changed.block.WhiteLatexTransportInterface;
import net.changed.block.entity.StasisChamberBlockEntity;
import net.changed.data.AccessorySlotContext;
import net.changed.data.AccessorySlotType;
import net.changed.data.AccessorySlots;
import net.changed.entity.*;
import net.changed.entity.ai.EntityAssimilationBehavior;
import net.changed.entity.latex.SpreadingLatexType;
import net.changed.entity.variant.EntityShape;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.fluid.AbstractLatexFluid;
import net.changed.fluid.Gas;
import net.changed.fluid.TransfurGas;
import net.changed.init.*;
import net.changed.item.AccessoryItem;
import net.changed.item.ExtendedItemProperties;
import net.changed.item.SpecializedAnimations;
import net.changed.network.packet.AccessorySyncPacket;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.changed.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityDataExtension {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Unique
    public int controlDisabledFor = 0;
    @Unique
    public int controlInvertedFor = 0;
    @Unique @Nullable
    public LivingEntity grabbedBy = null;
    @Unique
    public AccessorySlots accessorySlots = Util.make(new AccessorySlots((LivingEntity)(Object)this), slots -> {
        slots.initialize(
                AccessoryEntities.INSTANCE.canEntityTypeUseSlot(AccessoryEntities.getApparentEntityType(slots.owner)),
                AccessorySlots.defaultInvalidHandler(slots.owner));
    });

    @Override
    public int getNoControlTicks() {
        return controlDisabledFor;
    }

    @Override
    public void setNoControlTicks(int ticks) {
        this.controlDisabledFor = ticks;
    }

    @Override
    public int getInvertControlTicks() {
        return controlInvertedFor;
    }

    @Override
    public void setInvertControlTicks(int ticks) {
        this.controlInvertedFor = ticks;
    }

    @Nullable
    @Override
    public LivingEntity getGrabbedBy() {
        return grabbedBy;
    }

    @Override
    public void setGrabbedBy(@Nullable LivingEntity grabbedBy) {
        this.grabbedBy = grabbedBy;
    }

    @Override
    public Optional<AccessorySlots> getAccessorySlots() {
        return Optional.of(accessorySlots);
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void updateFallFlying(CallbackInfo callback) {
        if (this.level().isClientSide) return;
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (variant.canElytraGlide()) {
                this.setSharedFlag(7, player.isFallFlying() && !player.onGround() && !player.isPassenger() && !player.hasEffect(MobEffects.LEVITATION));
                callback.cancel();
            }
        });
    }

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void onClimbable(CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (variant) -> {
            if (variant.getParent().canClimb && this.horizontalCollision)
                callback.setReturnValue(true);
        });
    }

    @Inject(method = "wouldNotSuffocateAtTargetPose", at = @At("HEAD"), cancellable = true)
    protected void changed$wouldNotSuffocateAtTargetPose(Pose pose, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (StasisChamber.isEntityStabilized(livingEntity) && pose != Pose.STANDING) {
            callback.setReturnValue(false);
            return;
        }

        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(livingEntity));
        if (variant != null &&
                pose == Pose.SWIMMING &&
                variant.getChangedEntity().isAllowedToSwim() &&
                variant.getChangedEntity().canSwimInFluidType(NeoForgeMod.WATER_TYPE.value()) &&
                (livingEntity.isInWater() ||
                        livingEntity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) ||
                        livingEntity.isInFluidType(NeoForgeMod.WATER_TYPE.value()))) {
            callback.setReturnValue(true);
            return;
        }

        EntityShape.getShapeOf(livingEntity)
                .map(EntityShape::isLegless)
                .flatMap(legless -> {
                    if (legless && livingEntity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                        return Optional.of(pose == Pose.SWIMMING);
                    } else
                        return Optional.empty();
                }).ifPresent(callback::setReturnValue);
    }

    @WrapMethod(method = "getJumpPower")
    public float getJumpPower(Operation<Float> original) {
        var attributes = this.getAttributes();
        if (attributes.hasAttribute(ChangedAttributes.JUMP_STRENGTH)) {
            return original.call() * (float) attributes.getValue(ChangedAttributes.JUMP_STRENGTH);
        } else {
            return original.call();
        }
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    public void hasEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (variant.visionType.test(effect.value()))
                callback.setReturnValue(true);

            if (variant.miningStrength.test(effect.value()))
                callback.setReturnValue(true);

            if (effect.equals(MobEffects.NIGHT_VISION)) {
                if (WhiteLatexTransportInterface.isEntityInWhiteLatex(player))
                    callback.setReturnValue(true);
            }
            if (variant.breatheMode.canBreatheWater() && effect.equals(MobEffects.CONDUIT_POWER) && isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()))
                callback.setReturnValue(true);
        });
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    public void getEffect(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (variant.visionType.test(effect.value()))
                callback.setReturnValue(new MobEffectInstance(effect, 300, 1, false, false));

            if (variant.miningStrength.test(effect.value()))
                callback.setReturnValue(new MobEffectInstance(effect, 300, 1, false, false));

            if (effect.equals(MobEffects.NIGHT_VISION)) {
                if (WhiteLatexTransportInterface.isEntityInWhiteLatex(player))
                    callback.setReturnValue(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 1, false, false));
            }
            if (variant.breatheMode.canBreatheWater() && effect.equals(MobEffects.CONDUIT_POWER) && isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()))
                callback.setReturnValue(new MobEffectInstance(MobEffects.CONDUIT_POWER, 300, 1, false, false));
        });
    }

    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private static void getEquipmentSlotForItem(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callback) {
        if (stack.getItem() instanceof BlockItem blockItem)
            if (blockItem.getBlock() instanceof WearableBlock wearableBlock)
                callback.setReturnValue(wearableBlock.getEquipmentSlot());
    }

    @WrapMethod(method = "isVisuallySwimming")
    private boolean isVariantVisuallySwimming(Operation<Boolean> original) {
        boolean vanillaSwimming = original.call();
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(this));
        if (variant == null)
            return vanillaSwimming;

        if (variant.getChangedEntity().isVisuallySwimming())
            return true;

        return vanillaSwimming || changed$isTransfurVisuallySwimmingInWater(variant);
    }

    @Unique
    private boolean changed$isTransfurVisuallySwimmingInWater(TransfurVariantInstance<?> variant) {
        if (!variant.getChangedEntity().isAllowedToSwim())
            return false;
        if (!this.canSwimInFluidType(NeoForgeMod.WATER_TYPE.value()))
            return false;
        if (!(this.isInWater() || this.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) || this.isInFluidType(NeoForgeMod.WATER_TYPE.value())))
            return false;

        return this.isSwimming() ||
                this.getPose() == Pose.SWIMMING;
    }

    @Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    protected void triggerItemUseEffects(ItemStack itemStack, int particleCount, CallbackInfo callbackInfo) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof SpecializedAnimations specialized) {
            if (specialized.triggerItemUseEffects((LivingEntity)(Entity)this, itemStack, particleCount))
                callbackInfo.cancel();
        }
    }

    @Unique
    private Multimap<Holder<Attribute>, AttributeModifier> getAccessoryAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        stack.getAttributeModifiers().forEach(slot, builder::put);
        return builder.build();
    }

    @Unique
    private Map<AccessorySlotType, ItemStack> collectAccessoryChanges() {
        Map<AccessorySlotType, ItemStack> map = null;

        for (AccessorySlotType slotType : ChangedRegistry.ACCESSORY_SLOTS.get().getValues()) {
            ItemStack lastStack = accessorySlots.getLastItem(slotType);
            ItemStack newStack = accessorySlots.getItem(slotType).orElse(ItemStack.EMPTY);
            if (lastStack.getItem() != newStack.getItem() && this.tickCount > 5) {
                if (lastStack.getItem() instanceof AccessoryItem accessoryItem)
                    accessoryItem.accessoryRemoved(new AccessorySlotContext<>((LivingEntity)(Entity)this, slotType, lastStack));
                if (newStack.getItem() instanceof AccessoryItem accessoryItem)
                    accessoryItem.accessoryEquipped(new AccessorySlotContext<>((LivingEntity)(Entity)this, slotType, newStack));
            }

            if (!ItemStack.matches(newStack, lastStack)) {
                //net.neoforged.neoforge.common.MinecraftForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent(this, equipmentslot, lastStack, newStack));
                if (map == null) {
                    map = new HashMap<>();
                }

                map.put(slotType, newStack);
                if (!lastStack.isEmpty()) {
                    this.getAttributes().removeAttributeModifiers(getAccessoryAttributeModifiers(lastStack, slotType.getEquivalentSlot()));
                }

                if (!newStack.isEmpty()) {
                    this.getAttributes().addTransientAttributeModifiers(getAccessoryAttributeModifiers(newStack, slotType.getEquivalentSlot()));
                }
            }
        }

        return map;
    }

    @Unique
    private void handleAccessoryChanges(Map<AccessorySlotType, ItemStack> map) {
        map.forEach((slotType, stack) -> {
            accessorySlots.setLastItem(slotType, stack.copy());
        });
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new AccessorySyncPacket(this.getId(), map));
    }

    @Inject(method = "detectEquipmentUpdates", at = @At("RETURN"))
    private void detectAccessoryUpdates(CallbackInfo ci) {
        Map<AccessorySlotType, ItemStack> map = this.collectAccessoryChanges();
        if (map != null && !map.isEmpty())
            this.handleAccessoryChanges(map);
    }

    @Unique @Nullable private Gas eyeInGas = null;

    @Unique
    private void checkForGas() {
        eyeInGas = null;
        // Code from Entity.updateFluidOnEyes()
        double yCheck = this.getEyeY() - 0.11111111;

        BlockPos blockpos = EntityUtil.getBlock(this.getX(), yCheck, this.getZ());
        FluidState fluidstate = this.level().getFluidState(blockpos);
        double yFluid = ((float)blockpos.getY() + fluidstate.getHeight(this.level(), blockpos));
        if (yFluid > yCheck && fluidstate.getType() instanceof Gas transfurGas)
            eyeInGas = transfurGas;

        var blockstate = this.level().getBlockState(blockpos);
        if (blockstate.is(ChangedBlocks.STASIS_CHAMBER.get())) {
            this.level().getBlockEntity(
                    blockstate.getValue(StasisChamber.SECTION).getRelative(blockpos, blockstate.getValue(HorizontalDirectionalBlock.FACING), ThreeXThreeSection.CENTER),
                    ChangedBlockEntities.STASIS_CHAMBER.get()
            ).filter(chamber -> chamber.getFluidYHeight() > yCheck).flatMap(StasisChamberBlockEntity::getFluidType).ifPresent(fluid -> {
                this.forgeFluidTypeOnEyes = fluid.getFluidType();
                if (fluid instanceof Gas gas)
                    eyeInGas = gas;
            });
        }
    }

    @Override
    public <T extends Gas> Optional<T> isEyeInGas(Class<T> clazz) {
        return Optional.ofNullable(eyeInGas).flatMap(gas -> {
            if (clazz.isAssignableFrom(gas.getClass()))
                return Optional.of((T)gas);
            else
                return Optional.empty();
        });
    }

    @Override
    public void do_hurtCurrentlyUsedShield(float blocked) {
        this.hurtCurrentlyUsedShield(blocked);
    }

    @Override
    public void do_blockUsingShield(LivingEntity attacker) {
        this.blockUsingShield(attacker);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        if (controlDisabledFor > 0) {
            if ((Entity)this instanceof Mob mob) {
                MoveControl move = mob.getMoveControl();
                move.setWantedPosition(move.getWantedX(), move.getWantedY(), move.getWantedZ(), move.getSpeedModifier());
            }

            --controlDisabledFor;
        }

        if (controlInvertedFor > 0) {
            if ((Entity)this instanceof Mob mob) {
                MoveControl move = mob.getMoveControl();
                move.setWantedPosition(move.getWantedX(), move.getWantedY(), move.getWantedZ(), move.getSpeedModifier());
            }

            --controlInvertedFor;
        }

        if (grabbedBy != null) {
            if (grabbedBy.isDeadOrDying() || grabbedBy.isRemoved())
                grabbedBy = null;
            if (grabbedBy instanceof Player player && player.isSpectator())
                grabbedBy = null;
        }

        this.checkForGas();

        AccessorySlots.getForEntity((LivingEntity)(Object)this).ifPresent(AccessorySlots::tick);
    }

    @WrapMethod(method = "canStandOnFluid")
    public boolean canStandOnFluid(FluidState state, Operation<Boolean> original) {
        return state.getType() instanceof AbstractLatexFluid latexFluid && latexFluid.canEntityStandOn((LivingEntity)(Object)this) ||
                original.call(state);
    }

    @WrapMethod(method = "breakItem")
    public void useDifferentBreakSound(ItemStack itemStack, Operation<Void> original) {
        if (!(itemStack.getItem() instanceof ExtendedItemProperties extended) || itemStack.isEmpty()) {
            original.call(itemStack);
            return;
        }

        if (!this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), extended.getBreakSound(itemStack), this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F, false);
        }

        this.spawnItemParticles(itemStack, 5);
    }

    @Shadow public abstract boolean canStandOnFluid(FluidState state);
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);
    @Shadow public abstract AttributeInstance getAttribute(Holder<Attribute> attribute);
    @Shadow protected abstract boolean isAffectedByFluids();
    @Shadow public abstract Vec3 getFluidFallingAdjustedMovement(double d0, boolean flag, Vec3 movement);

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow protected abstract void spawnItemParticles(ItemStack p_21061_, int p_21062_);

    @Shadow protected abstract void hurtCurrentlyUsedShield(float p_21316_);

    @Shadow protected abstract void blockUsingShield(LivingEntity p_21200_);

    @Shadow public abstract AttributeMap getAttributes();

    @Shadow protected boolean jumping;

    @Unique private boolean isInLatex() {
        return !this.firstTick && this.fluidHeight.getDouble(ChangedTags.Fluids.LATEX) > 0.0D;
    }

    @Inject(method = "travel", at = @At("HEAD"))
    public void travel(Vec3 direction, CallbackInfo callback) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
            double d0 = gravity == null ? 0.08D : gravity.getValue();
            boolean flag = this.getDeltaMovement().y <= 0.0D;
            if (flag && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d0 = Math.min(d0, 0.01D);
                this.resetFallDistance();
            }

            FluidState fluidstate = this.level().getFluidState(this.blockPosition());
            if (this.isInLatex() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)) {
                double d8 = this.getY();
                this.moveRelative(0.02F, direction);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.getFluidHeight(ChangedTags.Fluids.LATEX) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, (double)0.8F, 0.5D));
                    Vec3 vec33 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                    this.setDeltaMovement(vec33);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                }

                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vec3 vec34 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(vec34.x, vec34.y + (double)0.6F - this.getY() + d8, vec34.z)) {
                    this.setDeltaMovement(vec34.x, (double)0.3F, vec34.z);
                }
            }
        }
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F", remap = false))
    public float getFrictionForEntity(BlockState instance, LevelReader levelReader, BlockPos pos, Entity entity, Operation<Float> original) {
        return EntityUtil.getFrictionOnBlock(instance, instance.getFriction(levelReader, pos, entity), levelReader, pos, entity);
    }

    @Inject(method = "push", at = @At("HEAD"), cancellable = true)
    public void pushEntityIfNotGrabbed(Entity entity, CallbackInfo callback) {
        var ability = AbstractAbility.getAbilityInstance((LivingEntity)(Object)this, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability != null && ability.grabbedEntity == entity) { // Grabbed entity is called to push
            callback.cancel();
            return;
        }

        if (this.grabbedBy == entity) {
            callback.cancel();
            return;
        }
    }

    @Inject(method = "isBlocking", at = @At("RETURN"), cancellable = true)
    public void isControllerBlocking(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue())
            return;

        LivingEntity controlling = GrabEntityAbility.getControllingEntity((LivingEntity)(Object)this);
        if (controlling == (Object)this)
            return;

        cir.setReturnValue(controlling.isBlocking());
    }

    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void maybeAddAir(int current, CallbackInfoReturnable<Integer> cir) {
        TransfurGas.validEntityInGas((LivingEntity)(Object)this).ifPresent(gas -> cir.setReturnValue(current));
    }

    @Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
    private void bedExistsOrIsStabilized(CallbackInfoReturnable<Boolean> cir) {
        if (StasisChamber.isEntityStabilized((LivingEntity)(Object)this))
            cir.setReturnValue(true);
    }

    @Inject(method = "isSleeping", at = @At("HEAD"), cancellable = true)
    public void isSleepingOrStabilized(CallbackInfoReturnable<Boolean> cir) {
        if (StasisChamber.isEntityStabilized((LivingEntity)(Object)this))
            cir.setReturnValue(true);
    }

    @Inject(method = "stopSleeping", at = @At("HEAD"), cancellable = true)
    public void unlessIsStabilizedAndMultiplayer(CallbackInfo ci) {
        if ((LivingEntity)(Object)this instanceof Player && level() instanceof ServerLevel serverLevel) {
            if (serverLevel.players().stream().filter(player -> !player.isSpectator()).count() == 1) {
                // Singleplayer, just skip stasis time
                if (this.getVehicle() instanceof SeatEntity seatEntity) {
                    this.level().getBlockEntity(seatEntity.getAttachedBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get())
                            .ifPresent(StasisChamberBlockEntity::trimSchedule);
                }
                return;
            }
        }
        if (StasisChamber.isEntityStabilized((LivingEntity)(Object)this))
            ci.cancel();
    }

    @Inject(method = "getBedOrientation", at = @At("HEAD"), cancellable = true)
    public void getStasisChamberOrientation(CallbackInfoReturnable<Direction> cir) {
        if (this.getVehicle() instanceof SeatEntity seatEntity) {
            seatEntity.getAttachedBlockState()
                    .map(state -> state.getBedDirection(this.level(), seatEntity.getAttachedBlockPos()))
                    .ifPresent(cir::setReturnValue);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addExtendedData(CompoundTag tag, CallbackInfo ci) {
        tag.put("ChangedAccessorySlots", accessorySlots.save());
        if (this instanceof PathFinderMobDataExtension ext && ext.isLatexAssimilated() && Changed.config.server.doMobAssimilation.get()) {
            tag.putBoolean("ChangedIsAssimilated", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readExtendedData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ChangedAccessorySlots"))
            accessorySlots.load(tag.getCompound("ChangedAccessorySlots"));
        if ((LivingEntity)(Object)this instanceof PathfinderMob pathfinder &&
                pathfinder instanceof PathFinderMobDataExtension ext &&
                tag.contains("ChangedIsAssimilated") &&
                tag.getBoolean("ChangedIsAssimilated")) {
            if (!ext.isLatexAssimilated() && ProcessTransfur.getEntityAssimilationBehavior(pathfinder) instanceof EntityAssimilationBehavior.InjectEntityWithTransfurGoals behavior) {
                if (Changed.config.server.doMobAssimilation.get())
                    behavior.assimilate(pathfinder);
            }
        }
    }

    @Inject(method = "dropEquipment", at = @At("RETURN"))
    public void dropAccessories(CallbackInfo ci) {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            for(int i = 0; i < accessorySlots.getContainerSize(); ++i) {
                ItemStack itemstack = accessorySlots.getItem(i);
                if (!itemstack.isEmpty() && EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    accessorySlots.removeItemNoUpdate(i);
                }
            }

            accessorySlots.dropAll(AccessorySlots.dropItemHandler((LivingEntity)(Object)this));
        }
    }

    @WrapOperation(method = "playBlockFallSound", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/block/SoundType;",
            remap = false))
    public SoundType extendedSoundEvent(BlockState instance, LevelReader reader, BlockPos blockPos, Entity entity, Operation<SoundType> original) {
        final LatexCoverState coverState = LatexCoverState.getAt(reader, blockPos.above());
        if (coverState.isAir())
            return original.call(instance, reader, blockPos, entity);
        if (coverState.getProperties().contains(SpreadingLatexType.DOWN) && !coverState.getValue(SpreadingLatexType.DOWN))
            return original.call(instance, reader, blockPos, entity);
        final SoundType coveredSound = coverState.getSoundType(reader, blockPos.above(), entity);
        return coveredSound != null ? coveredSound : original.call(instance, reader, blockPos, entity);
    }

    @WrapOperation(method = "getDamageAfterMagicAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)F"))
    public float andAccessorySlots(ServerLevel serverLevel, LivingEntity entity, DamageSource damageSource, Operation<Float> original) {
        return original.call(serverLevel, entity, damageSource);
    }

}
