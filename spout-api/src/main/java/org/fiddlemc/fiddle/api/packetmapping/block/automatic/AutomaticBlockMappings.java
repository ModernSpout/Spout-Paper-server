package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingsComposeEvent;
import java.util.function.Consumer;

/**
 * Provides proxy mapping functionality to
 * {@link BlockMappingsComposeEvent}.
 *
 * <p>
 * Proxy mappings are mappings where server-side blocks are mapped
 * (for {@linkplain ClientView.AwarenessLevel#RESOURCE_PACK players who have the resource pack})
 * to vanilla block states that have a visual duplicate. The look of that target block state is then overridden
 * in its corresponding {@code blockstates} resource pack file.
 * <br>
 * Actual occurrences of the target block state are mapped to its visual duplicate.
 * <br>
 * Examples of usable vanilla block states are note block states, leaves states
 * and cut copper stairs (which have a visually identical waxed variant).
 * <br>
 * Because these target block states can only have one visual look each, they are claimed on a
 * first-come first-serve basis. When no more block states are available, the server-side blocks
 * are instead mapped to existing vanilla fallback states that look and behave similar, but not the same.
 * <br>
 * By default, the fallback states are assumed to also be the best target block states for
 * {@linkplain ClientView.AwarenessLevel#VANILLA players without the resource pack}.
 * </p>
 *
 * <p>
 * Note that for all methods of this class,
 * the requests should be assumed to be for blocks that are
 * <ul>
 *     <li>a full cube visually,</li>
 *     <li>solid with respect to collisions, and</li>
 *     <li>non-light-emitting</li>
 * </ul>
 * unless otherwise mentioned.
 * Using a method for a different purpose can lead to visual and gameplay glitches.
 * </p>
 */
public interface AutomaticBlockMappings {

    /**
     * Attempts to find a proxy state for a single full block state.
     *
     * @return The state used as proxy state.
     * These may be the fallback state if no proxy state was available.
     */
    void fullBlock(Consumer<FullBlockRequestBuilder> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a slab.
     *
     * @return The states used as proxy states.
     * These may be the fallback states if no proxy states were available.
     */
    void slab(Consumer<SlabRequestBuilder> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of stairs.
     *
     * @return The states used as proxy states.
     * These may be the fallback states if no proxy states were available.
     */
    void stairs(Consumer<StairsRequestBuilder> builderConsumer);

    /**
     * Attempts to find proxy states for 2 leaves block states:
     * 1 non-waterlogged and 1 waterlogged.
     *
     * <p>
     * This is not suitable for blocks that are "less" instabreak than leaves.
     * The vanilla client generally believes leaves are instabreak when broken with the right tool.
     * In practice, this means you should only use this for server-side leaves.
     * </p>
     *
     * @return The states used as proxy states.
     * These may be the fallback states if no proxy states were available.
     */
    void leaves(Consumer<LeavesRequestBuilder> builderConsumer);

}
