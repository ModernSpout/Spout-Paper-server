package org.fiddlemc.fiddle.impl.clientview.set;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.impl.clientview.JavaVanillaClientViewImpl;
import org.fiddlemc.fiddle.impl.clientview.JavaWithClientModClientViewImpl;
import org.fiddlemc.fiddle.impl.clientview.JavaWithResourcePackClientViewImpl;
import org.fiddlemc.fiddle.impl.resourcepack.send.FiddleResourcePackSending;
import org.jspecify.annotations.Nullable;

/**
 * Keeps track of the status of determining the {@link ClientView} of a {@link Connection}.
 */
public final class ClientViewSetter {

    private final Connection connection;
    private boolean hasClientMod = false;
    public boolean isWaitingForResourcePackPacketResponse = false;

    public ClientViewSetter(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return A {@link ClientboundResourcePackPushPacket} for sending the generated resource pack,
     * or null if not applicable.
     */
    public @Nullable ClientboundResourcePackPushPacket getPacket() {
        if (this.hasClientMod) {
            return FiddleResourcePackSending.getClientModPacket();
        }
        return FiddleResourcePackSending.getVanillaPacket();
    }

    public void markHasClientMod() { // TODO implement calling if packet received (during login phase, client must send before it sends ServerboundLoginAcknowledgedPacket)
        if (this.connection.clientView == null) {
            this.hasClientMod = true;
            this.connection.clientView = new JavaWithClientModClientViewImpl(this.connection);
        }
    }

    public void markAcceptedResourcePack() {
        if (this.connection.clientView == null) {
            if (!this.hasClientMod) {
                this.connection.clientView = new JavaWithResourcePackClientViewImpl(this.connection);
            }
        }
    }

    public void markDeniedResourcePack() {
        if (this.connection.clientView == null) {
            if (!this.hasClientMod) {
                this.connection.clientView = new JavaVanillaClientViewImpl(this.connection);
            }
        }
    }

}
