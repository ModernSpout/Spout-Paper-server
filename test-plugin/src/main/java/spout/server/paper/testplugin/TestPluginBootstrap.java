package spout.server.paper.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import spout.server.paper.api.SpoutEvents;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("unused")
public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        // Don't do anything else if the server doesn't support Spout
        if (!CheckSpout.checkSpout()) return;

        registerIncludedDataPack(context);
        registerIncludedResourcePack(context);
    }

    /**
     * Makes sure the included data pack is loaded.
     * It contains drop tables, crafting recipes and more for the custom blocks and items we add.
     */
    private void registerIncludedDataPack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
            LifecycleEvents.DATAPACK_DISCOVERY,
            event -> {
                try {
                    event.registrar().discoverPack(Objects.requireNonNull(this.getClass().getResource("/data_pack")).toURI(), "provided");
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    /**
     * Makes sure the included resource pack is loaded.
     * It contains textures, models and more for the custom blocks and items we add.
     */
    private void registerIncludedResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
            SpoutEvents.PLUGIN_RESOURCE_PACK_DISCOVERY,
            event -> event.register(this, context)
        );
    }

}
