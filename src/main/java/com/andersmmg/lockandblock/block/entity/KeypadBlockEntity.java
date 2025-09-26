package com.andersmmg.lockandblock.block.entity;

import com.andersmmg.lockandblock.block.custom.KeypadBlock;
import com.andersmmg.lockandblock.sounds.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class KeypadBlockEntity extends BlockEntity {
    private final String CODE_KEY = "code";
    private String code = "";

    public KeypadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEYPAD_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putString(CODE_KEY, code);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        code = nbt.getString(CODE_KEY);
    }

    public void setCode(String code) {
        this.code = code;
        updateState();
    }

    public boolean hasCode() {
        return !code.isEmpty();
    }

    public boolean isSet() {
        updateState();
        return getCachedState().get(KeypadBlock.SET);
    }

    public void updateState() {
        if (world.isClient) return;
        BlockState state = getCachedState();
        if (hasCode() != state.get(KeypadBlock.SET)) {
            state = state.with(KeypadBlock.SET, hasCode());
            world.setBlockState(getPos(), state, 3);
        }
    }

    public boolean checkCode(String code) {
        updateState();
        return this.code.equals(code);
    }

    public boolean testCode(String currentCode, boolean activate) {
        if (getCachedState().getBlock() instanceof KeypadBlock keypadBlock) {
            if (hasCode()) {
                if (checkCode(currentCode)) {
                    if (activate) {
                        keypadBlock.activate(getCachedState(), getWorld(), getPos());
                    }
                    world.playSound(null, getPos(), ModSounds.BEEP_SUCCESS, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return true;
                } else {
                    world.playSound(null, getPos(), ModSounds.BEEP_ERROR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return false;
                }
            } else {
                setCode(currentCode);
                keypadBlock.activate(getCachedState(), getWorld(), getPos());
                return true;
            }
        }
        return false;
    }
}
