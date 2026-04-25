package spout.server.paper.impl.clientview.set;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.Identifier;
import spout.server.paper.api.clientview.ClientView;
import spout.common.branding.SpoutNamespace;
import spout.server.paper.impl.clientview.JavaVanillaClientViewImpl;
import spout.server.paper.impl.clientview.JavaWithClientModClientViewImpl;
import spout.server.paper.impl.clientview.JavaWithResourcePackClientViewImpl;
import spout.server.paper.impl.resourcepack.send.ResourcePackSending;
import org.jspecify.annotations.Nullable;

/**
 * Keeps track of the status of determining the {@link ClientView} of a {@link Connection}.
 */
public final class ClientViewSetter {

    private static final int CLIENT_MOD_DETECTION_PACKET_TRANSACTION_ID = -721055663;
    private static final int CLIENT_MOD_DETECTION_PACKET_NONCE = 345890285;
    private static final Identifier CLIENT_MOD_DETECTION_PACKET_ID = Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "detect_client_mod");
    private static final int MIN_CLIENT_MOD_PROTOCOL_VERSION = 2;
    private static final int MAX_CLIENT_MOD_PROTOCOL_VERSION = 2;
    private static final ClientboundCustomQueryPacket CLIENT_MOD_DETECTION_PACKET = new ClientboundCustomQueryPacket(CLIENT_MOD_DETECTION_PACKET_TRANSACTION_ID, new CustomQueryPayload() {

        @Override
        public Identifier id() {
            return CLIENT_MOD_DETECTION_PACKET_ID;
        }

        @Override
        public void write(final FriendlyByteBuf buffer) {
            // First write a 0 (can be changed later if protocol for this packet changes)
            buffer.writeVarInt(0);
            // Write the transaction id as a nonce
            buffer.writeVarInt(CLIENT_MOD_DETECTION_PACKET_NONCE);
            // Write compatible versions
            buffer.writeVarInt(MIN_CLIENT_MOD_PROTOCOL_VERSION);
            buffer.writeVarInt(MAX_CLIENT_MOD_PROTOCOL_VERSION);
        }

    });

    private final Connection connection;
    private boolean hasClientMod = false;
    public boolean isWaitingForResourcePackPacketResponse = false;

    public ClientViewSetter(Connection connection) {
        this.connection = connection;
    }

    public void sendClientModDetectionPacket() {
        this.connection.send(CLIENT_MOD_DETECTION_PACKET);
    }

    public boolean handleClientModDetectionPacket(ServerboundCustomQueryAnswerPacket packet) {
        if (packet.transactionId() != CLIENT_MOD_DETECTION_PACKET_TRANSACTION_ID) {
            return false;
        }
        try {
            if (packet.payload() instanceof ServerboundCustomQueryAnswerPacket.QueryAnswerPayload payload) {
                // Read payload to confirm
                boolean clientModConfirmed = false;
                payload.buffer.markReaderIndex();
                try {
                    payload.buffer.readerIndex(0);
                    // First there must be a 0
                    int protocolMarker = payload.buffer.readVarInt();
                    if (protocolMarker == 0) {
                        // Then comes the nonce to confirm the packet isn't a fluke
                        int nonce = payload.buffer.readVarInt();
                        if (nonce == CLIENT_MOD_DETECTION_PACKET_NONCE) {
                            // Then comes the version that the client wishes to use
                            int version = payload.buffer.readVarInt();
                            if (version >= MIN_CLIENT_MOD_PROTOCOL_VERSION && version <= MAX_CLIENT_MOD_PROTOCOL_VERSION) {
                                clientModConfirmed = true;
                            }
                        }
                    }
                } finally {
                    payload.buffer.resetReaderIndex();
                }
                if (clientModConfirmed) {
                    this.markHasClientMod();
                }
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    public boolean hasClientMod() {
        return this.hasClientMod;
    }

    private void markHasClientMod() {
        if (this.connection.clientView == null) {
            this.hasClientMod = true;
            this.connection.clientView = new JavaWithClientModClientViewImpl(this.connection);
        }
    }

    /**
     * @return A {@link ClientboundResourcePackPushPacket} for sending the generated resource pack,
     * or null if not applicable.
     */
    public @Nullable ClientboundResourcePackPushPacket getResourcePackPacket() {
        if (this.hasClientMod) {
            return ResourcePackSending.getClientModPacket();
        }
        return ResourcePackSending.getVanillaPacket();
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
