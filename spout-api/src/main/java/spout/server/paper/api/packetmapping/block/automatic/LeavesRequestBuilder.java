package spout.server.paper.api.packetmapping.block.automatic;

import org.jspecify.annotations.Nullable;

/**
 * A {@link ProxyStatesRequestBuilder} for {@link AutomaticBlockMappings#leaves}.
 */
public interface LeavesRequestBuilder extends FromBlockTypeRequestBuilder<UsedStates.Waterlogged>, ToBlockTypeRequestBuilder<UsedStates.Waterlogged> {

    /**
     * @param tinted Whether to use proxy states that are tinted.
     */
    void tinted(@Nullable Boolean tinted);

    /**
     * @return The current setting of {@link #tinted},
     * of null when not set yet.
     *
     * <p>
     * If this value remains unset, it will be based on {@link #from()} if it is a leaves block,
     * or assumed false otherwise.
     * </p>
     */
    @Nullable Boolean tinted();

}
