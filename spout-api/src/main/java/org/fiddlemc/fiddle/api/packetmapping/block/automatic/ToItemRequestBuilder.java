package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;

/**
 * A {@link ProxyStatesRequestBuilder} that maps to some server-side block state(s).
 */
public interface ToItemRequestBuilder<US extends UsedStates> extends ProxyStatesRequestBuilder<US> {

    /**
     * @param fallbackItem The item form of the fallback block state(s).
     *                 This does not need to be set if it can be inferred automatically.
     */
    void fallbackItem(@Nullable ItemType fallbackItem);

    /**
     * @return The current setting of {@link #fallbackItem},
     * or null if not set and not inferrable yet.
     */
    @Nullable ItemType fallbackItem();

}
