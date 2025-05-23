package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.KeypadBlockEntity;
import com.andersmmg.lockandblock.client.screen.KeypadScreen;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public class KeypadBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty SET = BooleanProperty.of("set");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty TOGGLE = BooleanProperty.of("toggle");
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(3, 3, 15, 13, 13, 16);

    public KeypadBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(SET, false)
                .with(TOGGLE, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking()) {
            if (world.isClient()) {
                // Sneak to show extra setting
                showScreen((KeypadBlockEntity) world.getBlockEntity(pos), state.get(TOGGLE), true);
            }
            return ActionResult.SUCCESS;
        }

        if (state.get(POWERED) && !state.get(TOGGLE)) {
            return ActionResult.FAIL;
        }

        if (world.isClient()) {
            showScreen((KeypadBlockEntity) world.getBlockEntity(pos), state.get(TOGGLE), false);
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void showScreen(KeypadBlockEntity blockEntity, boolean toggle, boolean unlocked) {
        MinecraftClient.getInstance().setScreen(new KeypadScreen(blockEntity, toggle, unlocked));
    }

    public void activate(BlockState state, World world, BlockPos pos) {
        if (!world.isClient()) {
            BlockState newState;
            if (state.get(TOGGLE)) {
                boolean newPowered = !state.get(POWERED);
                newState = state.with(POWERED, newPowered);
            } else {
                if (state.get(POWERED)) {
                    return;
                }
                newState = state.with(POWERED, true);
                world.scheduleBlockTick(pos, this, LockAndBlock.CONFIG.redstonePulseLength(), TickPriority.NORMAL);
            }
            world.setBlockState(pos, newState, 3);
            this.updateNeighbors(state, (ServerWorld) world, pos);
        }
    }

    private void updateNeighbors(BlockState state, ServerWorld world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && getDirection(state) == direction ? 15 : 0;
    }

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
        return VoxelUtils.rotateShape(getDirection(state), VOXEL_SHAPE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, SET, TOGGLE);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            if (!state.get(TOGGLE)) {
                world.setBlockState(pos, state.with(POWERED, false), 2);
            }
        }

        this.updateNeighbors(state, world, pos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        if (side == Direction.UP || side == Direction.DOWN) {
            return null;
        } else {
            return this.getDefaultState().with(FACING, side);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KeypadBlockEntity(pos, state);
    }
}
