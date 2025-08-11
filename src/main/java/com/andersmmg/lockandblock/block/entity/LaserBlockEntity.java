package com.andersmmg.lockandblock.block.entity;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.block.custom.LaserBlock;
import com.andersmmg.lockandblock.block.custom.LaserSensorBlock;
import com.andersmmg.lockandblock.block.custom.RedstoneLaser;
import com.andersmmg.lockandblock.block.custom.TripMineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LaserBlockEntity extends BlockEntity {
    public int currentDistance = 0;

    public LaserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, LaserBlockEntity laserBlockEntity) {
        updateDistance(blockState, world, blockPos, laserBlockEntity);
        if (!world.isClient) return;
        if (LockAndBlock.CONFIG.laserParticles()) {
            Direction direction = blockState.get(Properties.FACING);
            for (int i = 1; i <= laserBlockEntity.currentDistance; i++) {
                spawnParticles(blockState, world, blockPos.offset(direction, i));
            }
        }
    }

    public static void updateDistance(BlockState state, World world, BlockPos pos, LaserBlockEntity laserBlockEntity) {
        Direction direction = state.get(LaserSensorBlock.FACING);
        int newDistance = LockAndBlock.CONFIG.allowLaserInAir() ? LockAndBlock.CONFIG.maxLaserDistance() + 1 : 0;

        for (int i = 1; i <= LockAndBlock.CONFIG.maxLaserDistance() + 1; i++) {
            BlockState blockState = world.getBlockState(pos.offset(direction, i));
            String blockId = Registries.BLOCK.getId(blockState.getBlock()).toString();
            if (LockAndBlock.CONFIG.laserPassthroughWhitelist().contains(blockId)) {
                continue;
            }
            if (blockState.getCameraCollisionShape(world, pos.offset(direction, i), ShapeContext.absent()).isEmpty()) {
                continue;
            }
            if (blockState.isSideSolid(world, pos.offset(direction, i), direction.getOpposite(), SideShapeType.CENTER)) {
                newDistance = i;
                break;
            }
            if (blockState.isSideSolid(world, pos.offset(direction, i), direction, SideShapeType.CENTER)) {
                newDistance = i + 1;
                break;
            }
        }

        laserBlockEntity.currentDistance = newDistance;
    }

    public static int getDistance(LaserBlockEntity entity) {
        return entity.currentDistance;
    }

    public static int getColor(BlockState state) {
        Block block = state.getBlock();
        if (block.equals(ModBlocks.TRIP_MINE)) {
            return 0x00FF00;
        } else if (block.equals(ModBlocks.LASER_SENSOR)) {
            return 0x30BEFF;
        } else if (block.equals(ModBlocks.REDSTONE_LASER)) {
            return 0xFF0000;
        }
        return 0x000000; // Default Blue
    }

    private static void spawnParticles(BlockState state, World world, BlockPos pos) {
        // Ensure we are on the client side
        if (!world.isClient) return;

        int colorRgb = getColor(state);

        Direction direction = state.get(Properties.FACING).getOpposite();
        Direction direction2 = LaserBlock.getDirection(state).getOpposite();
        double d = (double) pos.getX() + 0.5 + 0.0 * (double) direction.getOffsetX() + 0.4 * (double) direction2.getOffsetX();
        double e = (double) pos.getY() + 0.5 + 0.0 * (double) direction.getOffsetY() + 0.4 * (double) direction2.getOffsetY();
        double f = (double) pos.getZ() + 0.5 + 0.0 * (double) direction.getOffsetZ() + 0.4 * (double) direction2.getOffsetZ();
        float steps = 10f;
        for (int i = 0; i < (int) steps; i++) {
            world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(colorRgb).toVector3f(), (float) 0.5), d + (double) direction.getOffsetX() * (i / steps), e + (double) direction.getOffsetY() * (i / steps), f + (double) direction.getOffsetZ() * (i / steps), 0.0, 0.0, 0.0);
        }
    }

    public boolean isActive(BlockState state) {
        Block block = state.getBlock();
        if (block.equals(ModBlocks.TRIP_MINE)) {
            return state.get(TripMineBlock.SET);
        } else if (block.equals(ModBlocks.LASER_SENSOR)) {
            return state.get(LaserSensorBlock.SET);
        } else if (block.equals(ModBlocks.REDSTONE_LASER)) {
            return state.get(RedstoneLaser.POWERED);
        }
        return false;
    }
}
