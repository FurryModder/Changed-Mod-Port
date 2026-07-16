package net.changed.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.changed.Changed;
import net.changed.ability.AbstractAbility;
import net.changed.ability.tree.AbilityTreeInstance;
import net.changed.block.WhiteLatexTransportInterface;
import net.changed.data.AccessorySlots;
import net.changed.entity.*;
import net.changed.block.StasisChamber;
import net.changed.entity.variant.EntityShape;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.init.*;
import net.changed.network.packet.SyncMoversPacket;
import net.changed.process.ProcessTransfur;
import net.changed.util.CameraUtil;
import net.changed.util.EntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.changed.network.PacketDistributor;
import net.changed.compat.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDataExtension {
    @Shadow public abstract boolean isSwimming();

    @Shadow public abstract boolean isSpectator();

    @Shadow @Final private Abilities abilities;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("HEAD"), cancellable = true)
    protected void changed$canPlayerFitWithinBlocksAndEntitiesWhen(Pose pose, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (StasisChamber.isEntityStabilized(livingEntity) && pose != Pose.STANDING) {
            callback.setReturnValue(false);
            return;
        }

        if (pose == Pose.SWIMMING && this.changed$canTransfurUseWaterSwimming()) {
            callback.setReturnValue(true);
            return;
        }

        EntityShape.getShapeOf(livingEntity)
                .map(EntityShape::isLegless)
                .flatMap(legless -> {
                    if (legless && livingEntity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                        return java.util.Optional.of(pose == Pose.SWIMMING);
                    } else
                        return java.util.Optional.empty();
                }).ifPresent(callback::setReturnValue);
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    protected void tryToStartFallFlying(CallbackInfoReturnable<Boolean> ci) {
        Player player = (Player)(Object)this;
        if (latexVariant != null && latexVariant.canElytraGlide()) {
            if (!player.onGround() && !player.isFallFlying() && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION)) {
                player.startFallFlying();
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickPre(CallbackInfo ci) {
        ProcessTransfur.tickPlayerTransfurProgress((Player)(Object)this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickPost(CallbackInfo ci) {
        var tug = CameraUtil.getTugData((Player)(Object)this);
        if (tug != null) {
            if (tug.shouldExpire(this)) {
                CameraUtil.resetTugData((Player)(Object)this);
                return;
            }

            tug.ticksExpire--;
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    protected void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> ci) {
        if (source.is(ChangedTags.DamageTypes.IS_TRANSFUR) && source.getEntity() != null)
            ci.setReturnValue(ChangedSounds.TRANSFUR_HURT.get());
    }

    @Inject(method = "getSwimSound", at = @At("HEAD"), cancellable = true)
    protected void getSwimSound(CallbackInfoReturnable<SoundEvent> ci) {
        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(this)) {
            ci.setReturnValue(ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("block.slime_block.step")));
            ci.cancel();
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void transfurAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof LivingEntity livingEntity))
            return;

        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            ItemStack attackingItem = this.getItemInHand(InteractionHand.MAIN_HAND);

            // Check if item contributes to transfur damage
            boolean weaponContributes = attackingItem.isEmpty() ||
                    attackingItem.getAttributeModifiers().modifiers().stream()
                            .filter(entry -> entry.slot().test(EquipmentSlot.MAINHAND))
                            .map(ItemAttributeModifiers.Entry::attribute)
                            .anyMatch(ChangedAttributes.TRANSFUR_DAMAGE::equals);

            if (weaponContributes && variant.getChangedEntity().tryTransfurTarget(target)) {
                attackingItem.hurtEnemy(livingEntity, player);

                ci.cancel();
            }
        });
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostAttackEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V", ordinal = 1))
    public void accessoryAttack(Entity target, CallbackInfo ci) {
        if (!level().isClientSide)
            AccessorySlots.getForEntity((LivingEntity)(Object)this).ifPresent(slots -> slots.onEntityAttack(InteractionHand.MAIN_HAND, target));
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    public void denyInvalidArmor(EquipmentSlot slot, ItemStack item, CallbackInfo ci) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (!variant.canWear(player, item, slot) && slot != EquipmentSlot.MAINHAND) {
                ci.cancel();
                this.setItemSlot(EquipmentSlot.MAINHAND, item);
            }
        });
    }

    // ADDITIONAL DATA
    @Nullable @Unique
    public PlayerMoverInstance<?> playerMover = null;
    @Unique
    public TransfurVariantInstance<?> latexVariant = null;
    @Unique
    public float transfurProgress = 0.0f;
    @Unique
    public CameraUtil.TugData wantToLookAt;
    @Unique
    public int paleExposure;
    @Unique
    public BasicPlayerInfo basicPlayerInfo = new BasicPlayerInfo();
    @Unique
    public AbilityTreeInstance abilityTree = new AbilityTreeInstance();

    @Override
    public TransfurVariantInstance<?> getTransfurVariant() {
        return latexVariant;
    }

    @Override
    public void setTransfurVariant(TransfurVariantInstance<?> latexVariant) {
        this.latexVariant = latexVariant;
    }

    @NotNull
    @Override
    public float getTransfurProgress() {
        return transfurProgress;
    }

    @Override
    public void setTransfurProgress(@NotNull float transfurProgress) {
        this.transfurProgress = transfurProgress;
    }

    @Override
    public CameraUtil.TugData getTugData() {
        return wantToLookAt;
    }

    @Override
    public void setTugData(CameraUtil.TugData data) {
        this.wantToLookAt = data;
    }

    @Override
    public int getPaleExposure() {
        return paleExposure;
    }

    @Override
    public void setPaleExposure(int paleExposure) {
        this.paleExposure = paleExposure;
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    public void makeStuckInBlock(BlockState state, Vec3 v3, CallbackInfo ci) {
        if (latexVariant != null)
            if (latexVariant.getParent().canClimb && state.is(Blocks.COBWEB))
                ci.cancel();
    }

    @WrapOperation(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    public void efficientFoodExhaustion(FoodData instance, float amount, Operation<Void> original) {
        original.call(instance, amount * (latexVariant == null ? 1.0f : latexVariant.getFoodEfficiency()));
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void checkGrabbed(CallbackInfo ci) {
        var grabbedBy = this.getGrabbedBy();
        var ability = AbstractAbility.getAbilityInstance(grabbedBy, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability != null && !ability.grabbedHasControl) {
            this.noPhysics = true;
            this.setOnGround(false);
        }
    }

    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    public void forceRiding(CallbackInfoReturnable<Boolean> cir) {
        if (this.getVehicle() instanceof SeatEntity seatEntity && !seatEntity.canSeatedDismount())
            cir.setReturnValue(false);
    }

    @Nullable
    @Override
    public PlayerMoverInstance<?> getPlayerMover() {
        return playerMover;
    }

    @Override
    public void setPlayerMover(@Nullable PlayerMoverInstance<?> playerMover) {
        if (this.playerMover != null)
            this.playerMover.onRemove((Player)(Object)this);

        this.playerMover = playerMover;
        if (this.playerMover != null)
            this.playerMover.onAdd((Player)(Object)this);
        if (!level().isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), SyncMoversPacket.Builder.of((Player)(Object)this, false));
    }

    @Override
    public BasicPlayerInfo getBasicPlayerInfo() {
        return basicPlayerInfo;
    }

    @Override
    public void setBasicPlayerInfo(BasicPlayerInfo basicPlayerInfo) {
        this.basicPlayerInfo = basicPlayerInfo;
    }

    @Override
    public AbilityTreeInstance getAbilityTree() {
        return abilityTree;
    }

    @WrapMethod(method = "getDefaultDimensions")
    public EntityDimensions changed$getTransfurDimensions(Pose pose, Operation<EntityDimensions> original) {
        var originalDimensions = original.call(pose);
        var dimensions = originalDimensions;

        if (this.getTransfurVariant() != null) {
            dimensions = this.getTransfurVariant().getTransfurDimensions(pose, originalDimensions)
                    .withEyeHeight(this.getTransfurVariant().getTransfurEyeHeight(pose, originalDimensions.eyeHeight()));
        }

        var mover = getPlayerMover();
        if (mover != null) {
            dimensions = getPlayerMover().getDimensions(this, pose, dimensions);
            dimensions = dimensions.withEyeHeight(getPlayerMover().getEyeHeight((LivingEntity)(Object)this, pose, dimensions.eyeHeight()));
        }

        return dimensions;
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"))
    public boolean canSwimInAir(FluidState instance, Operation<Boolean> original) {
        return !(this.getTransfurVariant() != null && this.getTransfurVariant().getChangedEntity().canSwimInFluidType(instance.getFluidType())) &&
                original.call(instance);
    }

    @Unique
    private boolean changed$isInWaterForTransfurSwimming() {
        return this.isInWater() ||
                this.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) ||
                this.isInFluidType(NeoForgeMod.WATER_TYPE.value());
    }

    @Unique
    private boolean changed$canTransfurUseWaterSwimming() {
        return this.getTransfurVariant() != null &&
                this.getTransfurVariant().getChangedEntity().isAllowedToSwim() &&
                this.canSwimInFluidType(NeoForgeMod.WATER_TYPE.value()) &&
                this.changed$isInWaterForTransfurSwimming();
    }

    @Override
    public boolean canStartSwimming() {
        return this.getTransfurVariant() != null ?
                super.canStartSwimming() || this.getTransfurVariant().getChangedEntity().canStartSwimming() :
                super.canStartSwimming();
    }

    @Override
    public boolean canSwimInFluidType(FluidType type) {
        return this.getTransfurVariant() != null ?
                super.canSwimInFluidType(type) || this.getTransfurVariant().getChangedEntity().canSwimInFluidType(type) :
                super.canSwimInFluidType(type);
    }

    @WrapMethod(method = "getFlyingSpeed")
    public float changed$getTransfurFlyingSpeed(Operation<Float> original) {
        return this.getTransfurVariant() != null ?
                this.getTransfurVariant().getChangedEntity().getFlyingSpeed() :
                original.call();
    }
}
