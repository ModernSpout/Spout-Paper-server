package org.fiddlemc.fiddle.api.resourcepack.construct;

import java.util.function.Function;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import org.jspecify.annotations.Nullable;

/**
 * A {@link Blockstates} view of a {@link FiddleResourcePackPath}.
 */
public interface BlockstatesFiddleResourcePackPath extends FiddleResourcePackPath {

    /**
     * @return Whether {@link #exists()} is true and the contents of the file represent a valid {@link Blockstates}.
     */
    boolean isBlockstates();

    /**
     * @return The {@link Blockstates} in the file at this path.
     * You must never change the contents of the returned object.
     * @throws IllegalStateException If {@link #isBlockstates()} is false.
     */
    Blockstates getImmutable();

    /**
     * Sets the contents of this file to the given {@link Blockstates}.
     * The object may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param blockstates A {@link Blockstates}.
     */
    void setMutable(Blockstates blockstates);

    /**
     * Changes the {@link Blockstates} in this file.
     *
     * @param function A function from the old {@link Blockstates} to the new {@link Blockstates}.
     *                 The old object will be null if {@link #isBlockstates()} is false.
     *                 The new object is allowed to be the same instances as the old object,
     *                 i.e. you can modify the object in-place.
     *                 The new object can also be null, in which case the file will be deleted.
     */
    void update(Function<@Nullable Blockstates, @Nullable Blockstates> function);

}
