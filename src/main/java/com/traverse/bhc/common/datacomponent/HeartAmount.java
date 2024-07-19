package com.traverse.bhc.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public record HeartAmount(Map<Integer, Integer> slotToAmount) {
    public static final Codec<HeartAmount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.INT, Codec.INT).fieldOf("slotToAmount").forGetter(HeartAmount::slotToAmount)
    ).apply(instance, HeartAmount::new));
    public static final StreamCodec<ByteBuf, HeartAmount> STREAM_CODEC = ByteBufCodecs.fromCodec(HeartAmount.CODEC);

    public static Map<Integer, Integer> createSizedMap(int size) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, 0);
        }
        return map;
    }
}
