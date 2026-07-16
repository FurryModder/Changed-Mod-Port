package net.changed.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.changed.init.ChangedRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.changed.compat.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContainerFillingRecipe implements CraftingRecipe {
    private static final int MAX_WIDTH = 3;
    private static final int MAX_HEIGHT = 3;
    private final ResourceLocation id;
    final String group;
    final NonNullList<Ingredient> ingredients;
    final NonNullList<Ingredient> minimumIngredients;
    final Item container;
    final int containerCountLimit;
    final Item result;

    public ContainerFillingRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, Item container, int containerCountLimit, Item result) {
        this.id = id;
        this.group = group;
        this.ingredients = ingredients;
        this.minimumIngredients = NonNullList.create();
        minimumIngredients.addAll(this.ingredients);
        minimumIngredients.add(Ingredient.of(container));

        this.container = container;
        this.containerCountLimit = containerCountLimit;
        this.result = result;
    }

    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return minimumIngredients;
    }

    @Override
    public boolean matches(@NotNull CraftingInput container, @NotNull Level level) {
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int nonEmptyStacks = 0;
        int containerStacks = 0;

        for(int j = 0; j < container.size(); ++j) {
            ItemStack itemstack = container.getItem(j);
            if (!itemstack.isEmpty()) {
                if (!itemstack.is(this.container))
                    ++nonEmptyStacks;
                else {
                    ++containerStacks;
                    continue;
                }

                inputs.add(itemstack);
            }
        }

        return nonEmptyStacks == this.ingredients.size() &&
                containerStacks > 0 && containerStacks <= containerCountLimit &&
                net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, this.ingredients) != null;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput container, @NotNull HolderLookup.Provider registryAccess) {
        int containerStacks = 0;

        for(int j = 0; j < container.size(); ++j) {
            ItemStack itemstack = container.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.is(this.container))
                    ++containerStacks;
            }
        }

        return new ItemStack(this.result, Mth.clamp(containerStacks, 1, this.containerCountLimit));
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registryAccess) {
        return new ItemStack(this.result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size() + 1;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ChangedRecipeSerializers.CONTAINER_FILL_RECIPE.get();
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    public static class Serializer implements RecipeSerializer<ContainerFillingRecipe> {
        public static final MapCodec<ContainerFillingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").xmap(Serializer::toNonNullList, List::copyOf).forGetter(recipe -> recipe.ingredients),
                ResourceLocation.CODEC.fieldOf("container").xmap(ForgeRegistries.ITEMS::getValue, ForgeRegistries.ITEMS::getKey).forGetter(recipe -> recipe.container),
                Codec.INT.optionalFieldOf("containerCountLimit", Integer.MAX_VALUE).forGetter(recipe -> recipe.containerCountLimit),
                ResourceLocation.CODEC.fieldOf("result").xmap(ForgeRegistries.ITEMS::getValue, ForgeRegistries.ITEMS::getKey).forGetter(recipe -> recipe.result)
        ).apply(instance, (group, ingredients, container, countLimit, result) -> {
            NonNullList<Ingredient> nonnulllist = ingredients;
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for container filling recipe");
            } else if (nonnulllist.size() > MAX_WIDTH * MAX_HEIGHT - 1) {
                throw new JsonParseException("Too many ingredients for container filling recipe. The maximum is " + (MAX_WIDTH * MAX_HEIGHT - 1));
            } else {
                if (nonnulllist.stream().anyMatch(ingredient -> {
                    assert container != null;
                    return ingredient.test(new ItemStack(container));
                }))
                    throw new JsonParseException("Cannot set container to ingredient item");
                return new ContainerFillingRecipe(ResourceLocation.fromNamespaceAndPath("changed", "crafting_container_fill"), group, nonnulllist, container, countLimit, result);
            }
        }));
        public static final StreamCodec<RegistryFriendlyByteBuf, ContainerFillingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ContainerFillingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ContainerFillingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            nonnulllist.addAll(ingredients);
            return nonnulllist;
        }

        private static ContainerFillingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();

            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }

            Item container = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
            int countLimit = buffer.readVarInt();
            Item out = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
            return new ContainerFillingRecipe(ResourceLocation.fromNamespaceAndPath("changed", "crafting_container_fill"), group, ingredients, container, countLimit, out);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ContainerFillingRecipe recipe) {
            buffer.writeUtf(recipe.group);

            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.container));
            buffer.writeVarInt(recipe.containerCountLimit);
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.result));
        }
    }
}
