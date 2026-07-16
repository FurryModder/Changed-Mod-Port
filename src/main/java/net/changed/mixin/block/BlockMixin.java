package net.changed.mixin.block;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.changed.client.LatexCoveredBlocksRenderer;
import net.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements ItemLike, net.neoforged.neoforge.common.extensions.IBlockExtension {
    private BlockMixin(Properties p_60452_) {
        super(p_60452_);
    }

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    public void fallOn(Level level, BlockState state, BlockPos blockPos, Entity entity, float distance, CallbackInfo callbackInfo) {
        if (state.getFluidState().is(ChangedTags.Fluids.LATEX))
            callbackInfo.cancel();
    }

    @WrapMethod(method = "shouldRenderFace")
    private static boolean shouldRenderLatexFace(BlockState blockState, BlockGetter level,
                                                 BlockPos blockPos, Direction normal,
                                                 BlockPos neighborBlockPos, Operation<Boolean> original) {
        var coverGetterOpt = LatexCoveredBlocksRenderer.getLatexCoverStateGetter();
        if (coverGetterOpt.isEmpty())
            return original.call(blockState, level, blockPos, normal, neighborBlockPos);
        
        var coverGetter = coverGetterOpt.get();
        var localCoverState = coverGetter.getLatexCover(blockPos);
        var occlusionBlock = localCoverState.getType().getBlock();
        if (occlusionBlock == null)
            return original.call(blockState, level, blockPos, normal, neighborBlockPos);

        var neighborCoverState = coverGetter.getLatexCover(neighborBlockPos);
        var neighborBlockState = coverGetter.getBlockState(neighborBlockPos);

        if (neighborCoverState.canOcclude(level, neighborBlockPos, localCoverState, blockPos)) {
            if (neighborBlockState.isAir() && blockState.isAir()) // Currently only allow states in air to occlude each other.
                return false;
        }

        // Trees our latex cover model as its block state counterpart
        var occlusionReference = occlusionBlock.defaultBlockState();
        return original.call(occlusionReference, level, blockPos, normal, neighborBlockPos);
    }
}
