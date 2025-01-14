package com.andersmmg.lockandblock.datagen;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerActivatable(blockStateModelGenerator, ModBlocks.KEYCARD_READER);
        registerActivatable(blockStateModelGenerator, ModBlocks.KEYPAD);
        registerRotatable(blockStateModelGenerator, ModBlocks.KEYCARD_WRITER);
        registerRotatable(blockStateModelGenerator, ModBlocks.KEYCARD_CLONER);
        registerRotatablePowered(blockStateModelGenerator, ModBlocks.TESLA_COIL);
        registerForceField(blockStateModelGenerator, ModBlocks.FORCEFIELD);
        registerForceFieldGenerator(blockStateModelGenerator, ModBlocks.FORCEFIELD_GENERATOR, TexturedModel.ORIENTABLE);
        registerRotatablePowered(blockStateModelGenerator, ModBlocks.PLAYER_SENSOR);
        registerMine(blockStateModelGenerator, ModBlocks.PROX_MINE);
        registerMine(blockStateModelGenerator, ModBlocks.LAND_MINE);
        registerMine(blockStateModelGenerator, ModBlocks.TRIP_MINE);
        registerMine(blockStateModelGenerator, ModBlocks.LASER_SENSOR);
        registerMine(blockStateModelGenerator, ModBlocks.DETONATOR_MINE);
        registerRotatablePowered(blockStateModelGenerator, ModBlocks.REDSTONE_LASER);

        BlockStateModelGenerator.BlockTexturePool reinforcedIronPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.REINFORCED_IRON_BLOCK);
        reinforcedIronPool.stairs(ModBlocks.REINFORCED_IRON_STAIRS);
        reinforcedIronPool.slab(ModBlocks.REINFORCED_IRON_SLAB);
        reinforcedIronPool.button(ModBlocks.REINFORCED_IRON_BUTTON);
        reinforcedIronPool.pressurePlate(ModBlocks.REINFORCED_IRON_PRESSURE_PLATE);
        reinforcedIronPool.fence(ModBlocks.REINFORCED_IRON_FENCE);
        reinforcedIronPool.fenceGate(ModBlocks.REINFORCED_IRON_FENCE_GATE);
        reinforcedIronPool.wall(ModBlocks.REINFORCED_IRON_WALL);

        blockStateModelGenerator.registerDoor(ModBlocks.REINFORCED_IRON_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.REINFORCED_IRON_TRAPDOOR);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.KEYCARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.GUIDEBOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.REMOTE_DETONATOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.CARBON_POWDER, Models.GENERATED);
        itemModelGenerator.register(ModItems.REINFORCED_IRON_INGOT, Models.GENERATED);
    }

    @SuppressWarnings("SameParameterValue")
    private void registerForceField(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        Identifier model_base = ModelIds.getBlockModelId(block);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model_base)));
    }

    public final void registerForceFieldGenerator(BlockStateModelGenerator blockStateModelGenerator, Block block, TexturedModel.Factory modelFactory) {
        Identifier identifier = modelFactory.upload(block, blockStateModelGenerator.modelCollector);
        Identifier identifier2 = TextureMap.getSubId(block, "_front_on");
        Identifier identifier3 = modelFactory.get(block).textures((textures) -> textures.put(TextureKey.FRONT, identifier2)).upload(block, "_on", blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, identifier3, identifier))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerMine(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        Identifier model_base = ModelIds.getBlockModelId(block);
        Identifier model_set = ModelIds.getBlockSubModelId(block, "_set");
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model_base))
                .coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(LockAndBlock.SET, model_set, model_base)));
    }

    private void registerRotatablePowered(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        Identifier model_base = ModelIds.getBlockModelId(block);
        Identifier model_powered = ModelIds.getBlockSubModelId(block, "_powered");
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model_base))
                .coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, model_powered, model_base)));
    }

    private void registerRotatable(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        Identifier model_base = ModelIds.getBlockModelId(block);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model_base))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerActivatable(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        Identifier model_base = ModelIds.getBlockModelId(block);
        Identifier model_open = ModelIds.getBlockSubModelId(block, "_active");
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, model_open, model_base)));
    }

}