package net.changed.init;

import net.changed.Changed;
import net.changed.recipe.InfuserRecipe;
import net.changed.recipe.PurifierRecipe;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedRecipeTypes {
    public static DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Changed.MODID);
    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String name) {
        return REGISTRY.register(name, () -> new RecipeType<T>() {
            public String toString() {
                return Changed.modResourceStr(name);
            }
        });
    }

    private static RecipeBookType registerBookType(String name) {
        return RecipeBookType.valueOf(name);
    }

    public static DeferredHolder<RecipeType<?>, RecipeType<InfuserRecipe>> INFUSER_RECIPE = register("infuser");
    public static DeferredHolder<RecipeType<?>, RecipeType<PurifierRecipe>> PURIFIER_RECIPE = register("purifier");

    public static final RecipeBookType INFUSER_BOOK = registerBookType( "CHANGED_INFUSER");
}
