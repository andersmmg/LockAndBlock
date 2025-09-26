package com.andersmmg.lockandblock.item.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }


    public static boolean hasUuid(ItemStack stack) {
        return stack.isOf(ModItems.KEYCARD) && stack.get(LockAndBlock.KEY_UUID_COMPONENT) != null;
    }

    public static String getUuid(ItemStack stack) {
        return stack.get(LockAndBlock.KEY_UUID_COMPONENT);
    }

    public static void setUuid(String uuid, ItemStack stack) {
        stack.set(LockAndBlock.KEY_UUID_COMPONENT, uuid);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (hasUuid(stack)) {
            tooltip.add(LockAndBlock.langText("key.written").formatted(Formatting.GREEN));
            return;
        }
        tooltip.add(LockAndBlock.langText("key.blank").formatted(Formatting.ITALIC, Formatting.GRAY));
    }
}
