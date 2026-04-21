package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.Nullable;

/**
 * A {@link ProxyStatesRequestBuilder} for {@link AutomaticBlockMappings#slab}.
 */
public interface SlabRequestBuilder extends FromBlockTypeRequestBuilder<UsedStates.Slab>, ToBlockTypeRequestBuilder<UsedStates.Slab> {

    /**
     * @param fullBlockFallback A block state that is the best fallback
     *                          for the full block states of this slab.
     */
    void fullBlockFallback(@Nullable BlockData fullBlockFallback);

    /**
     * Convenience method that calls {@link #fullBlockFallback} with the
     * {@linkplain BlockType#createBlockData() default block state}
     * of the given {@link BlockType}.
     */
    default void fullBlockFallbackDefaultStateOf(BlockType fullBlockFallback) {
        this.fullBlockFallback(fullBlockFallback.createBlockData());
    }

    /**
     * @return The current setting of {@link #fullBlockFallback},
     * or null if not set.
     */
    @Nullable BlockData fullBlockFallback();

}
