package com.andersmmg.lockandblock.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class CloningRecipeSerializer implements RecipeSerializer<CloningRecipe> {

    @Override
    public CloningRecipe read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");

        JsonObject sourceJson = JsonHelper.getObject(json, "source");
        String itemId = JsonHelper.getString(sourceJson, "item");
        ItemStack source = new ItemStack(Registries.ITEM.get(new Identifier(itemId)), 1);

        Map<Integer, Integer> requiredIngredients = new HashMap<>();
        JsonArray ingredientsArray = JsonHelper.getArray(json, "ingredients");

        for (int i = 0; i < ingredientsArray.size(); i++) {
            JsonObject ingredientJson = ingredientsArray.get(i).getAsJsonObject();
            String ingredientId = JsonHelper.getString(ingredientJson, "item");
            int ingredientCount = JsonHelper.getInt(ingredientJson, "count", 1);
            ItemStack ingredientStack = new ItemStack(Registries.ITEM.get(new Identifier(ingredientId)), ingredientCount);
            Integer rawId = Item.getRawId(ingredientStack.getItem());
            requiredIngredients.put(rawId, ingredientCount);
        }

        return new CloningRecipe(id, group, source, requiredIngredients);
    }

    @Override
    public CloningRecipe read(Identifier id, PacketByteBuf buf) {
        String group = buf.readString(32767);

        ItemStack source = buf.readItemStack();

        Map<Integer, Integer> requiredIngredients = new HashMap<>();
        int ingredientCount = buf.readInt();
        for (int i = 0; i < ingredientCount; i++) {
            String ingredientId = buf.readString(32767);
            int ingredientCountRaw = buf.readInt();
            ItemStack ingredientStack = new ItemStack(Registries.ITEM.get(new Identifier(ingredientId)), ingredientCountRaw);
            Integer rawId = Item.getRawId(ingredientStack.getItem());
            requiredIngredients.put(rawId, ingredientCountRaw);
        }

        return new CloningRecipe(id, group, source, requiredIngredients);
    }

    @Override
    public void write(PacketByteBuf buf, CloningRecipe recipe) {
        buf.writeString(recipe.getGroup());

        buf.writeItemStack(recipe.getOutput(null));

        buf.writeInt(recipe.requiredIngredients.size());
        for (Map.Entry<Integer, Integer> entry : recipe.requiredIngredients.entrySet()) {
            buf.writeString(Registries.ITEM.getId(Item.byRawId(entry.getKey())).toString());
            buf.writeInt(entry.getValue());
        }
    }
}
