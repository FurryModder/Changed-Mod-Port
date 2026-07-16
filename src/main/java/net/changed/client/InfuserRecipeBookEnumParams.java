package net.changed.client;

import net.changed.init.ChangedItems;
import net.changed.init.ChangedTransfurVariants;
import net.changed.item.Syringe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Supplier;

public final class InfuserRecipeBookEnumParams {
    private InfuserRecipeBookEnumParams() {}

    public static Object getSearchIcon(int index, Class<?> type) {
        return iconSupplier(index, type, () -> List.of(new ItemStack(Items.COMPASS)));
    }

    public static Object getDarkLatexIcon(int index, Class<?> type) {
        return iconSupplier(index, type, () -> List.of(new ItemStack(ChangedItems.DARK_LATEX_GOO.get())));
    }

    public static Object getWhiteLatexIcon(int index, Class<?> type) {
        return iconSupplier(index, type, () -> List.of(new ItemStack(ChangedItems.WHITE_LATEX_GOO.get())));
    }

    public static Object getAquaticIcon(int index, Class<?> type) {
        return iconSupplier(index, type, () -> List.of(new ItemStack(Items.TROPICAL_FISH_BUCKET)));
    }

    public static Object getAerialIcon(int index, Class<?> type) {
        return iconSupplier(index, type, () -> List.of(new ItemStack(Items.ELYTRA)));
    }

    public static Object getGenderedIcon(int index, Class<?> type) {
        return iconSupplier(index, type, () -> List.of(
                Syringe.setVariant(new ItemStack(ChangedItems.LATEX_SYRINGE.get()), ChangedTransfurVariants.LATEX_SHARK.getId()),
                Syringe.setVariant(new ItemStack(ChangedItems.LATEX_SYRINGE.get()), ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE.getId())));
    }

    private static Object iconSupplier(int index, Class<?> type, Supplier<List<ItemStack>> supplier) {
        if (index != 0) {
            throw new IllegalArgumentException("Unexpected RecipeBookCategories constructor parameter index: " + index);
        }

        return type.cast(supplier);
    }
}
