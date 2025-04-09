package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DetonatorMineBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty SET = LockAndBlock.SET;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(5, 6, 14, 11, 10, 16);

    public DetonatorMineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SET, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    public void detonate(World world, BlockPos pos) {
        if (world.isClient)
            return;
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, World.ExplosionSourceType.NONE);
        world.removeBlock(pos, false);
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
        return VoxelUtils.rotateShape(getDirection(state), VOXEL_SHAPE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SET);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getSide().getAxis() == Direction.Axis.Y) {
            return null;
        }
        return this.getDefaultState().with(FACING, ctx.getSide());
    }
}
