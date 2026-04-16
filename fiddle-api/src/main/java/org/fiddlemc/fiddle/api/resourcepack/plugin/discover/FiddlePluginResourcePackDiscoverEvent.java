package org.fiddlemc.fiddle.api.resourcepack.plugin.discover;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;

/**
 * Called when the Fiddle server resource pack has been constructed.
 */
public interface FiddlePluginResourcePackDiscoverEvent extends LifecycleEvent {

    default void register(PluginBootstrap bootstrap, BootstrapContext context) {
        this.register(bootstrap, context, FiddlePluginResourcePackDiscovery.DEFAULT_PATH);
    }

    void register(PluginBootstrap bootstrap, BootstrapContext context, String path);

}
