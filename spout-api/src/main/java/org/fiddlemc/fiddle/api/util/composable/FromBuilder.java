package org.fiddlemc.fiddle.api.util.composable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A builder that can be registered with a {@link BuilderComposeEvent},
 * that targets zero or more particular target elements.
 */
public interface FromBuilder<T> {

    /**
     * Sets the {@link T} as target for this builder.
     *
     * <p>
     * This replaces any previous value set with {@link #from}.
     * </p>
     */
    default void from(T from) {
        this.from(List.of(from));
    }

    /**
     * @see #from(T)
     */
    default void from(T[] from) {
        this.from(Arrays.asList(from));
    }

    /**
     * @see #from(T)
     */
    void from(Collection<? extends T> from);

    /**
     * Adds a {@link T} as target for this builder.
     */
    void addFrom(T from);

    /**
     * @see #addFrom(T)
     */
    default void addFrom(T[] from) {
        for (T value : from) {
            this.addFrom(value);
        }
    }

    /**
     * @see #addFrom(T)
     */
    default void addFrom(Collection<T> from) {
        for (T value : from) {
            this.addFrom(value);
        }
    }

}
