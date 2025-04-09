package com.andersmmg.lockandblock.recipe;

import com.andersmmg.lockandblock.LockAndBlock;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class CloningRecipe implements CraftingRecipe {
    private final Identifier id;
    private final String group;
    private final ItemStack source;
    public final Map<Integer, Integer> requiredIngredients;

    public CloningRecipe(Identifier id, String group, ItemStack source, Map<Integer, Integer> requiredIngredients) {
        this.id = id;
        this.group = group;
        this.source = source;
        this.requiredIngredients = requiredIngredients;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.MISC;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        Map<Integer, Integer> ingredientCounts = new HashMap<>();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                int rawId = Item.getRawId(stack.getItem());
                ingredientCounts.put(rawId, ingredientCounts.getOrDefault(rawId, 0) + 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : requiredIngredients.entrySet()) {
            if (!ingredientCounts.getOrDefault(entry.getKey(), 0).equals(entry.getValue())) {
                return false;
            }
        }

        int sourceRawId = Item.getRawId(source.getItem());
        if (!ingredientCounts.containsKey(sourceRawId) || ingredientCounts.get(sourceRawId) > 1) {
            return false;
        }

        return ingredientCounts.entrySet().stream()
                .allMatch(entry -> requiredIngredients.containsKey(entry.getKey()) || entry.getKey() == sourceRawId);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() == source.getItem() && stack.hasNbt()) {
                ItemStack clonedItem = source.copy();
                clonedItem.setNbt(stack.getNbt());
                return clonedItem;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return requiredIngredients.size() + 1 <= width * height;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return source;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LockAndBlock.KEY_CLONING_RECIPE_SERIALIZER;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> remainders = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (Item.getRawId(stack.getItem()) == Item.getRawId(source.getItem())) {
                remainders.set(i, stack.copy());
            }
        }

        return remainders;
    }
}