package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

/**
 * The states that are chosen as mapping targets for a
 * requested proxy mapping.
 *
 * <p>
 *     Some of the states may be the fallback state instead of the desired proxy state,
 *     in which case the respective {@code isFallback} method will return true.
 * </p>
 */
public interface UsedStates {

    interface Single extends UsedStates {

        BlockData get();

        boolean isFallback();

    }

    interface Waterlogged extends UsedStates {

        BlockData get(boolean waterlogged);

        boolean isFallback(boolean waterlogged);

    }

    interface Slab extends UsedStates {

        BlockData get(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged);

        boolean isFallback(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged);

    }

    interface Stairs extends UsedStates {

        BlockData get(org.bukkit.block.data.type.Stairs.Shape shape, Bisected.Half half, BlockFace facing, boolean waterlogged);

        boolean isFallback(org.bukkit.block.data.type.Stairs.Shape shape, Bisected.Half half, BlockFace facing, boolean waterlogged);

    }

}
