package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition;

import net.minecraft.resources.ResourceKey;

/**
 * Common interface for definitions of some hard-coded resource {@link T}.
 */
public interface Definition<T> {

    /**
     * @return A resource created according to this definition.
     */
    T toResource(ResourceKey<T> key);

}
