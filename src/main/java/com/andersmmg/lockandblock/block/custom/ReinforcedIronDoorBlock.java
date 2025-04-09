package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.block.entity.LockBlockEntity;
import com.andersmmg.lockandblock.item.ModItems;
import com.andersmmg.lockandblock.item.custom.KeyItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public class ReinforcedIronDoorBlock extends DoorBlock implements BlockEntityProvider {
    public ReinforcedIronDoorBlock(Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(POWERED)) {
            return ActionResult.FAIL;
        }
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(ModItems.KEY)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LockBlockEntity lockBlockEntity) {
                if (lockBlockEntity.hasUuid()) {
                    if (KeyItem.hasUuid(stack)) {
                        if (lockBlockEntity.getUuid().equals(KeyItem.getUuid(stack))) {
                            return this.activate(state, world, pos, player);
                        } else {
                            if (!world.isClient) {
//                                world.playSound(null, pos, ModSounds.BEEP_ERROR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                player.sendMessage(LockAndBlock.langText("wrong_key"), true);
                            }
                        }
                    } else {
                        if (!world.isClient) {
//                            world.playSound(null, pos, ModSounds.BEEP_ERROR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            player.sendMessage(LockAndBlock.langText("wrong_key"), true);
                        }
                    }
                } else {
                    if (KeyItem.hasUuid(stack)) {
                        if (!world.isClient) {
                            // encode lock with the key
                            lockBlockEntity.setUuid(KeyItem.getUuid(stack));
                            player.sendMessage(LockAndBlock.langText("lock_coded"), true);
                            return this.activate(state, world, pos, player);
                        }
                    } else {
                        if (!world.isClient) {
                            // encode both
                            String new_uuid = java.util.UUID.randomUUID().toString();
                            KeyItem.setUuid(new_uuid, stack);
                            lockBlockEntity.setUuid(new_uuid);
                            player.sendMessage(LockAndBlock.langText("lock_and_key_coded"), true);
                            return this.activate(state, world, pos, player);
                        }
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ActionResult activate(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        state = (BlockState)state.cycle(OPEN);
        world.setBlockState(pos, state, 10);
        world.emitGameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        this.playOpenCloseSound(player, world, pos, (Boolean)state.get(OPEN));
        return ActionResult.success(world.isClient);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            if (sourceBlock instanceof LockBlock || sourceBlock instanceof KeycardReaderBlock) {
                super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
            }
        }
    }

    private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean open) {
        world.playSound(entity, pos, open ? this.getBlockSetType().doorOpen() : this.getBlockSetType().doorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LockBlockEntity(pos, state);
    }
}
