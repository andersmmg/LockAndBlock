package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerSensorBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(5, 5, 14, 11, 11, 16);
    private static final VoxelShape VOXEL_SHAPE_UP = Block.createCuboidShape(5, 0, 5, 11, 2, 11);
    private static final VoxelShape VOXEL_SHAPE_DOWN = Block.createCuboidShape(5, 14, 5, 11, 16, 11);

    public PlayerSensorBlock(Settings settings) {
        super(settings.luminance((state) -> state.get(POWERED) ? 3 : 0));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, 3, TickPriority.NORMAL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world != null && !world.isClient) {
            boolean shouldPower = this.shouldPower(world, pos, state);
            if (shouldPower != state.get(Properties.POWERED)) {
                world.setBlockState(pos, state.with(Properties.POWERED, shouldPower), 3);
                state.updateNeighbors(world, pos, 3);
                world.updateNeighborsAlways(pos.down(), state.getBlock());
            }
        }
        assert world != null;
        world.scheduleBlockTick(pos, this, 3, TickPriority.byIndex(1));
    }

    private boolean shouldPower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(PlayerSensorBlock.FACING);
        BlockPos frontPos = pos.offset(direction, 1);
        Box detectionBox = new Box(frontPos).expand(LockAndBlock.CONFIG.playerSensorRange() - 1.0f);

        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, detectionBox, player -> true);
        return !players.isEmpty();
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (getDirection(state)) {
            case UP -> VOXEL_SHAPE_UP;
            case DOWN -> VOXEL_SHAPE_DOWN;
            default -> VoxelUtils.rotateShape(getDirection(state), VOXEL_SHAPE);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }
}
