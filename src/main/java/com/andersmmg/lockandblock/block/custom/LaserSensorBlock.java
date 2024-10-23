package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
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

public class LaserSensorBlock extends LaserBlock {
    public static final BooleanProperty SET = LockAndBlock.SET;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(6, 6, 14, 10, 10, 16);
    private static final VoxelShape VOXEL_SHAPE_UP = Block.createCuboidShape(6, 0, 6, 10, 2, 10);
    private static final VoxelShape VOXEL_SHAPE_DOWN = Block.createCuboidShape(6, 14, 6, 10, 16, 10);

    public LaserSensorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SET, false).with(POWERED, false).with(DISTANCE, 0));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, 20, TickPriority.NORMAL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(SET)) {
            boolean shouldPower = this.shouldPower(world, pos, state);
            if (state.get(POWERED) != shouldPower) {
                world.setBlockState(pos, state.with(POWERED, shouldPower), 3);
                state.updateNeighbors(world, pos, 3);
            }
        } else {
            world.setBlockState(pos, state.with(SET, true), 3);
        }
        world.scheduleBlockTick(pos, this, 1, TickPriority.byIndex(1));
    }

    private boolean shouldPower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(LaserSensorBlock.FACING);
        int distance = LockAndBlock.CONFIG.allowLaserInAir() ? LockAndBlock.CONFIG.maxLaserSensorDistance() + 1 : 0;

        for (int i = 1; i <= LockAndBlock.CONFIG.maxLaserSensorDistance() + 1; i++) {
            BlockState blockState = world.getBlockState(pos.offset(direction, i));
            String blockId = Registries.BLOCK.getId(blockState.getBlock()).toString();
            if (LockAndBlock.CONFIG.laserPassthroughWhitelist().contains(blockId)) {
                continue;
            }
            if (blockState.getCameraCollisionShape(world, pos.offset(direction, i), ShapeContext.absent()).isEmpty()) {
                continue;
            }
            if (blockState.isSideSolid(world, pos.offset(direction, i), direction.getOpposite(), SideShapeType.CENTER)) {
                distance = i;
                break;
            }
            if (blockState.isSideSolid(world, pos.offset(direction, i), direction, SideShapeType.CENTER)) {
                distance = i + 1;
                break;
            }
        }

        updateDistance(state, world, pos, distance);

        if (distance == 0) {
            return false;
        }

        // check if there are players in the area
        double expandOffset = ((distance - 1) / 2.0f);
        Box detectionBox = new Box(pos).contract(0.5f)
                .expand(direction.getOffsetX() * expandOffset, direction.getOffsetY() * expandOffset, direction.getOffsetZ() * expandOffset)
                .offset(direction.getOffsetX() * (distance - 1) * 0.5f, direction.getOffsetY() * (distance - 1) * 0.5f, direction.getOffsetZ() * (distance - 1) * 0.5f);

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, detectionBox, entity -> true);
        return !entities.isEmpty();
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (getDirection(state)) {
            case UP -> VOXEL_SHAPE_UP;
            case DOWN -> VOXEL_SHAPE_DOWN;
            default -> VoxelUtils.rotateShape(getDirection(state), VOXEL_SHAPE);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SET, POWERED, DISTANCE);
    }
}
