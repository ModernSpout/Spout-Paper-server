package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.jspecify.annotations.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A builder for {@linkplain AutomaticBlockMappings block mapping proxy requests}.
 *
 * <p>
 * By default:
 * <ul>
 *     <li>{@link #createFromToProxyMapping()} is true.</li>
 *     <li>{@link #createProxyToVisualDuplicateMapping()} is true.</li>
 *     <li>{@link #createItemMappings()} is true.</li>
 *     <li>{@link #createVanillaMappings()} is true.</li>
 *     <li>{@link #createResourcePackBlockstatesEntries()} is true.</li>
 *     <li>There is no {@linkplain #useResult result consumer}.</li>
 * </ul>
 * </p>
 */
public interface ProxyStatesRequestBuilder<US extends UsedStates> {

    /**
     * @param createFromToProxyMapping Whether to create a block mapping from the server-side block states
     *                                 that this mapping targets, to the corresponding used proxy states,
     *                                 for {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients.
     */
    void createFromToProxyMapping(boolean createFromToProxyMapping);

    /**
     * @return The current setting of {@link #createFromToProxyMapping}.
     */
    boolean createFromToProxyMapping();

    /**
     * @param createProxyToVisualDuplicateMapping Whether to create a block mapping from used proxy states
     *                                            to corresponding visual duplicates,
     *                                            for {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients.
     */
    void createProxyToVisualDuplicateMapping(boolean createProxyToVisualDuplicateMapping);

    /**
     * @return The current setting of {@link #createProxyToVisualDuplicateMapping}.
     */
    boolean createProxyToVisualDuplicateMapping();

    /**
     * @param createItemMappings Whether to create item mappings that correspond to the created block mappings,
     *                           during the {@link FiddleEvents#ITEM_MAPPING} event.
     */
    void createItemMappings(boolean createItemMappings);

    /**
     * @return The current setting of {@link #createItemMappings}.
     */
    boolean createItemMappings();

    /**
     * @param createVanillaMappings Whether to create block mappings to the fallback states for
     *                              {@link ClientView.AwarenessLevel#VANILLA} clients.
     */
    void createVanillaMappings(boolean createVanillaMappings);

    /**
     * @return The current setting of {@link #createVanillaMappings}.
     */
    boolean createVanillaMappings();

    /**
     * @param createResourcePackBlockstatesEntries Whether to create variants entries in {@code blockstates}
     *                                             resource pack files corresponding to the proxy states,
     *                                             for {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients.
     */
    void createResourcePackBlockstatesEntries(boolean createResourcePackBlockstatesEntries);

    /**
     * @return The current setting of {@link #createResourcePackBlockstatesEntries}.
     */
    boolean createResourcePackBlockstatesEntries();

    /**
     * @param resultConsumer A consumer for the resulting states that were chosen.
     */
    void useResult(Consumer<US> resultConsumer);

}
