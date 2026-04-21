package org.fiddlemc.fiddle.api.packetmapping.component;

import java.util.ServiceLoader;

import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;

/**
 * A service for the component mappings that Fiddle applies.
 */
public interface ComponentMappings<M> extends Composable<ComponentMappingsComposeEvent<M>> {

    /**
     * An internal interface to get the {@link ComponentMappings} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ComponentMappings<?>> {
    }

    /**
     * @return The {@link ComponentMappings} instance.
     */
    static ComponentMappings<?> get() {
        return ServiceLoader.load(ComponentMappings.ServiceProvider.class, ComponentMappings.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
