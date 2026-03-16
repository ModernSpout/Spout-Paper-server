package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedBlockCodec;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The implementation for {@link WrappedBlockCodec}.
 */
public final class WrappedBlockCodecImpl<B extends Block> implements WrappedBlockCodec<B> {

    private final MapCodec<B> codec;

    private WrappedBlockCodecImpl(MapCodec<B> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<B> getCodec() {
        return this.codec;
    }

    private static final Map<MapCodec<?>, WrappedBlockCodec<?>> MAP = new IdentityHashMap<>();

    public static <B extends Block> WrappedBlockCodec<B> wrap(MapCodec<B> codec) {
        return (WrappedBlockCodec<B>) MAP.computeIfAbsent(codec, $ -> new WrappedBlockCodecImpl<>(codec));
    }

}
