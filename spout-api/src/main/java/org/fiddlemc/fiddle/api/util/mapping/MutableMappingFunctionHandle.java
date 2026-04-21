package org.fiddlemc.fiddle.api.util.mapping;

/**
 * A {@link MappingFunctionHandle} for a type {@link T} that can be mutated in-place as an instance of {@link MT}.
 */
public interface MutableMappingFunctionHandle<T, MT extends T> extends MappingFunctionHandle<T> {

    /**
     * @return The current data, which may be mutated in-place.
     *
     * <p>
     * For performance reasons, only call this method once you are sure the mapping will make changes.
     * </p>
     */
    MT getMutable();

    /**
     * The same as {@link #set}, but with the explicit guarantee that the given instance
     * is allowed to be mutated by this or other mappings.
     *
     * <p>
     * Calling this method is strongly preferred over calling {@link #set}, when possible.
     * </p>
     */
    void setMutable(MT data);

}
