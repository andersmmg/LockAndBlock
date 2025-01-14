package com.andersmmg.lockandblock.datagen;

import com.andersmmg.lockandblock.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.TRIP_MINE);
        addDrop(ModBlocks.PROX_MINE);
        addDrop(ModBlocks.LAND_MINE);
        addDrop(ModBlocks.DETONATOR_MINE);
        addDrop(ModBlocks.LASER_SENSOR);
        addDrop(ModBlocks.PLAYER_SENSOR);
        addDrop(ModBlocks.REDSTONE_LASER);
        addDrop(ModBlocks.TESLA_COIL);
        addDrop(ModBlocks.KEYPAD);
        addDrop(ModBlocks.KEYCARD_READER);
        addDrop(ModBlocks.KEYCARD_WRITER);
        addDrop(ModBlocks.KEYCARD_CLONER);
        addDrop(ModBlocks.FORCEFIELD_GENERATOR);

        addDrop(ModBlocks.REINFORCED_IRON_BLOCK);
        addDrop(ModBlocks.REINFORCED_IRON_TRAPDOOR);
        addDrop(ModBlocks.REINFORCED_IRON_FENCE);
        addDrop(ModBlocks.REINFORCED_IRON_FENCE_GATE);
        addDrop(ModBlocks.REINFORCED_IRON_WALL);
        addDrop(ModBlocks.REINFORCED_IRON_STAIRS);
        addDrop(ModBlocks.REINFORCED_IRON_BUTTON);
        addDrop(ModBlocks.REINFORCED_IRON_PRESSURE_PLATE);

        addDrop(ModBlocks.REINFORCED_IRON_DOOR, doorDrops(ModBlocks.REINFORCED_IRON_DOOR));
        addDrop(ModBlocks.REINFORCED_IRON_SLAB, slabDrops(ModBlocks.REINFORCED_IRON_SLAB));
    }

}