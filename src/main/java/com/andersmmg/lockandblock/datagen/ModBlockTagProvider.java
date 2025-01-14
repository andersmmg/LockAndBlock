package com.andersmmg.lockandblock.datagen;

import com.andersmmg.lockandblock.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.REINFORCED_IRON_SLAB)
                .add(ModBlocks.REINFORCED_IRON_STAIRS)
                .add(ModBlocks.REINFORCED_IRON_WALL)
                .add(ModBlocks.REINFORCED_IRON_FENCE)
                .add(ModBlocks.REINFORCED_IRON_FENCE_GATE)
                .add(ModBlocks.REINFORCED_IRON_BUTTON)
                .add(ModBlocks.REINFORCED_IRON_PRESSURE_PLATE)
                .add(ModBlocks.REINFORCED_IRON_BLOCK)
                .add(ModBlocks.REINFORCED_IRON_TRAPDOOR)
                .add(ModBlocks.REINFORCED_IRON_DOOR);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.REINFORCED_IRON_SLAB)
                .add(ModBlocks.REINFORCED_IRON_STAIRS)
                .add(ModBlocks.REINFORCED_IRON_WALL)
                .add(ModBlocks.REINFORCED_IRON_FENCE)
                .add(ModBlocks.REINFORCED_IRON_FENCE_GATE)
                .add(ModBlocks.REINFORCED_IRON_BUTTON)
                .add(ModBlocks.REINFORCED_IRON_PRESSURE_PLATE)
                .add(ModBlocks.REINFORCED_IRON_BLOCK)
                .add(ModBlocks.REINFORCED_IRON_TRAPDOOR)
                .add(ModBlocks.REINFORCED_IRON_DOOR);

        getOrCreateTagBuilder(BlockTags.DOORS)
                .add(ModBlocks.REINFORCED_IRON_DOOR);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .add(ModBlocks.REINFORCED_IRON_TRAPDOOR);
        getOrCreateTagBuilder(BlockTags.BUTTONS)
                .add(ModBlocks.REINFORCED_IRON_BUTTON);
        getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES)
                .add(ModBlocks.REINFORCED_IRON_PRESSURE_PLATE);

        getOrCreateTagBuilder(BlockTags.SLABS)
                .add(ModBlocks.REINFORCED_IRON_SLAB);
        getOrCreateTagBuilder(BlockTags.STAIRS)
                .add(ModBlocks.REINFORCED_IRON_STAIRS);

        getOrCreateTagBuilder(BlockTags.FENCES)
                .add(ModBlocks.REINFORCED_IRON_FENCE);
        getOrCreateTagBuilder(BlockTags.FENCE_GATES)
                .add(ModBlocks.REINFORCED_IRON_FENCE_GATE);
        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.REINFORCED_IRON_WALL);
    }
}