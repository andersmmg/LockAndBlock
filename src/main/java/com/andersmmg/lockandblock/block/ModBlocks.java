package com.andersmmg.lockandblock.block;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.custom.KeycardClonerBlock;
import com.andersmmg.lockandblock.block.custom.KeycardReaderBlock;
import com.andersmmg.lockandblock.block.custom.KeycardWriterBlock;
import com.andersmmg.lockandblock.item.ModItemGroups;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {
    public static final Block KEYCARD_READER = registerBlock("keycard_reader",
            new KeycardReaderBlock(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE).nonOpaque()));
    public static final Block KEYCARD_WRITER = registerBlock("keycard_writer",
            new KeycardWriterBlock(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE).nonOpaque()));
    public static final Block KEYCARD_CLONER = registerBlock("keycard_cloner",
            new KeycardClonerBlock(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE).nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, LockAndBlock.id(name), block);
    }

    private static Block registerBlockOnly(String name, Block block) {
        return Registry.register(Registries.BLOCK, LockAndBlock.id(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, LockAndBlock.id(name),
                new BlockItem(block, new OwoItemSettings().group(ModItemGroups.LOCKBLOCK_GROUP)));
    }

    public static void registerModBlocks() {
        LockAndBlock.LOGGER.info("Registering ModBlocks for " + LockAndBlock.MOD_ID);
    }
}