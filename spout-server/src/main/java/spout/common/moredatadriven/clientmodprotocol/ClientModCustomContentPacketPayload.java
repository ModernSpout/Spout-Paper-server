package spout.common.moredatadriven.clientmodprotocol;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import spout.common.branding.SpoutNamespace;
import java.nio.charset.StandardCharsets;

public final class ClientModCustomContentPacketPayload implements CustomPacketPayload {

    private static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "custom_content");
    private static final CustomPacketPayload.Type<ClientModCustomContentPacketPayload> TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, ClientModCustomContentPacketPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientModCustomContentPacketPayload::write, ClientModCustomContentPacketPayload::new);
    public static final CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, ?> TYPE_AND_CODEC = new CustomPacketPayload.TypeAndCodec<>(TYPE, ClientModCustomContentPacketPayload.STREAM_CODEC);

    private static final Gson GSON = new Gson();

    @Override
    public CustomPacketPayload.Type<ClientModCustomContentPacketPayload> type() {
        return TYPE;
    }

    private final Element[] elements;

    ClientModCustomContentPacketPayload(Element[] elements) {
        this.elements = elements;
    }

    ClientModCustomContentPacketPayload(FriendlyByteBuf buffer) {
        this.elements = new Element[buffer.readVarInt()];
        for (int i = 0; i < this.elements.length; i++) {
            this.elements[i] = new Element(buffer);
        }
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.elements.length);
        for (Element element : this.elements) {
            element.write(buffer);
        }
    }

    static class Element {

        static final Element END = new Element(Type.END);

        /**
         * The {@link Type} of this payload element.
         */
        private final Type type;

        /**
         * The content, {@linkplain Gson#toJson encoded} as a string,
         * then converted to {@link StandardCharsets#UTF_8} bytes.
         * or null if the current {@link #type} is {@link Type#END}.
         */
        final byte @Nullable [] content;

        Element(Type type, JsonElement content) {
            this(type, GSON.toJson(content));
        }

        Element(Type type, String content) {
            this(type, content.getBytes(StandardCharsets.UTF_8));
        }

        Element(Type type, byte[] content) {
            this.type = type;
            this.content = content;
        }

        private Element(Type type) {
            this.type = type;
            this.content = null;
        }

        Element(FriendlyByteBuf buffer) {
            // Read the type
            this.type = Type.VALUES[buffer.readByte()];
            // Read the content
            this.content = this.type == Type.END ? null : buffer.readByteArray();
        }

        private void write(FriendlyByteBuf buffer) {
            // Write the type
            buffer.writeByte(this.type.ordinal());
            // Write the content
            if (this.content != null) {
                buffer.writeByteArray(this.content);
            }
        }

        String getContentAsString() {
            return new String(this.content, StandardCharsets.UTF_8);
        }

        JsonElement getContentAsJsonElement() {
            return GSON.fromJson(this.getContentAsString(), JsonElement.class);
        }

        /**
         * The content type for an element.
         */
        public enum Type {

            /**
             * A custom block.
             */
            BLOCK,
            /**
             * A custom item.
             */
            ITEM,
            /**
             * A special type indicating the end of the custom content.
             */
            END;

            private static final Type[] VALUES = values();

        }

    }

}
