package net.changed.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedLootItemFunctions;
import net.changed.item.Syringe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public class SetVariantFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetVariantFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(ResourceLocation.CODEC.optionalFieldOf("variant").forGetter(function -> Optional.ofNullable(function.variant)))
                    .apply(instance, SetVariantFunction::new));

    final ResourceLocation variant;

    SetVariantFunction(List<LootItemCondition> predicates, Optional<ResourceLocation> variant) {
        super(predicates);
        this.variant = variant.orElse(null);
    }

    @Override
    public LootItemFunctionType<SetVariantFunction> getType() {
        return ChangedLootItemFunctions.SET_VARIANT.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        if (variant != null)
            Syringe.setUnpureVariant(itemStack, variant);
        return itemStack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetVariantFunction.Builder> {
        private ResourceLocation variant = null;

        protected SetVariantFunction.Builder getThis() {
            return this;
        }

        public SetVariantFunction.Builder withVariant(TransfurVariant<?> variant) {
            this.variant = variant.getFormId();
            return this;
        }

        public SetVariantFunction.Builder withVariant(ResourceLocation variant) {
            this.variant = variant;
            return this;
        }

        public LootItemFunction build() {
            return new SetVariantFunction(this.getConditions(), Optional.ofNullable(this.variant));
        }
    }
}
