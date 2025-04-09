package com.andersmmg.lockandblock.block.entity;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {
    public static void registerBlockEntities() {
        LockAndBlock.LOGGER.info("Registering Block Entities for " + LockAndBlock.MOD_ID);
    }

    public static final BlockEntityType<KeycardReaderBlockEntity> KEYCARD_READER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, LockAndBlock.id("keycard_reader_be"),
                    FabricBlockEntityTypeBuilder.create(KeycardReaderBlockEntity::new,
                            ModBlocks.KEYCARD_READER).build());
    public static final BlockEntityType<KeycardClonerBlockEntity> KEYCARD_CLONER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, LockAndBlock.id("keycard_cloner_be"),
                    FabricBlockEntityTypeBuilder.create(KeycardClonerBlockEntity::new,
                            ModBlocks.KEYCARD_CLONER).build());
    public static final BlockEntityType<KeypadBlockEntity> KEYPAD_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, LockAndBlock.id("keypad_be"),
                    FabricBlockEntityTypeBuilder.create(KeypadBlockEntity::new,
                            ModBlocks.KEYPAD).build());
    public static final BlockEntityType<LaserBlockEntity> LASER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, LockAndBlock.id("laser_be"),
                    FabricBlockEntityTypeBuilder.create(LaserBlockEntity::new,
                            ModBlocks.REDSTONE_LASER, ModBlocks.LASER_SENSOR, ModBlocks.TRIP_MINE).build());
    public static final BlockEntityType<LockBlockEntity> LOCK_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, LockAndBlock.id("lock_be"),
                    FabricBlockEntityTypeBuilder.create(LockBlockEntity::new,
                            ModBlocks.LOCK_BLOCK, ModBlocks.REINFORCED_IRON_DOOR).build());


}