package org.fiddlemc.fiddle.api.packetmapping.block;

import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link BlockMappingBuilder#to}.
 */
public interface BlockMappingHandle extends WithContextMappingFunctionHandle<BlockData, BlockMappingFunctionContext>, WithOriginalMappingFunctionHandle<BlockData> {
}
