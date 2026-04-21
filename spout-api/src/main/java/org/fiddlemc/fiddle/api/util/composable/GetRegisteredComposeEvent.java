package org.fiddlemc.fiddle.api.util.composable;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * A compose event where the list of existing registered {@linkplain E elements}
 * (registered by some key of type {@link K}) can be observed.
 */
public interface GetRegisteredComposeEvent<K, E> extends LifecycleEvent {

    /**
     * @param key The key for which to get the registered elements.
     * @return The list of registered elements for the given key.
     * This list must not be modified.
     */
    List<E> getRegistered(K key);

}
