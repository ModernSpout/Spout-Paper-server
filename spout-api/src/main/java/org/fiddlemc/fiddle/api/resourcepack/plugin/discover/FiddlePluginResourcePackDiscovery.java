package org.fiddlemc.fiddle.api.resourcepack.plugin.discover;

import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service to discover Fiddle plugin resource pack content.
 */
public interface FiddlePluginResourcePackDiscovery extends Composable<FiddlePluginResourcePackDiscoverEvent> {

    /**
     * An internal interface to get the {@link FiddlePluginResourcePackDiscovery} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<FiddlePluginResourcePackDiscovery> {
    }

    /**
     * @return The {@link FiddlePluginResourcePackDiscovery} instance.
     */
    static FiddlePluginResourcePackDiscovery get() {
        return ServiceLoader.load(FiddlePluginResourcePackDiscovery.ServiceProvider.class, FiddlePluginResourcePackDiscovery.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    String DEFAULT_PATH = "resource_pack";

}
