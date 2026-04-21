package org.fiddlemc.fiddle.api.util.mapping;

/**
 * A handle for the data that a mapping function is being applied on.
 */
public interface MappingFunctionHandle<T> {

    /**
     * @return The current data, after potential previous mappings were applied.
     * The returned instance must not be modified.
     * You can use this to decide whether you wish to mutate it.
     */
    T getImmutable();

    /**
     * Replaces the data with the given one.
     * The given instance will not be modified.
     *
     * @param data The data that results from this mapping.
     */
    void set(T data);

}
