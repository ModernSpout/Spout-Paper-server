package org.fiddlemc.fiddle.api.resourcepack.construct;

import java.util.function.Function;
import com.google.gson.JsonElement;
import org.jspecify.annotations.Nullable;

/**
 * A {@link JsonElement} view of a {@link FiddleResourcePackPath}.
 */
public interface JsonElementFiddleResourcePackPath extends FiddleResourcePackPath {

    /**
     * @return Whether {@link #exists()} is true and the contents of the file represent a valid {@link JsonElement}.
     */
    boolean isJsonElement();

    /**
     * @return The {@link JsonElement} in the file at this path.
     * You must never change the contents of the returned element.
     * @throws IllegalStateException If {@link #isJsonElement()} is false.
     */
    JsonElement getImmutable();

    /**
     * Sets the contents of this file to the given {@link JsonElement}.
     * The element may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param jsonElement A {@link JsonElement}.
     */
    void setMutable(JsonElement jsonElement);

    /**
     * Changes the {@link JsonElement} in this file.
     *
     * @param function A function from the old {@link JsonElement} to the new {@link JsonElement}.
     *                 The old element will be null if {@link #isJsonElement()} is false.
     *                 The new element is allowed to be the same instances as the old element,
     *                 i.e. you can modify the element in-place.
     *                 The new element can also be null, in which case the file will be deleted.
     */
    void update(Function<@Nullable JsonElement, @Nullable JsonElement> function);

    /**
     * Convenience method for {@link #setMutable(JsonElement)}.
     */
    void setParsedFromString(String json);

}
