package net.changed.block;

import net.changed.entity.robot.AbstractRobot;
import net.changed.entity.robot.ChargerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface IRobotCharger {
    ChargerType getChargerType();

    default void broadcastPosition(Level level, BlockPos pos) {
        broadcastPosition(level, pos, true);
    }

    default void broadcastPosition(Level level, BlockPos pos, boolean useable) {
        AABB aabb = new AABB(pos).inflate(32);
        level.getNearbyEntities(AbstractRobot.class, TargetingConditions.forNonCombat(), null, aabb).forEach(robot ->
                robot.broadcastNearbyCharger(pos, getChargerType(), useable));
    }

    void acceptRobot(BlockState state, Level level, BlockPos pos, AbstractRobot robot);

    void acceptRobotRemoved(BlockState state, Level level, BlockPos pos, @Nullable AbstractRobot oldRobot);
}
