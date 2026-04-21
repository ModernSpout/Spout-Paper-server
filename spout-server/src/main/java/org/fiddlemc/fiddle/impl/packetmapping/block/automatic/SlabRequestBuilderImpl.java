package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.SlabRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link SlabRequestBuilder}.
 */
public class SlabRequestBuilderImpl extends FromToBlockTypeRequestBuilderImpl<UsedStates.Slab> implements SlabRequestBuilder {

    public @Nullable BlockState fullBlockFallback; // TODO use

    @Override
    public void fullBlockFallback(@Nullable BlockData fullBlockFallback) {
        this.fullBlockFallback = fullBlockFallback == null ? null : ((CraftBlockData) fullBlockFallback).getState();
    }

    @Override
    public @Nullable BlockData fullBlockFallback() {
        return this.fullBlockFallback == null ? null : this.fullBlockFallback.createCraftBlockData();
    }

}
