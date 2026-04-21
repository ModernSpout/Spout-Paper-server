package org.fiddlemc.fiddle.api.packetmapping.block;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.AutomaticBlockMappings;

/**
 * Provides functionality to register mappings to the {@link BlockMappings}.
 */
public interface BlockMappingsComposeEvent extends LifecycleEvent {

    /**
     * @return A {@link ManualBlockMappings} instance, to register mappings manually.
     */
    ManualBlockMappings<?> manualMappings();

    /**
     * @return A {@link AutomaticBlockMappings} instance, to register mappings by choosing proxies.
     */
    AutomaticBlockMappings automaticMappings();

}
