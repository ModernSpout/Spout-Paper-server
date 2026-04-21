package org.fiddlemc.fiddle.impl.moredatadriven.minecraft;

import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

/**
 * An implementation of {@link IdMapper} to hold all vanilla items registered to {@link BuiltInRegistries#ITEM}.
 *
 * <p>
 * A single instance of this class shall exist: {@link #INSTANCE}.
 * </p>
 */
public final class VanillaOnlyItemRegistry extends IdMapper<Item> {

    private static @Nullable VanillaOnlyItemRegistry INSTANCE;

    public static VanillaOnlyItemRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new VanillaOnlyItemRegistry();
        }
        return INSTANCE;
    }

    private VanillaOnlyItemRegistry() {
        super();
    }

    @Override
    public void add(Item value) {
        // Add the value
        super.add(value);
        // Initialize the cached registry index field
        value.indexInVanillaOnlyItemRegistry = super.getId(value);
    }

    @Override
    public int getId(Item value) {
        // Use the cached value
        return value.indexInVanillaOnlyItemRegistry;
    }

}
