package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.util.VoxelUtils;
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
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class TeslaCoilBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 7, 6, 9, 9, 14), Block.createCuboidShape(5, 5, 14, 11, 11, 16), BooleanBiFunction.OR), Stream.of(
            Block.createCuboidShape(4, 5, 7, 6, 11, 9),
            Block.createCuboidShape(10, 5, 7, 12, 11, 9),
            Block.createCuboidShape(5, 10, 7, 11, 12, 9),
            Block.createCuboidShape(5, 4, 7, 11, 6, 9)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape VOXEL_SHAPE_UP = VoxelShapes.combineAndSimplify(VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 2, 7, 9, 10, 9), Block.createCuboidShape(5, 0, 5, 11, 2, 11), BooleanBiFunction.OR), Stream.of(
            Block.createCuboidShape(4, 7, 5, 6, 9, 11),
            Block.createCuboidShape(10, 7, 5, 12, 9, 11),
            Block.createCuboidShape(5, 7, 10, 11, 9, 12),
            Block.createCuboidShape(5, 7, 4, 11, 9, 6)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape VOXEL_SHAPE_DOWN = VoxelShapes.combineAndSimplify(VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 6, 7, 9, 14, 9), Block.createCuboidShape(5, 14, 5, 11, 16, 11), BooleanBiFunction.OR), Stream.of(
            Block.createCuboidShape(4, 7, 5, 6, 9, 11),
            Block.createCuboidShape(10, 7, 5, 12, 9, 11),
            Block.createCuboidShape(5, 7, 4, 11, 9, 6),
            Block.createCuboidShape(5, 7, 10, 11, 9, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);

    public TeslaCoilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, 10, TickPriority.NORMAL);
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                world.setBlockState(pos, state.cycle(POWERED), 2);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient()) return;
        if (state.get(POWERED)) {
            Box detectionBox = new Box(pos).expand(LockAndBlock.CONFIG.teslaCoilRange());

            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, detectionBox, player -> true);

            for (LivingEntity entity : entities) {
                if (entity.canTakeDamage())
                    entity.damage(LockAndBlock.damageOf(world, LockAndBlock.TESLA_COIL_DAMAGE), LockAndBlock.CONFIG.teslaCoilDamage());
            }
        }
        world.scheduleBlockTick(pos, this, random.nextBetween(10, 20), TickPriority.byIndex(1));
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                world.setBlockState(pos, state.cycle(POWERED), 2);
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
