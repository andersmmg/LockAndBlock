package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LandMineBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty SET = LockAndBlock.SET;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(5, 0, 5, 11, 2, 11);
    private static final VoxelShape VOXEL_SHAPE_SET = Block.createCuboidShape(5, 0, 5, 11, 1, 11);

    public LandMineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SET, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, 40, TickPriority.NORMAL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world != null && !world.isClient) {
            if (state.get(SET)) {
                boolean shouldPower = this.shouldPower(world, pos, state);
                if (shouldPower) {
                    world.removeBlock(pos, false);
                    world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1.5f, World.ExplosionSourceType.BLOCK);
                }
            } else {
                world.setBlockState(pos, state.with(SET, true), 3);
            }
        }
        assert world != null;
        world.scheduleBlockTick(pos, this, 3, TickPriority.byIndex(1));
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (world.isClient)
            return;
        world.removeBlock(pos, false);
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1.5f, World.ExplosionSourceType.BLOCK);
    }

    @SuppressWarnings("unused")
    private boolean shouldPower(World world, BlockPos pos, BlockState state) {
        Box detectionBox = new Box(pos);

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, detectionBox, player -> true);
        return !entities.isEmpty();
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
        if (state.get(SET)) {
            return VOXEL_SHAPE_SET;
        }
        return VOXEL_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SET);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getSide() == Direction.UP) {
            return this.getDefaultState().with(FACING, Direction.UP);
        }
        return null;
    }
}
