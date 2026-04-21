package spout.server.paper.impl.configuration;

import com.mojang.logging.LogUtils;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.configuration.Configurations;
import io.papermc.paper.configuration.PaperConfigurations;
import io.papermc.paper.configuration.serializer.ServerboundPacketClassSerializer;
import io.papermc.paper.configuration.serializer.registry.RegistryValueSerializer;
import io.papermc.paper.configuration.transformation.Transformations;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import joptsimple.OptionSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.Services;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * The configurations for Spout. The instance can be set up before bootstrap,
 * so that the global configuration can be loaded before bootstrap.
 *
 * <p>
 * Analogous to the Paper {@link PaperConfigurations.OriginalPaperConfigurations}.
 * </p>
 */
@SuppressWarnings("Convert2Diamond")
public final class SpoutConfigurations extends PaperConfigurations<SpoutGlobalConfiguration, SpoutWorldConfiguration> {

    private static SpoutConfigurations INSTANCE;

    /**
     * @param optionSet The {@link OptionSet}, which must be non-null if the {@link #INSTANCE} needs to be created.
     */
    public static SpoutConfigurations get(@Nullable OptionSet optionSet) {
        if (INSTANCE == null) {
            if (optionSet == null) {
                throw new IllegalStateException();
            }
            final Path configDirPath = Services.getConfigDirPath(optionSet);
            try {
                INSTANCE = setup(configDirPath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return INSTANCE;
    }

    private static final Logger LOGGER = LogUtils.getClassLogger();

    private static final String SPOUT_WORLD_CONFIG_FILE_NAME = "spout-world.yml";

    private static final String SPOUT_GLOBAL_HEADER = String.format("""
        This is the global configuration file for Spout.
        
        If you need help with the configuration or have any questions related to Spout,
        join us in our Discord, or check the GitHub Wiki pages.
        
        The world configuration options are inside
        their respective world folder. The files are named %s
        
        Wiki: https://github.com/FiddleMC/Spout-Paper-server/wiki
        Discord: https://discord.gg/EduvcVmKS7""", SPOUT_WORLD_CONFIG_FILE_NAME);

    @Override
    protected String getWorldDefaultsHeader() {
        return """
            This is the world defaults configuration file for Spout.
            
            If you need help with the configuration or have any questions related to Spout,
            join us in our Discord, or check the GitHub Wiki pages.
            
            Configuration options here apply to all worlds, unless you specify overrides inside
            the world-specific config file inside each world folder.
            
            Wiki: https://github.com/FiddleMC/Spout-Paper-server/wiki
            Discord: https://discord.gg/EduvcVmKS7""";
    }

    @Override
    protected String getWorldHeader(ContextMap map) {
        return String.format("""
                This is a world configuration file for Spout.
                This file may start empty but can be filled with settings to override ones in the %s/%s
                
                World: %s (%s)""",
            PaperConfigurations.CONFIG_DIR,
            this.defaultWorldConfigFileName,
            map.require(WORLD_NAME),
            map.require(WORLD_KEY)
        );
    }

    @Override
    protected SpoutGlobalConfiguration getGlobalConfiguration() {
        return SpoutGlobalConfiguration.get();
    }

    @Override
    protected SpoutWorldConfiguration getWorldConfiguration(ServerLevel level) {
        return level.spoutConfig();
    }

    private SpoutConfigurations(final Path globalFolder) {
        super(globalFolder, SpoutGlobalConfiguration.class, SpoutWorldConfiguration.class, "spout");
    }

    @Override
    protected int globalConfigVersion() {
        return SpoutGlobalConfiguration.CURRENT_VERSION;
    }

    @Override
    protected int worldConfigVersion() {
        return SpoutWorldConfiguration.CURRENT_VERSION;
    }

    @Override
    protected YamlConfigurationLoader.Builder createGlobalLoaderBuilder(RegistryAccess registryAccess) {
        return super.createGlobalLoaderBuilder(registryAccess)
            .defaultOptions((options) -> defaultGlobalOptions(registryAccess, options));
    }

    private static ConfigurationOptions defaultGlobalOptions(RegistryAccess registryAccess, ConfigurationOptions options) {
        return options
            .header(SPOUT_GLOBAL_HEADER)
            .serializers(builder -> builder
                .register(new ServerboundPacketClassSerializer())
                .register(new RegistryValueSerializer<>(new TypeToken<DataComponentType<?>>() {
                }, registryAccess, Registries.DATA_COMPONENT_TYPE, false))
            );
    }

    @Override
    public SpoutGlobalConfiguration initializeGlobalConfiguration(final @Nullable RegistryAccess registryAccess) throws ConfigurateException {
        LOGGER.info("Initializing Spout global configuration...");
        SpoutGlobalConfiguration configuration = super.initializeGlobalConfiguration(registryAccess);
        SpoutGlobalConfiguration.set(configuration);
        return configuration;
    }

    @Override
    protected ContextMap.Builder createDefaultContextMap(final RegistryAccess registryAccess) {
        return super.createDefaultContextMap(registryAccess)
            .put(PaperConfigurations.SPIGOT_WORLD_CONFIG_CONTEXT_KEY, PaperConfigurations.SPIGOT_WORLD_DEFAULTS);
    }

    @Override
    protected SpoutWorldConfiguration createWorldConfigInstance(ContextMap contextMap) {
        return new SpoutWorldConfiguration(
            contextMap.require(PaperConfigurations.SPIGOT_WORLD_CONFIG_CONTEXT_KEY).get(),
            contextMap.require(Configurations.WORLD_KEY)
        );
    }

    @Override
    protected void applyWorldConfigTransformations(final ContextMap contextMap, final ConfigurationNode node, final @Nullable ConfigurationNode defaultsNode) throws ConfigurateException {
        // Not needed now
        // When needed in future due to backwards-incompatible configuration changes,
        // use PaperConfigurations#applyWorldConfigTransformations as template
    }

    @Override
    protected void applyGlobalConfigTransformations(ConfigurationNode node) throws ConfigurateException {
        // Not needed now
        // When needed in future due to backwards-incompatible configuration changes,
        // use PaperConfigurations#applyGlobalConfigTransformations as template
    }

    private static final List<Transformations.DefaultsAware> DEFAULT_AWARE_TRANSFORMATIONS = List.of(
    );

    @Override
    protected void applyDefaultsAwareWorldConfigTransformations(final ContextMap contextMap, final ConfigurationNode worldNode, final ConfigurationNode defaultsNode) throws ConfigurateException {
        final ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();
        // ADD FUTURE TRANSFORMS HERE (these transforms run after the defaults have been merged into the node)
        DEFAULT_AWARE_TRANSFORMATIONS.forEach(transform -> transform.apply(builder, contextMap, defaultsNode));
        builder.build().apply(worldNode);
    }

    public static SpoutConfigurations setup(final Path configDir) throws Exception {
        try {
            PaperConfigurations.createDirectoriesSymlinkAware(configDir);
            return new SpoutConfigurations(configDir);
        } catch (final IOException ex) {
            throw new RuntimeException("Could not setup " + SpoutConfigurations.class.getSimpleName(), ex);
        }
    }
}
