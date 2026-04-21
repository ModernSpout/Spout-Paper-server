package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.nms;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.BlockRegistryEntry;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link BlockRegistryEntry.Builder} that allows building a {@link Block} using Minecraft internals.
 */
public interface BlockRegistryEntryBuilderNMS extends BlockRegistryEntry.Builder, KeyAwareRegistryEntryNMS {

    /**
     * Sets the factory to use, and marks this builder as using NMS.
     */
    BlockRegistryEntryBuilderNMS factoryNMS(Function<BlockBehaviour.Properties, Block> factory);

    /**
     * Replaces the NMS properties for the block.
     */
    BlockRegistryEntryBuilderNMS propertiesNMS(BlockBehaviour.Properties properties);

    /**
     * Modifies the NMS properties for the block.
     */
    BlockRegistryEntryBuilderNMS propertiesNMS(Consumer<BlockBehaviour.Properties> properties);

}
