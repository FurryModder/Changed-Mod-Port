package net.changed.init;

import net.changed.Changed;
import net.changed.entity.decoration.WallSignVariant;
import net.changed.item.WallSignItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedWallSigns {
    public static final DeferredRegister<WallSignVariant> REGISTRY = ChangedRegistry.WALL_SIGN_VARIANT.createDeferred(Changed.MODID);

    private static DeferredHolder<WallSignVariant, WallSignVariant> register(int width, int height, String name) {
        final DeferredHolder<Item, Item> item = ChangedItems.REGISTRY.register("wall_sign_" + name,
                () -> new WallSignItem(new Item.Properties()));
        return REGISTRY.register(name, () -> new WallSignVariant(width, height, item));
    }

    public static final DeferredHolder<WallSignVariant, WallSignVariant> DO_NOT_TOUCH = register(32, 24, "do_not_touch");
    public static final DeferredHolder<WallSignVariant, WallSignVariant> SQUID = register(32, 24, "squid");
    public static final DeferredHolder<WallSignVariant, WallSignVariant> PROTOTYPE = register(32, 24, "prototype");
    public static final DeferredHolder<WallSignVariant, WallSignVariant> CAT = register(32, 24, "cat");
    public static final DeferredHolder<WallSignVariant, WallSignVariant> DO_NOT_TOUCH_LATEXES = register(20, 40, "do_not_touch_latexes");
    public static final DeferredHolder<WallSignVariant, WallSignVariant> DO_NOT_SPEAK_WITH_DARK_LATEXES = register(20, 40, "do_not_speak_with_dark_latexes");
}
