package org.fiddlemc.fiddle.api.resourcepack.construct;

import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/**
 * A string view of a {@link FiddleResourcePackPath}.
 */
public interface StringFiddleResourcePackPath extends FiddleResourcePackPath {

    /**
     * @return The string in the file at this path.
     * @throws IllegalStateException If {@link #exists()} is false.
     */
    String get();

    /**
     * Sets the contents of this file to the given string.
     *
     * @param string A string.
     */
    void set(String string);

    /**
     * Changes the string in this file.
     *
     * @param function A function from the old string to the new string.
     *                 The old string will be null if {@link #exists()} is false.
     *                 The new string is allowed to be the same instances as the old string.
     *                 The new string can also be null, in which case the file will be deleted.
     */
    void update(Function<@Nullable String, @Nullable String> function);

}
