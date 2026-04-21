package org.fiddlemc.fiddle.impl.moredatadriven.minecraft;

import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

/**
 * An implementation of {@link IdMapper} specially for {@link Block#BLOCK_STATE_REGISTRY}.
 *
 * <p>
 * A single instance of this class shall exist: {@link #INSTANCE}.
 * </p>
 */
public final class BlockStateRegistry extends IdMapper<BlockState> {

    private static @Nullable BlockStateRegistry INSTANCE;

    public static BlockStateRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new BlockStateRegistry();
        }
        return INSTANCE;
    }

    private BlockStateRegistry() {
        super();
    }

    @Override
    public void add(BlockState value) {
        // Add the value
        super.add(value);
        // Initialize the cached registry index field
        value.indexInBlockStateRegistry = super.getId(value);
        // Also add the block state to the vanilla-only registry if applicable
        if (value.isVanilla()) {
            VanillaOnlyBlockStateRegistry.get().add(value);
        }
    }

    @Override
    public int getId(BlockState value) {
        // Use the cached value
        return value.indexInBlockStateRegistry;
    }

}
