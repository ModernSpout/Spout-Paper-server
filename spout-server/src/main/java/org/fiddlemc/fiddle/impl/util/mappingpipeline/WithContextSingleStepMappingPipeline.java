package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;

/**
 * A {@link SingleStepMappingPipeline} for {@link WithContextMappingFunctionHandle}s.
 */
public interface WithContextSingleStepMappingPipeline<T, C extends MappingFunctionContext, H extends WithContextMappingFunctionHandle<T, C>> extends SingleStepMappingPipeline<T, H> {

    /**
     * @return A new handle for the given data and context.
     */
    H createHandle(T data, C context);

    /**
     * {@linkplain MappingPipelineStep#apply Applies} all applicable mappings to the input.
     *
     * @param data    The data to map. It may be mutated.
     * @param context The context of the mapping.
     * @return The resulting data, which may be the given instance if no changes were made.
     */
    default T apply(T data, C context) {
        return this.apply(this.createHandle(data, context));
    }

    /**
     * Creates a generic {@linkplain C context} for {@link #applyGenerically}.
     *
     * <p>
     * While this method is allowed to return a very generic context,
     * it also has the freedom to attempt to make the returned context as accurate as possible,
     * with the information available.
     * </p>
     */
    C createGenericContext();

    /**
     * Convenience method for {@link #apply(T, MappingFunctionContext)},
     * which can be called when a context is not available.
     */
    default T applyGenerically(T data) {
        return this.apply(data, this.createGenericContext());
    }

}
