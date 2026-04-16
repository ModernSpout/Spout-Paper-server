package org.fiddlemc.fiddle.impl.resourcepack.plugin.discover;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import it.unimi.dsi.fastutil.Pair;
import org.fiddlemc.fiddle.api.resourcepack.plugin.discover.FiddlePluginResourcePackDiscoverEvent;

/**
 * The implementation for {@link FiddlePluginResourcePackDiscoverEvent}.
 */
public final class FiddlePluginResourcePackDiscoverEventImpl implements FiddlePluginResourcePackDiscoverEvent, PaperLifecycleEvent {

    FiddlePluginResourcePackDiscoverEventImpl() {
    }

    @Override
    public void register(PluginBootstrap bootstrap, BootstrapContext context, String path) {
        FiddlePluginResourcePackDiscoveryImpl.get().resourcePackPaths.put(bootstrap, Pair.of(context.getPluginSource(), path));
    }

}
