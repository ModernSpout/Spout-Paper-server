package spout.server.paper.impl.configuration;

import com.mojang.logging.LogUtils;
import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.WorldConfiguration;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * The world configuration for Spout.
 *
 * <p>
 * Analogous to the Paper {@link WorldConfiguration}.
 * </p>
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "NotNullFieldNotInitialized", "InnerClassMayBeStatic"})
public class SpoutWorldConfiguration extends ConfigurationPart {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int CURRENT_VERSION = 1; // (when you change the version, change the comment, so it conflicts on rebases): initial version

    private transient final SpigotWorldConfig spigotConfig;
    private transient final Identifier worldKey;

    public SpoutWorldConfiguration(final SpigotWorldConfig spigotConfig, final Identifier worldKey) {
        this.spigotConfig = spigotConfig;
        this.worldKey = worldKey;
    }

    public boolean isDefault() {
        return this.worldKey.equals(SpoutConfigurations.WORLD_DEFAULTS_KEY);
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;
}
