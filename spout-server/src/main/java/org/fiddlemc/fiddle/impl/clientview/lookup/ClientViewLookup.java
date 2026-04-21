package org.fiddlemc.fiddle.impl.clientview.lookup;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.impl.clientview.FallbackClientViewImpl;
import org.jspecify.annotations.Nullable;

/**
 * An interface for classes that can provide a {@link ClientView} for a client that they represent.
 */
public interface ClientViewLookup {

    /**
     * @return The {@link ClientView} of the client, or null if none is determined.
     */
    default @Nullable ClientView getClientView() {
        if (this instanceof ServerLoginPacketListenerImpl serverLoginPacketListener) {
            return serverLoginPacketListener.connection.getClientView();
        } else if (this instanceof ServerCommonPacketListenerImpl serverCommonPacketListener) {
            return serverCommonPacketListener.connection.getClientView();
        } else if (this instanceof ServerPlayer serverPlayer) {
            return serverPlayer.connection.connection.getClientView();
        }
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support getClientView");
    }

    /**
     * Convenience method to get the {@link ClientView} of the client, or a fallback.
     *
     * @return The same as {@link ClientView},
     * except null will be replaced by {@link FallbackClientViewImpl#INSTANCE}.
     */
    default ClientView getClientViewOrFallback() {
        @Nullable ClientView clientView = this.getClientView();
        return clientView != null ? clientView : FallbackClientViewImpl.INSTANCE;
    }

}
