package spout.common.moredatadriven.clientmodprotocol;

import net.minecraft.network.Connection;
import net.minecraft.network.VarInt;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.impl.clientview.JavaWithClientModClientViewImpl;
import spout.server.paper.impl.clientview.lookup.ClientViewLookup;
import spout.server.paper.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import spout.server.paper.impl.moredatadriven.minecraft.ItemRegistry;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Collects all custom content to send to client mods.
 */
public final class ClientModCustomContentSending {

    private ClientModCustomContentSending() {
        throw new UnsupportedOperationException();
    }

    /**
     * The packets containing the payloads (each of type {@link ClientModCustomContentPacketPayload}),
     * or null if not initialized yet.
     */
    private static ClientboundCustomPayloadPacket @Nullable [] packets;

    /**
     * A {@link CountDownLatch} guarding the initialization of {@link #packets}.
     */
    private static final CountDownLatch packetsInitializationLatch = new CountDownLatch(1);

    private static void initializePackets() {

        // Look up the custom content to send
        List<Block> blocks = BlockRegistry.get().stream().filter(block -> !block.isVanilla()).toList();
        List<Item> items = ItemRegistry.get().stream().filter(item -> !item.isVanilla()).toList();

        // Set a client mod client view to prevent any mapping
        ClientViewLookupThreadLocal.THREAD_LOCAL.set(new WeakReference<>(new ClientViewLookup() {

            @Override
            public ClientView getClientView() {
                return new JavaWithClientModClientViewImpl(null);
            }

        }));

        // Create the content instance
        ClientModCustomContent customContent = ClientModCustomContent.create(blocks, items);

        // Remove the temporary client view
        ClientViewLookupThreadLocal.THREAD_LOCAL.remove();

        // Create the payloads
        class FillPayloadsHelper {

            static final int MAX_PAYLOAD_SIZE = 80000;

            static List<ClientModCustomContentPacketPayload> payloads = new ArrayList<>();
            static List<ClientModCustomContentPacketPayload.Element> nextPayloadElements = new ArrayList<>();
            static int nextPayloadSize = 0;

            static void addElement(ClientModCustomContentPacketPayload.Element element) {
                int elementSize = 1 + (element.content == null ? 0 : VarInt.getByteSize(element.content.length) + element.content.length);
                if (nextPayloadSize + elementSize <= MAX_PAYLOAD_SIZE || nextPayloadSize == 0) {
                    nextPayloadElements.add(element);
                    nextPayloadSize += elementSize;
                } else {
                    finishCurrentPayload();
                    addElement(element);
                }
            }

            static void finishCurrentPayload() {
                if (!nextPayloadElements.isEmpty()) {
                    payloads.add(new ClientModCustomContentPacketPayload(nextPayloadElements.toArray(ClientModCustomContentPacketPayload.Element[]::new)));
                    nextPayloadElements.clear();
                    nextPayloadSize = 0;
                }
            }

        }
        customContent.blockJSONs.forEach(json -> FillPayloadsHelper.addElement(new ClientModCustomContentPacketPayload.Element(ClientModCustomContentPacketPayload.Element.Type.BLOCK, json)));
        customContent.itemJSONs.forEach(json -> FillPayloadsHelper.addElement(new ClientModCustomContentPacketPayload.Element(ClientModCustomContentPacketPayload.Element.Type.ITEM, json)));
        FillPayloadsHelper.addElement(ClientModCustomContentPacketPayload.Element.END);
        FillPayloadsHelper.finishCurrentPayload();

        // Fill the packets array
        packets = FillPayloadsHelper.payloads.stream().map(ClientboundCustomPayloadPacket::new).toArray(ClientboundCustomPayloadPacket[]::new);

    }

    private static void initializePacketsIfNecessary() {
        if (packetsInitializationLatch.getCount() == 0) {
            // Skip, already initialized and visible
        }
        // Run the initialization on the current thread, or wait for another thread to complete it
        synchronized (ClientModCustomContentPacketPayload.class) {
            if (packetsInitializationLatch.getCount() == 1) {
                try {
                    // Initialize
                    initializePackets();
                } finally {
                    packetsInitializationLatch.countDown();
                }
            }
        }
        // Wait for the initialization to complete
        try {
            packetsInitializationLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendAllPackets(Connection connection) {
        initializePacketsIfNecessary();
        for (ClientboundCustomPayloadPacket packet : packets) {
            connection.send(packet);
        }
    }

}
