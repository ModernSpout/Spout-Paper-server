package org.fiddlemc.fiddle.api.resourcepack.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * The contents of a {@code lang} file in a resource pack.
 */
public final class Lang {

    private final JsonObject json;

    private Lang(JsonObject json) {
        this.json = json;
    }

    public void putTranslation(String key, String value) {
        this.json.addProperty(key, value);
    }

    public void removeTranslation(String key, String value) {
        this.json.remove(key);
    }

    public List<Pair<String, String>> getTranslations() {
        return this.json.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue().getAsString())).toList();
    }

    public @Nullable String getTranslation(String key) {
        @Nullable JsonElement value = this.json.get(key);
        return value == null ? null : value.getAsString();
    }

    public JsonObject getJson() {
        return this.json;
    }

    @Override
    public int hashCode() {
        return this.json.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lang other && this.json.equals(other.json);
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

    public static Lang wrapCopyOf(JsonObject jsonObject) {
        return new Lang(jsonObject.deepCopy());
    }

    public static Lang wrap(JsonObject jsonObject) {
        return new Lang(jsonObject);
    }

    public static Lang create() {
        return new Lang(new JsonObject());
    }

}
