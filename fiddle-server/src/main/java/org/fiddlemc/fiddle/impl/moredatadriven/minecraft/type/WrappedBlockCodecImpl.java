package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedBlockCodec;
import org.fiddlemc.fiddle.impl.packetmapping.block.datadriven.UnappliedDataDrivenBlockMapping;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The implementation for {@link WrappedBlockCodec}.
 */
public final class WrappedBlockCodecImpl<B extends Block> implements WrappedBlockCodec<B> {

    private final MapCodec<B> codec;

    /**
     * A codec wrapping {@link #codec},
     * that sets {@link Block#unappliedDataPackMappings}.
     */
    private final MapCodec<B> extendedCodec;

    private WrappedBlockCodecImpl(MapCodec<B> codec) {
        this.codec = codec;
        this.extendedCodec = new MapCodec<>() {

            @Override
            public <T> RecordBuilder<T> encode(B input, DynamicOps<T> ops, RecordBuilder<T> recordBuilder) {
                return codec.encode(input, ops, recordBuilder);
            }

            @Override
            public <T> DataResult<B> decode(DynamicOps<T> ops, MapLike<T> mapLike) {
                return codec.decode(ops, mapLike).flatMap(block -> {
                    T mappingsInput = mapLike.get("mappings");
                    if (mappingsInput != null) {
                        DataResult<Pair<List<UnappliedDataDrivenBlockMapping>, T>> mappings = UnappliedDataDrivenBlockMapping.LIST_CODEC.decode(ops, mappingsInput);
                        if (mappings.isError()) {
                            return mappings.map($ -> null);
                        }
                        block.unappliedDataPackMappings = mappings.getOrThrow().getFirst();
                    }
                    return DataResult.success(block);
                });
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return codec.keys(ops);
            }

        };
    }

    @Override
    public MapCodec<B> getCodec() {
        return this.codec;
    }

    @Override
    public MapCodec<B> getExtendedCodec() {
        return this.extendedCodec;
    }

    private static final Map<MapCodec<?>, WrappedBlockCodec<?>> MAP = new IdentityHashMap<>();

    public static <B extends Block> WrappedBlockCodec<B> wrap(MapCodec<B> codec) {
        return (WrappedBlockCodec<B>) MAP.computeIfAbsent(codec, $ -> new WrappedBlockCodecImpl<>(codec));
    }

}
