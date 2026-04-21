package spout.server.paper.impl.configuration;

import com.mojang.logging.LogUtils;
import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.GlobalConfiguration;
import io.papermc.paper.configuration.type.number.IntOr;
import org.slf4j.Logger;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import java.util.List;

/**
 * The global configuration for Spout.
 *
 * <p>
 * Analogous to the Paper {@link GlobalConfiguration}.
 * </p>
 */
@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "NotNullFieldNotInitialized", "InnerClassMayBeStatic"})
public class SpoutGlobalConfiguration extends ConfigurationPart {
    private static final Logger LOGGER = LogUtils.getClassLogger();
    static final int CURRENT_VERSION = 1; // (when you change the version, change the comment, so it conflicts on rebases): initial version
    private static SpoutGlobalConfiguration instance;

    public static SpoutGlobalConfiguration get() {
        return instance;
    }

    static void set(final SpoutGlobalConfiguration instance) {
        SpoutGlobalConfiguration.instance = instance;
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;

    public boolean _enabled = false;

    public GeneratedResourcePack generatedResourcePack;

    public class GeneratedResourcePack extends ConfigurationPart {

        public Output output;

        public class Output extends ConfigurationPart {

            public ServeOverHttp serveOverHttp;

            public class ServeOverHttp extends ConfigurationPart {

                /**
                 * Whether to serve the resource pack over HTTP.
                 */
                public boolean enabled = true;

                public String ip = "localhost";

                /**
                 * The port to serve the resource pack over.
                 *
                 * <p>
                 * This is allowed to be the same as the server port.
                 * By default, it is the server port.
                 * </p>
                 */
                public IntOr.Default port = IntOr.Default.USE_DEFAULT;

                /**
                 * The external port at which the resource pack will be reachable.
                 * This is useful when the resource pack is served through a port-changing proxy.
                 *
                 * <p>
                 * By default, it is the same as {@link #port}.
                 * </p>
                 */
                public IntOr.Default externalPort = IntOr.Default.USE_DEFAULT;

                // public boolean keepPackInMemory = false; // TODO implement option (currently always true)

            }

            // public SaveToFile saveToFile;
            //
            // public class SaveToFile extends ConfigurationPart {
            //
            // } // TODO implement

        }

    }

    public Tooltips tooltips;

    public class Tooltips extends ConfigurationPart {

        public Items items;

        public class Items extends ConfigurationPart {

            public boolean namespace = false;

        }

    }

    public ServerSideTranslations serverSideTranslations;

    public class ServerSideTranslations extends ConfigurationPart {

        public List<String> preferredLocalesInOrder = List.of("en_us");

    }

}
