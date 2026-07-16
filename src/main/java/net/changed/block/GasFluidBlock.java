package net.changed.block;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class GasFluidBlock extends LiquidBlock {
    public GasFluidBlock(Supplier<? extends FlowingFluid> fluid) {
        super(fluid.get(), Properties.of().replaceable().strength(100f));
    }
}
