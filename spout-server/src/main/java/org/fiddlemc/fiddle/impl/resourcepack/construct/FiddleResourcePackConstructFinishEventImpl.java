package org.fiddlemc.fiddle.impl.resourcepack.construct;

import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleConstructedResourcePack;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructFinishEvent;
import org.jspecify.annotations.Nullable;
import java.util.Map;

/**
 * The implementation for {@link FiddleResourcePackConstructFinishEvent}.
 */
public record FiddleResourcePackConstructFinishEventImpl(Map<ClientView.AwarenessLevel, FiddleConstructedResourcePackImpl> packs) implements FiddleResourcePackConstructFinishEvent, PaperLifecycleEvent {

    @Override
    public FiddleConstructedResourcePack get(ClientView.AwarenessLevel awarenessLevel) {
        @Nullable FiddleConstructedResourcePack pack = this.packs.get(awarenessLevel);
        if (pack == null) {
            throw new IllegalArgumentException("No generated resource pack exists for awareness level " + awarenessLevel);
        }
        return pack;
    }

}
