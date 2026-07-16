package net.changed.item;

import net.changed.block.IRobotCharger;
import net.changed.entity.robot.AbstractRobot;
import net.changed.entity.robot.ChargerType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class RoombaItem<T extends AbstractRobot> extends PlaceableEntity<T> {
    public RoombaItem(Properties builder, Supplier<EntityType<T>> entityType) {
        super(builder, entityType);
    }

    @Override
    protected void finalizeEntity(T entity, ItemStack itemStack) {
        super.finalizeEntity(entity, itemStack);
        entity.loadFromItemStack(itemStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        var blockState = level.getBlockState(context.getClickedPos());

        if (blockState.getBlock() instanceof IRobotCharger charger && charger.getChargerType() == ChargerType.ROOMBA) {
            var robot = this.placeAndShrink(placeContext);
            if (robot != null) {
                charger.acceptRobot(blockState, level, context.getClickedPos(), robot);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }
}
