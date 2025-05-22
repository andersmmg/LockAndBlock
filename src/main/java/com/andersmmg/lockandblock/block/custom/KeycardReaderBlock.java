package com.andersmmg.lockandblock.block.custom;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.KeycardReaderBlockEntity;
import com.andersmmg.lockandblock.client.screen.KeycardReaderScreen;
import com.andersmmg.lockandblock.item.ModItems;
import com.andersmmg.lockandblock.item.custom.KeycardItem;
import com.andersmmg.lockandblock.sounds.ModSounds;
import com.andersmmg.lockandblock.util.VoxelUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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

public class KeycardReaderBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty TOGGLE = BooleanProperty.of("toggle");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(3, 3, 15, 13, 13, 16);

    public KeycardReaderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(TOGGLE, false));
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        // Only prevent interaction if powered and not in toggle mode
        if (state.get(POWERED) && !state.get(TOGGLE)) {
            return ActionResult.FAIL;
        }
        if (stack.isOf(ModItems.KEYCARD)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KeycardReaderBlockEntity keycardReaderBlockEntity) {
                if (keycardReaderBlockEntity.hasUuid()) {
                    if (KeycardItem.hasUuid(stack)) {
                        if (keycardReaderBlockEntity.getUuid().equals(KeycardItem.getUuid(stack))) {
                            return this.activate(state, world, pos);
                        } else {
                            if (!world.isClient) {
                                world.playSound(null, pos, ModSounds.BEEP_ERROR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                player.sendMessage(LockAndBlock.langText("wrong_keycard"), true);
                            }
                        }
                    } else {
                        if (!world.isClient) {
                            world.playSound(null, pos, ModSounds.BEEP_ERROR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            player.sendMessage(LockAndBlock.langText("blank_keycard"), true);
                        }
                    }
                } else {
                    if (KeycardItem.hasUuid(stack)) {
                        if (!world.isClient) {
                            keycardReaderBlockEntity.setUuid(KeycardItem.getUuid(stack));
                            player.sendMessage(LockAndBlock.langText("reader_programmed"), true);
                            return this.activate(state, world, pos);
                        }
                    } else {
                        if (!world.isClient) {
                            world.playSound(null, pos, ModSounds.BEEP_ERROR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            player.sendMessage(LockAndBlock.langText("blank_keycard"), true);
                        }
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    private ActionResult activate(BlockState state, World world, BlockPos pos) {
        if (!world.isClient()) {
            BlockState newState;
            if (state.get(TOGGLE)) {
                // Toggle the powered state
                newState = state.cycle(POWERED);
            } else {
                // Only activate if not already powered
                if (state.get(POWERED)) {
                    return ActionResult.CONSUME;
                }
                newState = state.with(POWERED, true);
                // Schedule the power-off tick only in pulse mode (when TOGGLE is false)
                world.scheduleBlockTick(pos, this, LockAndBlock.CONFIG.redstonePulseLength(), TickPriority.NORMAL);
            }

            world.playSound(null, pos, ModSounds.BEEP_SUCCESS, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockState(pos, newState, 3);
            this.updateNeighbors(state, (ServerWorld) world, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
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
        builder.add(FACING, POWERED, TOGGLE);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Only turn off if not in toggle mode
        if (state.get(POWERED) && !state.get(TOGGLE)) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
            this.updateNeighbors(state, world, pos);
        }
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
        return new KeycardReaderBlockEntity(pos, state);
    }

    public void edit(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).get(POWERED)) {
            return;
        }
        if (context.getWorld().isClient()) {
            this.showEditScreen(context);
        }
    }


    @Environment(EnvType.CLIENT)
    public void showEditScreen(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }
        if (blockEntity instanceof KeycardReaderBlockEntity keycardReaderBlockEntity) {
            ItemStack stack = context.getPlayer().getStackInHand(context.getHand());
            if (!keycardReaderBlockEntity.checkKeycard(stack)) {
                return;
            }
            String uuid = KeycardItem.getUuid(context.getPlayer().getStackInHand(context.getHand()));
            KeycardReaderScreen screen = new KeycardReaderScreen(keycardReaderBlockEntity, uuid, state.get(TOGGLE));
            MinecraftClient.getInstance().setScreen(screen);
        }
    }
}