package org.fiddlemc.fiddle.impl.util.java.function;

import java.util.function.Consumer;

/**
 * Equivalent to {@link Consumer}, accept {@link #accept} can throw exceptions.
 */
@FunctionalInterface
public interface ConsumerThrowsException<T, E extends Throwable> {

    /**
     * @see Consumer#accept
     */
    void accept(T t) throws E;

}
