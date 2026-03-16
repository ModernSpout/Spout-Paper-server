package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.nms;

import net.minecraft.resources.Identifier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.KeyAwareRegistryEntry;

/**
 * Extension of {@link KeyAwareRegistryEntry} for Minecraft internals.
 */
public interface KeyAwareRegistryEntryNMS extends KeyAwareRegistryEntry {

    /**
     * @see #getKey
     */
    Identifier getKeyNMS();

    @Override
    default NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(this.getKeyNMS());
    }

}
