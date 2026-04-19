package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;
import org.fiddlemc.fiddle.impl.packetmapping.item.datadriven.UnappliedDataDrivenItemMapping;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The implementation for {@link WrappedItemCodec}.
 */
public final class WrappedItemCodecImpl<I extends Item> implements WrappedItemCodec<I> {

    private final MapCodec<I> codec;

    /**
     * A codec wrapping {@link #codec},
     * that sets {@link Item#unappliedDataPackMappings}.
     */
    private final MapCodec<I> extendedCodec;

    private WrappedItemCodecImpl(MapCodec<I> codec) {
        this.codec = codec;
        this.extendedCodec = new MapCodec<>() {

            @Override
            public <T> RecordBuilder<T> encode(I input, DynamicOps<T> ops, RecordBuilder<T> recordBuilder) {
                return codec.encode(input, ops, recordBuilder);
            }

            @Override
            public <T> DataResult<I> decode(DynamicOps<T> ops, MapLike<T> mapLike) {
                return codec.decode(ops, mapLike).flatMap(item -> {
                    T mappingsInput = mapLike.get("mappings");
                    if (mappingsInput != null) {
                        DataResult<Pair<List<UnappliedDataDrivenItemMapping>, T>> mappings = UnappliedDataDrivenItemMapping.LIST_CODEC.decode(ops, mappingsInput);
                        if (mappings.isError()) {
                            return mappings.map($ -> null);
                        }
                        item.unappliedDataPackMappings = mappings.getOrThrow().getFirst();
                    }
                    return DataResult.success(item);
                });
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return codec.keys(ops);
            }

        };
    }

    @Override
    public MapCodec<I> getCodec() {
        return this.codec;
    }

    @Override
    public MapCodec<I> getExtendedCodec() {
        return this.extendedCodec;
    }

    private static final Map<MapCodec<?>, WrappedItemCodec<?>> MAP = new IdentityHashMap<>();

    public static <I extends Item> WrappedItemCodec<I> wrap(MapCodec<I> codec) {
        return (WrappedItemCodec<I>) MAP.computeIfAbsent(codec, $ -> new WrappedItemCodecImpl<>(codec));
    }

}
