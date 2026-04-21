package org.fiddlemc.fiddle.impl.bukkit.enuminjection.material.match;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.impl.bukkit.enuminjection.KeyedSourceBukkitEnumSynchronizer;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The implementation of {@link MaterialByKeyLookup}.
 */
public final class MaterialByKeyLookupImpl implements MaterialByKeyLookup {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<MaterialByKeyLookup, MaterialByKeyLookupImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(MaterialByKeyLookupImpl.class);
        }

    }

    public static MaterialByKeyLookupImpl get() {
        return (MaterialByKeyLookupImpl) MaterialByKeyLookup.get();
    }

    /**
     * A backing map for {@link #lookupByStrictKey},
     * or null if not initialized yet.
     */
    private @Nullable Map<String, Material> byLowerCaseKeyMap;

    /**
     * A backing map used in {@link #lookupWithoutColon},
     * or null if not initialized yet.
     */
    private @Nullable Map<String, Material> byLowerCasePathMap;

    private MaterialByKeyLookupImpl() {
    }

    @Override
    public @Nullable Material lookup(Key key) {

        // First transform the key to lower-case
        String namespace = key.namespace();
        String path = key.value();
        String lowerCaseNamespace = namespace.toLowerCase(Locale.ROOT);
        String lowerCasePath = path.toLowerCase(Locale.ROOT);
        Key lowerCaseKey = !namespace.equals(lowerCaseNamespace) || !path.equals(lowerCasePath) ? Key.key(lowerCaseNamespace, lowerCasePath) : key;

        // If the namespace is non-default, we do a strict lookup
        if (!key.namespace().toLowerCase(Locale.ROOT).equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
            return this.lookupByStrictKey(lowerCaseKey.asString());
        }

        // Otherwise, perform a lookup for the default namespace
        return this.lookupForMinecraftNamespace(lowerCasePath);

    }

    @Override
    public @Nullable Material lookup(String keyString, boolean isLegacyName) {

        // Use the original implementation for legacy name lookup
        if (isLegacyName) {
            return this.lookupLegacyName(keyString);
        }

        // Get a lowercase version of the input
        String lowerCaseKeyString = keyString.toLowerCase(Locale.ROOT);

        // Check for errors in the input, in which case lookup fails
        if (keyString.isEmpty()) {
            // Empty strings do not allow for meaningful matching
            return null;
        }
        boolean hasNonUnderscore = false;
        int colonIndex = -1;
        for (int i = 0; i < lowerCaseKeyString.length(); i++) {
            char c = lowerCaseKeyString.charAt(i);
            if (c == '_') {
                // Underscores are allowed
                continue;
            }
            hasNonUnderscore = true;
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                // Alphanumeric characters are allowed
                continue;
            }
            if (c == ':') {
                // A key separator is allowed
                if (colonIndex != -1) {
                    // Multiple colons are not allowed
                    return null;
                }
                colonIndex = i;
                continue;
            }
            // Other characters are not allowed
            return null;
        }
        if (!hasNonUnderscore) {
            // Only underscores does not allow for meaningful matching
            return null;
        }
        // The input can be used for looking up

        // Split into two cases: with a colon and without a colon
        return colonIndex == -1 ? this.lookupWithoutColon(lowerCaseKeyString) : this.lookupWithColon(lowerCaseKeyString, colonIndex);

    }

    /**
     * Lookup for legacy names: the implementation is unchanged from the original
     * {@link Material#matchMaterial(String, boolean)}.
     */
    private Material lookupLegacyName(String name) {
        String filtered = name;
        if (filtered.startsWith(NamespacedKey.MINECRAFT + ":")) {
            filtered = filtered.substring((NamespacedKey.MINECRAFT + ":").length());
        }

        filtered = filtered.toUpperCase(Locale.ROOT);

        filtered = filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
        return Material.getMaterial(filtered, true);
    }

    /**
     * Auxiliary function for {@link #lookup(String, boolean)}, for input that contains no colon.
     */
    private @Nullable Material lookupWithoutColon(String lowerCaseString) {

        // Try it as a Minecraft key path
        @Nullable Material foundMaterial = this.lookupByStrictKey(NamespacedKey.MINECRAFT_NAMESPACE + ":" + lowerCaseString);
        if (foundMaterial != null) {
            return foundMaterial;
        }

        // Try it as an enum name
        String upperCaseString = lowerCaseString.toUpperCase(Locale.ROOT);
        foundMaterial = Material.valueOf(upperCaseString);
        if (foundMaterial != null) {
            return foundMaterial;
        }

        // Try it as a key path regardless of namespace
        if (this.byLowerCasePathMap == null) {
            Material[] materials = Material.values();
            this.byLowerCasePathMap = new HashMap<>(materials.length * 3);
            for (Material material : materials) {
                this.byLowerCaseKeyMap.putIfAbsent(material.getKey().getKey().toLowerCase(Locale.ROOT), material);
            }
        }
        foundMaterial = this.byLowerCasePathMap.get(lowerCaseString);
        if (foundMaterial != null) {
            return foundMaterial;
        }

        // Try it as a string without the Fiddle enum prefix
        if (lowerCaseString.startsWith(KeyedSourceBukkitEnumSynchronizer.DEFAULT_ENUM_PREFIX.toLowerCase(Locale.ROOT))) {
            if (lowerCaseString.length() > KeyedSourceBukkitEnumSynchronizer.DEFAULT_ENUM_PREFIX.length()) {
                foundMaterial = this.lookupWithoutColon(lowerCaseString.substring(KeyedSourceBukkitEnumSynchronizer.DEFAULT_ENUM_PREFIX.length()));
                if (foundMaterial != null) {
                    return foundMaterial;
                }
            }
        }

        // Try placing a key separator at an underscore
        char[] lowerCaseStringCharArray = lowerCaseString.toCharArray();
        for (int i = 1; i < lowerCaseStringCharArray.length - 1; i++) {
            if (lowerCaseStringCharArray[i] == '_') {
                lowerCaseStringCharArray[i] = ':';
                foundMaterial = this.lookup(new String(lowerCaseStringCharArray), false);
                if (foundMaterial != null) {
                    return foundMaterial;
                }
                lowerCaseStringCharArray[i] = '_';
            }
        }

        // Give up
        return null;

    }

    /**
     * Auxiliary function for {@link #lookup(String, boolean)}, for input that contains a colon.
     */
    private @Nullable Material lookupWithColon(String lowerCaseKeyString, int colonIndex) {

        // If the colon is at the end, there is nothing to look up
        if (colonIndex == lowerCaseKeyString.length() - 1) {
            return null;
        }

        // If the colon is at the start, treat the string as having no namespace
        if (colonIndex == 0) {
            return this.lookupWithoutColon(lowerCaseKeyString.substring(1));
        }

        // Get the namespace and path and check their validity
        String namespace = lowerCaseKeyString.substring(0, colonIndex);
        if (!this.isValidNamespacedKeyPartForLookup(namespace)) {
            return null;
        }
        String path = lowerCaseKeyString.substring(colonIndex + 1);
        if (!this.isValidNamespacedKeyPartForLookup(namespace)) {
            return null;
        }

        // If the namespace is not the default namespace, we assume the caller is looking for a specific key
        if (!namespace.equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
            return this.lookupByStrictKey(lowerCaseKeyString);
        }

        // Otherwise, perform a lookup for the default namespace
        return this.lookupForMinecraftNamespace(path);

    }

    /**
     * Auxiliary function for {@link #lookupWithColon}, that checks whether we can do a lookup
     * with the given part of a {@link NamespacedKey}. This assumes that the given string is lower-case
     * and only checks what isn't covered by other checks.
     */
    private boolean isValidNamespacedKeyPartForLookup(String lowerCaseKeyPart) {
        // We know that the part is lower-case and consists only of valid characters
        // Then, the only check necessary is to see if it is only underscores
        for (char c : lowerCaseKeyPart.toCharArray()) {
            if (c != '_') {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a lookup for which {@link Material} has the given key string
     * as their {@link Material#getKey()}, case-insensitive.
     */
    private @Nullable Material lookupByStrictKey(String lowerCaseKeyString) {
        if (this.byLowerCaseKeyMap == null) {
            Material[] materials = Material.values();
            this.byLowerCaseKeyMap = new HashMap<>(materials.length * 3);
            for (Material material : materials) {
                this.byLowerCaseKeyMap.put(material.getKey().asString().toLowerCase(Locale.ROOT), material);
            }
        }
        return this.byLowerCaseKeyMap.get(lowerCaseKeyString);
    }

    /**
     * Performs a flexible lookup for a key with the {@link NamespacedKey#MINECRAFT_NAMESPACE} namespace
     * and the given path.
     */
    private @Nullable Material lookupForMinecraftNamespace(String lowerCasePath) {
        // We treat it the same way as generic input that has no colon
        return this.lookupWithoutColon(lowerCasePath);
    }

}
