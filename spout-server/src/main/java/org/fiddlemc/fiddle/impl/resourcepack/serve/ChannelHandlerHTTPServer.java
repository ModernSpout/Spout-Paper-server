package org.fiddlemc.fiddle.impl.resourcepack.serve;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import net.kyori.adventure.key.Key;
import spout.common.branding.SpoutNamespace;
import java.nio.charset.StandardCharsets;

/**
 * Serves resource packs using a channel handler that hooks into the existing connection channel.
 *
 * @author Original by Alvin (https://github.com/Alvinn8), adapted by Martijn Muijsers
 */
public final class ChannelHandlerHTTPServer {

    private ChannelHandlerHTTPServer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Minimum number of bytes in {@code "GET /<resource pack path>"}.
     */
    public static final int MIN_PACKET_LENGTH;

    static {
        MIN_PACKET_LENGTH = 5 + Math.min(
            FiddleResourcePackServing.VANILLA_PACK_PATH.length(),
            FiddleResourcePackServing.CLIENT_MOD_PACK_PATH.length()
        );
        int index = 0;
        while (FiddleResourcePackServing.VANILLA_PACK_PATH.charAt(index) == FiddleResourcePackServing.CLIENT_MOD_PACK_PATH.charAt(index)) {
            index++;
        }

    }

    public static void inject() {
        ChannelInitializeListenerHolder.addListener(Key.key(SpoutNamespace.SPOUT, "channel_handler_http_server_add_listener"), channel -> {
            channel.pipeline().addFirst("channel_handler_http_server", new ChannelInboundHandlerAdapter() {

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (msg instanceof ByteBuf byteBuf) {
                        byteBuf.markReaderIndex();
                        try {
                            if (tryHandle(ctx, byteBuf)) {
                                return;
                            }
                        } catch (Exception ignored) {
                        }
                        byteBuf.resetReaderIndex();
                    }
                    // Let vanilla handle the message
                    super.channelRead(ctx, msg);
                }

            });
        });
    }

    private static boolean tryHandle(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (byteBuf.capacity() < MIN_PACKET_LENGTH) return false;

        // First, efficiently compare byte by byte as this is very hot code; it runs for every received packet

        if (byteBuf.readByte() != 'G') return false;
        if (byteBuf.readByte() != 'E') return false;
        if (byteBuf.readByte() != 'T') return false;
        if (byteBuf.readByte() != ' ') return false;
        if (byteBuf.readByte() != '/') return false;
        // An HTTP GET request was received, now check whether it is requesting a known file

        try {

            byte[] requestBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(requestBytes);
            String request = new String(requestBytes, StandardCharsets.UTF_8);
            String path = request.substring(0, request.indexOf(' '));

            byte[] dataToSend;
            if (path.equals(FiddleResourcePackServing.VANILLA_PACK_PATH)) {
                dataToSend = FiddleResourcePackServing.vanillaPackBytes;
            } else if (path.equals(FiddleResourcePackServing.CLIENT_MOD_PACK_PATH)) {
                dataToSend = FiddleResourcePackServing.clientModPackBytes;
            } else {
                // Not a known path
                ctx.close();
                return true;
            }

            // Determine the header
            String headerText =
                """
                    HTTP/1.1 200 OK
                    Server: Fiddle
                    Content-Type: application/zip
                    Content-Length: %d
                    
                    """.formatted(dataToSend.length);
            byte[] headerBytes = headerText.getBytes(StandardCharsets.UTF_8);

            // Create the response
            ByteBuf response = Unpooled.buffer(headerBytes.length + dataToSend.length);
            response.writeBytes(headerBytes);
            response.writeBytes(dataToSend);

            // Send the response
            ctx.pipeline().firstContext().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return true;

        } catch (Exception e) {
            // An exception occurred while processing a HTTP GET request, close the connection
            ctx.close();
            return true;
        }

    }

}
