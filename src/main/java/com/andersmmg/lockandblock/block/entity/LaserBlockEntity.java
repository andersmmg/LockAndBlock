package com.andersmmg.lockandblock.block.entity;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.block.custom.LaserBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LaserBlockEntity extends BlockEntity {
    public LaserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, LaserBlockEntity ignoredLaserBlockEntity) {
        if (!world.isClient) return;
        Direction direction = blockState.get(Properties.FACING);
        for (int i = 1; i <= blockState.get(LockAndBlock.DISTANCE); i++) {
            spawnParticles(blockState, world, blockPos.offset(direction, i));
        }
    }

    private static void spawnParticles(BlockState state, World world, BlockPos pos) {
        // Ensure we are on the client side
        if (!world.isClient) return;

        Block block = state.getBlock();
        int colorRgb;

        if (block.equals(ModBlocks.TRIP_MINE)) {
            colorRgb = 0x00FF00;
        } else if (block.equals(ModBlocks.LASER_SENSOR)) {
            colorRgb = 0x30BEFF;
        } else if (block.equals(ModBlocks.REDSTONE_LASER)) {
            colorRgb = 0xFF0000;
        } else {
            colorRgb = 0x000000; // Default Blue
        }

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
}
