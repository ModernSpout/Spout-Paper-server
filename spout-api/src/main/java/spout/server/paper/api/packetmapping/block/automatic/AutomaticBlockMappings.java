package spout.server.paper.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.block.BlockMappingsComposeEvent;
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
     * Attempts to find a proxy state for every possible state of a button.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_BUTTON}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder<UsedStates.Switch> & ToBlockTypeRequestBuilder<UsedStates.Switch>> void button(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for a single full block state.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockStateRequestBuilder#fallback()} is the default block state of {@link BlockType#STONE}.</li>
     * </ul>
     */
    <B extends FromBlockStateRequestBuilder<UsedStates.Single> & ToBlockStateRequestBuilder<UsedStates.Single>> void fullBlock(Consumer<? extends B> builderConsumer);

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
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_LEAVES}.</li>
     * </ul>
     */
    <B extends LeavesRequestBuilder> void leaves(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a pressure plate.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_PRESSURE_PLATE}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder<UsedStates.Powerable> & ToBlockTypeRequestBuilder<UsedStates.Powerable>> void pressurePlate(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a slab.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_SLAB}.</li>
     * </ul>
     */
    <B extends SlabRequestBuilder> void slab(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of stairs.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_STAIRS}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder<UsedStates.Stairs> & ToBlockTypeRequestBuilder<UsedStates.Stairs>> void stairs(Consumer<? extends B> builderConsumer);

}
