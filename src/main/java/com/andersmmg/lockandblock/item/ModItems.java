package com.andersmmg.lockandblock.item;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.item.custom.KeycardItem;
import com.andersmmg.lockandblock.item.custom.RemoteDetonatorItem;
import io.wispforest.lavender.book.LavenderBookItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item KEYCARD = registerItem("keycard", new KeycardItem(new OwoItemSettings().maxCount(1).group(ModItemGroups.LOCKBLOCK_GROUP)));
    public static final Item GUIDEBOOK = LavenderBookItem.registerForBook(LockAndBlock.id("guidebook"), new OwoItemSettings().maxCount(1).group(ModItemGroups.LOCKBLOCK_GROUP));
    public static final Item REMOTE_DETONATOR = registerItem("remote_detonator", new RemoteDetonatorItem(new OwoItemSettings().maxCount(1).group(ModItemGroups.LOCKBLOCK_GROUP)));
    public static final Item REINFORCED_IRON_INGOT = registerItem("reinforced_iron_ingot", new Item(new OwoItemSettings().maxCount(64).group(ModItemGroups.LOCKBLOCK_GROUP)));
    public static final Item CARBON_POWDER = registerItem("carbon_powder", new Item(new OwoItemSettings().maxCount(64).group(ModItemGroups.LOCKBLOCK_GROUP)));

    @SuppressWarnings("SameParameterValue")
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, LockAndBlock.id(name), item);
    }

    public static void registerModItems() {
        LockAndBlock.LOGGER.info("Registering Mod Items for " + LockAndBlock.MOD_ID);
    }
}