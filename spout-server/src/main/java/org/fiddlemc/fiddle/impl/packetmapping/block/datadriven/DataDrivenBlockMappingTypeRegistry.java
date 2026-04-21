package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import net.minecraft.resources.Identifier;
import spout.common.branding.SpoutNamespace;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple ad-hoc registry of existing types of mapping types.
 */
public final class DataDrivenBlockMappingTypeRegistry {

    private DataDrivenBlockMappingTypeRegistry() {
        throw new UnsupportedOperationException();
    }

    private static final Map<Identifier, DataDrivenBlockMappingType> registry = new HashMap<>();

    public static void register(Identifier key, DataDrivenBlockMappingType type) {
        if (registry.containsKey(key)) {
            throw new IllegalArgumentException("Data-driven block mapping type " + key + " is already registered!");
        }
        registry.put(key, type);
    }

    public static @Nullable DataDrivenBlockMappingType get(String key) {
        BuiltInDataDrivenBlockMappingTypes.bootstrapIfNecessary();
        if (key.indexOf(Identifier.NAMESPACE_SEPARATOR) == -1) {
            key = SpoutNamespace.SPOUT + Identifier.NAMESPACE_SEPARATOR + key;
        }
        return get(Identifier.parse(key));
    }

    public static @Nullable DataDrivenBlockMappingType get(Identifier key) {
        return registry.get(key);
    }

}
