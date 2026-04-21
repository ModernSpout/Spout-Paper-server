package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;

/**
 * A {@link ProxyStatesRequestBuilder} that maps to a {@linkplain BlockType block type}.
 *
 * <p>
 * By default:
 * <ul>
 *     <li>{@link #fallback()} is {@link BlockType#STONE}.</li>
 * </ul>
 */
public interface ToBlockTypeRequestBuilder<US extends UsedStates> extends ToItemRequestBuilder<US> {

    /**
     * @param fallback A block type that is the best fallback
     *                 for when no proxy block type can be claimed.
     */
    void fallback(BlockType fallback);

    /**
     * @return The current setting of {@link #fallback}.
     */
    BlockType fallback();

}
