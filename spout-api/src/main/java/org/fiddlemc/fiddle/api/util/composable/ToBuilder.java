package org.fiddlemc.fiddle.api.util.composable;

/**
 * A builder that can be registered with a {@link BuilderComposeEvent},
 * that has zero or one particular result.
 */
public interface ToBuilder<T> {

    /**
     * Sets the desired result of this builder.
     *
     * <p>
     * This replaces any previous value set.
     * </p>
     */
    void to(T to);

}
