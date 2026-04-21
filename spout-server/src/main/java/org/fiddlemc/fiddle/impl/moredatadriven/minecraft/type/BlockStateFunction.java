package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A replacement for {@link Function}, as a type for {@link BlockBehaviour.Properties#mapColor}
 * and {@link BlockBehaviour.Properties#lightEmission}.
 */
public sealed interface BlockStateFunction<T> extends Function<BlockState, T> permits BlockStateFunction.Single, BlockStateFunction.ByProperties {

    record Single<T>(T value) implements BlockStateFunction<T> {

        @Override
        public T apply(BlockState blockState) {
            return this.value;
        }

    }

    final class ByProperties<T> implements BlockStateFunction<T> {

        private final List<String> propertyNames;
        private final Map<List<String>, T> precomputedValues;

        public ByProperties(List<String> propertyNames, Map<List<String>, T> precomputedValues) {
            this.propertyNames = propertyNames;
            this.precomputedValues = precomputedValues;
        }

        public ByProperties(List<Property<?>> properties, Function<PropertyValueMap, T> byPropertyValues) {
            this.propertyNames = properties.stream().map(Property::getName).toList();
            this.precomputedValues = new HashMap<>();
            addPrecomputedValues(properties, byPropertyValues, this.precomputedValues, new ArrayList<>(properties.size()), new PropertyValueMapImpl(properties.size()), 0);
        }

        @Override
        public T apply(BlockState blockState) {
            Stream<Property<?>> properties = this.propertyNames.stream().map(propertyName -> blockState.getProperties().stream().filter(property -> property.getName().equals(propertyName)).findAny().get());
            List<String> values = properties.map(property -> ((Property) property).getName(blockState.getValue(property))).toList();
            return this.precomputedValues.get(values);
        }

        public List<Pair<List<Pair<String, String>>, T>> listAllCombinations() {
            return this.precomputedValues.entrySet().stream().map(entry -> {
                List<Pair<String, String>> keyValueList = new ArrayList<>(this.propertyNames.size());
                for (int i = 0; i < this.propertyNames.size(); i++) {
                    keyValueList.add(Pair.of(this.propertyNames.get(i), entry.getKey().get(i)));
                }
                return Pair.of(keyValueList, entry.getValue());
            }).toList();
        }

        private static <T> void addPrecomputedValues(List<Property<?>> properties, Function<PropertyValueMap, T> byPropertyValues, Map<List<String>, T> precomputedValues, List<String> valuesStringsAsList, PropertyValueMapImpl valuesAsMap, int propertyI) {
            if (propertyI == properties.size()) {
                precomputedValues.put(new ArrayList<>(valuesStringsAsList), byPropertyValues.apply(valuesAsMap));
                return;
            }
            Property<?> property = properties.get(propertyI);
            for (Object value : property.getPossibleValues()) {
                valuesStringsAsList.add(((Property) property).getName((Comparable) value));
                valuesAsMap.put(property, value);
                addPrecomputedValues(properties, byPropertyValues, precomputedValues, valuesStringsAsList, valuesAsMap, propertyI + 1);
                valuesStringsAsList.removeLast();
                valuesAsMap.remove(property);
            }
        }

        public interface PropertyValueMap {

            <V extends Comparable<V>> V getValue(Property<V> property);

            <V extends Comparable<V>> V getValueOrElse(Property<V> property, V defaultValue);

        }

        private static class PropertyValueMapImpl implements PropertyValueMap {

            private final Map<Property<?>, Object> internal;

            private PropertyValueMapImpl(int size) {
                this.internal = new HashMap<>(size);
            }

            @Override
            public <V extends Comparable<V>> V getValue(Property<V> property) {
                return (V) this.internal.get(property);
            }

            @Override
            public <V extends Comparable<V>> V getValueOrElse(Property<V> property, V defaultValue) {
                V value = (V) this.internal.get(property);
                return value != null ? value : defaultValue;
            }

            public Map<String, String> asStringMap() {
                return this.internal.entrySet().stream().map(entry -> Pair.of(entry.getKey().getName(), ((Property) entry.getKey()).getName((Comparable) entry.getValue()))).collect(Collectors.toMap(Pair::left, Pair::right));
            }

            private void put(Property<?> property, Object value) {
                this.internal.put(property, value);
            }

            private void remove(Property<?> property) {
                this.internal.remove(property);
            }

        }

    }

    static <A> Codec<BlockStateFunction<A>> codec(Codec<A> valueCodec) {
        Codec<BlockStateFunction.Single<A>> singleCodec = valueCodec.xmap(BlockStateFunction.Single::new, BlockStateFunction.Single::value);
        Codec<BlockStateFunction.ByProperties<A>> byPropertiesCodec = new Codec<>() {

            @Override
            public <T> DataResult<T> encode(BlockStateFunction.ByProperties<A> input, DynamicOps<T> ops, T prefix) {
                RecordBuilder<T> builder = ops.mapBuilder();
                input.listAllCombinations().forEach(combination -> {
                    builder.add(BlockStateStringConversion.propertyKeyValuesToString(combination.left()), combination.right(), valueCodec);
                });
                return builder.build(prefix);
            }

            @Override
            public <T> DataResult<com.mojang.datafixers.util.Pair<BlockStateFunction.ByProperties<A>, T>> decode(DynamicOps<T> ops, T input) {
                return ops.getMap(input).flatMap(mapLike -> {
                    List<String> propertyNames;
                    {
                        com.mojang.datafixers.util.Pair<T, T> entry = mapLike.entries().findAny().get();
                        propertyNames = BlockStateStringConversion.propertyKeyValuesFromString(ops.getStringValue(entry.getFirst()).getOrThrow()).stream().map(Pair::left).toList();
                    }
                    Map<List<String>, A> precomputedValues = mapLike.entries().collect(Collectors.toMap(
                        entry -> BlockStateStringConversion.propertyKeyValuesFromString(ops.getStringValue(entry.getFirst()).getOrThrow()).stream().map(Pair::right).toList(),
                        entry -> valueCodec.decode(ops, input).getOrThrow().getFirst())
                    );
                    return DataResult.success(com.mojang.datafixers.util.Pair.of(new BlockStateFunction.ByProperties<>(propertyNames, precomputedValues), input));
                });
            }

        };
        return new Codec<>() {

            @Override
            public <T> DataResult<T> encode(BlockStateFunction<A> input, DynamicOps<T> ops, T prefix) {
                return switch (input) {
                    case BlockStateFunction.Single single -> singleCodec.encode(single, ops, prefix);
                    case BlockStateFunction.ByProperties byProperties ->
                        byPropertiesCodec.encode(byProperties, ops, prefix);
                };
            }

            @Override
            public <T> DataResult<com.mojang.datafixers.util.Pair<BlockStateFunction<A>, T>> decode(DynamicOps<T> ops, T input) {
                boolean isSingleValue = ops.getStringValue(input).isSuccess() || ops.getNumberValue(input).isSuccess() || ops.getBooleanValue(input).isSuccess();
                if (isSingleValue) {
                    return (DataResult) singleCodec.decode(ops, input);
                }
                return (DataResult) byPropertiesCodec.decode(ops, input);
            }

        };
    }

}
