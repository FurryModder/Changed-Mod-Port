package net.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Generator extends AbstractLargePanel {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final VoxelShape SHAPE_WHOLE = Block.box(-14.0D, 0.0D, 8.0D, 30.0D, 45.0D, 16.0D);

    public Generator() {
        super(Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 7.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_WHOLE);

        double x = 0.0D;
        double z = 0.0D;

        switch (state.getValue(FACING)) {
            case NORTH -> x = 1.0D;
            case EAST -> z = 1.0D;
            case SOUTH -> x = -1.0D;
            case WEST -> z = -1.0D;
        }

        switch (state.getValue(SECTION)) {
            case BOTTOM_LEFT -> { return shape.move(-x, 0.0D, -z); }
            case MIDDLE_LEFT -> { return shape.move(-x, -1.0D, -z); }
            case TOP_LEFT -> { return shape.move(-x, -2.0D, -z); }

            case BOTTOM_MIDDLE -> { return shape.move(0, 0.0D, 0); }
            case CENTER -> { return shape.move(0, -1.0D, 0); }
            case TOP_MIDDLE -> { return shape.move(0, -2.0D, 0); }

            case BOTTOM_RIGHT -> { return shape.move(x, 0.0D, z); }
            case MIDDLE_RIGHT -> { return shape.move(x, -1.0D, z); }
            case TOP_RIGHT -> { return shape.move(x, -2.0D, z); }
        }

        return shape;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }
}
