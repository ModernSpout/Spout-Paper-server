package org.fiddlemc.fiddle.api.util.composable;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;

/**
 * The {@link LifecycleEventType} for the compose events of some {@link Composable}.
 */
public interface ComposableEventType<E extends LifecycleEvent> extends LifecycleEventType<BootstrapContext, E, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> {
}
