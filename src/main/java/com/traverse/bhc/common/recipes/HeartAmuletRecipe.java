package com.traverse.bhc.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.traverse.bhc.common.init.RegistryHandler;
import com.traverse.bhc.common.util.InventoryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.items.ItemStackHandler;

public class HeartAmuletRecipe extends ShapelessRecipe {

    final String group;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;



    public HeartAmuletRecipe(String group,ItemStack stack, NonNullList<Ingredient> list) {
        super(group, CraftingBookCategory.EQUIPMENT, stack, list);
        this.group = group;
        this.result = stack;
        this.ingredients = list;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        ItemStack oldCanister = ItemStack.EMPTY;
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack input = craftingInput.getItem(i);
            if(input.getItem() == RegistryHandler.HEART_AMULET.get()) {
                oldCanister = input;
                break;
            }
        }
        ItemStack stack = super.assemble(craftingInput, registries);
        ItemStackHandler oldInv = InventoryUtil.createVirtualInventory(4, oldCanister);
        ItemStackHandler newInv = InventoryUtil.createVirtualInventory(5, stack);
        for (int i = 0; i < oldInv.getSlots(); i++) {
            newInv.setStackInSlot(i, oldInv.getStackInSlot(i));
        }
        InventoryUtil.serializeInventory(newInv, stack);
        return stack;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegistryHandler.HEART_AMULET_RECIPE_SERIALIZER.get();
    }

    public static class BHCSerializer implements RecipeSerializer<HeartAmuletRecipe> {

        private static final MapCodec<HeartAmuletRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Ingredient.CODEC_NONEMPTY
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                array -> {
                                                    Ingredient[] aingredient = array
                                                            .toArray(Ingredient[]::new); //Forge skip the empty check and immediatly create the array.
                                                    if (aingredient.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                                    } else {
                                                        return aingredient.length > 3 * 3
                                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(3 * 3))
                                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(recipe -> recipe.ingredients)
                        )
                        .apply(instance, HeartAmuletRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, HeartAmuletRecipe> STREAM_CODEC = StreamCodec.of(
            HeartAmuletRecipe.BHCSerializer::toNetwork, HeartAmuletRecipe.BHCSerializer::fromNetwork
        );

        public static HeartAmuletRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));

            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new HeartAmuletRecipe(s, itemstack, nonnulllist);
        }

        @Override
        public MapCodec<HeartAmuletRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeartAmuletRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, HeartAmuletRecipe heartAmuletRecipe) {
            buffer.writeUtf(heartAmuletRecipe.getGroup());
            buffer.writeVarInt(heartAmuletRecipe.getIngredients().size());

            for(Ingredient ingredient : heartAmuletRecipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, heartAmuletRecipe.result);
        }

    }
}
