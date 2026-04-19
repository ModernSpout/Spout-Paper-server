package org.fiddlemc.fiddle.impl.packetmapping.item.datadriven;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import org.fiddlemc.fiddle.impl.branding.FiddleNamespace;
import org.jspecify.annotations.Nullable;

/**
 * A simple ad-hoc registry of existing types of mapping types.
 */
public final class DataDrivenItemMappingTypeRegistry {

    private DataDrivenItemMappingTypeRegistry() {
        throw new UnsupportedOperationException();
    }

    private static final Map<Identifier, DataDrivenItemMappingType> registry = new HashMap<>();

    public static void register(Identifier key, DataDrivenItemMappingType type) {
        if (registry.containsKey(key)) {
            throw new IllegalArgumentException("Data-driven item mapping type " + key + " is already registered!");
        }
        registry.put(key, type);
    }

    public static @Nullable DataDrivenItemMappingType get(String key) {
        BuiltInDataDrivenItemMappingTypes.bootstrapIfNecessary();
        if (key.indexOf(Identifier.NAMESPACE_SEPARATOR) == -1) {
            key = FiddleNamespace.FIDDLE + Identifier.NAMESPACE_SEPARATOR + key;
        }
        return get(Identifier.parse(key));
    }

    public static @Nullable DataDrivenItemMappingType get(Identifier key) {
        return registry.get(key);
    }

}
