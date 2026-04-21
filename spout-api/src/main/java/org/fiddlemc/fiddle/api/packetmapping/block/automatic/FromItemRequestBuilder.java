package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;

/**
 * A {@link ProxyStatesRequestBuilder} that maps some server-side block state(s).
 */
public interface FromItemRequestBuilder<US extends UsedStates> extends ProxyStatesRequestBuilder<US> {

    /**
     * @param fromItem The item form of the block state(s) being mapped from.
     *                 This does not need to be set if it can be inferred automatically.
     */
    void fromItem(@Nullable ItemType fromItem);

    /**
     * @return The current setting of {@link #fromItem},
     * or null if not set and not inferrable yet.
     */
    @Nullable ItemType fromItem();

}
