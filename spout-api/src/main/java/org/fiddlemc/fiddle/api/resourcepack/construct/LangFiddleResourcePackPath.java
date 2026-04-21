package org.fiddlemc.fiddle.api.resourcepack.construct;

import java.util.function.Function;
import org.fiddlemc.fiddle.api.resourcepack.content.Lang;
import org.jspecify.annotations.Nullable;

/**
 * A {@link Lang} view of a {@link FiddleResourcePackPath}.
 */
public interface LangFiddleResourcePackPath extends FiddleResourcePackPath {

    /**
     * @return Whether {@link #exists()} is true and the contents of the file represent a valid {@link Lang}.
     */
    boolean isLang();

    /**
     * @return The {@link Lang} in the file at this path.
     * You must never change the contents of the returned object.
     * @throws IllegalStateException If {@link #isLang()} is false.
     */
    Lang getImmutable();

    /**
     * Sets the contents of this file to the given {@link Lang}.
     * The object may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param lang A {@link Lang}.
     */
    void setMutable(Lang lang);

    /**
     * Changes the {@link Lang} in this file.
     *
     * @param function A function from the old {@link Lang} to the new {@link Lang}.
     *                 The old object will be null if {@link #isLang()} is false.
     *                 The new object is allowed to be the same instances as the old object,
     *                 i.e. you can modify the object in-place.
     *                 The new object can also be null, in which case the file will be deleted.
     */
    void update(Function<@Nullable Lang, @Nullable Lang> function);

}
