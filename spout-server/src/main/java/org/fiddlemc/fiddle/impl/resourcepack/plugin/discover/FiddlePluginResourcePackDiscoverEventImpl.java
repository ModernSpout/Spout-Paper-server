package org.fiddlemc.fiddle.impl.resourcepack.plugin.discover;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import it.unimi.dsi.fastutil.Pair;
import org.fiddlemc.fiddle.api.resourcepack.plugin.discover.FiddlePluginResourcePackDiscoverEvent;
import org.fiddlemc.fiddle.impl.util.io.JarFileUtil;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The implementation for {@link FiddlePluginResourcePackDiscoverEvent}.
 */
public final class FiddlePluginResourcePackDiscoverEventImpl implements FiddlePluginResourcePackDiscoverEvent, PaperLifecycleEvent {

    FiddlePluginResourcePackDiscoverEventImpl() {
    }

    @Override
    public void register(PluginBootstrap bootstrap, BootstrapContext context, String path) {
        // Store the resource pack path of the plugin
        FiddlePluginResourcePackDiscoveryImpl.get().resourcePackPathsByPlugin.put(bootstrap, Pair.of(context.getPluginSource(), path));
        // Include all resource pack files
        try {
            JarFileUtil.forEachFileBelowDirectory(context.getPluginSource().toFile(), path, (entry, jar, relativePath) -> FiddlePluginResourcePackDiscoveryImpl.get().providingPluginsByResourcePackFileRelativePath.computeIfAbsent(relativePath, $ -> new ArrayList<>(1)).add(Pair.of(bootstrap, path)));
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading resource pack contents of plugin with bootstrap " + bootstrap.getClass().getName(), e);
        }
    }

}
