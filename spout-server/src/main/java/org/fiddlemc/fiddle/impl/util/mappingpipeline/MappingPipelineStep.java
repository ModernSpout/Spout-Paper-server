package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionHandle;

/**
 * A step that is applied as part of a mapping pipeline,
 * defined as a single operation.
 */
public interface MappingPipelineStep<H extends MappingFunctionHandle<?>> {

    /**
     * Applies this mapping.
     *
     * @param handle The handle being mapped.
     */
    void apply(H handle);

}
