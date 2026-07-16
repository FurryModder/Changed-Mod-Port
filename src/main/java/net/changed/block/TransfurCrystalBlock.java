package net.changed.block;

import com.mojang.serialization.MapCodec;
import net.changed.entity.ChangedEntity;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedLatexTypes;
import net.changed.init.ChangedTags;
import net.changed.process.ProcessTransfur;
import net.changed.util.LevelUtil;
import net.changed.world.LatexCoverGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.changed.compat.PlantType;

import java.util.List;
import java.util.function.Supplier;

import static net.changed.block.AbstractDoubleTransfurCrystal.HALF;

public abstract class TransfurCrystalBlock extends BushBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private final Supplier<? extends TransfurVariant<?>> variant;
    private final Supplier<? extends Item> crystal;

    public TransfurCrystalBlock(Supplier<? extends TransfurVariant<?>> variant, Supplier<? extends Item> crystal, Properties p_53514_) {
        super(p_53514_.pushReaction(PushReaction.DESTROY));
        this.variant = variant;
        this.crystal = crystal;
    }

    public TransfurCrystalBlock(Supplier<? extends Item> crystal, Properties properties) {
        super(properties.pushReaction(PushReaction.DESTROY));
        this.variant = null;
        this.crystal = crystal;
    }

    @Override
    protected MapCodec<? extends TransfurCrystalBlock> codec() {
        return MapCodec.unit(this);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE_WHOLE;
    }

    protected boolean mayPlaceOn(BlockState otherBlock, BlockGetter level, BlockPos blockPos) {
        return otherBlock.is(ChangedTags.Blocks.GROWS_LATEX_CRYSTALS) ||
                AbstractLatexBlock.isSurfaceOfType(LatexCoverGetter.extendDefault(level), blockPos, Direction.DOWN, SupportType.RIGID, ChangedLatexTypes.DARK_LATEX.get());
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        if (!canSupportRigidBlock(level, blockPos.below()))
            return false;
        return blockState.is(ChangedTags.Blocks.GROWS_LATEX_CRYSTALS) ||
                AbstractLatexBlock.isSurfaceOfType(level, blockPos, Direction.DOWN, SupportType.RIGID, ChangedLatexTypes.DARK_LATEX.get());
    }

    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.get("latex_crystal");
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(variant.get(), TransfurContext.hazard(TransfurCause.CRYSTAL), 8.3f);
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));

        if (variant == null) return;

        if (entity instanceof LivingEntity le && !(entity instanceof ChangedEntity)) {
            if (!LevelUtil.isTouchingBlockInteraction(level, pos, state, entity))
                return;
            if (entity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player))
                return;
            if (!level.isClientSide) {
                ProcessTransfur.progressTransfur(le, this.makeAssimilationDecision(le));
            }

        }
    }

    public boolean shouldDrop(BlockState blockState) {
        return !blockState.getProperties().contains(HALF) || blockState.getValue(HALF) != DoubleBlockHalf.UPPER;
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder lootBuilder) {
        if (!shouldDrop(blockState))
            return List.of();

        var silkTouch = lootBuilder.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        if (EnchantmentHelper.getItemEnchantmentLevel(silkTouch, lootBuilder.getParameter(LootContextParams.TOOL)) > 0)
            return List.of(new ItemStack(this));

        if (this instanceof AbstractDoubleTransfurCrystal)
            return List.of(new ItemStack(crystal.get(), 2));
        else
            return List.of(new ItemStack(crystal.get(), 1));
    }
}
