package net.changed.mixin.gui;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import net.changed.client.RecipeCategories;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
    @Inject(method = "categorizeAndGroupRecipes", at = @At("RETURN")) // Inject multicategory recipes after processing
    private static void categorizeAndGroupRecipes(Iterable<RecipeHolder<?>> recipes, CallbackInfoReturnable<Map<RecipeBookCategories, List<List<RecipeHolder<?>>>>> ci) {
        Map<RecipeBookCategories, List<List<RecipeHolder<?>>>> map = ci.getReturnValue();
        Table<RecipeBookCategories, String, List<RecipeHolder<?>>> table = HashBasedTable.create();

        for(RecipeHolder<?> recipeHolder : recipes) {
            Recipe<?> recipe = recipeHolder.value();
            if (!recipe.isSpecial() && !recipe.isIncomplete()) {
                List<RecipeBookCategories> categories = null;
                for (var func : RecipeCategories.MULTICATEGORY_FINDER) {
                    categories = func.apply(recipe);
                    if (categories != null && !categories.isEmpty())
                        break;
                }
                if (categories == null || categories.isEmpty())
                    continue;

                for (var recipebookcategories : categories) {
                    String s = recipe.getGroup().isEmpty() ? recipeHolder.id().toString() : recipe.getGroup();
                    if (s.isEmpty()) {
                        map.computeIfAbsent(recipebookcategories, (p_90645_) -> Lists.newArrayList()).add(ImmutableList.of(recipeHolder));
                    } else {
                        List<RecipeHolder<?>> list = table.get(recipebookcategories, s);
                        if (list == null) {
                            list = Lists.newArrayList();
                            table.put(recipebookcategories, s, list);
                            map.computeIfAbsent(recipebookcategories, (p_90641_) -> Lists.newArrayList()).add(list);
                        }

                        list.add(recipeHolder);
                    }
                }
            }
        }
    }
}
