package com.andersmmg.lockandblock.item.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.custom.KeycardReaderBlock;
import com.andersmmg.lockandblock.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeycardItem extends Item implements DyeableItem {
    public KeycardItem(Settings settings) {
        super(settings);
    }

    public static boolean hasUuid(ItemStack stack) {
        return stack.isOf(ModItems.KEYCARD) && stack.hasNbt() && stack.getNbt().contains(LockAndBlock.CARD_UUID_KEY);
    }

    public static String getUuid(ItemStack stack) {
        return stack.getNbt().getString(LockAndBlock.CARD_UUID_KEY);
    }

    public static void setUuid(String uuid, ItemStack stack) {
        stack.getOrCreateNbt().putString(LockAndBlock.CARD_UUID_KEY, uuid);
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return DyeableItem.super.hasColor(stack);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : 0xFFFFFF;
    }

    @Override
    public void removeColor(ItemStack stack) {
        DyeableItem.super.removeColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableItem.super.setColor(stack, color);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer().isSneaking() && context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof KeycardReaderBlock keycardReaderBlock) {
            keycardReaderBlock.edit(context);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (hasUuid(stack)) {
            tooltip.add(LockAndBlock.langText("keycard.written").formatted(Formatting.GOLD));
            return;
        }
        tooltip.add(LockAndBlock.langText("keycard.blank").formatted(Formatting.ITALIC, Formatting.GRAY));
    }
}
