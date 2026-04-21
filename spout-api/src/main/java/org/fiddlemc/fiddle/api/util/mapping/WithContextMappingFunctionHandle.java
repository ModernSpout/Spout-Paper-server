package org.fiddlemc.fiddle.api.util.mapping;

/**
 * A {@link MappingFunctionHandle} that can provide context for the current mapping.
 */
public interface WithContextMappingFunctionHandle<T, C extends MappingFunctionContext> extends MappingFunctionHandle<T> {

    /**
     * @return The context for which the current mapping is being applied.
     */
    C getContext();

}
