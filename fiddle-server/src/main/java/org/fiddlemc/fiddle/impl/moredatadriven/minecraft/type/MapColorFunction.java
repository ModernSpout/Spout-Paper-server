package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A replacement for {@link Function}, as a type for {@link BlockBehaviour.Properties#mapColor}.
 */
public sealed interface MapColorFunction extends Function<BlockState, MapColor> permits MapColorFunction.Single, MapColorFunction.ByProperties {

    record Single(MapColor mapColor) implements MapColorFunction {

        @Override
        public MapColor apply(BlockState blockState) {
            return this.mapColor;
        }

    }

    final class ByProperties implements MapColorFunction {

        private final List<String> propertyNames;
        private final Map<List<String>, MapColor> precomputedMapColors;

        public ByProperties(List<String> propertyNames, Map<List<String>, MapColor> precomputedMapColors) {
            this.propertyNames = propertyNames;
            this.precomputedMapColors = precomputedMapColors;
        }

        public ByProperties(List<Property<?>> properties, Function<PropertyValueMap, MapColor> byPropertyValues) {
            this.propertyNames = properties.stream().map(Property::getName).toList();
            this.precomputedMapColors = new HashMap<>();
            addPrecomputedMapColors(properties, byPropertyValues, this.precomputedMapColors, new ArrayList<>(properties.size()), new PropertyValueMapImpl(properties.size()), 0);
        }

        @Override
        public MapColor apply(BlockState blockState) {
            Stream<Property<?>> properties = this.propertyNames.stream().map(propertyName -> blockState.getProperties().stream().filter(property -> property.getName().equals(propertyName)).findAny().get());
            List<?> values = properties.map(blockState::getValue).toList();
            return this.precomputedMapColors.get(values);
        }

        public List<Pair<List<Pair<String, String>>, MapColor>> listAllCombinations() {
            return this.precomputedMapColors.entrySet().stream().map(entry -> {
                List<Pair<String, String>> keyValueList = new ArrayList<>(this.propertyNames.size());
                for (int i = 0; i < this.propertyNames.size(); i++) {
                    keyValueList.add(Pair.of(this.propertyNames.get(i), entry.getKey().get(i)));
                }
                return Pair.of(keyValueList, entry.getValue());
            }).toList();
        }

        private static void addPrecomputedMapColors(List<Property<?>> properties, Function<PropertyValueMap, MapColor> byPropertyValues, Map<List<String>, MapColor> precomputedMapColors, List<String> valuesStringsAsList, PropertyValueMapImpl valuesAsMap, int propertyI) {
            if (propertyI == properties.size()) {
                precomputedMapColors.put(new ArrayList<>(valuesStringsAsList), byPropertyValues.apply(valuesAsMap));
                return;
            }
            Property<?> property = properties.get(propertyI);
            for (Object value : property.getPossibleValues()) {
                valuesStringsAsList.add(value.toString());
                valuesAsMap.put(property, value);
                addPrecomputedMapColors(properties, byPropertyValues, precomputedMapColors, valuesStringsAsList, valuesAsMap, propertyI + 1);
                valuesStringsAsList.removeLast();
                valuesAsMap.remove(property);
            }
        }

        public interface PropertyValueMap {

            <V extends Comparable<V>> V getValue(Property<V> property);

        }

        private class PropertyValueMapImpl implements PropertyValueMap {

            private final Map<Property<?>, Object> internal;

            private PropertyValueMapImpl(int size) {
                this.internal = new HashMap<>(size);
            }

            @Override
            public <V extends Comparable<V>> V getValue(Property<V> property) {
                return (V) internal.get(property);
            }

            private void put(Property<?> property, Object value) {
                this.internal.put(property, value);
            }

            private void remove(Property<?> property) {
                this.internal.remove(property);
            }

        }

    }

}
