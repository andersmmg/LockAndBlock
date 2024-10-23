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

public class RedstoneLaser extends LaserBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(6, 6, 14, 10, 10, 16);
    private static final VoxelShape VOXEL_SHAPE_UP = Block.createCuboidShape(6, 0, 6, 10, 2, 10);
    private static final VoxelShape VOXEL_SHAPE_DOWN = Block.createCuboidShape(6, 14, 6, 10, 16, 10);

    public RedstoneLaser(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(DISTANCE, 0));
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
        world.scheduleBlockTick(pos, this, 1, TickPriority.NORMAL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            this.processLaser(world, pos, state);
        } else {
            updateDistance(state, world, pos, 0);
        }
        world.scheduleBlockTick(pos, this, 1, TickPriority.byIndex(1));
    }

    private void processLaser(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(RedstoneLaser.FACING);
        int distance = LockAndBlock.CONFIG.allowTripMinesAir() ? LockAndBlock.CONFIG.maxTripMineDistance() + 1 : 0;

        for (int i = 1; i <= LockAndBlock.CONFIG.maxTripMineDistance() + 1; i++) {
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
            return;
        }
        // check if there are players in the area
        double expandOffset = ((double) (distance - 1) / 2.0f);
        Box detectionBox = new Box(pos).contract(0.5f)
                .expand(direction.getOffsetX() * expandOffset, direction.getOffsetY() * expandOffset, direction.getOffsetZ() * expandOffset)
                .offset(direction.getOffsetX() * (distance - 1) * 0.5f, direction.getOffsetY() * (distance - 1) * 0.5f, direction.getOffsetZ() * (distance - 1) * 0.5f);

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, detectionBox, entity -> true);

        if (!world.isClient) {
            for (LivingEntity entity : entities) {
                if (entity.canTakeDamage())
                    entity.damage(LockAndBlock.damageOf(world, LockAndBlock.LASER_DAMAGE), LockAndBlock.CONFIG.redstoneLaserDamage());
            }
        }

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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (getDirection(state)) {
            case UP -> VOXEL_SHAPE_UP;
            case DOWN -> VOXEL_SHAPE_DOWN;
            default -> VoxelUtils.rotateShape(getDirection(state), VOXEL_SHAPE);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DISTANCE, POWERED);
    }
}
