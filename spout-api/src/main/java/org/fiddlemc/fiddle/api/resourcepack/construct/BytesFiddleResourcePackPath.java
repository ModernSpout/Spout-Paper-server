package org.fiddlemc.fiddle.api.resourcepack.construct;

import org.jspecify.annotations.Nullable;
import java.util.function.Function;

/**
 * A byte array view of a {@link FiddleResourcePackPath}.
 */
public interface BytesFiddleResourcePackPath extends FiddleResourcePackPath {

    /**
     * @return The bytes in the file at this path.
     * You must never change the contents of the returned byte array.
     * @throws IllegalStateException If {@link #exists()} is false.
     */
    byte[] getImmutable();

    /**
     * Sets the contents of this file to the given bytes.
     * The byte array may be modified afterward, so make sure you do not keep any references to it.
     *
     * @param bytes A byte array.
     */
    void setMutable(byte[] bytes);

    /**
     * Changes the bytes in this file.
     *
     * @param function A function from the old bytes to the new bytes.
     *                 The old bytes will be null if {@link #exists()} is false.
     *                 The new bytes is allowed to be the same instances as the old bytes,
     *                 i.e. you can modify the bytes in-place.
     *                 The new bytes can also be null, in which case the file will be deleted.
     */
    void update(Function<byte @Nullable [], byte @Nullable []> function);

}
