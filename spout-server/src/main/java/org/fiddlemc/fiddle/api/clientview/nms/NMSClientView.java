package org.fiddlemc.fiddle.api.clientview.nms;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.jspecify.annotations.Nullable;
import java.util.Locale;

/**
 * An extension of the {@link ClientView} interface that adds methods to get Minecraft internals.
 *
 * <p>
 * Every instance of {@link ClientView} is also an instance of {@link NMSClientView}.
 * </p>
 */
public interface NMSClientView extends ClientView {

    /**
     * @return The {@link Connection} of this client,
     * or null if not available.
     *
     * <p>
     * The connection (if present) instance stays the same during the entire connection session of a client.
     * </p>
     */
    @Nullable Connection getConnection();

    /**
     * @return The {@link ServerPlayer} of this client,
     * or null if not available (for example when the client is still in the configuration phase).
     */
    default @Nullable ServerPlayer getNMSPlayer() {
        @Nullable Connection connection = this.getConnection();
        return connection == null ? null : connection.getPlayer();
    }

    @Override
    default @Nullable Player getPlayer() {
        @Nullable ServerPlayer nmsPlayer = this.getNMSPlayer();
        return nmsPlayer == null ? null : nmsPlayer.getBukkitEntity();
    }

    @Override
    default @Nullable String getLocale() {
        @Nullable ServerPlayer nmsPlayer = this.getNMSPlayer();
        return nmsPlayer == null ? null : nmsPlayer.language.toLowerCase(Locale.ROOT);
    }

}
