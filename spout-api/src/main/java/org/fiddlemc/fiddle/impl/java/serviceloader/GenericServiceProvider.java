package org.fiddlemc.fiddle.impl.java.serviceloader;

/**
 * A simple service provider, where {@link #get} returns a singleton instance of a service {@link S}.
 */
public interface GenericServiceProvider<S> {

    /**
     * @return An instance of the service {@link S}. Every invocation of this method returns the same instance.
     */
    S get();

}
