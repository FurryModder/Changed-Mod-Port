package net.changed.item;

import net.changed.Changed;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.function.Consumer;

public class TscShield extends TscWeapon implements SpecializedItemRendering {
    public TscShield() {
        super(new Properties().durability(500));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    private static final ResourceLocation SHIELD_IN_HAND = Changed.modResource("item/tsc_shield_in_hand");

    @Override
    public ResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type) {
        return SpecializedItemRendering.isGUI(type) ? null : SHIELD_IN_HAND;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> loader) {
        loader.accept(SHIELD_IN_HAND);
    }

    public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity source) {
        sweepWeapon(source, attackRange());
        applyShock(enemy, attackStun());
        itemStack.hurtAndBreak(1, source, EquipmentSlot.MAINHAND);
        return true;
    }

    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return blockState.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
    }

    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
        if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(2, entity, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.swingTime > 0)
            return true;
        sweepWeapon(entity, attackRange());
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public int attackStun() {
        return 5;
    }

    @Override
    public double attackDamage() {
        return Tiers.STONE.getAttackDamageBonus();
    }

    @Override
    public double attackSpeed() {
        return -2.4;
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility toolAction) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @EventBusSubscriber
    public static class ShieldEvent {
        @SubscribeEvent
        public static void onShieldBlock(LivingShieldBlockEvent event) {
            if (event.getEntity().getUseItem().is(ChangedItems.TSC_SHIELD.get())) {
                if (event.getDamageSource().getEntity() instanceof LivingEntity source) {
                    TscWeapon.applyShock(source, ChangedItems.TSC_SHIELD.get().attackStun());
                    if (TransfurVariant.getEntityVariant(source) != null)
                        source.hurt(event.getEntity().level().damageSources().mobAttack(event.getEntity()), 1);
                }
            }
        }
    }
}
