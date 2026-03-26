package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.BlockEntityTypeRegistryEntry;

public interface BlockEntityTypeRegistryEntryBuilderNMS extends BlockEntityTypeRegistryEntry.Builder {
    BlockEntityTypeRegistryEntryBuilderNMS factorNMS(BlockEntityType.BlockEntitySupplier<?> factory);

    BlockEntityTypeRegistryEntryBuilderNMS validBlocksNMS(Block... blocks);
}
