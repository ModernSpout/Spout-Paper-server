package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A utility for converting block states to canonical strings and back.
 */
public final class BlockStateStringConversion {

    private BlockStateStringConversion() {
        throw new UnsupportedOperationException();
    }

    public static String propertyKeyValuesToString(Collection<Pair<String, String>> properties) {
        if (properties.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;
        for (Pair<String, String> property : properties) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(',');
            }

            stringBuilder.append(property.left());
            stringBuilder.append('=');
            stringBuilder.append(property.right());
        }

        return stringBuilder.toString();
    }

    public static String propertiesToString(BlockState blockState, Collection<Property<?>> properties) {
        return propertyKeyValuesToString(properties.stream().map(property -> Pair.of(property.getName(), ((Property) property).getName(blockState.getValue(property)))).toList());
    }

    public static String blockStateToString(BlockState blockState) {

        // Start with the block identifier
        Identifier blockIdentifier = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
        StringBuilder sb = new StringBuilder();

        // Get the properties string
        String propertiesString = propertiesToString(blockState, blockState.getProperties());
        if (propertiesString.isEmpty()) {
            return blockIdentifier.toString();
        }

        // Append the properties string
        StringBuilder stringBuilder = new StringBuilder(blockIdentifier.toString());
        stringBuilder.append('[');
        stringBuilder.append(propertiesString);
        stringBuilder.append(']');
        return stringBuilder.toString();

    }

    public static List<Pair<String, String>> propertyKeyValuesFromString(@Nullable String string) {
        if (string == null || string.isEmpty()) {
            return Collections.emptyList();
        }

        String[] propertyEntries = string.split(",");
        List<Pair<String, String>> parsedProperties = new ArrayList<>(propertyEntries.length);
        for (String propertyEntry : propertyEntries) {
            String[] propertyKeyAndValue = propertyEntry.split("=");
            if (propertyKeyAndValue.length != 2) {
                throw new IllegalArgumentException("Invalid block state property: " + propertyEntry);
            }
            parsedProperties.add(Pair.of(propertyKeyAndValue[0], propertyKeyAndValue[1]));
        }
        return parsedProperties;
    }

    public static Map<String, String> propertyKeyValueMapFromString(@Nullable String string) {
        return propertyKeyValuesFromString(string).stream().collect(Collectors.toMap(Pair::left, Pair::right));
    }

    public static BlockState blockStateFromString(String string) {

        // First separate the string into the block identifier and the properties
        String blockIdentifierPart = string;
        String propertiesPart = null;
        int bracketIndex = string.indexOf('[');
        if (bracketIndex != -1) {
            blockIdentifierPart = string.substring(0, bracketIndex);
            propertiesPart = string.substring(bracketIndex + 1, string.length() - 1);
        }

        // Get the block
        Identifier blockIdentifier = Identifier.parse(blockIdentifierPart);
        Block block = Objects.requireNonNull(BuiltInRegistries.BLOCK.get(blockIdentifier).orElse(null), "Unknown block: " + blockIdentifier).value();

        // Create the block state
        BlockState blockState = block.defaultBlockState();

        // Parse properties
        Map<String, String> parsedProperties = propertyKeyValueMapFromString(propertiesPart);

        // Apply properties
        if (!parsedProperties.isEmpty()) {
            for (Property<?> property : blockState.getProperties()) {
                String parsedPropertyValue = parsedProperties.get(property.getName());
                if (parsedPropertyValue != null) {
                    Object propertyValue = property.getValue(parsedPropertyValue);
                    blockState.setValue((Property) property, (Comparable) propertyValue);
                }
            }
        }

        // Return the block state
        return blockState;

    }

}
