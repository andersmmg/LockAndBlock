package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.LockBlockEntity;
import com.andersmmg.lockandblock.item.ModItems;
import com.andersmmg.lockandblock.item.custom.KeyItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public class LockBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LockBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
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
                    if (lockBlockEntity.checkKey(stack)) {
                        return this.activate(state, world, pos);
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
                            return this.activate(state, world, pos);
                        }
                    } else {
                        if (!world.isClient) {
                            // encode both
                            String new_uuid = java.util.UUID.randomUUID().toString();
                            KeyItem.setUuid(new_uuid, stack);
                            lockBlockEntity.setUuid(new_uuid);
                            player.sendMessage(LockAndBlock.langText("lock_and_key_coded"), true);
                            return this.activate(state, world, pos);
                        }
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ActionResult activate(BlockState state, World world, BlockPos pos) {
        if (!state.get(POWERED)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(POWERED, true), 3);
                world.scheduleBlockTick(pos, this, 20, TickPriority.NORMAL);
                this.updateNeighbors(state, (ServerWorld) world, pos);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LockBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private void updateNeighbors(BlockState state, ServerWorld world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.cycle(POWERED), 2);
            this.updateNeighbors(state, world, pos);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}
