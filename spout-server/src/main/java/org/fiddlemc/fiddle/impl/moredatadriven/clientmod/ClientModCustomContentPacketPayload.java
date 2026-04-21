package org.fiddlemc.fiddle.impl.moredatadriven.clientmod;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import spout.common.branding.SpoutNamespace;

public final class ClientModCustomContentPacketPayload implements CustomPacketPayload {

    private static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "custom_content");
    private static final CustomPacketPayload.Type<ClientModCustomContentPacketPayload> TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, ClientModCustomContentPacketPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientModCustomContentPacketPayload::write, ClientModCustomContentPacketPayload::new);
    public static final CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, ?> TYPE_AND_CODEC = new CustomPacketPayload.TypeAndCodec<>(TYPE, ClientModCustomContentPacketPayload.STREAM_CODEC);

    private static final ClientModCustomContentPacketPayload INSTANCE = new ClientModCustomContentPacketPayload();
    private static final ClientboundCustomPayloadPacket PACKET_INSTANCE = new ClientboundCustomPayloadPacket(INSTANCE);

    private ClientModCustomContentPacketPayload() {
    }

    private ClientModCustomContentPacketPayload(FriendlyByteBuf buffer) {
        // No need to parse: only needs to happen on the client
    }

    @Override
    public Type<ClientModCustomContentPacketPayload> type() {
        return TYPE;
    }

    private void write(FriendlyByteBuf buffer) {

        // Set a client mod client view to prevent any mapping
        org.fiddlemc.fiddle.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal.THREAD_LOCAL.set(new java.lang.ref.WeakReference<>(new org.fiddlemc.fiddle.impl.clientview.lookup.ClientViewLookup() {
            @Override
            public org.fiddlemc.fiddle.api.clientview.ClientView getClientView() {
                return new org.fiddlemc.fiddle.impl.clientview.JavaWithClientModClientViewImpl(null);
            }
        }));

        // Write the custom content
        ClientModCustomContent customContent = new ClientModCustomContent(
            org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry.get().stream().filter(block -> !block.isVanilla()).toList(),
            org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry.get().stream().filter(item -> !item.isVanilla()).toList()
        );
        buffer.writeJsonWithCodec(ClientModCustomContent.CODEC, customContent);

        // Remove the temporary client view
        org.fiddlemc.fiddle.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal.THREAD_LOCAL.remove();

    }

    public static void send(Connection connection) {
        connection.send(PACKET_INSTANCE);
    }

}
