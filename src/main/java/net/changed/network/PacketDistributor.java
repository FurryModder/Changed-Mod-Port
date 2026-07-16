package net.changed.network;

import java.util.function.Supplier;

public final class PacketDistributor {
    public static final TargetFactory ALL = new TargetFactory(TargetKind.ALL);
    public static final TargetFactory PLAYER = new TargetFactory(TargetKind.PLAYER);
    public static final TargetFactory SERVER = new TargetFactory(TargetKind.SERVER);
    public static final TargetFactory TRACKING_ENTITY = new TargetFactory(TargetKind.TRACKING_ENTITY);
    public static final TargetFactory TRACKING_ENTITY_AND_SELF = new TargetFactory(TargetKind.TRACKING_ENTITY_AND_SELF);

    private PacketDistributor() {}

    public enum TargetKind {
        ALL,
        PLAYER,
        SERVER,
        TRACKING_ENTITY,
        TRACKING_ENTITY_AND_SELF
    }

    public record PacketTarget(TargetKind kind, Supplier<?> target) {}

    public static class TargetFactory {
        private final TargetKind kind;

        private TargetFactory(TargetKind kind) {
            this.kind = kind;
        }

        public PacketTarget noArg() {
            return new PacketTarget(kind, () -> null);
        }

        public PacketTarget with(Supplier<?> target) {
            return new PacketTarget(kind, target);
        }
    }
}
