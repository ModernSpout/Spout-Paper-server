package org.fiddlemc.fiddle.impl.resourcepack.serve;

import io.papermc.paper.configuration.type.number.IntOr;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.fiddlemc.fiddle.impl.configuration.FiddleGlobalConfiguration;
import org.fiddlemc.fiddle.impl.resourcepack.construct.FiddleResourcePackConstructionImpl;
import org.jspecify.annotations.Nullable;

/**
 * Handles the serving of the resource pack over HTTP.
 */
public final class FiddleResourcePackServing {

    private FiddleResourcePackServing() {
        throw new UnsupportedOperationException();
    }

    public static final String VANILLA_PACK_PATH = "resource_pack.zip";
    public static final String CLIENT_MOD_PACK_PATH = "mod_resource_pack.zip";

    public static byte @Nullable [] vanillaPackBytes;
    public static byte @Nullable [] clientModPackBytes;

    public static boolean isEnabled() {
        return FiddleResourcePackConstructionImpl.get().isEnabled() && FiddleGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.enabled;
    }

    private static int getServerPort() {
        int serverPort = MinecraftServer.getServer().getPort();
        if (serverPort >= 0) {
            return serverPort;
        }
        return ((DedicatedServer) MinecraftServer.getServer()).settings.getProperties().serverPort;
    }

    public static int getPort() {
        IntOr.Default configuredPort = FiddleGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.port;
        if (configuredPort.isDefined()) {
            return configuredPort.intValue();
        }
        return getServerPort();
    }

    public static void start(byte[] vanillaPack, byte[] clientModPack) {
        vanillaPackBytes = vanillaPack;
        clientModPackBytes = clientModPack;
        int port = getPort();
        if (port == getServerPort()) {
            ChannelHandlerHTTPServer.inject();
        } else {
            throw new IllegalStateException(); // TODO implement separate HTTP server
        }
    }

}
