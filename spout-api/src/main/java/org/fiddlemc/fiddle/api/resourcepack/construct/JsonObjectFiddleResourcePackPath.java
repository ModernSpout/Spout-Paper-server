package org.fiddlemc.fiddle.api.resourcepack.construct;

import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;
import java.util.function.Function;

/**
 * A {@link JsonObject} view of a {@link FiddleResourcePackPath}.
 */
public interface JsonObjectFiddleResourcePackPath extends FiddleResourcePackPath {

    /**
     * @return Whether {@link #exists()} is true and the contents of the file represent a valid {@link JsonObject}.
     */
    boolean isJsonObject();

    /**
     * @return The {@link JsonObject} in the file at this path.
     * You must never change the contents of the returned object.
     * @throws IllegalStateException If {@link #isJsonObject()} is false.
     */
    JsonObject getImmutable();

    /**
     * Sets the contents of this file to the given {@link JsonObject}.
     * The object may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param jsonObject A {@link JsonObject}.
     */
    void setMutable(JsonObject jsonObject);

    /**
     * Changes the {@link JsonObject} in this file.
     *
     * @param function A function from the old {@link JsonObject} to the new {@link JsonObject}.
     *                 The old object will be null if {@link #isJsonObject()} is false.
     *                 The new object is allowed to be the same instances as the old object,
     *                 i.e. you can modify the object in-place.
     *                 The new object can also be null, in which case the file will be deleted.
     */
    void update(Function<@Nullable JsonObject, @Nullable JsonObject> function);

    /**
     * Convenience method for {@link #setMutable(JsonObject)}.
     */
    void setParsedFromString(String json);

}
