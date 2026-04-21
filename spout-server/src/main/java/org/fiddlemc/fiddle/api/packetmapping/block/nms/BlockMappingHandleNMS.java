package org.fiddlemc.fiddle.api.packetmapping.block.nms;

import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link BlockMappingBuilderNMS#function}.
 */
public interface BlockMappingHandleNMS extends WithContextMappingFunctionHandle<BlockState, BlockMappingFunctionContext>, WithOriginalMappingFunctionHandle<BlockState> {
}
