package com.andersmmg.lockandblock.item.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.custom.KeycardReaderBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.List;

public class KeycardItem extends KeyItem {

    public KeycardItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null && context.getPlayer().isSneaking() && context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof KeycardReaderBlock keycardReaderBlock) {
            keycardReaderBlock.edit(context);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (hasUuid(stack)) {
            tooltip.add(LockAndBlock.langText("keycard.written").formatted(Formatting.GOLD));
            return;
        }
        tooltip.add(LockAndBlock.langText("keycard.blank").formatted(Formatting.ITALIC, Formatting.GRAY));
    }
}
