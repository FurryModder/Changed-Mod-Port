package net.changed.item;

import net.changed.client.ChangedClient;
import net.changed.entity.decoration.WallSign;
import net.changed.entity.decoration.WallSignVariant;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedRegistry;
import net.changed.init.ChangedTags;
import net.changed.util.TagUtil;
import net.changed.util.UniversalDist;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WallSignItem extends Item {
    public Optional<WallSignVariant> getVariant() {
        return ChangedRegistry.WALL_SIGN_VARIANT.get().getValues().stream()
                .filter(variant -> variant.getItem() == this)
                .findFirst();
    }

    public WallSignItem(Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos blockpos1 = blockpos.relative(direction);
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
            return InteractionResult.FAIL;
        } else {
            Level level = context.getLevel();
            return this.getVariant().flatMap(variant -> WallSign.create(level, blockpos1, direction, variant)).map(wallSign -> {
                CompoundTag compoundtag = TagUtil.getCustomData(itemstack);
                if (compoundtag != null) {
                    EntityType.updateCustomEntityTag(level, player, wallSign, CustomData.of(compoundtag));
                }

                if (wallSign.survives()) {
                    if (!level.isClientSide) {
                        wallSign.playPlacementSound();
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, wallSign.position());
                        level.addFreshEntity(wallSign);
                    }

                    itemstack.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                    return InteractionResult.CONSUME;
                }
            }).orElse(InteractionResult.FAIL);
        }
    }

    protected boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos blockPos) {
        return !direction.getAxis().isVertical() && player.mayUseItemAt(blockPos, direction, itemStack);
    }

    public static void fillItemList(Predicate<WallSignVariant> predicate, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ChangedRegistry.WALL_SIGN_VARIANT.get().getValues().stream()
                .filter(predicate).forEach(variant -> {
                    output.accept(variant.getItem());
                });
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ChangedClient.itemEntityRenderer.get();
            }
        });
    }
}
