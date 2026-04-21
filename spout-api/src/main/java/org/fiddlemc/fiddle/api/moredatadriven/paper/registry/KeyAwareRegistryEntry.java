package org.fiddlemc.fiddle.api.moredatadriven.paper.registry;

import org.bukkit.NamespacedKey;

/**
 * A registry entry that is aware of the {@linkplain NamespacedKey key}
 * with which it is being registered to a registry.
 */
public interface KeyAwareRegistryEntry {

    /**
     * The key with which this entry is being registered,
     * or null if not set yet.
     *
     * <p>
     * Plugins can assume it will always be set.
     * </p>
     */
    NamespacedKey getKey();

}
