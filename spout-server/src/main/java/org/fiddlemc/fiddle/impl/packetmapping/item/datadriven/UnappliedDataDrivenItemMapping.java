package org.fiddlemc.fiddle.impl.packetmapping.item.datadriven;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import java.util.List;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A data-driven mapping for a {@link Item} that has not been applied yet.
 */
public final class UnappliedDataDrivenItemMapping {

    public static final Codec<UnappliedDataDrivenItemMapping> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(UnappliedDataDrivenItemMapping mapping, DynamicOps<T> ops, T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> DataResult<Pair<UnappliedDataDrivenItemMapping, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(mapLike -> DataResult.success(Pair.of(new UnappliedDataDrivenItemMapping(ops, mapLike), input)));
        }

    };

    public static final Decoder<List<UnappliedDataDrivenItemMapping>> LIST_CODEC = Codec.list(CODEC);

    private final DynamicOps<?> ops;
    private final MapLike<?> mapLike;

    private UnappliedDataDrivenItemMapping(DynamicOps<?> ops, MapLike<?> mapLike) {
        this.ops = ops;
        this.mapLike = mapLike;
    }

    public void apply(ItemMappingsComposeEventImpl event, Item item) {
        apply(event, item, (DynamicOps) this.ops, this.mapLike);
    }

    private static <T> void apply(ItemMappingsComposeEventImpl event, @Nullable Item item, DynamicOps<T> ops, MapLike<T> mapLike) {

        // Parse the type
        T typeInput = mapLike.get("type");
        DataDrivenItemMappingType type;
        if (typeInput == null) {
            // By default, we assume they mean manual
            type = BuiltInDataDrivenItemMappingTypes.MANUAL;
        } else {
            DataResult<String> typeResult = ops.getStringValue(typeInput);
            if (typeResult.isError()) {
                throw new IllegalArgumentException("Invalid mapping type for a mapping" + (item == null ? "" : " for item " + item) + typeResult.error().map(error -> ": " + error.message()).orElse(""));
            }
            String typeString = typeResult.getOrThrow();
            type = DataDrivenItemMappingTypeRegistry.get(typeString);
            if (type == null) {
                throw new IllegalArgumentException("Unknown mapping type for a mapping" + (item == null ? "" : " for item " + item) + ": " + typeString);
            }
        }

        // Let the type apply the mapping
        type.apply(event, item, ops, mapLike);

    }

}
