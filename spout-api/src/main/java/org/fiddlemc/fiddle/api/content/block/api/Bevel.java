package org.fiddlemc.fiddle.api.content.block.api;

import org.bukkit.block.data.Waterlogged;

public interface Bevel extends Waterlogged {

    /**
     * @return Whether the given corner is filled.
     */
    boolean isFilled(Corner corner);

    /**
     * Sets whether the given corner is filled.
     *
     * @param corner A {@link Corner}.
     * @param filled Whether the corner should be filled.
     */
    void setFilled(Corner corner, boolean filled);

    /**
     * A corner of a bevel.
     */
    enum Corner {
        NORTH_WEST_BOTTOM,
        SOUTH_WEST_BOTTOM,
        NORTH_WEST_TOP,
        SOUTH_WEST_TOP,
        NORTH_EAST_BOTTOM,
        SOUTH_EAST_BOTTOM,
        NORTH_EAST_TOP,
        SOUTH_EAST_TOP
    }

}
