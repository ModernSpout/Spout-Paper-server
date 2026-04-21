package org.fiddlemc.fiddle.api.resourcepack.construct;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import org.fiddlemc.fiddle.api.clientview.ClientView;

/**
 * Called when the Fiddle server resource pack has been constructed.
 */
public interface FiddleResourcePackConstructFinishEvent extends LifecycleEvent {

    /**
     * @param awarenessLevel A {@link ClientView.AwarenessLevel}.
     * @return The constructed resource pack for the given awareness level.
     * @throws IllegalArgumentException If the given {@link ClientView.AwarenessLevel}
     *                                  does not support a resource pack.
     */
    FiddleConstructedResourcePack get(ClientView.AwarenessLevel awarenessLevel);

}
