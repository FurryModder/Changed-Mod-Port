package net.changed.init;

import net.changed.Changed;
import net.changed.recipe.InfuserRecipe;
import net.changed.recipe.ContainerFillingRecipe;
import net.changed.recipe.PurifierRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedRecipeSerializers {
    public static DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Changed.MODID);

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfuserRecipe>> INFUSER_RECIPE = REGISTRY.register("infuser", InfuserRecipe.Serializer::new);
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PurifierRecipe>> PURIFIER_RECIPE = REGISTRY.register("purifier", PurifierRecipe.Serializer::new);

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ContainerFillingRecipe>> CONTAINER_FILL_RECIPE = REGISTRY.register("crafting_container_fill", ContainerFillingRecipe.Serializer::new);
}
