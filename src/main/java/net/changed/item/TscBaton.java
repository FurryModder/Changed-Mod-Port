package net.changed.item;

import net.changed.Changed;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class TscBaton extends TscWeapon implements SpecializedItemRendering {
    public TscBaton() {
        super(new Properties().durability(500));
    }

    private static final ResourceLocation BATON_IN_HAND = Changed.modResource("item/tsc_baton_in_hand");

    @Override
    public ResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type) {
        return SpecializedItemRendering.isGUI(type) ? null : BATON_IN_HAND;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> loader) {
        loader.accept(BATON_IN_HAND);
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
        return 1 + Tiers.IRON.getAttackDamageBonus();
    }

    @Override
    public double attackSpeed() {
        return -2.4;
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }
}
