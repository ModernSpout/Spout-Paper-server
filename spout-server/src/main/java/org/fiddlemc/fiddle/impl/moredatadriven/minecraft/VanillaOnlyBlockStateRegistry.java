package org.fiddlemc.fiddle.impl.moredatadriven.minecraft;

import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * An implementation of {@link IdMapper} to hold all vanilla items registered to {@link Block#BLOCK_STATE_REGISTRY}.
 *
 * <p>
 * A single instance of this class shall exist: {@link #INSTANCE}.
 * </p>
 */
public final class VanillaOnlyBlockStateRegistry extends IdMapper<BlockState> {

    private static @Nullable VanillaOnlyBlockStateRegistry INSTANCE;

    public static VanillaOnlyBlockStateRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new VanillaOnlyBlockStateRegistry();
        }
        return INSTANCE;
    }

    private VanillaOnlyBlockStateRegistry() {
        super();
    }

    @Override
    public void add(BlockState value) {
        // Add the value
        super.add(value);
        // Initialize the cached registry index field
        value.indexInVanillaOnlyBlockStateRegistry = super.getId(value);
    }

    @Override
    public int getId(BlockState value) {
        // Use the cached value
        return value.indexInVanillaOnlyBlockStateRegistry;
    }

}
