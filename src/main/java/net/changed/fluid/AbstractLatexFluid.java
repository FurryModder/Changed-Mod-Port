package net.changed.fluid;

import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.latex.LatexType;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedDamageSources;
import net.changed.init.ChangedTags;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber
public abstract class AbstractLatexFluid extends BaseFlowingFluid {
    private final List<Supplier<? extends TransfurVariant<?>>> form;
    private final Supplier<? extends LatexType> gooType;

    protected AbstractLatexFluid(Properties properties, Supplier<? extends LatexType> gooType, List<Supplier<? extends TransfurVariant<?>>> form) {
        super(properties);
        this.gooType = gooType;
        this.form = form;
    }

    public static FluidType.Properties createProperties() {
        return FluidType.Properties.create()
                .density(6000)
                .viscosity(6000)
                .motionScale(-0.014D);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    public LatexType getLatexType() {
        return gooType.get();
    }

    public abstract boolean canEntityStandOn(LivingEntity entity);

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(Util.getRandom(form, target.getRandom()).get(),
                TransfurContext.hazard(TransfurCause.LATEX_PUDDLE), 5.0f);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity))
            return;

        Level level = livingEntity.level();
        AbstractLatexFluid fluid = null;
        BlockState state = Blocks.AIR.defaultBlockState();
        if (level.getFluidState(livingEntity.blockPosition()).getType() instanceof AbstractLatexFluid fluidFeet) {
            state = level.getBlockState(livingEntity.blockPosition());
            fluid = fluidFeet;
        }
        if (level.getFluidState(EntityUtil.getEyeBlock(livingEntity)).getType() instanceof AbstractLatexFluid fluidHead) {
            state = level.getBlockState(livingEntity.blockPosition());
            fluid = fluidHead;
        }

        if (fluid != null) {
            if (TransfurVariant.getEntityVariant(livingEntity) != null) {
                var living = livingEntity;
                var delta = living.getDeltaMovement();
                living.resetFallDistance();
                living.setDeltaMovement(living.getDeltaMovement().multiply(1.0, delta.y > 0.0 ? 1.1 : 0.5, 1.0));
            } else
                livingEntity.makeStuckInBlock(state, new Vec3(0.75, 0.75, 0.75));
        }

        if (livingEntity.isAlive() && !livingEntity.isDeadOrDying() && fluid != null) {
            LatexType latexType = LatexType.getEntityLatexType(livingEntity);
            if (latexType == null)
                ProcessTransfur.progressTransfur(livingEntity, fluid.makeAssimilationDecision(livingEntity));
            else if (fluid.getLatexType().isHostileTo(latexType))
                livingEntity.hurt(ChangedDamageSources.LATEX_FLUID.source(livingEntity.level().registryAccess()), 2.0f);
        }
    }

    private void fizz(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
    }

    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction != Direction.UP) {
            FluidState otherState = level.getFluidState(pos);
            if (this.is(ChangedTags.Fluids.LATEX) && otherState.is(FluidTags.LAVA)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, net.changed.compat.ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, pos,
                            Blocks.SOUL_SOIL.defaultBlockState()), 3);
                }

                this.fizz(level, pos);
                return;
            }
        }

        super.spreadTo(level, pos, blockState, direction, fluidState);
    }
}
