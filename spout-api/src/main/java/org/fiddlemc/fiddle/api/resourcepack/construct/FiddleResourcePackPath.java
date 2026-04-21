package org.fiddlemc.fiddle.api.resourcepack.construct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import org.fiddlemc.fiddle.api.resourcepack.content.Lang;

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
     * @return A view that allows treating the file contents at this path as a byte array.
     */
    BytesFiddleResourcePackPath asBytes();

    /**
     * @return A view that allows treating the file contents at this path as a string.
     */
    StringFiddleResourcePackPath asString();

    /**
     * @return A viewx that allows treating the file contents at this path as a {@link JsonElement}.
     */
    JsonElementFiddleResourcePackPath asJsonElement();

    /**
     * @return A view that allows treating the file contents at this path as a {@link JsonObject}.
     */
    JsonObjectFiddleResourcePackPath asJsonObject();

    /**
     * @return A view that allows treating the file contents at this path as a {@link Blockstates}.
     */
    BlockstatesFiddleResourcePackPath asBlockstates();

    /**
     * @return A view that allows treating the file contents at this path as a {@link Lang}.
     */
    LangFiddleResourcePackPath asLang();

}
