package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.component.DataComponentMap;
import org.fiddlemc.fiddle.impl.util.mojang.codec.CodecUtil;
import java.util.Optional;

/**
 * A definition of an item, which can be used to add an item.
 */
public final class ItemDefinition implements Definition {

    public static final Codec<ItemDefinition> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<Pair<ItemDefinition, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).map(mapLike -> {
                ItemDefinition definition = new ItemDefinition();
                definition.components = CodecUtil.getOptional(ops, mapLike, "components", DataComponentMap.CODEC);
                return Pair.of(definition, input);
            });
        }

        @Override
        public <T> DataResult<T> encode(ItemDefinition input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();
            CodecUtil.setOptional(ops, builder, "components", input.components, DataComponentMap.CODEC);
            return builder.build(prefix);
        }

    };

    public Optional<DataComponentMap> components = Optional.empty();

    // Missing: craftingRemainingItem, requiredFeatures
    // Not included on purpose: id, drops (these are not defined through definitions, but through other means)
    // Missing: descriptionId

    public ItemDefinition() {
    }

    public ItemDefinition(
        Optional<DataComponentMap> components
    ) {
        this.components = components;
    }

}
