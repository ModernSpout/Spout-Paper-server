package org.fiddlemc.fiddle.impl.moredatadriven.minecraft;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * An implementation of {@link Registry} specially for {@link BuiltInRegistries#BLOCK}.
 *
 * <p>
 * A single instance of this class shall exist: {@link #INSTANCE}.
 * </p>
 */
public final class BlockRegistry extends DefaultedMappedRegistry<Block> {

    private static @Nullable BlockRegistry INSTANCE;

    public static BlockRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new BlockRegistry();
        }
        return INSTANCE;
    }

    /**
     * Whether {@link BlockState#initCache} can be called.
     *
     * <p>
     * Because calling {@link BlockState#initCache} may reference some {@link Item}
     * instances defined in {@link Items},
     * which in turns requires the static initialization of some {@link Block} instances
     * in {@link Blocks} to have finished,
     * this only becomes true after all the {@link Block} instances in
     * {@link Blocks} have been  initialized.
     * </p>
     */
    public boolean canInitializeBlockStateCaches;

    private BlockRegistry() {
        super("air", Registries.BLOCK, Lifecycle.stable(), true);
    }

    @Override
    public Holder.Reference<Block> register(ResourceKey<Block> key, Block entry, RegistrationInfo registrationInfo) {
        // Register the entry
        var reference = super.register(key, entry, registrationInfo);
        // Initialize the cached registry key field
        entry.keyInBlockRegistry = super.getKey(entry);
        // Initialize the cached registry index field
        entry.indexInBlockRegistry = super.getId(entry);
        // Register the states
        for (net.minecraft.world.level.block.state.BlockState blockState : entry.getStateDefinition().getPossibleStates()) {
            Block.BLOCK_STATE_REGISTRY.add(blockState);
            if (this.canInitializeBlockStateCaches) {
                blockState.initCache();
            }
        }
        // Also add the block to the vanilla-only registry if applicable
        if (entry.isVanilla()) {
            VanillaOnlyBlockRegistry.get().add(entry);
        }
        // Return the reference
        return reference;
    }

    @Override
    public Identifier getKey(Block value) {
        // Use the cached value
        return value.keyInBlockRegistry;
    }

    @Override
    public int getId(@Nullable Block value) {
        // Use the cached value
        return value == null ? -1 : value.indexInBlockRegistry;
    }

    @Override
    public boolean isFreezingDelayed() {
        return true;
    }

}
