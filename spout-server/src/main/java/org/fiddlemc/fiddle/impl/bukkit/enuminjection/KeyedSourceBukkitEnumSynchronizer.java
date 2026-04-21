package org.fiddlemc.fiddle.impl.bukkit.enuminjection;

import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamePickFunctionHandle;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamesComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.impl.util.java.enuminjection.EnumInjector;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SingleStepMappingPipeline;

/**
 * A {@link BukkitEnumSynchronizer} for source values that have an associated {@link NamespacedKey}.
 */
public abstract class KeyedSourceBukkitEnumSynchronizer<E extends Enum<E>, S, I extends EnumInjector<E>, P extends BukkitEnumNameMappingPipelineImpl<S> & SingleStepMappingPipeline<String, BukkitEnumNamePickFunctionHandle<S>>> extends BukkitEnumSynchronizer<E, S, I, P> {

    public static final String DEFAULT_ENUM_PREFIX = "FIDDLE_";

    /**
     * @return The {@link NamespacedKey} for the given source value.
     */
    protected abstract NamespacedKey getKey(S sourceValue);

    @Override
    protected String getInitialEnumName(S sourceValue) throws EnumNameMappingException {
        // Get the key
        NamespacedKey key = this.getKey(sourceValue);
        // Check if the key is acceptable
        this.checkAcceptableNamespacedKey(key);
        // Map the key to the enum name
        return DEFAULT_ENUM_PREFIX + this.mapNamespacedKeyPartToEnumPart(key.namespace()) + "_" + this.mapNamespacedKeyPartToEnumPart(key.getKey());
    }

    /**
     * @param key A {@link NamespacedKey} to be mapped to an enum name.
     * @throws EnumNameMappingException If the given {@code key} cannot be mapped to an acceptable enum name.
     */
    protected void checkAcceptableNamespacedKey(NamespacedKey key) throws EnumNameMappingException {
        this.checkAcceptableNamespacedKeyPart(key, key.namespace());
        this.checkAcceptableNamespacedKeyPart(key, key.getKey());
    }

    /**
     * @param key  A {@link NamespacedKey} to be mapped to an enum name.
     * @param part A part of the {@code key}: the namespace or the path.
     * @throws EnumNameMappingException If the given {@code part} makes it impossible to map
     *                                  the given {@code key} to an acceptable enum name.
     */
    protected void checkAcceptableNamespacedKeyPart(NamespacedKey key, String part) throws EnumNameMappingException {
        if (part.isEmpty()) {
            throw new EnumNameMappingException("A key (" + key + ") contains an empty part, which is not allowed by Minecraft");
        }
        boolean hasNonUnderscore = false;
        for (char c : part.toCharArray()) {
            if (c == '_') {
                // Underscores are allowed
                continue;
            }
            hasNonUnderscore = true;
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                // Lowercase alphanumeric characters are allowed
                continue;
            }
            if (c == '.' || c == '-' || c == '/') {
                throw new EnumNameMappingException("A key (" + key + ") contains a " + c + " character, which is technically allowed by Minecraft, but not by Fiddle");
            }
            throw new EnumNameMappingException("A key (" + key + ") contains a " + c + " character, which is not allowed by Minecraft");
        }
        if (!hasNonUnderscore) {
            throw new EnumNameMappingException("A key (" + key + ") contains a part with only underscores, which is technically allowed by Minecraft, but not by Fiddle");
        }
        // The part is acceptable
    }

    /**
     * Maps a {@link NamespacedKey} namespaced or path to a fitting enum name part.
     *
     * <p>
     * This method assumes that the part is {@linkplain #checkAcceptableNamespacedKeyPart acceptable}.
     * </p>
     *
     * @return The enum name part.
     */
    protected String mapNamespacedKeyPartToEnumPart(String part) {
        return part.toUpperCase(Locale.ROOT);
    }

}
