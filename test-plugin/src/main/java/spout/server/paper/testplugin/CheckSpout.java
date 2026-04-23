package spout.server.paper.testplugin;


import java.util.logging.Logger;

public final class CheckSpout {

    private static final Logger LOGGER = Logger.getLogger(CheckSpout.class.getName());

    public static boolean checkSpout() {
        try {
            Class.forName("spout.server.paper.api.SpoutMarker");
            return true;
        } catch (ClassNotFoundException ignored) {
            LOGGER.warning("This plugin requires Spout: https://github.com/ModernSpout/Spout-Paper-server");
            return false;
        }
    }

}
