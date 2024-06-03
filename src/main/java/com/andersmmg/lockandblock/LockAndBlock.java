package com.andersmmg.lockandblock;

import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.block.entity.KeycardReaderBlockEntity;
import com.andersmmg.lockandblock.block.entity.KeypadBlockEntity;
import com.andersmmg.lockandblock.block.entity.ModBlockEntities;
import com.andersmmg.lockandblock.config.ModConfig;
import com.andersmmg.lockandblock.item.ModItemGroups;
import com.andersmmg.lockandblock.item.ModItems;
import com.andersmmg.lockandblock.record.KeycardReaderPacket;
import com.andersmmg.lockandblock.record.KeypadCodePacket;
import com.andersmmg.lockandblock.sounds.ModSounds;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockAndBlock implements ModInitializer {
    public static final String MOD_ID = "lockandblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModConfig CONFIG = ModConfig.createAndLoad();
    public static final String CARD_UUID_KEY = "card_uuid";
    public static final BooleanProperty SET = BooleanProperty.of("set");

    public static final OwoNetChannel KEYCARD_READER_CHANNEL = OwoNetChannel.create(id("keycard_reader"));
    public static final OwoNetChannel KEYPAD_CODE_CHANNEL = OwoNetChannel.create(id("keypad_code"));

    public static final RegistryKey<DamageType> TESLA_COIL_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("tesla_coil_damage_type"));
    public static final RegistryKey<DamageType> LASER_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("laser_damage_type"));

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static MutableText langText(String key) {
        return langText(key, "text");
    }

    public static MutableText langText(String key, String type) {
        return Text.translatable(type + "." + LockAndBlock.MOD_ID + "." + key);
    }

    public static DamageSource damageOf(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        ModItemGroups.registerItemGroups();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModSounds.registerSounds();

        KEYCARD_READER_CHANNEL.registerServerbound(KeycardReaderPacket.class, (message, access) -> {
            World world = access.player().getServerWorld();
            BlockEntity blockEntity = world.getBlockEntity(message.pos());
            if (blockEntity instanceof KeycardReaderBlockEntity readerBlockEntity && readerBlockEntity.hasUuid() && readerBlockEntity.getUuid().equals(message.uuid())) {
                if (!message.remove()) {
                    readerBlockEntity.clearUuid();
                } else {
                    world.breakBlock(message.pos(), true);
                }
            }
        });
        KEYPAD_CODE_CHANNEL.registerServerbound(KeypadCodePacket.class, (message, access) -> {
            World world = access.player().getServerWorld();
            BlockEntity blockEntity = world.getBlockEntity(message.pos());
            if (blockEntity instanceof KeypadBlockEntity keypadBlockEntity) {
                keypadBlockEntity.testCode(message.code());
            }
        });
    }
}
