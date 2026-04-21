package org.fiddlemc.fiddle.api.util.composable;

import java.util.function.Consumer;

/**
 * A builder that can be registered with a {@link BuilderComposeEvent},
 * that can hold a specific function to run.
 */
public interface FunctionBuilder<T> {

    /**
     * Sets the function that is applied for this mapping.
     *
     * <p>
     * If set, the mapping will ignore any direct {@link ToBuilder#to} set.
     * </p>
     *
     * <p>
     * Function mappings are much less efficient than simple mappings.
     * Please use simple mappings ({@link ToBuilder#to}) whenever you can.
     * </p>
     *
     * <p>
     * The given function is preferred to be deterministic.
     * In other words, if it is called twice with the same arguments and environment,
     * it should return the same result. Otherwise, you may get unstable visuals.
     * </p>
     *
     * <p>
     * If this mapping relies on external factors (for example, the time, or the player
     * having a certain advancement), then when those factors change, any blocks influenced by it should be re-sent
     * to the player to avoid desynchronization.
     * </p>
     *
     * <p>
     * The given function may or may not be run on the main thread.
     * Making sure the code is thread-safe is your own responsibility.
     * </p>
     *
     * @param function            The function to apply.
     */
    void to(Consumer<T> function);

}
