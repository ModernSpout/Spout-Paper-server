package org.fiddlemc.fiddle.api.resourcepack.construct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;
import java.util.function.Function;

/**
 * A path in the Fiddle server resource pack.
 */
public interface FiddleResourcePackPath {

    /**
     * @return Whether a file exists at this path.
     */
    boolean exists();

    /**
     * Deletes the file at this path, if it existed.
     */
    void delete();

    /**
     * @return The bytes in the file at this path.
     * You must never change the contents of the returned byte array.
     * @throws IllegalStateException If {@link #exists()} is false.
     */
    byte[] getBytesImmutable();

    /**
     * Sets the contents of this file to the given bytes.
     * The byte array may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param bytes A byte array.
     */
    void setBytesMutable(byte[] bytes);

    /**
     * Changes the bytes in this file.
     *
     * @param function A function from the old bytes to the new bytes.
     *                 The old bytes will be null if {@link #exists()} is false.
     *                 The new bytes is allowed to be the same instances as the old bytes,
     *                 i.e. you can modify the bytes in-place.
     *                 The new bytes can also be null, in which case the file will be deleted.
     */
    void updateBytes(Function<byte @Nullable [], byte @Nullable []> function);

    /**
     * @return The string in the file at this path.
     * @throws IllegalStateException If {@link #exists()} is false.
     */
    String getStringImmutable();

    /**
     * Sets the contents of this file to the given string.
     *
     * @param string A string.
     */
    void setStringMutable(String string);

    /**
     * Changes the string in this file.
     *
     * @param function A function from the old string to the new string.
     *                 The old string will be null if {@link #exists()} is false.
     *                 The new string is allowed to be the same instances as the old string.
     *                 The new string can also be null, in which case the file will be deleted.
     */
    void updateString(Function<@Nullable String, @Nullable String> function);

    /**
     * @return Whether {@link #exists()} is true and the contents of the file represent a valid {@link JsonElement}.
     */
    boolean isJsonElement();

    /**
     * @return The {@link JsonElement} in the file at this path.
     * You must never change the contents of the returned element.
     * @throws IllegalStateException If {@link #isJsonElement()} is false.
     */
    JsonElement getJsonElementImmutable();

    /**
     * Sets the contents of this file to the given {@link JsonElement}.
     * The element may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param jsonElement A {@link JsonElement}.
     */
    void setJsonElementMutable(JsonElement jsonElement);

    /**
     * Changes the {@link JsonElement} in this file.
     *
     * @param function A function from the old {@link JsonElement} to the new {@link JsonElement}.
     *                 The old element will be null if {@link #isJsonElement()} is false.
     *                 The new element is allowed to be the same instances as the old element,
     *                 i.e. you can modify the element in-place.
     *                 The new element can also be null, in which case the file will be deleted.
     */
    void updateJsonElement(Function<@Nullable JsonElement, @Nullable JsonElement> function);

    /**
     * Convenience method for {@link #setJsonElementMutable(JsonElement)}.
     */
    void setJsonParsedFromString(String json);

    /**
     * @return Whether {@link #exists()} is true and the contents of the file represent a valid {@link JsonObject}.
     */
    boolean isJsonObject();

    /**
     * @return The {@link JsonObject} in the file at this path.
     * You must never change the contents of the returned object.
     * @throws IllegalStateException If {@link #isJsonObject()} is false.
     */
    JsonObject getJsonObjectImmutable();

    /**
     * Sets the contents of this file to the given {@link JsonObject}.
     * The object may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param jsonObject A {@link JsonObject}.
     */
    void setJsonObjectMutable(JsonObject jsonObject);

    /**
     * Changes the {@link JsonObject} in this file.
     *
     * @param function A function from the old {@link JsonObject} to the new {@link JsonObject}.
     *                 The old object will be null if {@link #isJsonObject()} is false.
     *                 The new object is allowed to be the same instances as the old object,
     *                 i.e. you can modify the object in-place.
     *                 The new object can also be null, in which case the file will be deleted.
     */
    void updateJsonObject(Function<@Nullable JsonObject, @Nullable JsonObject> function);

}
