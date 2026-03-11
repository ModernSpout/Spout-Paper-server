package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.impl.util.mojang.codec.CodecUtil;
import java.util.Optional;

/**
 * A definition of an {@link Item} resource, which can be serialized.
 */
public final class ItemDefinition implements Definition<Item> {

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

    @Override
    public Item toResource(ResourceKey<Item> id) {
        Item.Properties properties = new Item.Properties();
        this.components.ifPresent(components -> {
            components.forEach(component -> {
                copyComponentToProperties(properties, component);
            });
        });
        properties.setId(id);
        return new Item(properties);
    }

    /**
     * Utility method for {@link #toResource}.
     */
    private static <T> void copyComponentToProperties(Item.Properties properties, TypedDataComponent<T> component) {
        properties.component(component.type(), component.value());
    }

    /**
     * @return A definition for the given {@link Item}.
     */
    public static ItemDefinition fromResource(Item item) {
        return new ItemDefinition(
            Optional.of(item.components())
        );
    }

}
