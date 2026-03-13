package org.fiddlemc.fiddle.impl.resourcepack.construct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackPath;
import org.jspecify.annotations.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * The implementation for {@link FiddleResourcePackPath}.
 */
public final class FiddleResourcePackPathImpl implements FiddleResourcePackPath {

    /**
     * The event that this instance was constructed for.
     */
    private final FiddleResourcePackConstructEventImpl event;

    /**
     * The string path this instance represents.
     */
    private final String path;

    /**
     * Whether a file at this path exists.
     */
    boolean exists;

    /**
     * The file contents as a byte array,
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    byte @Nullable [] bytes;

    /**
     * The file contents as a string,
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable String string;

    /**
     * The file contents as a {@link JsonElement},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable JsonElement jsonElement;

    /**
     * The file contents as a {@link JsonObject},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable JsonObject jsonObject;

    FiddleResourcePackPathImpl(FiddleResourcePackConstructEventImpl event, String path) {
        this.event = event;
        this.path = path;
    }

    @Override
    public boolean exists() {
        return this.exists;
    }

    @Override
    public void delete() {
        this.exists = false;
        this.clear();
    }

    private void setBytesFromContents() {
        if (this.string != null || this.jsonElement != null || this.jsonObject != null) {
            this.bytes = this.getStringImmutable().getBytes();
        }
    }

    @Override
    public byte[] getBytesImmutable() {
        this.throwExceptionIfNotExists();
        if (this.bytes == null) {
            // Find the contents and turn it into bytes
            this.setBytesFromContents();
        }
        return this.bytes;
    }

    @Override
    public void setBytesMutable(byte[] bytes) {
        this.exists = true;
        this.clear();
        this.bytes = bytes;
    }

    @Override
    public void updateBytes(Function<byte @Nullable [], byte @Nullable []> function) {
        byte @Nullable [] oldValue = this.exists ? this.getBytesImmutable() : null;
        byte @Nullable [] newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setBytesMutable(newValue);
        } else {
            this.delete();
        }
    }

    private void setStringFromContents() {
        if (this.bytes != null) {
            this.string = new String(this.bytes, StandardCharsets.UTF_8);
            return;
        }
        if (this.jsonElement != null || this.jsonObject != null) {
            this.string = this.getJsonElementImmutable().toString();
        }
    }

    @Override
    public String getStringImmutable() {
        this.throwExceptionIfNotExists();
        if (this.string == null) {
            // Find the contents and turn it into a string
            this.setStringFromContents();
        }
        return this.string;
    }

    @Override
    public void setStringMutable(String string) {
        this.exists = true;
        this.clear();
        this.string = string;
    }

    @Override
    public void updateString(Function<@Nullable String, @Nullable String> function) {
        @Nullable String oldValue = this.exists ? this.getStringImmutable() : null;
        @Nullable String newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setStringMutable(newValue);
        } else {
            this.delete();
        }
    }

    private void trySetJsonElementFromContents() {
        if (this.jsonObject != null) {
            this.jsonElement = this.jsonObject;
            return;
        }
        if (this.bytes != null || this.string != null) {
            try {
                this.jsonElement = JsonParser.parseString(this.getStringImmutable());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean isJsonElement() {
        if (this.jsonElement == null) {
            // Find the contents and turn it into a JsonElement
            this.trySetJsonElementFromContents();
        }
        return this.jsonElement != null;
    }

    @Override
    public JsonElement getJsonElementImmutable() {
        this.throwExceptionIfNot(this.isJsonElement(), "is not valid JSON");
        return this.jsonElement;
    }

    @Override
    public void setJsonElementMutable(JsonElement jsonElement) {
        this.exists = true;
        this.clear();
        this.jsonElement = jsonElement;
    }

    @Override
    public void updateJsonElement(Function<@Nullable JsonElement, @Nullable JsonElement> function) {
        @Nullable JsonElement oldValue = this.isJsonElement() ? this.getJsonElementImmutable() : null;
        @Nullable JsonElement newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setJsonElementMutable(newValue);
        } else {
            this.delete();
        }
    }

    @Override
    public void setJsonParsedFromString(String json) {
        this.setJsonElementMutable(JsonParser.parseString(json));
    }

    private void trySetJsonObjectFromContents() {
        if (this.bytes != null || this.string != null || this.jsonElement != null) {
            if (this.isJsonElement()) {
                try {
                    this.jsonObject = this.getJsonElementImmutable().getAsJsonObject();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public boolean isJsonObject() {
        if (this.jsonObject == null) {
            // Find the contents and turn it into a JsonObject
            this.trySetJsonObjectFromContents();
        }
        return this.jsonObject != null;
    }

    @Override
    public JsonObject getJsonObjectImmutable() {
        this.throwExceptionIfNot(this.isJsonObject(), "is not a valid JSON object");
        return this.jsonObject;
    }

    @Override
    public void setJsonObjectMutable(JsonObject jsonObject) {
        this.exists = true;
        this.clear();
        this.jsonObject = jsonObject;
    }

    @Override
    public void updateJsonObject(Function<@Nullable JsonObject, @Nullable JsonObject> function) {
        @Nullable JsonObject oldValue = this.isJsonObject() ? this.getJsonObjectImmutable() : null;
        @Nullable JsonObject newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setJsonObjectMutable(newValue);
        } else {
            this.delete();
        }
    }

    /**
     * @throws IllegalStateException If {@link #exists} is false.
     */
    private void throwExceptionIfNot(boolean value, String reason) {
        if (!value) {
            throw new IllegalStateException("File at this resource pack path " + reason + ": " + this.path);
        }
    }

    /**
     * @throws IllegalStateException If {@link #exists} is false.
     */
    private void throwExceptionIfNotExists() {
        this.throwExceptionIfNot(this.exists(), "does not exist");
    }

    /**
     * Sets all fields containing the data in this file to null.
     */
    private void clear() {
        this.bytes = null;
    }

}
