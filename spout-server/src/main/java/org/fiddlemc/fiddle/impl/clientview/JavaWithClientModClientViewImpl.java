package org.fiddlemc.fiddle.impl.clientview;

import net.minecraft.network.Connection;
import org.fiddlemc.fiddle.api.clientview.ClientView;

/**
 * A simple implementation of {@link ClientView}
 * for {@link AwarenessLevel#CLIENT_MOD} clients.
 */
public class JavaWithClientModClientViewImpl extends ConnectionClientViewImpl {

    public JavaWithClientModClientViewImpl(Connection connection) {
        super(connection);
    }

    @Override
    public AwarenessLevel getAwarenessLevel() {
        return AwarenessLevel.CLIENT_MOD;
    }

}
