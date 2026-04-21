package org.fiddlemc.fiddle.impl.content.block;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Additional block state properties that are not in {@link BlockStateProperties}.
 */
public final class FiddleBlockStateProperties {

    private FiddleBlockStateProperties() {
        throw new UnsupportedOperationException();
    }

    public static final BooleanProperty NORTH_WEST_BOTTOM = BooleanProperty.create("west_down_north");
    public static final BooleanProperty SOUTH_WEST_BOTTOM = BooleanProperty.create("west_down_south");
    public static final BooleanProperty NORTH_WEST_TOP = BooleanProperty.create("west_up_north");
    public static final BooleanProperty SOUTH_WEST_TOP = BooleanProperty.create("west_up_south");
    public static final BooleanProperty NORTH_EAST_BOTTOM = BooleanProperty.create("east_down_north");
    public static final BooleanProperty SOUTH_EAST_BOTTOM = BooleanProperty.create("east_down_south");
    public static final BooleanProperty NORTH_EAST_TOP = BooleanProperty.create("east_up_north");
    public static final BooleanProperty SOUTH_EAST_TOP = BooleanProperty.create("east_up_south");

}
