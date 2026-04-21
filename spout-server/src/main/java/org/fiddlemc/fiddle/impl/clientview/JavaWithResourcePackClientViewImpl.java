package org.fiddlemc.fiddle.impl.clientview;

import net.minecraft.network.Connection;
import org.fiddlemc.fiddle.api.clientview.ClientView;

/**
 * A simple implementation of {@link ClientView}
 * for {@link AwarenessLevel#RESOURCE_PACK} clients.
 */
public class JavaWithResourcePackClientViewImpl extends ConnectionClientViewImpl {

    public JavaWithResourcePackClientViewImpl(Connection connection) {
        super(connection);
    }

    @Override
    public AwarenessLevel getAwarenessLevel() {
        return AwarenessLevel.RESOURCE_PACK;
    }

}
