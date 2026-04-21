package org.fiddlemc.fiddle.impl.moredatadriven.minecraft;

import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * An implementation of {@link IdMapper} to hold all vanilla items registered to {@link BuiltInRegistries#BLOCK}.
 *
 * <p>
 * A single instance of this class shall exist: {@link #INSTANCE}.
 * </p>
 */
public final class VanillaOnlyBlockRegistry extends IdMapper<Block> {

    private static @Nullable VanillaOnlyBlockRegistry INSTANCE;

    public static VanillaOnlyBlockRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new VanillaOnlyBlockRegistry();
        }
        return INSTANCE;
    }

    private VanillaOnlyBlockRegistry() {
        super();
    }

    @Override
    public void add(Block value) {
        // Add the value
        super.add(value);
        // Initialize the cached registry index field
        value.indexInVanillaOnlyBlockRegistry = super.getId(value);
    }

    @Override
    public int getId(Block value) {
        // Use the cached value
        return value.indexInVanillaOnlyBlockRegistry;
    }

}
