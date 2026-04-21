package org.fiddlemc.fiddle.api.util.composable;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;

/**
 * An abstract type of composable that fires an event of type {@link E} when it is composed.
 */
public interface Composable<E extends LifecycleEvent> {

    /**
     * @return The {@link LifecycleEventType} for this pipeline.
     */
    ComposableEventType<E> compose();

}
