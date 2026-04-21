package org.fiddlemc.fiddle.impl.bukkit.enuminjection.material.match;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import org.jspecify.annotations.Nullable;
import java.util.ServiceLoader;

/**
 * Provides a flexible lookup from a {@link NamespacedKey} to a {@link org.bukkit.Material}.
 */
public interface MaterialByKeyLookup {

    /**
     * An internal interface to get the {@link MaterialByKeyLookup} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<MaterialByKeyLookup> {
    }

    /**
     * @return The {@link MaterialByKeyLookup} instance.
     */
    static MaterialByKeyLookup get() {
        return ServiceLoader.load(MaterialByKeyLookup.ServiceProvider.class, MaterialByKeyLookup.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    /**
     * Performs a lookup for a {@link Material} for a given key.
     *
     * <p>
     * The check for whether a key matches allows for some assumptions,
     * such as every enum name being the uppercase version of the {@link NamespacedKey#getKey()},
     * or {@link NamespacedKey#getNamespace()} always being {@link NamespacedKey#MINECRAFT_NAMESPACE}.
     * </p>
     *
     * @param key A {@linkplain Key namespaced key}.
     * @return The {@link Material} that best matches the given key, or null if none could be found.
     */
    @Nullable Material lookup(Key key);

    default @Nullable Material lookup(String key) {
        return this.lookup(key, false);
    }

    /**
     * Performs a lookup for a {@link Material} for a given string that is assumed to represent
     * a {@link NamespacedKey} in some way.
     *
     * <p>
     * The check for whether the string matches allows for some flexibility,
     * such as that the string is only the {@link NamespacedKey#getKey()} part of a {@link NamespacedKey}.
     * </p>
     *
     * @param keyString    The string to perform the lookup on.
     * @param isLegacyName Whether the given string is a legacy enum name
     *                     (see {@link Material#matchMaterial(String, boolean)}).
     * @return The {@link Material} that best matches the given string, or null if none could be found.
     */
    @Nullable Material lookup(String keyString, boolean isLegacyName);

}
