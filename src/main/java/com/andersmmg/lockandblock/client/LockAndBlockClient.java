package com.andersmmg.lockandblock.client;

import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.block.entity.ModBlockEntities;
import com.andersmmg.lockandblock.client.render.LaserBlockEntityRenderer;
import com.andersmmg.lockandblock.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.util.Colors;

public class LockAndBlockClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FORCEFIELD, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_IRON_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_IRON_TRAPDOOR, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.LASER_BLOCK_ENTITY, LaserBlockEntityRenderer::new);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (stack.isOf(ModItems.KEYCARD)) {
                return DyedColorComponent.getColor(stack, Colors.WHITE);
            }
            return 0;
        }, ModItems.KEYCARD);
    }
}
