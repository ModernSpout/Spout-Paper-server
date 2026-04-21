package org.fiddlemc.fiddle.api.packetmapping.block.claim;

import io.papermc.paper.registry.event.RegistryEvents;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Provides information about which {@linkplain BlockData block states} are visual duplicates of each other.
 *
 * <p>
 * Two block states are visual duplicates of each other if one block state can be
 * substituted with the other in packets sent to the client without any game-breaking effects
 * (such as that do not allow a state to be observed by the player visually,
 * or that do not allow a client to perform certain interactions with a block
 * due to a mismatch in expectations in what is possible).
 * In some cases, slight, but acceptable, temporary visual glitches may still occur (such as the client forcibly
 * updating the note block state based on the block below it) for 1 tick.
 * </p>
 *
 * <p>
 * Being a visual duplicates is a symmetric relation:
 * if A is a visual duplicate of B then B is a visual duplicate of A.
 * </p>
 *
 * <p>
 * Some blocks are visually similar but still not visually the same,
 * such as waterlogged double slabs (which have water particles, unlike their unwaterlogged counterpart).
 * Those do not count as visual duplicates.
 * </p>
 *
 * <p>
 * Note that this class requires vanilla block states to have been registered,
 * so it is generally not allowed to call any method of this class
 * before the {@link RegistryEvents#BLOCK} event.
 * </p>
 */
public interface VisualDuplicates {

    /**
     * An internal interface to get the {@link VisualDuplicates} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<VisualDuplicates> {
    }

    /**
     * @return The {@link VisualDuplicates} instance.
     */
    static VisualDuplicates get() {
        return ServiceLoader.load(VisualDuplicates.ServiceProvider.class, VisualDuplicates.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    /**
     * @return The visual duplicates for the given {@link BlockData}.
     * For each visual duplicate in the same group,
     * the return value will be the same {@link VisualDuplicateGroup} instance.
     */
    @Nullable VisualDuplicateGroup getVisualDuplicates(BlockData blockState);

    /**
     * @return Whether the given {@link BlockData} has any visual duplicates.
     */
    boolean hasVisualDuplicates(BlockData blockState);

    /**
     * A group of visual duplicates.
     */
    interface VisualDuplicateGroup {

        /**
         * @return The {@linkplain BlockData block states} in this group.
         */
        List<? extends BlockData> getStates();

    }

}
