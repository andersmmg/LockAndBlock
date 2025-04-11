package com.andersmmg.lockandblock.datagen;

import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.FORCEFIELD_GENERATOR, 4)
                .pattern("rSr")
                .pattern("SnS")
                .pattern("rSr")
                .input('n', Items.NETHER_STAR)
                .input('S', Blocks.STONE)
                .input('r', Items.REDSTONE)
                .criterion("has_item", conditionsFromItem(Blocks.REDSTONE_BLOCK))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.TESLA_COIL, 2)
                .pattern("III")
                .pattern(" c ")
                .pattern("rCr")
                .input('I', Blocks.IRON_BLOCK)
                .input('c', Items.COPPER_INGOT)
                .input('r', Items.REDSTONE)
                .input('C', Blocks.COPPER_BLOCK)
                .criterion(hasItem(Blocks.IRON_BLOCK), conditionsFromItem(Blocks.IRON_BLOCK))
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.KEYPAD)
                .pattern("ici")
                .pattern(" R ")
                .pattern("i i")
                .input('i', Items.IRON_INGOT)
                .input('c', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .criterion("has_item", conditionsFromItem(ModItems.KEYCARD))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.KEYCARD_READER)
                .input(ModBlocks.KEYPAD)
                .input(Items.COPPER_INGOT)
                .criterion("has_item", conditionsFromItem(ModItems.KEYCARD))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.KEYCARD_WRITER)
                .pattern("i i")
                .pattern("cRc")
                .pattern("i i")
                .input('i', Items.IRON_INGOT)
                .input('c', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .criterion("has_item", conditionsFromItem(ModItems.KEYCARD))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.KEYCARD_CLONER)
                .pattern("i i")
                .pattern("cRc")
                .pattern("iki")
                .input('i', Items.IRON_INGOT)
                .input('c', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .input('k', ModItems.KEYCARD)
                .criterion("has_item", conditionsFromItem(ModItems.KEYCARD))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.KEYCARD)
                .input(Items.PAPER)
                .input(Items.REDSTONE)
                .input(Items.IRON_NUGGET)
                .criterion("has_item", conditionsFromItem(Items.PAPER))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.PLAYER_SENSOR)
                .pattern(" c ")
                .pattern("REG")
                .pattern(" c ")
                .input('c', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .input('E', Items.ENDER_PEARL)
                .input('G', Blocks.GLASS_PANE)
                .criterion("has_item", conditionsFromItem(Items.ENDER_PEARL))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModBlocks.LAND_MINE, 2)
                .pattern(" p ")
                .pattern("cTc")
                .input('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .input('T', Blocks.TNT)
                .input('c', Items.COPPER_INGOT)
                .criterion("has_item", conditionsFromItem(Blocks.TNT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModBlocks.TRIP_MINE, 2)
                .pattern("ii ")
                .pattern("TPG")
                .pattern("ii ")
                .input('i', Items.IRON_INGOT)
                .input('T', Blocks.TNT)
                .input('P', Items.PRISMARINE_SHARD)
                .input('G', Blocks.GREEN_STAINED_GLASS_PANE)
                .criterion(hasItem(Blocks.TNT), conditionsFromItem(Blocks.TNT))
                .criterion(hasItem(Items.PRISMARINE_SHARD), conditionsFromItem(Items.PRISMARINE_SHARD))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModBlocks.PROX_MINE)
                .input(ModBlocks.PLAYER_SENSOR)
                .input(Blocks.TNT)
                .input(Items.COPPER_INGOT)
                .criterion("has_item", conditionsFromItem(ModBlocks.PLAYER_SENSOR))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.LASER_SENSOR)
                .pattern(" g ")
                .pattern("rPr")
                .pattern(" c ")
                .input('g', Blocks.GLASS_PANE)
                .input('r', Items.REDSTONE)
                .input('P', Items.PRISMARINE_SHARD)
                .input('c', Items.COMPARATOR)
                .criterion(hasItem(Items.PRISMARINE_SHARD), conditionsFromItem(Items.PRISMARINE_SHARD))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.REDSTONE_LASER)
                .pattern(" p ")
                .pattern("rBr")
                .pattern(" g ")
                .input('p', Items.PRISMARINE_SHARD)
                .input('r', Items.REDSTONE)
                .input('B', Items.BLAZE_ROD)
                .input('g', Blocks.GLASS_PANE)
                .criterion("has_item", conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModBlocks.DETONATOR_MINE)
                .pattern(" r ")
                .pattern("cTc")
                .pattern(" a ")
                .input('r', Items.REDSTONE)
                .input('T', Blocks.TNT)
                .input('c', Items.COPPER_INGOT)
                .input('a', Items.AMETHYST_SHARD)
                .criterion("has_item", conditionsFromItem(Blocks.TNT))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CARBON_POWDER)
                .input(ItemTags.COALS)
                .criterion("has_item", conditionsFromItem(Items.COAL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.REINFORCED_IRON_INGOT)
                .input(ModItems.CARBON_POWDER)
                .input(Items.IRON_INGOT)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, "reinforced_iron_ingot_from_carbon");

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.REINFORCED_IRON_INGOT, 9)
                .input(ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter, "reinforced_iron_ingot_from_block");

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_IRON_BLOCK)
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .input('B', ModItems.REINFORCED_IRON_INGOT)
                .criterion(hasItem(ModItems.REINFORCED_IRON_INGOT), conditionsFromItem(ModItems.REINFORCED_IRON_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_IRON_STAIRS, 4)
                .pattern("B  ")
                .pattern("BB ")
                .pattern("BBB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_IRON_SLAB, 6)
                .pattern("BBB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.REINFORCED_IRON_BUTTON)
                .input(ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.REINFORCED_IRON_PRESSURE_PLATE)
                .pattern("BB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.REINFORCED_IRON_FENCE, 3)
                .pattern("BIB")
                .pattern("BIB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.REINFORCED_IRON_FENCE_GATE)
                .pattern("IBI")
                .pattern("IBI")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.REINFORCED_IRON_WALL, 6)
                .pattern("BBB")
                .pattern("BBB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.REINFORCED_IRON_DOOR, 3)
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.REINFORCED_IRON_TRAPDOOR, 2)
                .pattern("BBB")
                .pattern("BBB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.KEY, 1)
                .pattern("g")
                .pattern("g")
                .input('g', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.LOCK_BLOCK, 2)
                .pattern("BBB")
                .pattern("BRB")
                .pattern("BBB")
                .input('B', ModBlocks.REINFORCED_IRON_BLOCK)
                .input('R', Items.REDSTONE)
                .criterion(hasItem(ModBlocks.REINFORCED_IRON_BLOCK), conditionsFromItem(ModBlocks.REINFORCED_IRON_BLOCK))
                .offerTo(exporter);
    }
}
