package net.changed.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedLootItemFunctions;
import net.changed.init.ChangedRegistry;
import net.changed.item.Syringe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class RandomVariantFunction extends LootItemConditionalFunction {
    public static final MapCodec<RandomVariantFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(ResourceLocation.CODEC.listOf().optionalFieldOf("variants", List.of()).forGetter(function -> function.variants))
                    .apply(instance, RandomVariantFunction::new));

    final List<ResourceLocation> variants;

    RandomVariantFunction(List<LootItemCondition> predicates, List<ResourceLocation> variants) {
        super(predicates);
        this.variants = variants.isEmpty()
                ? TransfurVariant.getPublicTransfurVariants().map(ChangedRegistry.TRANSFUR_VARIANT::getKey).toList()
                : List.copyOf(variants);
    }

    @Override
    public LootItemFunctionType<RandomVariantFunction> getType() {
        return ChangedLootItemFunctions.RANDOM_VARIANT.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        if (!variants.isEmpty()) {
            Syringe.setUnpureVariant(itemStack, variants.get(context.getRandom().nextInt(variants.size())));
        }
        return itemStack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<RandomVariantFunction.Builder> {
        private List<ResourceLocation> variants = new ArrayList<>();

        protected RandomVariantFunction.Builder getThis() {
            return this;
        }

        public RandomVariantFunction.Builder withVariant(TransfurVariant<?> variant) {
            this.variants.add(variant.getFormId());
            return this;
        }

        public RandomVariantFunction.Builder withVariant(ResourceLocation variant) {
            this.variants.add(variant);
            return this;
        }

        public RandomVariantFunction.Builder withAllVariants() {
            this.variants.addAll(TransfurVariant.getPublicTransfurVariants().map(ChangedRegistry.TRANSFUR_VARIANT::getKey).toList());
            return this;
        }

        public LootItemFunction build() {
            return buildTyped();
        }

        public RandomVariantFunction buildTyped() {
            return new RandomVariantFunction(this.getConditions(), this.variants);
        }
    }
}
