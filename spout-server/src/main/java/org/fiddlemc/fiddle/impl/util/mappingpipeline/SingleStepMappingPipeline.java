package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionHandle;
import org.jspecify.annotations.Nullable;

/**
 * A pipeline that applies {@link MappingPipelineStep}s to an initial {@linkplain H handle}.
 */
public interface SingleStepMappingPipeline<T, H extends MappingFunctionHandle<T>> {

    /**
     * @return The smallest possible list containing all steps in this pipeline
     * that may apply to the given handle.
     * It may be null to indicate that there are none.
     */
    @Nullable MappingPipelineStep<H> @Nullable [] getStepsThatMayApplyTo(H handle);

    /**
     * {@linkplain MappingPipelineStep#apply Applies} all applicable steps to the input.
     *
     * @param handle The handle to map. The data in it may be mutated.
     * @return The resulting data, which may be the given instance if no changes were made.
     */
    default T apply(H handle) {
        MappingPipelineStep<H> @Nullable [] mappings = this.getStepsThatMayApplyTo(handle);
        if (mappings != null && mappings.length != 0) {
            for (MappingPipelineStep<H> mapping : mappings) {
                mapping.apply(handle);
            }
        }
        return handle.getImmutable();
    }

}
