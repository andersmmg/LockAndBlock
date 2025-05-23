package com.andersmmg.lockandblock;

import com.andersmmg.lockandblock.block.ModBlocks;
import com.andersmmg.lockandblock.block.custom.KeycardReaderBlock;
import com.andersmmg.lockandblock.block.entity.KeycardReaderBlockEntity;
import com.andersmmg.lockandblock.block.entity.KeypadBlockEntity;
import com.andersmmg.lockandblock.block.entity.ModBlockEntities;
import com.andersmmg.lockandblock.config.ModConfig;
import com.andersmmg.lockandblock.item.ModItemGroups;
import com.andersmmg.lockandblock.item.ModItems;
import com.andersmmg.lockandblock.recipe.CloningRecipe;
import com.andersmmg.lockandblock.recipe.CloningRecipeSerializer;
import com.andersmmg.lockandblock.record.KeycardReaderPacket;
import com.andersmmg.lockandblock.record.KeypadCodePacket;
import com.andersmmg.lockandblock.sounds.ModSounds;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockAndBlock implements ModInitializer {
    public static final String MOD_ID = "lockandblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModConfig CONFIG = ModConfig.createAndLoad();
    public static final String CARD_UUID_KEY = "card_uuid";
    public static final String KEY_UUID_KEY = "key_uuid";
    public static final String DETONATOR_PAIR_KEY = "paired_blocks";
    public static final BooleanProperty SET = BooleanProperty.of("set");
    public static final IntProperty DISTANCE = IntProperty.of("distance", 0, 255);

    public static final OwoNetChannel KEYCARD_READER_CHANNEL = OwoNetChannel.create(id("keycard_reader"));
    public static final OwoNetChannel KEYPAD_CODE_CHANNEL = OwoNetChannel.create(id("keypad_code"));

    public static final RecipeSerializer<CloningRecipe> KEY_CLONING_RECIPE_SERIALIZER =
            Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(LockAndBlock.MOD_ID, "cloning"), new CloningRecipeSerializer());

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
            BlockPos pos = message.pos();
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KeycardReaderBlockEntity readerBlockEntity && readerBlockEntity.hasUuid() && readerBlockEntity.getUuid().equals(message.uuid())) {
                switch (message.type()) {
                    case CLEAR:
                        readerBlockEntity.clearUuid();
                        break;
                    case REMOVE:
                        world.breakBlock(pos, true);
                        break;
                    case TOGGLE_ON:
                        world.setBlockState(pos, state.with(KeycardReaderBlock.TOGGLE, true));
                        break;
                    case TOGGLE_OFF:
                        world.setBlockState(pos, state.with(KeycardReaderBlock.TOGGLE, false));
                        break;
                }
            }
        });
        KEYPAD_CODE_CHANNEL.registerServerbound(KeypadCodePacket.class, (message, access) -> {
            World world = access.player().getServerWorld();
            BlockPos pos = message.pos();
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KeypadBlockEntity keypadBlockEntity) {
                switch (message.type()) {
                    case CHECK:
                        keypadBlockEntity.testCode(message.code(), true);
                        break;
                    case TOGGLE_ON:
                        if (keypadBlockEntity.testCode(message.code(), false)) {
                            world.setBlockState(pos, state.with(KeycardReaderBlock.TOGGLE, true));
                        }
                        break;
                    case TOGGLE_OFF:
                        if (keypadBlockEntity.testCode(message.code(), false)) {
                            world.setBlockState(pos, state.with(KeycardReaderBlock.TOGGLE, false));
                        }
                        break;
                }
            }
        });
    }
}
