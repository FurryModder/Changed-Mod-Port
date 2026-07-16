package net.changed.compat;

import java.util.function.Supplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class DistExecutor {
    private DistExecutor() {}

    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> runnable) {
        if (FMLEnvironment.dist == dist) {
            runnable.get().run();
        }
    }

    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<Supplier<T>> supplier) {
        return FMLEnvironment.dist == dist ? supplier.get().get() : null;
    }
}
