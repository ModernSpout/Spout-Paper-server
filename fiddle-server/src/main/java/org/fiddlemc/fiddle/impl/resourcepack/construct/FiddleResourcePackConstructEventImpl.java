package org.fiddlemc.fiddle.impl.resourcepack.construct;

import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackPath;
import org.jspecify.annotations.Nullable;

/**
 * The implementation for {@link org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent}.
 */
public final class FiddleResourcePackConstructEventImpl implements PaperLifecycleEvent, FiddleResourcePackConstructEvent {

    @Override
    public FiddleResourcePackPath getPath(String path) {
        return new FiddleResourcePackPathImpl(this, path);
    }

    @Override
    public FiddleResourcePackPath getAssetPath(String directoryName, NamespacedKey key, @Nullable String extension) {
        return this.getPath("assets/" + key.getNamespace() + "/" + directoryName + "/" + key.getKey() + (extension != null ? "." + extension : ""));
    }

}
