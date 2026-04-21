package org.fiddlemc.fiddle.impl.content.block.api;

import com.google.common.base.Preconditions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.content.block.api.Bevel;
import org.fiddlemc.fiddle.impl.content.block.BevelBlock;

public class CraftBevel extends CraftBlockData implements Bevel {

    private static final BooleanProperty NORTH_WEST_BOTTOM = BevelBlock.NORTH_WEST_BOTTOM;
    private static final BooleanProperty SOUTH_WEST_BOTTOM = BevelBlock.SOUTH_WEST_BOTTOM;
    private static final BooleanProperty NORTH_WEST_TOP = BevelBlock.NORTH_WEST_TOP;
    private static final BooleanProperty SOUTH_WEST_TOP = BevelBlock.SOUTH_WEST_TOP;
    private static final BooleanProperty NORTH_EAST_BOTTOM = BevelBlock.NORTH_EAST_BOTTOM;
    private static final BooleanProperty SOUTH_EAST_BOTTOM = BevelBlock.SOUTH_EAST_BOTTOM;
    private static final BooleanProperty NORTH_EAST_TOP = BevelBlock.NORTH_EAST_TOP;
    private static final BooleanProperty SOUTH_EAST_TOP = BevelBlock.SOUTH_EAST_TOP;
    private static final BooleanProperty WATERLOGGED = BevelBlock.WATERLOGGED;

    public CraftBevel(BlockState state) {
        super(state);
    }

    @Override
    public boolean isFilled(Corner corner) {
        Preconditions.checkArgument(corner != null, "corner cannot be null!");
        return this.get(cornerToProperty(corner));
    }

    @Override
    public void setFilled(Corner corner, boolean filled) {
        Preconditions.checkArgument(corner != null, "corner cannot be null!");
        this.set(cornerToProperty(corner), filled);
    }

    @Override
    public boolean isWaterlogged() {
        return this.get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(final boolean waterlogged) {
        this.set(WATERLOGGED, waterlogged);
    }

    private static BooleanProperty cornerToProperty(Corner corner) {
        return switch (corner) {
            case NORTH_WEST_BOTTOM -> NORTH_WEST_BOTTOM;
            case SOUTH_WEST_BOTTOM -> SOUTH_WEST_BOTTOM;
            case NORTH_WEST_TOP -> NORTH_WEST_TOP;
            case SOUTH_WEST_TOP -> SOUTH_WEST_TOP;
            case NORTH_EAST_BOTTOM -> NORTH_EAST_BOTTOM;
            case SOUTH_EAST_BOTTOM -> SOUTH_EAST_BOTTOM;
            case NORTH_EAST_TOP -> NORTH_EAST_TOP;
            case SOUTH_EAST_TOP -> SOUTH_EAST_TOP;
        };
    }

}
