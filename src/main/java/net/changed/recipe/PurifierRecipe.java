package net.changed.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.changed.init.ChangedItems;
import net.changed.init.ChangedRecipeSerializers;
import net.changed.init.ChangedRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.changed.compat.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class PurifierRecipe implements Recipe<SimpleContainerRecipeInput> {
    private final ResourceLocation id;
    final String group;
    final Ingredient ingredient;
    final Item result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public PurifierRecipe(ResourceLocation id, String group, Ingredient ingredient, Item result) {
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.isSimple = ingredient.isSimple();
        this.ingredients = NonNullList.of(ingredient, ingredient);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ChangedRecipeSerializers.PURIFIER_RECIPE.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return new ItemStack(ChangedItems.LATEX_SYRINGE.get());
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public Item getResult() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(SimpleContainerRecipeInput p_44262_, Level p_44263_) {
        StackedContents stackedcontents = new StackedContents();
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;

        for(int j = 0; j < p_44262_.size(); ++j) {
            ItemStack itemstack = p_44262_.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == 1 && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    @Override
    public ItemStack assemble(SimpleContainerRecipeInput container, HolderLookup.Provider registryAccess) {
        return this.getResultItem();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return this.getResultItem();
    }

    public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
        return p_44252_ * p_44253_ >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<PurifierRecipe> {
        public static final MapCodec<PurifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                ResourceLocation.CODEC.fieldOf("result").xmap(ForgeRegistries.ITEMS::getValue, ForgeRegistries.ITEMS::getKey).forGetter(recipe -> recipe.result)
        ).apply(instance, (group, ingredient, result) -> new PurifierRecipe(ResourceLocation.fromNamespaceAndPath("changed", "purifier"), group, ingredient, result)));
        public static final StreamCodec<RegistryFriendlyByteBuf, PurifierRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<PurifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PurifierRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PurifierRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient in = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Item out = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
            return new PurifierRecipe(ResourceLocation.fromNamespaceAndPath("changed", "purifier"), group, in, out);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, PurifierRecipe recipe) {
            buffer.writeUtf(recipe.group);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.result));
        }
    }

    @Override
    public RecipeType<?> getType() {
        return ChangedRecipeTypes.PURIFIER_RECIPE.get();
    }
}
