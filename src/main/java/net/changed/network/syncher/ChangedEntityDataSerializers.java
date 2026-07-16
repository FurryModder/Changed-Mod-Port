package net.changed.network.syncher;

import net.changed.Changed;
import net.changed.entity.BasicPlayerInfo;
import net.changed.entity.ai.DarkLatexAttackCondition;
import net.changed.entity.ai.DarkLatexAttackType;
import net.changed.entity.ai.DarkLatexFavor;
import net.changed.entity.ai.DarkLatexTargetType;
import net.changed.entity.decoration.WallSignVariant;
import net.changed.init.ChangedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ChangedEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Changed.MODID);

    public static final EntityDataSerializer<BasicPlayerInfo> BASIC_PLAYER_INFO = new EntityDataSerializer<BasicPlayerInfo>() {
        private final StreamCodec<RegistryFriendlyByteBuf, BasicPlayerInfo> codec = StreamCodec.of(
                (buffer, info) -> {
                    var tag = new CompoundTag();
                    info.save(tag);
                    buffer.writeNbt(tag);
                },
                buffer -> {
                    BasicPlayerInfo info = new BasicPlayerInfo();
                    var tag = buffer.readNbt();
                    if (tag != null)
                        info.load(tag);
                    return info;
                });

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BasicPlayerInfo> codec() {
            return codec;
        }

        @Override
        public BasicPlayerInfo copy(BasicPlayerInfo info) {
            BasicPlayerInfo newInfo = new BasicPlayerInfo();
            newInfo.copyFrom(info);
            return newInfo;
        }
    };

    public static final EntityDataSerializer<WallSignVariant> WALL_SIGN_VARIANT = EntityDataSerializer.forValueType(ByteBufCodecs.idMapper(ChangedRegistry.WALL_SIGN_VARIANT.asIdMap()));
    public static final EntityDataSerializer<DarkLatexTargetType> DARK_LATEX_TARGET_TYPE = simpleEnum(DarkLatexTargetType.class);
    public static final EntityDataSerializer<DarkLatexAttackType> DARK_LATEX_ATTACK_TYPE = simpleEnum(DarkLatexAttackType.class);
    public static final EntityDataSerializer<DarkLatexAttackCondition> DARK_LATEX_ATTACK_CONDITION = simpleEnum(DarkLatexAttackCondition.class);
    public static final EntityDataSerializer<DarkLatexFavor> DARK_LATEX_FAVOR = simpleEnum(DarkLatexFavor.class);

    private static <T extends Enum<T>> EntityDataSerializer<T> simpleEnum(Class<T> type) {
        T[] values = type.getEnumConstants();
        return EntityDataSerializer.forValueType(ByteBufCodecs.VAR_INT.map(id -> values[id], value -> value.ordinal()));
    }

    static {
        REGISTRY.register("basic_player_info", () -> BASIC_PLAYER_INFO);
        REGISTRY.register("wall_sign_variant", () -> WALL_SIGN_VARIANT);
        REGISTRY.register("dark_latex_target_type", () -> DARK_LATEX_TARGET_TYPE);
        REGISTRY.register("dark_latex_attack_type", () -> DARK_LATEX_ATTACK_TYPE);
        REGISTRY.register("dark_latex_attack_condition", () -> DARK_LATEX_ATTACK_CONDITION);
        REGISTRY.register("dark_latex_favor", () -> DARK_LATEX_FAVOR);
    }
}
