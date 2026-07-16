package net.changed.item;


import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedTabs;
import net.changed.process.ProcessTransfur;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class TransfurCrystalItem extends Item {
    private final List<Supplier<? extends TransfurVariant<?>>> variants;

    public TransfurCrystalItem(List<Supplier<? extends TransfurVariant<?>>> variants) {
        super(new Properties());
        this.variants = variants;
    }

    public TransfurCrystalItem(Supplier<? extends TransfurVariant<?>> variant) {
        this(List.of(variant));
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(Util.getRandom(variants, target.getRandom()).get(), TransfurContext.hazard(TransfurCause.CRYSTAL), 5.0f);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity source) {
        ProcessTransfur.progressTransfur(entity, this.makeAssimilationDecision(entity));
        return super.hurtEnemy(stack, entity, source);
    }
}
