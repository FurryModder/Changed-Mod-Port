package net.changed.block;

import net.changed.init.ChangedTags;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VentHatchBlock extends TrapDoorBlock {
    public VentHatchBlock(Properties properties) {
        super(BlockSetType.IRON, properties);
    }

    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        level.setBlock(blockPos, state, 2);
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        this.playSound(player, level, blockPos, state.getValue(OPEN));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof LivingEntity livingEntity) {
            if (EntityUtil.maybeGetOverlaying(livingEntity).getType().is(ChangedTags.EntityTypes.PUDDING))
                return Shapes.empty();
        }

        return super.getCollisionShape(state, level, blockPos, context);
    }
}
