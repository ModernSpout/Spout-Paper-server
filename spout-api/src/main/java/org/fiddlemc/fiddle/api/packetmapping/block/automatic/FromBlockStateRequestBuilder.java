package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.Nullable;

/**
 * A {@link ProxyStatesRequestBuilder} that maps a single server-side {@linkplain BlockData block state}.
 */
public interface FromBlockStateRequestBuilder<US extends UsedStates> extends FromItemRequestBuilder<US> {

    /**
     * @param from The block state you are wanting to map.
     */
    void from(BlockData from);

    /**
     * Convenience method that calls {@link #from} with the
     * {@linkplain BlockType#createBlockData() default block state}
     * of the given {@link BlockType}.
     */
    default void fromDefaultStateOf(BlockType from) {
        this.from(from.createBlockData());
    }

    /**
     * @return The current setting of {@link #from},
     * or null if not set yet.
     *
     * <p>
     * This value must be set.
     * </p>
     */
    @Nullable BlockData from();

}
