package com.andersmmg.lockandblock.record;

import net.minecraft.util.math.BlockPos;

public record KeypadCodePacket(BlockPos pos, String code, KeypadCodePacketType type) {
}