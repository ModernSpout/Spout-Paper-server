package org.fiddlemc.fiddle.api.util.composable;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * A compose event where the list of existing registered {@linkplain E elements}
 * (registered by some key of type {@link K}) can be modified.
 */
public interface ChangeRegisteredComposeEvent<K, E> extends LifecycleEvent {

    /**
     * Changes the list of registered elements for the given key.
     *
     * <p>
     * This list is freely mutable.
     * This can be used to remove existing elements or insert an element at a desired index.
     * </p>
     *
     * @param key          The key for which to change the registered elements.
     * @param listConsumer The consumer that applies the desired changes to the list of elements.
     */
    void changeRegistered(K key, Consumer<List<E>> listConsumer);

}
