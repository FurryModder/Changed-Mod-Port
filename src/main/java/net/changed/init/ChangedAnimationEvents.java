package net.changed.init;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.changed.Changed;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.ability.ILatexAssimilatedEntity;
import net.changed.entity.TransfurContext;
import net.changed.entity.animation.*;
import net.changed.entity.variant.TransfurVariant;
import net.changed.network.packet.AnimationEventPacket;
import net.minecraft.world.entity.LivingEntity;
import net.changed.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.List;

public class ChangedAnimationEvents {
    public static DeferredRegister<AnimationEvent<?>> REGISTRY = ChangedRegistry.ANIMATION_EVENTS.createDeferred(Changed.MODID);

    public static DeferredHolder<AnimationEvent<?>, AnimationEvent<TransfurAnimationParameters>> TRANSFUR = register("transfur", TransfurAnimationParameters.CODEC);

    public static DeferredHolder<AnimationEvent<?>, AnimationEvent<StasisAnimationParameters>> STASIS_IDLE = register("stasis_idle", StasisAnimationParameters.CODEC);
    public static DeferredHolder<AnimationEvent<?>, AnimationEvent<StunAnimationParameters>> SHOCK_STUN = register("shock_stun", StunAnimationParameters.CODEC);

    private static <T extends AnimationParameters> DeferredHolder<AnimationEvent<?>, AnimationEvent<T>> register(String name, Codec<T> parameters) {
        return REGISTRY.register(name, () -> new AnimationEvent<>(parameters));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEvent<T> event, @Nullable T parameters) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AnimationEventPacket<>(livingEntity.getId(), event, null, parameters, IntList.of(), List.of()));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEvent<T> event, AnimationCategory category, @Nullable T parameters) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AnimationEventPacket<>(livingEntity.getId(), event, category, parameters, IntList.of(), List.of()));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEventPacket<T> packet) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), packet);
    }

    public static void broadcastTransfurAnimation(LivingEntity livingEntity, TransfurVariant<?> variant, TransfurContext context) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server
        if (!livingEntity.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION)) return;

        if (context.source() != null)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                    AnimationEventPacket.Builder.of(livingEntity, TRANSFUR.get(), AnimationCategory.TRANSFUR,
                            new TransfurAnimationParameters(variant, context.cause())).addEntity(context.source()
                            .map(IAbstractChangedEntity::getEntity, ILatexAssimilatedEntity::getEntity)).build());
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                    AnimationEventPacket.Builder.of(livingEntity, TRANSFUR.get(), AnimationCategory.TRANSFUR,
                            new TransfurAnimationParameters(variant, context.cause())).build());
    }
}
