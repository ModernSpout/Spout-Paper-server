package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;

/**
 * A {@link ProxyStatesRequestBuilder} that maps to a single {@linkplain BlockData block state}.
 *
 * <p>
 * By default:
 * <ul>
 *     <li>{@link #fallback()} is the default block state of {@link BlockType#STONE}.</li>
 * </ul>
 */
public interface ToBlockStateRequestBuilder<US extends UsedStates> extends ToItemRequestBuilder<US> {

    /**
     * @param fallback A block state that is the best fallback
     *                 for when no proxy state can be claimed.
     */
    void fallback(BlockData fallback);

    /**
     * Convenience method that calls {@link #fallback} with the
     * {@linkplain BlockType#createBlockData() default block state}
     * of the given {@link BlockType}.
     */
    default void fallbackDefaultStateOf(BlockType fallback) {
        this.fallback(fallback.createBlockData());
    }

    /**
     * @return The current setting of {@link #fallback}.
     */
    BlockData fallback();

}
