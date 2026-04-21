package org.fiddlemc.fiddle.impl.bukkit.enuminjection;

import com.mojang.logging.LogUtils;
import net.minecraft.server.dedicated.DedicatedServer;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamePickFunctionHandle;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNames;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamesComposeEvent;
import org.fiddlemc.fiddle.impl.util.java.enuminjection.EnumInjector;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SingleStepMappingPipeline;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import java.util.List;

/**
 * A class that injects new values into a Bukkit enum {@link E}
 * that correspond to values from another source (of type {@link S}).
 *
 * <p>
 * To apply the synchronization, call {@link #run}.
 * </p>
 */
public abstract class BukkitEnumSynchronizer<E extends Enum<E>, S, I extends EnumInjector<E>, P extends BukkitEnumNameMappingPipelineImpl<S> & SingleStepMappingPipeline<String, BukkitEnumNamePickFunctionHandle<S>>> {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The {@link EnumInjector} used by this synchronizer,
     * or null if not initialized yet.
     */
    private @Nullable I injector;

    /**
     * @return The {@link BukkitEnumNames} used by this synchronizer.
     */
    protected abstract P getNameMappingPipeline();

    /**
     * Acquires the injector used by this synchronizer.
     *
     * @return The desired return value of {@link #getInjector()}.
     * @throws Exception If something unexpected goes wrong.
     */
    protected abstract I createInjector() throws Exception;

    /**
     * @return The {@link EnumInjector} used by this synchronizer.
     * @throws Exception If something unexpected goes wrong.
     */
    protected I getInjector() throws Exception {
        if (this.injector == null) {
            this.injector = createInjector();
        }
        return this.injector;
    }

    /**
     * @return The source values that new values must be injected for.
     */
    protected abstract List<S> getSourceValues();

    /**
     * Stages an enum value for the given name and source value.
     *
     * @param enumName    The {@link Enum#name()} for the new value.
     * @param sourceValue The source value for which this new value is being injected.
     * @throws Exception If something unexpected goes wrong.
     */
    protected abstract void stage(String enumName, S sourceValue) throws Exception;

    /**
     * @param sourceValue A source value.
     * @return The desired {@link Enum#name()} for the corresponding new value,
     * before any pipeline mappings are applied.
     * While not generally enforced, this should only consist of uppercase letters, digits and underscores.
     */
    protected abstract String getInitialEnumName(S sourceValue) throws EnumNameMappingException;

    /**
     * @param sourceValue A source value.
     * @return The desired {@link Enum#name()} for the corresponding new value.
     * While not generally enforced, this should only consist of uppercase letters, digits and underscores.
     * @throws EnumNameMappingException When the source value cannot be mapped to an acceptable enum name.
     */
    protected String determineEnumName(S sourceValue) throws EnumNameMappingException {
        // Get the original name
        String originalName = this.getInitialEnumName(sourceValue);
        // Apply the mappings
        return this.getNameMappingPipeline().apply(new BukkitEnumNameMappingHandleImpl<>(originalName, sourceValue));
    }

    /**
     * @param enumName A string being considered for an enum value's {@link Enum#name()}.
     * @throws EnumNameMappingException If the given string is not an acceptable enum name.
     */
    protected void checkAcceptableEnumName(String enumName) throws EnumNameMappingException {
        if (enumName.isEmpty()) {
            throw new EnumNameMappingException("An enum name is empty, which is not allowed by Java");
        }
        boolean hasNonUnderscore = false;
        for (char c : enumName.toCharArray()) {
            if (c == '_') {
                // Underscores are allowed
                continue;
            }
            hasNonUnderscore = true;
            if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                // Uppercase alphanumeric characters are allowed
                continue;
            }
            throw new EnumNameMappingException("An enum name (" + enumName + ") contains a " + c + " character, which is not allowed by Fiddle");
        }
        if (!hasNonUnderscore) {
            throw new EnumNameMappingException("An enum name (" + enumName + ") contains only underscores, which is not allowed by Java");
        }
        // The enum name is acceptable
    }

    /**
     * Adds the new values into the enum.
     *
     * @throws EnumNameMappingException If a source value cannot be mapped to an acceptable enum name.
     * @throws Exception                If something unexpected goes wrong.
     */
    public void run() throws EnumNameMappingException, Exception {

        // Get the source values
        List<S> sourceValues = this.getSourceValues();

        // Skip if there is nothing to synchronize
        if (sourceValues.isEmpty()) {
            return;
        }

        // Stage the new values
        for (S sourceValue : sourceValues) {
            // Determine the enum name
            String enumName = this.determineEnumName(sourceValue);
            // Verify sure the resulting enum name is valid
            this.checkAcceptableEnumName(enumName);
            // Stage the new value
            this.stage(enumName, sourceValue);
        }

        // Commit the new values
        this.injector.commit();

    }

    /**
     * A convenience method to {@link #run} this synchronizer from {@link DedicatedServer#initServer}.
     *
     * @return True if {@link #run} was successful, false otherwise.
     * @throws IllegalStateException If running was unsuccessful for an unexpected reason.
     */
    public boolean runFromDedicatedServerInit() throws IllegalStateException {
        try {
            this.run();
            // Success
            return true;
        } catch (EnumNameMappingException e) {
            LOGGER.warn("Failed to extend Bukkit Material enum: {}", e.getMessage());
            // Don't start the server with an incomplete Material enum
            return false;
        } catch (Exception e) {
            // Don't start the server with an incomplete Material enum
            throw new IllegalStateException("Failed to extend Bukkit Material enum", e);
        }
    }

    /**
     * An exception that can be thrown when mapping to an acceptable enum name is not possible.
     */
    public static class EnumNameMappingException extends Exception {

        EnumNameMappingException(String message) {
            super(message);
        }

    }

}
