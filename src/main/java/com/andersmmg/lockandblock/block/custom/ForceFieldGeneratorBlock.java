package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.ModBlocks;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public class ForceFieldGeneratorBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public ForceFieldGeneratorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                if (world.isReceivingRedstonePower(pos)) {
                    world.scheduleBlockTick(pos, this, 5, TickPriority.NORMAL);
                }
                world.setBlockState(pos, state.cycle(POWERED), 2);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                if (world.isReceivingRedstonePower(pos)) {
                    world.scheduleBlockTick(pos, this, 5, TickPriority.NORMAL);
                }
                world.setBlockState(pos, state.cycle(POWERED), 2);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient()) return;
        LockAndBlock.LOGGER.debug("scheduledTick: " + pos);
        if (state.get(POWERED)) {
            for (int i = 1; i <= LockAndBlock.CONFIG.maxForceFieldLength() + 1; i++) {
                BlockState blockState = world.getBlockState(pos.offset(state.get(FACING), i));
                if (blockState.getBlock() instanceof ForceFieldGeneratorBlock) {
                    if (blockState.get(FACING) == state.get(FACING).getOpposite()) {
                        for (int j = 1; j < i; j++) {
                            world.setBlockState(pos.offset(state.get(FACING), j), ModBlocks.FORCEFIELD.getDefaultState(), 2);
                        }
                        break;
                    }
                    break;
                } else if (blockState.getBlock() instanceof ForceFieldBlock) {
                    continue;
                } else if (blockState.getBlock() instanceof AirBlock) {
                    continue;
                }
                break;
            }
            world.scheduleBlockTick(pos, this, 5, TickPriority.NORMAL);
        } else {
            // check if there is a force field block in front of the generator
            Block block = world.getBlockState(pos.offset(state.get(FACING))).getBlock();
            if (block instanceof ForceFieldBlock) {
                world.removeBlock(pos.offset(state.get(FACING)), false);
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
}
