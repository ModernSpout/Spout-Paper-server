package spout.server.paper.impl.confirmpermanence;

import org.slf4j.Logger;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;

/**
 * Utility class that checks whether the user has understood
 * the permanence of running Spout.
 */
public final class ConfirmPermanence {

    private ConfirmPermanence() {
        throw new UnsupportedOperationException();
    }

    public static boolean check(Logger logger) {
        if (SpoutGlobalConfiguration.get()._enabled) {
            return true;
        }
        if (Boolean.getBoolean("spout.server.paper.enabled")) {
            logger.warn("You have used the command line flag to enable Spout.");
            logger.warn("The '_enabled' setting in config/spout-global.yml will be ignored.");
            logger.warn("If you do not wish to run Spout, please stop your server and set it to false immediately.");
            return true;
        }
        logger.warn("");
        logger.warn("********************************************************************************");
        logger.warn("You must confirm that you wish to run Spout in this folder.");
        logger.warn("");
        logger.warn("Spout allows you to add new blocks and items. These are saved into your world");
        logger.warn("and plugin data, meaning the world and plugin data is affected in the same way");
        logger.warn("as on a modded server. Loading a world in Spout is therefore PERMANENT.");
        logger.warn("");
        logger.warn("DO NOT RUN SPOUT ON A SERVER IF YOU DO NOT WANT THE WORLD AND PLUGIN DATA TO BE");
        logger.warn("PERMANENTLY AFFECTED.");
        logger.warn("");
        logger.warn("Make sure to back up any world and plugin data before enabling Spout.");
        logger.warn("");
        logger.warn("To confirm, open config/spout-global.yml and change '_enabled' to true.");
        logger.warn("By changing '_enabled' to true, you are indicating that you understand that");
        logger.warn("the server world and plugin data will become modded PERMANENTLY.");
        logger.warn("********************************************************************************");
        logger.warn("");
        return false;
    }

}
