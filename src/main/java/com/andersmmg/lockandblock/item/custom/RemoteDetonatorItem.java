package com.andersmmg.lockandblock.item.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.custom.DetonatorMineBlock;
import com.andersmmg.lockandblock.item.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RemoteDetonatorItem extends Item {
    public record PairedPositions(List<BlockPos> positions) {
        public static final Codec<PairedPositions> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                BlockPos.CODEC.listOf().fieldOf("positions").forGetter(PairedPositions::positions)
            ).apply(instance, PairedPositions::new)
        );
    }

    public static final ComponentType<PairedPositions> PAIRED_POSITIONS = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of(LockAndBlock.MOD_ID, "paired_positions"),
        ComponentType.<PairedPositions>builder().codec(PairedPositions.CODEC).build()
    );

    public RemoteDetonatorItem(Settings settings) {
        super(settings);
    }

    public static boolean isPaired(ItemStack stack) {
        if (!stack.isOf(ModItems.REMOTE_DETONATOR)) {
            return false;
        }
        PairedPositions pairedPositions = stack.get(PAIRED_POSITIONS);
        return pairedPositions != null && !pairedPositions.positions().isEmpty();
    }

    private static List<BlockPos> getPairedPositions(ItemStack stack) {
        if (!stack.isOf(ModItems.REMOTE_DETONATOR)) {
            return new ArrayList<>();
        }
        PairedPositions pairedPositions = stack.get(PAIRED_POSITIONS);
        return pairedPositions != null ? new ArrayList<>(pairedPositions.positions()) : new ArrayList<>();
    }

    private static void addPairedPosition(ItemStack stack, BlockPos pos) {
        if (!stack.isOf(ModItems.REMOTE_DETONATOR)) {
            return;
        }
        
        List<BlockPos> positions = new ArrayList<>(getPairedPositions(stack));
        if (!positions.contains(pos)) {
            positions.add(pos);
            stack.set(PAIRED_POSITIONS, new PairedPositions(positions));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof DetonatorMineBlock) {
            if (context.getWorld().isClient()) {
                return ActionResult.SUCCESS;
            }
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            ItemStack stack = context.getStack();

            // Get current positions
            List<BlockPos> positions = getPairedPositions(stack);
            
            // Check if position is already paired
            if (positions.stream().noneMatch(p -> p.equals(pos))) {
                // Add new position
                addPairedPosition(stack, pos);
                world.setBlockState(pos, world.getBlockState(pos).with(DetonatorMineBlock.SET, true), 3);

                assert player != null;
                player.sendMessage(LockAndBlock.langText("detonator.pair_added"), true);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
        if (!user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            if (isPaired(stack)) {
                List<BlockPos> positions = getPairedPositions(stack);
                if (!positions.isEmpty()) {
                    // Process detonations
                    positions.stream()
                        .filter(pos -> world.getBlockState(pos).getBlock() instanceof DetonatorMineBlock)
                        .forEach(pos -> ((DetonatorMineBlock) world.getBlockState(pos).getBlock()).detonate(world, pos));
                    
                    user.sendMessage(Text.translatable("text." + LockAndBlock.MOD_ID + "." + "detonator.detonated", positions.size()), true);
                    
                    // Clear the pairs after detonation
                    stack.remove(PAIRED_POSITIONS);
                } else {
                    user.sendMessage(Text.translatable("text." + LockAndBlock.MOD_ID + "." + "detonator.no_mines"), true);
                }
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (isPaired(stack)) {
            int count = getPairedPositions(stack).size();
            tooltip.add(Text.translatable("text." + LockAndBlock.MOD_ID + ".detonator.paired_mines", count).formatted(Formatting.GOLD));
        }
    }
}
