package org.fiddlemc.fiddle.api.resourcepack.construct;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.jspecify.annotations.Nullable;
import java.io.IOException;

/**
 * Provides functionality to edit the constructed Fiddle server resource pack.
 */
public interface FiddleResourcePackConstructEvent extends LifecycleEvent {

    /**
     * @param awarenessLevel The {@link ClientView.AwarenessLevel} for which to modify the resource pack.
     * @return The {@link FiddleResourcePackPath} at the given string path,
     * where directories are separated by forward slashes ({@code /}),
     * for example {@code "assets/example/models/item/ash.json"} to get the path to the model of the
     * {@code "example:ash"} item.
     * @throws IllegalArgumentException If the given {@link ClientView.AwarenessLevel}
     *                                  does not support a resource pack.
     */
    FiddleResourcePackPath path(ClientView.AwarenessLevel awarenessLevel, String path);

    /**
     * @param awarenessLevel The {@link ClientView.AwarenessLevel} for which to modify the resource pack.
     * @param directoryName  The name of the directory in which the asset is.
     *                       This must not start with {@code "assets/"} (it is automatically added).
     *                       The name is allowed to contain forward slashes,
     *                       e.g. {@code "models/item"}, with namespaced key {@code "example:ash"}
     *                       and extension {@code "json"}, to get the path
     *                       {@code "assets/example/models/item/ash.json"}.
     * @param key            A {@link NamespacedKey} of the asset.
     *                       The key part is allowed to contain forward slashes,
     *                       e.g. {@code "item/ash"}, with namespace {@code "example"}
     *                       and directory {@code "models"} and extension {@code "json"}, to get the path
     *                       {@code "assets/example/models/item/ash.json"}.
     * @param extension      The file extension, for example {@code "json"} or {@code "png"}.
     * @return The {@link FiddleResourcePackPath} for the asset in the given directory,
     * at the given {@link NamespacedKey}.
     * @throws IllegalArgumentException If the given {@link ClientView.AwarenessLevel}
     *                                  does not support a resource pack.
     */
    FiddleResourcePackPath asset(ClientView.AwarenessLevel awarenessLevel, String directoryName, NamespacedKey key, @Nullable String extension);

    /**
     * @see #asset(ClientView.AwarenessLevel, String, NamespacedKey, String)
     */
    FiddleResourcePackPath asset(ClientView.AwarenessLevel awarenessLevel, String directoryName, Keyed keyed, @Nullable String extension);

    /**
     * @see #asset(ClientView.AwarenessLevel, String, NamespacedKey, String)
     */
    FiddleResourcePackPath asset(ClientView.AwarenessLevel awarenessLevel, String directoryName, Key keyed, @Nullable String extension);

    /**
     * @see #asset(ClientView.AwarenessLevel, String, NamespacedKey, String)
     */
    FiddleResourcePackPath asset(ClientView.AwarenessLevel awarenessLevel, String directoryName, net.kyori.adventure.key.Keyed keyed, @Nullable String extension);

    /**
     * Copies a plugin resource to the resource pack.
     *
     * @param pathInPluginResources A path to a file in the plugin's resources
     *                              (relative to the {@code src/main/resources} folder),
     *                              for example {@code "resource_pack/assets/example/models/block/ash_block.json"}.
     * @param pathInResourcePack    A path to a file in the resource pack,
     *                              for example {@code "assets/example/models/block/ash_block.json"}.
     */
    void copyPluginResource(PluginBootstrap bootstrap, ClientView.AwarenessLevel awarenessLevel, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResource(PluginBootstrap, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResource(PluginBootstrap bootstrap, ClientView.AwarenessLevel[] awarenessLevels, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResource(PluginBootstrap, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResource(PluginBootstrap bootstrap, Iterable<ClientView.AwarenessLevel> awarenessLevels, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResource(PluginBootstrap, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResource(Class<? extends PluginBootstrap> bootstrapClass, ClientView.AwarenessLevel awarenessLevel, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResource(PluginBootstrap, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResource(Class<? extends PluginBootstrap> bootstrapClass, ClientView.AwarenessLevel[] awarenessLevels, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResource(PluginBootstrap, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResource(Class<? extends PluginBootstrap> bootstrapClass, Iterable<ClientView.AwarenessLevel> awarenessLevels, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * Copies plugin resources to the resource pack.
     *
     * @param pathInPluginResources A path to a folder in the plugin's resources
     *                              (relative to the {@code src/main/resources} folder),
     *                              for example {@code "resource_pack/assets/example/models"}.
     * @param pathInResourcePack    A path to a folder in the resource pack,
     *                              for example {@code "assets/example/models"}.
     *                              To copy to the root of the resource pack, put a blank string ({@code ""}).
     */
    void copyPluginResources(BootstrapContext context, ClientView.AwarenessLevel awarenessLevel, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResources(BootstrapContext, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResources(BootstrapContext context, ClientView.AwarenessLevel[] awarenessLevels, String pathInPluginResources, String pathInResourcePack) throws IOException;

    /**
     * @see #copyPluginResources(BootstrapContext, ClientView.AwarenessLevel, String, String)
     */
    void copyPluginResources(BootstrapContext context, Iterable<ClientView.AwarenessLevel> awarenessLevels, String pathInPluginResources, String pathInResourcePack) throws IOException;

}
