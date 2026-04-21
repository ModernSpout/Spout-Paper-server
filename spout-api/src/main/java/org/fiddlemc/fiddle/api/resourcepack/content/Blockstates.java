package org.fiddlemc.fiddle.api.resourcepack.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * The contents of a {@code blockstates} file in a resource pack.
 */
public final class Blockstates {

    private final JsonObject json;

    private Blockstates(JsonObject json) {
        this.json = json;
    }

    private @Nullable JsonObject getVariantsJsonOrNull() {
        return this.json.getAsJsonObject("variants");
    }

    private JsonObject getOrCreateVariantsJson() {
        @Nullable JsonObject variants = this.getVariantsJsonOrNull();
        if (variants == null) {
            variants = new JsonObject();
            this.json.add("variants", variants);
        }
        return variants;
    }

    private @Nullable JsonObject getVariantJsonOrNull(String variantKey) {
        @Nullable JsonObject variants = this.getVariantsJsonOrNull();
        if (variants == null) return null;
        return variants.getAsJsonObject(variantKey);
    }

    private JsonObject getOrCreateVariantJson(String variantKey) {
        JsonObject variants = this.getOrCreateVariantsJson();
        @Nullable JsonObject variantJson = variants.getAsJsonObject(variantKey);
        if (variantJson == null) {
            variantJson = new JsonObject();
            variants.add(variantKey, variantJson);
        }
        return variantJson;
    }

    public @Nullable JsonObject getVariant(String variantKey) {
        return this.getVariantJsonOrNull(variantKey);
    }

    public @Nullable JsonObject getVariant(BlockData state) {
        String variantKey = getVariantKey(state);
        @Nullable JsonObject direct = this.getVariant(variantKey);
        if (direct != null) return direct;
        // Try less-complete variant keys
        @Nullable JsonObject variants = this.getVariantsJsonOrNull();
        if (variants == null) return null;
        Set<String> variantKeyElements = Set.of(variantKey.split(","));
        for (Map.Entry<String, JsonElement> entry : variants.asMap().entrySet()) {
            if (entry.getKey().isBlank()) {
                return entry.getValue().getAsJsonObject();
            }
            String[] elements = entry.getKey().split(",");
            boolean containsAllElements = true;
            for (String element : elements) {
                if (!variantKeyElements.contains(element)) {
                    containsAllElements = false;
                    break;
                }
            }
            if (containsAllElements) {
                return entry.getValue().getAsJsonObject();
            }
        }
        return null;
    }

    public void setVariant(String variantKey, JsonObject variantJson) {
        JsonObject variants = this.getOrCreateVariantsJson();
        variants.add(variantKey, variantJson);
    }

    public void setVariant(BlockData state, JsonObject variantJson) {
        this.setVariant(getVariantKey(state), variantJson);
    }

    public void setVariantToModel(String variantKey, String model) {
        JsonObject variantJson = new JsonObject();
        variantJson.addProperty("model", model);
        this.setVariant(variantKey, variantJson);
    }

    public void setVariantToModel(BlockData state, String model) {
        this.setVariantToModel(getVariantKey(state), model);
    }

    public void setVariantToModel(String variantKey, NamespacedKey model) {
        this.setVariantToModel(variantKey, model.toString());
    }

    public void setVariantToModel(BlockData state, NamespacedKey model) {
        this.setVariantToModel(getVariantKey(state), model);
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
        return obj instanceof Blockstates other && this.json.equals(other.json);
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

    public static Blockstates ofImmutable(JsonObject jsonObject) {
        return new Blockstates(jsonObject.deepCopy());
    }

    public static Blockstates ofMutable(JsonObject jsonObject) {
        return new Blockstates(jsonObject);
    }

    public static Blockstates create() {
        return new Blockstates(new JsonObject());
    }

    public static String getVariantKey(BlockData state) {
        String string = state.getAsString(false);
        int openBracketIndex = string.indexOf('[');
        if (openBracketIndex == -1) {
            return "";
        }
        return string.substring(openBracketIndex + 1, string.length() - 1).replace(" ", "");
    }

}
