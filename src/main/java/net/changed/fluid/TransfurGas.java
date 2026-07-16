package net.changed.fluid;

import com.google.common.collect.ImmutableList;
import net.changed.entity.LivingEntityDataExtension;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.ai.NonLatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedItems;
import net.changed.init.ChangedTags;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber
public abstract class TransfurGas extends Gas {
    public final ImmutableList<Supplier<? extends TransfurVariant<?>>> variants;

    protected TransfurGas(Properties properties, Supplier<? extends TransfurVariant<?>> variant) {
        super(properties);
        this.variants = ImmutableList.of(variant);
    }

    protected TransfurGas(Properties properties, List<Supplier<? extends TransfurVariant<?>>> variants) {
        super(properties);
        this.variants = ImmutableList.copyOf(variants);
    }

    public static Optional<TransfurGas> validEntityInGas(LivingEntity entity) {
        if (entity instanceof Player player && player.getAbilities().invulnerable)
            return Optional.empty();
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.GAS_MASK.get()))
            return Optional.empty();
        if (!entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS))
            return Optional.empty();
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null)
            return Optional.empty();

        if (entity instanceof LivingEntityDataExtension ext)
            return ext.isEyeInGas(TransfurGas.class);
        return Optional.empty();
    }

    protected Optional<NonLatexAssimilationDecision<?>> makeAssimilationDecision(LivingEntity target) {
        return Util.getRandomSafe(variants, target.getRandom()).map(Supplier::get)
                .map(variant -> NonLatexAssimilationDecision.fromBlockOrItem(variant, TransfurCause.FACE_HAZARD, 8.0f, 1.0f));
    }

    @SubscribeEvent
    public static void onLivingUpdate(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity))
            return;

        validEntityInGas(entity).ifPresent(transfurGas -> {
            int air = entity.getAirSupply();
            int i = 0;
            air = i > 0 && entity.getRandom().nextInt(i + 1) > 0 ? air : air - 3;

            if(air <= 0) {
                air = 0;
                transfurGas.makeAssimilationDecision(entity).ifPresent(decision -> ProcessTransfur.progressTransfur(entity, decision));
            }

            entity.setAirSupply(air);
        });
    }
}
