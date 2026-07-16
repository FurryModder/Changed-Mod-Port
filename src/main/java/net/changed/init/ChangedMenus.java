package net.changed.init;

import net.changed.Changed;
import net.changed.world.inventory.*;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Changed.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AbilityRadialMenu>> ABILITY_RADIAL = register("ability_radial", AbilityRadialMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<HairStyleRadialMenu>> HAIRSTYLE_RADIAL = register("hairstyle_radial", HairStyleRadialMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<SpecialStateRadialMenu>> SPECIAL_RADIAL = register("special_radial", SpecialStateRadialMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ComputerMenu>> COMPUTER = register("computer", ComputerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<InfuserMenu>> INFUSER = register("infuser", InfuserMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<PurifierMenu>> PURIFIER = register("purifier", PurifierMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<KeypadMenu>> KEYPAD = register("keypad", KeypadMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ClipboardMenu>> CLIPBOARD = register("clipboard", ClipboardMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<NoteMenu>> NOTE = register("note", NoteMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<StasisChamberMenu>> STASIS_CHAMBER = register("stasis_chamber", StasisChamberMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<AccessoryAccessMenu>> ACCESSORY_ACCESS = register("accessory_access", AccessoryAccessMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<TamedDarkLatexMenu>> TAMED_DARK_LATEX = register("tamed_dark_latex", TamedDarkLatexMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<TamedDarkLatexInventoryMenu>> TAMED_DARK_LATEX_INVENTORY = register("tamed_dark_latex_inventory", TamedDarkLatexInventoryMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, IContainerFactory<T> containerFactory) {
        return REGISTRY.register(name, () -> new MenuType<>(containerFactory, FeatureFlagSet.of()));
    }

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, IContainerFactory<T> containerFactory, FeatureFlagSet requiredFlags) {
        return REGISTRY.register(name, () -> new MenuType<>(containerFactory, requiredFlags));
    }
}
