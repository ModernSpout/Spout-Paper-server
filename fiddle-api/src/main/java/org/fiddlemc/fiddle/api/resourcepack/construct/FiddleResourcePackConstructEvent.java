package org.fiddlemc.fiddle.api.resourcepack.construct;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.jspecify.annotations.Nullable;

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
    FiddleResourcePackPath getPath(ClientView.AwarenessLevel awarenessLevel, String path);

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
    FiddleResourcePackPath getAssetPath(ClientView.AwarenessLevel awarenessLevel, String directoryName, NamespacedKey key, @Nullable String extension);

}
