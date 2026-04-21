package spout.common.branding;

import org.bukkit.NamespacedKey;

/**
 * Holds the {@link #SPOUT} namespace.
 */
public final class SpoutNamespace {

    private SpoutNamespace() {
        throw new UnsupportedOperationException();
    }

    /**
      * The namespace for Spout {@link NamespacedKey}s.
     *
     * <p>
      * This is for {@link NamespacedKey}s that are defined by and belong to Spout itself,
      * not those of content added by Spout plugins (those plugins should use their own namespaces).
     * </p>
      */
    public static final String SPOUT = "fiddle";

}
