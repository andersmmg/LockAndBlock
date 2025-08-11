package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.LaserBlockEntity;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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

public class TripMineBlock extends LaserBlock {
    public static final BooleanProperty SET = LockAndBlock.SET;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(5, 6, 14, 11, 10, 16);
    private static final VoxelShape VOXEL_SHAPE_UP = Block.createCuboidShape(5, 0, 6, 11, 2, 10);
    private static final VoxelShape VOXEL_SHAPE_DOWN = Block.createCuboidShape(5, 14, 6, 11, 16, 10);

    public TripMineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SET, false));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, 40, TickPriority.NORMAL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(SET)) {
            boolean shouldPower = this.shouldPower(world, pos, state);
            if (shouldPower) {
                world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, World.ExplosionSourceType.NONE);
                world.removeBlock(pos, false);
            }
        } else {
            world.setBlockState(pos, state.with(SET, true), 3);
        }
        world.scheduleBlockTick(pos, this, 1, TickPriority.byIndex(1));
    }

    private boolean shouldPower(World world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof LaserBlockEntity laserBlockEntity) {
            int distance = laserBlockEntity.currentDistance;
            Direction direction = state.get(TripMineBlock.FACING);

            if (distance == 0) {
                return false;
            }

            // check if there are players in the area
            double expandOffset = ((double) (distance - 1) / 2.0f);
            Box detectionBox = new Box(pos).contract(0.5f)
                    .expand(direction.getOffsetX() * expandOffset, direction.getOffsetY() * expandOffset, direction.getOffsetZ() * expandOffset)
                    .offset(direction.getOffsetX() * (distance - 1) * 0.5f, direction.getOffsetY() * (distance - 1) * 0.5f, direction.getOffsetZ() * (distance - 1) * 0.5f);

            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, detectionBox, entity -> true);
            return !entities.isEmpty();
        }
        return false;
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
        builder.add(FACING, SET);
    }
}
