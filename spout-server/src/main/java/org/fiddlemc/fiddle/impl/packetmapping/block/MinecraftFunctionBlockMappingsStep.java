package org.fiddlemc.fiddle.impl.packetmapping.block;

import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingHandleNMS;
import java.util.function.Consumer;

/**
 * A {@link BlockMappingsStep} that performs a specific function.
 */
public record MinecraftFunctionBlockMappingsStep(Consumer<BlockMappingHandleNMS> function, boolean requiresCoordinates) implements FunctionBlockMappingsStep {

    @Override
    public void apply(BlockMappingHandleNMSImpl handle) {
        this.function.accept(handle);
    }

}
