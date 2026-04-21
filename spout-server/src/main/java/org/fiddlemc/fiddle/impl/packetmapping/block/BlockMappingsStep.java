package org.fiddlemc.fiddle.impl.packetmapping.block;

import org.fiddlemc.fiddle.impl.util.mappingpipeline.MappingPipelineStep;

/**
 * A mapping that is stored in {@link BlockMappingsImpl}.
 */
public sealed interface BlockMappingsStep extends MappingPipelineStep<BlockMappingHandleNMSImpl> permits SimpleBlockMappingsStep, FunctionBlockMappingsStep {
}
