package net.changed.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.changed.entity.ChangedEntity;
import net.changed.entity.Gender;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.*;
import net.changed.util.TagUtil;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InfuserRecipe implements Recipe<SimpleContainerRecipeInput> {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    private final ResourceLocation id;
    final String group;
    public final boolean gendered;
    public final ResourceLocation form;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public static final Map<? extends Item, Function<ItemStack, ItemStack>> INFUSER_BASE_CONVERSION =
            Util.make(new HashMap<>(), map -> {
                map.put(Items.ARROW, stack -> new ItemStack(ChangedItems.LATEX_TIPPED_ARROW.get(), Math.min(stack.getCount(), 16)));
                map.put(ChangedItems.BLOOD_SYRINGE.get(), stack -> new ItemStack(ChangedItems.LATEX_SYRINGE.get()));
                map.put(ChangedBlocks.ERLENMEYER_FLASK.get().asItem(), stack -> new ItemStack(ChangedItems.LATEX_FLASK.get()));
            });

    public static ItemStack getBaseFor(ItemStack stack) {
        var func = INFUSER_BASE_CONVERSION.get(stack.getItem());
        if (func != null)
            return func.apply(stack);
        else
            return ItemStack.EMPTY;
    }

    public static List<ItemStack> getAllowedInputs() {
        List<ItemStack> list = new ArrayList<>();
        INFUSER_BASE_CONVERSION.keySet().forEach(key -> {
            list.add(new ItemStack(key));
        });
        return list;
    }

    public List<ItemStack> getPossibleResults() {
        List<ItemStack> list = new ArrayList<>();
        getAllowedInputs().forEach(baseItem -> {
            baseItem.setCount(baseItem.getMaxStackSize());
            var newItem = INFUSER_BASE_CONVERSION.get(baseItem.getItem()).apply(baseItem);

            if (gendered) {
                for (Gender gender : Gender.values()) {
                    list.add(processItem(newItem.copy(), gender));
                }
            }

            else {
                list.add(processItem(newItem, Gender.MALE));
            }
        });
        return list;
    }

    public InfuserRecipe(ResourceLocation id, String group, boolean gendered, ResourceLocation form, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.group = group;
        this.gendered = gendered;
        this.form = form;
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ChangedRecipeSerializers.INFUSER_RECIPE.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack processItem(ItemStack stack, Gender gender) {
        TagUtil.updateCustomData(stack, tag -> {
            if (gendered)
                TagUtil.putResourceLocation(tag, "form", gender.convertToGendered(form));
            else
                TagUtil.putResourceLocation(tag, "form", form);
            tag.putBoolean("safe", false);
        });
        return stack;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(SimpleContainerRecipeInput p_44262_, Level p_44263_) {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
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

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    @Override
    public ItemStack assemble(SimpleContainerRecipeInput container, HolderLookup.Provider registryAccess) {
        return new ItemStack(ChangedItems.LATEX_SYRINGE.get());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return new ItemStack(ChangedItems.LATEX_SYRINGE.get());
    }

    public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
        return p_44252_ * p_44253_ >= this.ingredients.size();
    }

    public Component getNameFor(Level level, Gender gender) {
        ResourceLocation formId = form;
        if (gendered)
            formId = ResourceLocation.parse(formId + "/" + gender.toString().toLowerCase());
        TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(formId);
        if (variant == null)
            return Component.translatable("syringe." + form);
        ChangedEntity entity = ChangedEntities.getCachedEntity(level, variant.getEntityType());
        Component component = entity.getDisplayName();
        entity.remove(Entity.RemovalReason.DISCARDED);
        return component;
    }

    public static class Serializer implements RecipeSerializer<InfuserRecipe> {
        public static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").xmap(Serializer::toNonNullList, List::copyOf).forGetter(recipe -> recipe.ingredients),
                Codec.BOOL.optionalFieldOf("gendered", false).forGetter(recipe -> recipe.gendered),
                ResourceLocation.CODEC.fieldOf("form").forGetter(recipe -> recipe.form)
        ).apply(instance, (group, ingredients, gendered, form) -> {
            if (ingredients.size() > MAX_WIDTH * MAX_HEIGHT)
                throw new JsonParseException("Too many ingredients for infuser recipe. The maximum is " + (MAX_WIDTH * MAX_HEIGHT));

            return new InfuserRecipe(ResourceLocation.fromNamespaceAndPath("changed", "infuser"), group, gendered, form, ingredients);
        }));
        public static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<InfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            nonnulllist.addAll(ingredients);
            return nonnulllist;
        }

        private static InfuserRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }

            boolean gendered = buffer.readBoolean();
            ResourceLocation form = buffer.readResourceLocation();
            return new InfuserRecipe(ResourceLocation.fromNamespaceAndPath("changed", "infuser"), s, gendered, form, nonnulllist);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, InfuserRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            buffer.writeBoolean(recipe.gendered);
            buffer.writeResourceLocation(recipe.form);
        }
    }

    @Override
    public RecipeType<?> getType() {
        return ChangedRecipeTypes.INFUSER_RECIPE.get();
    }
}
