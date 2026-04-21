package org.fiddlemc.fiddle.impl.packetmapping.block;

import java.util.function.Consumer;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingHandle;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingHandleNMS;

/**
 * A {@link BlockMappingsStep} that performs a specific function.
 */
public record BukkitFunctionBlockMappingsStep(Consumer<BlockMappingHandle> function, boolean requiresCoordinates) implements FunctionBlockMappingsStep {

    @Override
    public void apply(BlockMappingHandleNMSImpl handle) {
        this.function.accept(handle.bukkitHandle());
    }

}
