package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.KeycardClonerBlockEntity;
import com.andersmmg.lockandblock.item.ModItems;
import com.andersmmg.lockandblock.item.custom.KeycardItem;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class KeycardClonerBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape VOXEL_SHAPE = Stream.of(
            Block.createCuboidShape(3, 0, 3, 13, 2, 13),
            Block.createCuboidShape(3, 2, 3, 4, 4, 13),
            Block.createCuboidShape(5, 2, 3, 6, 4, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public KeycardClonerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(ModItems.KEYCARD)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KeycardClonerBlockEntity keycardClonerBlockEntity) {
                if (keycardClonerBlockEntity.hasUuid()) {
                    if (KeycardItem.hasUuid(stack)) {
                        if (!world.isClient)
                            player.sendMessage(LockAndBlock.langText("card_not_blank"), true);
                        return ActionResult.FAIL;
                    } else {
                        if (!world.isClient) {
                            KeycardItem.setUuid(keycardClonerBlockEntity.getUuid(), stack);
                            player.sendMessage(LockAndBlock.langText("card_copied"), true);
                            keycardClonerBlockEntity.clearUuid();
                        }
                    }
                    return ActionResult.SUCCESS;
                } else {
                    if (KeycardItem.hasUuid(stack)) {
                        if (!world.isClient) {
                            keycardClonerBlockEntity.setUuid(KeycardItem.getUuid(stack));
                            player.sendMessage(LockAndBlock.langText("card_saved"), true);
                        }
                    } else {
                        if (!world.isClient)
                            player.sendMessage(LockAndBlock.langText("card_blank"), true);
                    }
                }
            }
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KeycardClonerBlockEntity keycardClonerBlockEntity && keycardClonerBlockEntity.hasUuid()) {
                if (!world.isClient) {
                    keycardClonerBlockEntity.clearUuid();
                    player.sendMessage(LockAndBlock.langText("card_cleared"), true);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
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
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KeycardClonerBlockEntity(pos, state);
    }
}
