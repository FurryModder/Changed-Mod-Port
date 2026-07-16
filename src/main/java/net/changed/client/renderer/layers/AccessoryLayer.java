package net.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.client.renderer.accessory.AccessoryRenderer;
import net.changed.data.AccessorySlotContext;
import net.changed.data.AccessorySlots;
import net.changed.entity.AccessoryEntities;
import net.changed.util.Cacheable;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class AccessoryLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
    private static final Map<ItemLike, Cacheable<AccessoryRenderer>> RENDERERS = new HashMap<>();
    private static final List<Set<ItemLike>> RENDERER_ORDER = new ArrayList<>();
    private static final Cacheable<Function<ItemLike, Integer>> renderOrderCache = Cacheable.of(() ->
            Util.memoize((itemLike) -> {
                for (int i = 0; i < RENDERER_ORDER.size(); ++i)
                    if (RENDERER_ORDER.get(i).contains(itemLike))
                        return i;
                return RENDERER_ORDER.size(); // Point to the end of the order, so that unspecified items render on top
            })
    );

    public enum RenderOrder {
        /**
         * The given item should render before the reference item, therefor appearing below
         */
        BELOW,
        /**
         * The given item is not likely to rendering with the reference item, but is equivalent to its order
         */
        SAME,
        /**
         * The given item should render after the reference item, therefor appearing above
         */
        ABOVE
    }

    public static void registerRenderer(ItemLike item, Supplier<AccessoryRenderer> renderer) {
        RENDERERS.put(item, Cacheable.of(renderer));
    }

    public static void registerItemRenderOrder(Collection<ItemLike> items, RenderOrder order, ItemLike referenceItem) {
        int referenceSet = -1;
        for (int i = 0; i < RENDERER_ORDER.size(); ++i)
            if (RENDERER_ORDER.get(i).contains(referenceItem))
                referenceSet = i;

        if (referenceSet == -1) {
            RENDERER_ORDER.add(new HashSet<>(Collections.singleton(referenceItem)));
            referenceSet = RENDERER_ORDER.size() - 1;
        }

        switch (order) {
            case SAME -> RENDERER_ORDER.get(referenceSet).addAll(items);
            case ABOVE -> {
                if (referenceSet == RENDERER_ORDER.size() - 1)
                    RENDERER_ORDER.add(new HashSet<>());
                RENDERER_ORDER.get(referenceSet + 1).addAll(items);
            }
            case BELOW -> {
                if (referenceSet == 0) {
                    RENDERER_ORDER.add(0, new HashSet<>());
                    referenceSet++;
                }
                RENDERER_ORDER.get(referenceSet - 1).addAll(items);
            }
        }

        renderOrderCache.clear();
    }

    public static void registerItemRenderOrder(ItemLike item, RenderOrder order, ItemLike referenceItem) {
        registerItemRenderOrder(Collections.singleton(item), order, referenceItem);
    }

    public static Optional<AccessoryRenderer> getRenderer(ItemLike item) {
        return Optional.ofNullable(RENDERERS.get(item)).map(Supplier::get);
    }

    private final RenderLayerParent<T, M> parent;

    public AccessoryLayer(RenderLayerParent<T, M> parent) {
        super(parent);
        this.parent = parent;
    }

    private int findOrderedItemIndex(ItemLike item) {
        return renderOrderCache.get().apply(item);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        final var slotTypePredicate = AccessoryEntities.INSTANCE.canEntityTypeUseSlot(AccessoryEntities.getApparentEntityType(entity));
        AccessorySlots.getForEntity(entity).ifPresent(slots -> {
            slots.getSlotTypes().map(slotType -> {
                var stack = slots.getItem(slotType).orElse(ItemStack.EMPTY);
                if (stack.isEmpty())
                    return null;
                if (!RENDERERS.containsKey(stack.getItem()))
                    return null;
                if (!slotTypePredicate.test(slotType) || !slotType.canHoldItem(stack, entity))
                    return null; // Ensure lag doesn't crash with an invalid slot
                return new AccessorySlotContext<>(entity, slotType, stack);
            }).filter(Objects::nonNull).sorted((leftSlotContext, rightSlotContext) -> {
                int slotLeft = findOrderedItemIndex(leftSlotContext.stack().getItem());
                int slotRight = findOrderedItemIndex(rightSlotContext.stack().getItem());
                return Integer.compare(slotLeft, slotRight);
            }).forEach(slotContext -> {
                RENDERERS.get(slotContext.stack().getItem()).get()
                        .render(slotContext, poseStack, this.parent, buffers, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            });
        });
    }

    @Override
    public void renderFirstPersonOnArms(PoseStack poseStack, MultiBufferSource buffers, int packedLight, T entity, HumanoidArm arm, PartPose armPose, float partialTick) {
        final var slotTypePredicate = AccessoryEntities.INSTANCE.canEntityTypeUseSlot(AccessoryEntities.getApparentEntityType(entity));
        AccessorySlots.getForEntity(entity).ifPresent(slots -> {
            slots.getSlotTypes().map(slotType -> {
                var stack = slots.getItem(slotType).orElse(ItemStack.EMPTY);
                if (stack.isEmpty())
                    return null;
                if (!RENDERERS.containsKey(stack.getItem()))
                    return null;
                if (!slotTypePredicate.test(slotType) || !slotType.canHoldItem(stack, entity))
                    return null; // Ensure lag doesn't crash with an invalid slot
                return new AccessorySlotContext<>(entity, slotType, stack);
            }).filter(Objects::nonNull).sorted((leftSlotContext, rightSlotContext) -> {
                int slotLeft = findOrderedItemIndex(leftSlotContext.stack().getItem());
                int slotRight = findOrderedItemIndex(rightSlotContext.stack().getItem());
                return Integer.compare(slotLeft, slotRight);
            }).forEach(slotContext -> {
                RENDERERS.get(slotContext.stack().getItem()).get()
                        .renderFirstPersonOnArms(slotContext, poseStack, this.parent, buffers, packedLight, arm, armPose, partialTick);
            });
        });
    }
}
