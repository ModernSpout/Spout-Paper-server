package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A utility for converting block states to canonical strings and back.
 */
public final class BlockStateStringConversion {

    private BlockStateStringConversion() {
        throw new UnsupportedOperationException();
    }

    public static String toString(BlockState blockState) {

        // Start with the block identifier
        Identifier blockIdentifier = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
        StringBuilder sb = new StringBuilder(blockIdentifier.toString());

        // Append the properties
        if (!blockState.getProperties().isEmpty()) {
            sb.append('[');

            boolean first = true;
            for (Property<?> prop : blockState.getProperties()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }

                sb.append(prop.getName());
                sb.append('=');
                sb.append(((Property) prop).getName(blockState.getValue(prop)));
            }

            sb.append(']');
        }

        // Build the string
        return sb.toString();

    }

    public static BlockState fromString(String string) {

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

        // Build the block state
        BlockState blockState = block.defaultBlockState();

        // If no properties, we are done
        if (propertiesPart == null || propertiesPart.isEmpty()) {
            return blockState;
        }

        // Parse properties
        String[] propertyEntries = propertiesPart.split(",");
        Map<String, String> parsedProperties = new HashMap<>(propertyEntries.length);
        for (String propertyEntry : propertyEntries) {
            String[] propertyKeyAndValue = propertyEntry.split("=");
            if (propertyKeyAndValue.length != 2) {
                throw new IllegalArgumentException("Invalid block state property: " + propertyEntry);
            }
            parsedProperties.put(propertyKeyAndValue[0], propertyKeyAndValue[1]);
        }

        // Apply properties
        for (Property<?> property : blockState.getProperties()) {
            String parsedPropertyValue = parsedProperties.get(property.getName());
            if (parsedPropertyValue != null) {
                Object propertyValue = property.getValue(parsedPropertyValue);
                blockState.setValue((Property) property, (Comparable) propertyValue);
            }
        }
        return blockState;

    }

}
