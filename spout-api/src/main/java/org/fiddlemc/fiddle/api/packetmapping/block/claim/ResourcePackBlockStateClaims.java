package org.fiddlemc.fiddle.api.packetmapping.block.claim;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingsComposeEvent;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * Provides functionality to register claims to block states
 * as they are shown to {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients,
 * to avoid collisions with other plugins.
 *
 * <p>
 * When your plugin relies on the visual representation of a block state, as it appears to
 * {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients, you should register your claim
 * to that block state here.
 * </p>
 *
 * <p>
 * This ensures that other plugins (and Fiddle built-in functionality such as proxy mappings)
 * do not alter the visuals of that block state.
 * </p>
 *
 * <p>
 * Claims are not run immediately when you call them. Rather, various claims are collected
 * and evaluated according to their {@linkplain ClaimRequestPriority priority}.
 * </p>
 *
 * <p>
 * Successful claims should normally be followed up by registering the claimed states
 * with {@link BlockMappingsComposeEvent#manualMappings()}.
 * </p>
 */
public interface ResourcePackBlockStateClaims {

    /**
     * An internal interface to get the {@link VisualDuplicates} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ResourcePackBlockStateClaims> {
    }

    /**
     * @return The {@link VisualDuplicates} instance.
     */
    static ResourcePackBlockStateClaims get() {
        return ServiceLoader.load(ResourcePackBlockStateClaims.ServiceProvider.class, ResourcePackBlockStateClaims.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    /**
     * Attempts to claim the given block state.
     *
     * @param state                   A block state to claim.
     * @param priority                The priority for claiming this block state.
     *                                This may affect the result: higher priorities are awarded claims first.
     * @param resultConsumer          A consumer of whether the claim was successful.
     *                                Can be null if not needed.
     *                                The consumer is allowed to make more claims to this
     *                                {@link ResourcePackBlockStateClaims} instance,
     *                                or register mappings using a {@link BlockMappingsComposeEvent} instance.
     * @param visualDuplicateConsumer A consumer of the {@link BlockData} that can serve as a visual replacement
     *                                of the claimed block state.
     *                                Can be null if not needed.
     *                                It is recommended to map the claimed block state to this block state.
     *                                This consumer is invoked if and only if the claim was successful.
     */
    void claim(BlockData state, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<BlockData> visualDuplicateConsumer);

    /**
     * The same as {@link #claim(BlockData, ClaimRequestPriority, BooleanConsumer, Consumer)},
     * but attempts to claim every given block state.
     *
     * <p>
     * If any block state in the given list could not be claimed, no block states are claimed at all.
     * After processing the claim, the given {@code resultConsumer} is run with {@code} true
     * if the claim was successful (i.e. every given block state was claimed), and {@code false} otherwise
     * (i.e. none of the given block states were claimed).
     * </p>
     */
    void claimAll(List<BlockData> states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer);

    /**
     * @see #claimAll(List, ClaimRequestPriority, BooleanConsumer, Consumer)
     */
    default void claimAll(BlockData[] states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer) {
        this.claimAll(Arrays.asList(states), priority, resultConsumer, visualDuplicatesConsumer);
    }

    /**
     * The same as {@link #claim(BlockData, ClaimRequestPriority, BooleanConsumer, Consumer)},
     * but also accepts any {@link BlockData} that is visually identical to the given one.
     *
     * <p>
     * The given {@code resultConsumer} is run with the {@link BlockData} that was claimed,
     * or null if the claim was unsuccessful.
     * </p>
     */
    void claimOrSimilar(BlockData state, ClaimRequestPriority priority, @Nullable Consumer<@Nullable BlockData> resultConsumer, @Nullable Consumer<BlockData> visualDuplicateConsumer);

    /**
     * The same as {@link #claimOrSimilar(BlockData, ClaimRequestPriority, Consumer, Consumer)},
     * but attempts to claim every given block state,
     * just like {@link #claimAll(List, ClaimRequestPriority, BooleanConsumer, Consumer)}
     *
     * <p>
     * If any block state (or a visually identical one) in the given list could not be claimed,
     * no block states are claimed at all.
     * After processing the claim, the given {@code resultConsumer} is run with a list of the {@link BlockData}s
     * that were claimed if the claim was successful (i.e. every given block state was claimed)
     * (in the same order as the given {@code states}, so that the claimed state matches its given state at every
     * index), or with {@code null} otherwise (i.e. no block states were claimed).
     * The block states passed to the given {@code visualDuplicatesConsumer} are also in the same order.
     * </p>
     */
    void claimAllOrSimilar(List<BlockData> states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer);

    /**
     * @see #claimAllOrSimilar(List, ClaimRequestPriority, Consumer, Consumer)
     */
    default void claimAllOrSimilar(BlockData[] states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer) {
        this.claimAllOrSimilar(Arrays.asList(states), priority, resultConsumer, visualDuplicatesConsumer);
    }

    /**
     * Attempts to claim the given block state.
     *
     * <p>
     * This is similar to {@link #claim(BlockData, ClaimRequestPriority, BooleanConsumer, Consumer)},
     * except it claims the given block state with its vanilla look.
     * If this claim is successful, you should not alter the visuals of the claimed block states.
     * </p>
     *
     * @param state          A block state to claim.
     * @param priority       The priority for claiming this block state.
     *                       This may affect the result: higher priorities are awarded claims first.
     * @param resultConsumer A consumer of whether the claim was successful.
     *                       Can be null if not needed.
     *                       The consumer is allowed to make more claims to this
     *                       {@link ResourcePackBlockStateClaims} instance,
     *                       or register mappings using a {@link BlockMappingsComposeEvent} instance.
     */
    void claimUsingVanillaLook(BlockData state, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer);

    /**
     * The same as {@link #claimUsingVanillaLook(BlockData, ClaimRequestPriority, BooleanConsumer)},
     * but attempts to claim every given block state.
     *
     * <p>
     * If any block state in the given list could not be claimed, no block states are claimed at all.
     * After processing the claim, the given {@code resultConsumer} is run with {@code} true
     * if the claim was successful (i.e. every given block state was claimed), and {@code false} otherwise
     * (i.e. none of the given block states were claimed).
     * </p>
     */
    void claimAllUsingVanillaLook(List<BlockData> states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer);

    /**
     * @see #claimAllUsingVanillaLook(List, ClaimRequestPriority, BooleanConsumer)
     */
    default void claimAllUsingVanillaLook(BlockData[] states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer) {
        this.claimAllUsingVanillaLook(Arrays.asList(states), priority, resultConsumer);
    }

    /**
     * The same as {@link #claimUsingVanillaLook(BlockData, ClaimRequestPriority, BooleanConsumer)},
     * but also accepts any {@link BlockData} that is visually identical to the given one.
     *
     * <p>
     * The given {@code resultConsumer} is run with the {@link BlockData} that was claimed,
     * or null if the claim was unsuccessful.
     * </p>
     */
    void claimOrSimilarUsingVanillaLook(BlockData state, ClaimRequestPriority priority, @Nullable Consumer<@Nullable BlockData> resultConsumer);

    /**
     * The same as {@link #claimOrSimilarUsingVanillaLook(BlockData, ClaimRequestPriority, Consumer)},
     * but attempts to claim every given block state,
     * just like {@link #claimAllUsingVanillaLook(List, ClaimRequestPriority, BooleanConsumer)}
     *
     * <p>
     * If any block state (or a visually identical one) in the given list could not be claimed,
     * no block states are claimed at all.
     * After processing the claim, the given {@code resultConsumer} is run with a list of the {@link BlockData}s
     * that were claimed if the claim was successful (i.e. every given block state was claimed)
     * (in the same order as the given {@code states}, so that the claimed state matches its given state at every
     * index), or with {@code null} otherwise (i.e. no block states were claimed).
     * </p>
     */
    void claimAllOrSimilarUsingVanillaLook(List<BlockData> states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer);

    /**
     * @see #claimAllOrSimilarUsingVanillaLook(List, ClaimRequestPriority, Consumer)
     */
    default void claimAllOrSimilarUsingVanillaLook(BlockData[] states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer) {
        this.claimAllOrSimilarUsingVanillaLook(Arrays.asList(states), priority, resultConsumer);
    }

}
