package net.changed.item;

import net.changed.entity.latex.LatexType;
import net.changed.fluid.AbstractLatexFluid;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public class AbstractLatexBucket extends BucketItem {
    public final Supplier<? extends AbstractLatexFluid> fluid;
    public final Supplier<? extends LatexType> latexType;

    public AbstractLatexBucket(Supplier<? extends AbstractLatexFluid> supplier, Supplier<? extends LatexType> latexType) {
        super(supplier.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON));
        this.fluid = supplier;
        this.latexType = latexType;
    }

    public static Supplier<AbstractLatexBucket> from(Supplier<? extends AbstractLatexFluid> fluid, Supplier<? extends LatexType> latexType) {
        return () -> new AbstractLatexBucket(fluid, latexType);
    }

    public LatexType getLatexType() {
        return latexType.get();
    }

}
