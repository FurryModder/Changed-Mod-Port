package net.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public enum ThreeXThreeSection implements StringRepresentable {
    BACK_BOTTOM_LEFT("back_bottom_left", ZAxis.BACK, YAxis.BOTTOM, XAxis.LEFT),
    BACK_MIDDLE_LEFT("back_middle_left", ZAxis.BACK, YAxis.MIDDLE, XAxis.LEFT),
    BACK_TOP_LEFT("back_top_left", ZAxis.BACK, YAxis.TOP, XAxis.LEFT),
    BACK_TOP_MIDDLE("back_top_middle", ZAxis.BACK, YAxis.TOP, XAxis.MIDDLE),
    BACK_TOP_RIGHT("back_top_right", ZAxis.BACK, YAxis.TOP, XAxis.RIGHT),
    BACK_MIDDLE_RIGHT("back_middle_right", ZAxis.BACK, YAxis.MIDDLE, XAxis.RIGHT),
    BACK_BOTTOM_RIGHT("back_bottom_right", ZAxis.BACK, YAxis.BOTTOM, XAxis.RIGHT),
    BACK_BOTTOM_MIDDLE("back_bottom_middle", ZAxis.BACK, YAxis.BOTTOM, XAxis.MIDDLE),
    BACK_MIDDLE_MIDDLE("back_middle_middle", ZAxis.BACK, YAxis.MIDDLE, XAxis.MIDDLE),

    MIDDLE_BOTTOM_LEFT("middle_bottom_left", ZAxis.MIDDLE, YAxis.BOTTOM, XAxis.LEFT),
    MIDDLE_MIDDLE_LEFT("middle_middle_left", ZAxis.MIDDLE, YAxis.MIDDLE, XAxis.LEFT),
    MIDDLE_TOP_LEFT("middle_top_left", ZAxis.MIDDLE, YAxis.TOP, XAxis.LEFT),
    MIDDLE_TOP_MIDDLE("middle_top_middle", ZAxis.MIDDLE, YAxis.TOP, XAxis.MIDDLE),
    MIDDLE_TOP_RIGHT("middle_top_right", ZAxis.MIDDLE, YAxis.TOP, XAxis.RIGHT),
    MIDDLE_MIDDLE_RIGHT("middle_middle_right", ZAxis.MIDDLE, YAxis.MIDDLE, XAxis.RIGHT),
    MIDDLE_BOTTOM_RIGHT("middle_bottom_right", ZAxis.MIDDLE, YAxis.BOTTOM, XAxis.RIGHT),
    MIDDLE_BOTTOM_MIDDLE("middle_bottom_middle", ZAxis.MIDDLE, YAxis.BOTTOM, XAxis.MIDDLE),
    CENTER("center", ZAxis.MIDDLE, YAxis.MIDDLE, XAxis.MIDDLE),

    FRONT_BOTTOM_LEFT("front_bottom_left", ZAxis.FRONT, YAxis.BOTTOM, XAxis.LEFT),
    FRONT_MIDDLE_LEFT("front_middle_left", ZAxis.FRONT, YAxis.MIDDLE, XAxis.LEFT),
    FRONT_TOP_LEFT("front_top_left", ZAxis.FRONT, YAxis.TOP, XAxis.LEFT),
    FRONT_TOP_MIDDLE("front_top_middle", ZAxis.FRONT, YAxis.TOP, XAxis.MIDDLE),
    FRONT_TOP_RIGHT("front_top_right", ZAxis.FRONT, YAxis.TOP, XAxis.RIGHT),
    FRONT_MIDDLE_RIGHT("front_middle_right", ZAxis.FRONT, YAxis.MIDDLE, XAxis.RIGHT),
    FRONT_BOTTOM_RIGHT("front_bottom_right", ZAxis.FRONT, YAxis.BOTTOM, XAxis.RIGHT),
    FRONT_BOTTOM_MIDDLE("front_bottom_middle", ZAxis.FRONT, YAxis.BOTTOM, XAxis.MIDDLE),
    FRONT_MIDDLE_MIDDLE("front_middle_middle", ZAxis.FRONT, YAxis.MIDDLE, XAxis.MIDDLE);

    ThreeXThreeSection(String serialName, ZAxis zAxis, YAxis yAxis, XAxis xAxis) {
        this.serialName = serialName;
        this.yAxis = yAxis;
        this.xAxis = xAxis;
        this.zAxis = zAxis;
    }

    public enum YAxis {
        BOTTOM, MIDDLE, TOP;

        public int relativeTo(YAxis other) {
            return switch (this) {
                case BOTTOM -> switch (other) {
                    case BOTTOM -> 0;
                    case MIDDLE -> 1;
                    case TOP -> 2;
                };
                case MIDDLE -> switch (other) {
                    case BOTTOM -> -1;
                    case MIDDLE -> 0;
                    case TOP -> 1;
                };
                case TOP -> switch (other) {
                    case BOTTOM -> -2;
                    case MIDDLE -> -1;
                    case TOP -> 0;
                };
            };
        }
    }

    public enum XAxis {
        LEFT, MIDDLE, RIGHT;

        public int relativeTo(XAxis other) {
            return switch (this) {
                case LEFT -> switch (other) {
                    case LEFT -> 0;
                    case MIDDLE -> 1;
                    case RIGHT -> 2;
                };
                case MIDDLE -> switch (other) {
                    case LEFT -> -1;
                    case MIDDLE -> 0;
                    case RIGHT -> 1;
                };
                case RIGHT -> switch (other) {
                    case LEFT -> -2;
                    case MIDDLE -> -1;
                    case RIGHT -> 0;
                };
            };
        }
    }

    public enum ZAxis {
        BACK, MIDDLE, FRONT;

        public int relativeTo(ZAxis other) {
            return switch (this) {
                case BACK -> switch (other) {
                    case BACK -> 0;
                    case MIDDLE -> 1;
                    case FRONT -> 2;
                };
                case MIDDLE -> switch (other) {
                    case BACK -> -1;
                    case MIDDLE -> 0;
                    case FRONT -> 1;
                };
                case FRONT -> switch (other) {
                    case BACK -> -2;
                    case MIDDLE -> -1;
                    case FRONT -> 0;
                };
            };
        }
    }

    private final String serialName;
    public final YAxis yAxis;
    public final XAxis xAxis;
    public final ZAxis zAxis;

    @NotNull
    @Override
    public String getSerializedName() {
        return serialName;
    }

    @NotNull
    public Collection<ThreeXThreeSection> getOtherValues() {
        return Arrays.stream(values()).filter(section -> section != this).collect(Collectors.toSet());
    }

    /**
     * Gets a position relative to the current quarter and position
     * @param current position of the current quarter
     * @param facing perpendicular direction of the quarter
     * @param other quarter to find the position of
     * @return BlockPos relative to the current position
     */
    @NotNull
    public BlockPos getRelative(BlockPos current, Direction facing, ThreeXThreeSection other) {
        if (other == this || facing.getAxis() == Direction.Axis.Y)
            return current;

        int x = this.xAxis.relativeTo(other.xAxis);
        int y = this.yAxis.relativeTo(other.yAxis);
        int z = this.zAxis.relativeTo(other.zAxis);

        return switch (facing) {
            case NORTH -> current.offset(-x, y, -z);
            case EAST -> current.offset(z, y, -x);
            case SOUTH -> current.offset(x, y, z);
            case WEST -> current.offset(-z, y, x);
            default -> current;
        };
    }

    /**
     * Gets an offset relative to the current quarter
     * @param facing perpendicular direction of the quarter
     * @param other quarter to find the position of
     * @return Vec3i offset relative to the other section
     */
    @NotNull
    public Vec3i getOffset(Direction facing, ThreeXThreeSection other) {
        if (other == this || facing.getAxis() == Direction.Axis.Y)
            return Vec3i.ZERO;

        int x = this.xAxis.relativeTo(other.xAxis);
        int y = this.yAxis.relativeTo(other.yAxis);
        int z = this.zAxis.relativeTo(other.zAxis);

        return switch (facing) {
            case NORTH -> new Vec3i(-x, y, -z);
            case EAST -> new Vec3i(z, y, -x);
            case SOUTH -> new Vec3i(x, y, z);
            case WEST -> new Vec3i(-z, y, x);
            default -> Vec3i.ZERO;
        };
    }

    public boolean isOnAxis(ThreeXThreeSection other, Direction facing, Direction.Axis axis) {
        if (this == other)
            return true;

        boolean xMatch = this.xAxis == other.xAxis;
        boolean yMatch = this.yAxis == other.yAxis;
        boolean zMatch = this.zAxis == other.zAxis;

        if (axis != Direction.Axis.Y) {
            switch (axis) {
                case X: switch (facing) {
                    case EAST, WEST -> axis = Direction.Axis.Z;
                } break;
                case Z: switch (facing) {
                    case EAST, WEST -> axis = Direction.Axis.X;
                } break;
            }
        }

        return switch (axis) {
            case X -> !xMatch && yMatch && zMatch;
            case Y -> xMatch && !yMatch && zMatch;
            case Z -> xMatch && yMatch && !zMatch;
        };
    }

    public boolean isRelative(ThreeXThreeSection other, Direction facing, Direction where) {
        if (!this.isOnAxis(other, facing, where.getAxis()))
            return false;

        if (this == other)
            return false;

        return switch (where.getAxis()) {
            case X -> switch (facing) {
                case NORTH -> (this.yAxis == other.yAxis) && (this.xAxis.relativeTo(other.xAxis) == -where.getStepX());
                case SOUTH -> (this.yAxis == other.yAxis) && (this.xAxis.relativeTo(other.xAxis) == where.getStepX());
                case EAST -> (this.yAxis == other.yAxis) && (this.zAxis.relativeTo(other.zAxis) == where.getStepX());
                case WEST -> (this.yAxis == other.yAxis) && (this.zAxis.relativeTo(other.zAxis) == -where.getStepX());
                default -> false;
            };
            case Y -> (this.yAxis.relativeTo(other.yAxis) == where.getStepY()) && (this.xAxis == other.xAxis);
            case Z -> switch (facing) {
                case NORTH -> (this.yAxis == other.yAxis) && (this.zAxis.relativeTo(other.zAxis) == -where.getStepZ());
                case SOUTH -> (this.yAxis == other.yAxis) && (this.zAxis.relativeTo(other.zAxis) == where.getStepZ());
                case EAST -> (this.yAxis == other.yAxis) && (this.xAxis.relativeTo(other.xAxis) == -where.getStepZ());
                case WEST -> (this.yAxis == other.yAxis) && (this.xAxis.relativeTo(other.xAxis) == where.getStepZ());
                default -> false;
            };
        };
    }
}
