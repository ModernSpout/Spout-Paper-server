package org.fiddlemc.fiddle.api.util.composable;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import java.util.function.Consumer;

/**
 * A compose event where new elements can be registered by a builder pattern.
 */
public interface BuilderComposeEvent<B> extends LifecycleEvent {

    /**
     * Register a new element, that can be customized with the builder.
     *
     * @param builderConsumer A consumer that makes desired changes to a builder.
     */
    void register(Consumer<B> builderConsumer);

}
