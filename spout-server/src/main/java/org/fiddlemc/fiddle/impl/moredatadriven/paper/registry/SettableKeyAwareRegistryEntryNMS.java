package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry;

import net.minecraft.resources.Identifier;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.nms.KeyAwareRegistryEntryNMS;

/**
 * Allows for the setting of the {@link #getKeyNMS} property of a {@link KeyAwareRegistryEntryNMS}.
 */
public interface SettableKeyAwareRegistryEntryNMS extends KeyAwareRegistryEntryNMS {

    /**
     * Sets the {@link #getKeyNMS} of this registry entry.
     */
    void setKeyNMS(Identifier key);

}
