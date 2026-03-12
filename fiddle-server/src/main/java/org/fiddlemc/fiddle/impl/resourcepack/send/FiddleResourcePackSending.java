package org.fiddlemc.fiddle.impl.resourcepack.send;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.network.ConfigurationTask;
import org.fiddlemc.fiddle.impl.configuration.FiddleGlobalConfiguration;
import org.fiddlemc.fiddle.impl.resourcepack.construct.FiddleResourcePackConstructionImpl;
import org.fiddlemc.fiddle.impl.resourcepack.serve.FiddleResourcePackServing;
import org.jspecify.annotations.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles resource pack sending.
 */
public final class FiddleResourcePackSending {

    private FiddleResourcePackSending() {
        throw new UnsupportedOperationException();
    }

    /**
     * The return value for {@link #getVanillaPacket()}.
     */
    private static @Nullable ClientboundResourcePackPushPacket vanillaPacket;

    /**
     * The return value for {@link #getClientModPacket()}.
     */
    private static @Nullable ClientboundResourcePackPushPacket clientModPacket;

    private static String sha1(byte[] pack) {
        try {
            return ByteSource.wrap(pack).hash(Hashing.sha1()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Exception while hashing generated resource pack contents", e);
        }
    }

    private static UUID uuid(String sha1) {
        return UUID.nameUUIDFromBytes(sha1.getBytes(StandardCharsets.UTF_8));
    }

    public static void initialize(byte[] vanillaPack, byte[] clientModPack) {
        FiddleResourcePackConstructionImpl construction = FiddleResourcePackConstructionImpl.get();
        if (!construction.isEnabled()) return;
        String vanillaPackSha1 = sha1(vanillaPack);
        UUID vanillaPackUUID = uuid(vanillaPackSha1);
        String clientModPackSha1 = sha1(clientModPack);
        UUID clientModPackUUID = uuid(clientModPackSha1);
        String vanillaURL;
        String clientModURL;
        if (FiddleResourcePackServing.isEnabled()) {
            String baseURL = "http://" + FiddleGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.ip + ":" + FiddleResourcePackServing.getPort()+ "/";
            vanillaURL = baseURL + FiddleResourcePackServing.VANILLA_PACK_PATH;
            clientModURL = baseURL + FiddleResourcePackServing.CLIENT_MOD_PACK_PATH;
        } else {
            throw new IllegalStateException(); // TODO allow using URL referring to some external service
        }
        vanillaPacket = new ClientboundResourcePackPushPacket(
            vanillaPackUUID,
            vanillaURL,
            vanillaPackSha1,
            false,
            Optional.of(Component.literal("\n")
                .append(Component.literal("\nThis server adds additional custom blocks and items"))
                .append(Component.literal("\nTo use them, click \"Yes\" below (Optional)")))
        );
        clientModPacket = new ClientboundResourcePackPushPacket(
            clientModPackUUID,
            clientModURL,
            clientModPackSha1,
            true,
            Optional.of(Component.literal("(This resource pack contains the textures for the custom blocks and items, and must be accepted"))
        );
    }

    /**
     * @return A {@link ClientboundResourcePackPushPacket} for sending the generated resource pack
     * meant for clients with a vanilla client, or null if not applicable.
     */
    public static @Nullable ClientboundResourcePackPushPacket getVanillaPacket() {
        return vanillaPacket;
    }

    /**
     * @return A {@link ClientboundResourcePackPushPacket} for sending the generated resource pack
     * meant for clients with the client mod, or null if not applicable.
     */
    public static @Nullable ClientboundResourcePackPushPacket getClientModPacket() {
        return clientModPacket;
    }

}
