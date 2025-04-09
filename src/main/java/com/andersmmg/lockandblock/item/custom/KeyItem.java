package com.andersmmg.lockandblock.item.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }

    public static boolean hasUuid(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.isOf(ModItems.KEY) && stack.hasNbt() && stack.getNbt().contains(LockAndBlock.KEY_UUID_KEY);
        }
        return false;
    }

    public static String getUuid(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getString(LockAndBlock.KEY_UUID_KEY);
        }
        return "";
    }

    public static void setUuid(String uuid, ItemStack stack) {
        stack.getOrCreateNbt().putString(LockAndBlock.KEY_UUID_KEY, uuid);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (hasUuid(stack)) {
            tooltip.add(LockAndBlock.langText("key.written").formatted(Formatting.GREEN));
            return;
        }
        tooltip.add(LockAndBlock.langText("key.blank").formatted(Formatting.ITALIC, Formatting.GRAY));
    }
}
