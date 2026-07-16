package net.changed.network.packet;

import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.changed.entity.animation.AnimationCategory;
import net.changed.entity.animation.AnimationEvent;
import net.changed.entity.animation.AnimationParameters;
import net.changed.init.ChangedRegistry;
import net.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.changed.compat.DistExecutor;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class AnimationEventPacket<T extends AnimationParameters> implements ChangedPacket {
    private final int targetId;
    private final AnimationEvent<T> event;
    private final @Nullable AnimationCategory category;
    private final @Nullable T parameters;
    private final IntList propEntityIds;
    private final List<ItemStack> propItemStacks;

    public AnimationEventPacket(int targetId, AnimationEvent<T> event, @Nullable AnimationCategory category, @Nullable T parameters,
                                IntList propEntityIds,
                                List<ItemStack> propItemStacks) {
        this.targetId = targetId;
        this.event = event;
        this.category = category;
        this.parameters = parameters;
        this.propEntityIds = propEntityIds;
        this.propItemStacks = propItemStacks;
    }

    public AnimationEventPacket(FriendlyByteBuf buffer) {
        this.targetId = buffer.readInt();
        this.event = (AnimationEvent<T>) ChangedRegistry.ANIMATION_EVENTS.readRegistryObject(buffer);
        this.category = buffer.readOptional(FriendlyByteBuf::readUtf).map(AnimationCategory::fromSerial).flatMap(DataResult::result).orElse(null);
        this.parameters = buffer.readOptional(buf -> buf.readNbt()).map(nbt ->
                this.event.getCodec().parse(NbtOps.INSTANCE, nbt).getOrThrow(RuntimeException::new)
        ).orElse(null);
        this.propEntityIds = buffer.readIntIdList();
        var registries = UniversalDist.getLevel().registryAccess();
        this.propItemStacks = buffer.readList(buf -> buf.readNbt()).stream()
                .map(nbt -> ItemStack.parseOptional(registries, nbt))
                .toList();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.targetId);
        ChangedRegistry.ANIMATION_EVENTS.writeRegistryObject(buffer, this.event);
        buffer.writeOptional(Optional.ofNullable(category), (buf, cat) -> buf.writeUtf(cat.getSerializedName()));
        buffer.writeOptional(Optional.ofNullable(this.parameters), (buf, param) -> {
            buf.writeNbt((CompoundTag) this.event.getCodec().encodeStart(NbtOps.INSTANCE, param).getOrThrow(RuntimeException::new));
        });
        buffer.writeIntIdList(this.propEntityIds);
        var registries = UniversalDist.getLevel().registryAccess();
        buffer.writeCollection(this.propItemStacks.stream()
                .map(stack -> (CompoundTag)stack.saveOptional(registries))
                .toList(), (buf, tag) -> buf.writeNbt(tag));
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                final var entities = this.propEntityIds.intStream().mapToObj(level::getEntity).map(entity -> {
                    if (entity instanceof LivingEntity livingEntity)
                        return livingEntity;
                    return null;
                }).toList();

                if (level.getEntity(this.targetId) instanceof LivingEntity livingEntity) {
                    net.changed.client.animations.AnimationAssociations.dispatchAnimation(livingEntity, this.event, this.category, this.parameters, entities, this.propItemStacks);
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }

    public static class Builder<T extends AnimationParameters> {
        private final int targetId;
        private final AnimationEvent<T> event;
        private final @Nullable AnimationCategory category;
        private final @Nullable T parameters;
        private final IntList propEntities = new IntArrayList();
        private final List<ItemStack> propItems = new ArrayList<>();

        private Builder(int targetId, AnimationEvent<T> event, @Nullable AnimationCategory category, @Nullable T parameters) {
            this.targetId = targetId;
            this.event = event;
            this.category = category;
            this.parameters = parameters;
        }

        public static <T extends AnimationParameters> Builder<T> of(LivingEntity target, AnimationEvent<T> event) {
            return new Builder<>(target.getId(), event, null, null);
        }

        public static <T extends AnimationParameters> Builder<T> of(LivingEntity target, AnimationEvent<T> event, @Nullable T parameters) {
            return new Builder<>(target.getId(), event, null, parameters);
        }

        public static <T extends AnimationParameters> Builder<T> of(LivingEntity target, AnimationEvent<T> event, @Nullable AnimationCategory category, @Nullable T parameters) {
            return new Builder<>(target.getId(), event, category, parameters);
        }

        public Builder<T> addEntity(LivingEntity entity) {
            propEntities.add(entity.getId());
            return this;
        }

        public Builder<T> addItem(ItemStack itemStack) {
            propItems.add(itemStack);
            return this;
        }

        public AnimationEventPacket<T> build() {
            return new AnimationEventPacket<>(targetId, event, category, parameters, propEntities, propItems);
        }
    }
}
