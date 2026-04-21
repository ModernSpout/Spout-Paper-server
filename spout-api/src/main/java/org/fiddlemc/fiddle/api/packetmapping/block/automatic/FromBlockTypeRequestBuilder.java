package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import org.jspecify.annotations.Nullable;

/**
 * A {@link ProxyStatesRequestBuilder} that maps a server-side {@linkplain BlockType block type}.
 */
public interface FromBlockTypeRequestBuilder<US extends UsedStates> extends FromItemRequestBuilder<US> {

    /**
     * @param from The block type you are wanting to map.
     */
    void from(BlockType from);

    /**
     * @return The current setting of {@link #from},
     * or null if not set yet.
     *
     * <p>
     * This value must be set.
     * </p>
     */
    @Nullable BlockType from();

}
