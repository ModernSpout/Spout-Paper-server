package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The implementation for {@link WrappedItemCodec}.
 */
public final class WrappedItemCodecImpl<I extends Item> implements WrappedItemCodec<I> {

    private final MapCodec<I> codec;

    private WrappedItemCodecImpl(MapCodec<I> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<I> getCodec() {
        return this.codec;
    }

    private static final Map<MapCodec<?>, WrappedItemCodec<?>> MAP = new IdentityHashMap<>();

    public static <I extends Item> WrappedItemCodec<I> wrap(MapCodec<I> codec) {
        return (WrappedItemCodec<I>) MAP.computeIfAbsent(codec, $ -> new WrappedItemCodecImpl<>(codec));
    }

}
