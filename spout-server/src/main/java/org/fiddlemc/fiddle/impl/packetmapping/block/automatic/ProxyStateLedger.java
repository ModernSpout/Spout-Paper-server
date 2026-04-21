package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Keeps track of which states have been claimed as proxy states
 * by proxy block mappings.
 */
public final class ProxyStateLedger {

    private ProxyStateLedger() {
        throw new UnsupportedOperationException();
    }

    /**
     * The return value of {@link #claimed()},
     * or null if not initialized yet.
     */
    private static boolean @Nullable [] claimed;

    /**
     * @return The internal ledger, indexed by {@link BlockState#indexInVanillaOnlyBlockStateRegistry},
     */
    private static boolean[] claimed() {
        if (claimed == null) {
            claimed = new boolean[VanillaOnlyBlockStateRegistry.get().size()];
        }
        return claimed;
    }

    /**
     * Attempts to claim all given block states.
     * Only actually claims them if they are all available.
     *
     * @return Whether the claim was successful.
     */
    boolean claim(List<BlockState> states) {
        boolean[] claimed = claimed();
        for (BlockState state : states) {
            if (claimed[state.indexInVanillaOnlyBlockStateRegistry]) {
                return false;
            }
        }
        for (BlockState state : states) {
            claimed[state.indexInVanillaOnlyBlockStateRegistry] = true;
        }
        return true;
    }

    /**
     * @see #claim(List)
     */
    boolean claim(BlockState... states) {
        return claim(Arrays.asList(states));
    }

    /**
     * Attempts to {@linkplain #claim(List) claim} every block state of every given block.
     *
     * @return Whether the claim was successful.
     */
    boolean claim(Block... types) {
        return claim(Arrays.stream(types).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).toList());
    }

}
