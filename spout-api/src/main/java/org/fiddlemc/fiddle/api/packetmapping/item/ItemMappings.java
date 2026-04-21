package org.fiddlemc.fiddle.api.packetmapping.item;

import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service for the item mappings that Fiddle applies.
 */
public interface ItemMappings<M> extends Composable<ItemMappingsComposeEvent<M>> {

    /**
     * An internal interface to get the {@link ItemMappings} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ItemMappings<?>> {
    }

    /**
     * @return The {@link ItemMappings} instance.
     */
    static ItemMappings<?> get() {
        return ServiceLoader.load(ItemMappings.ServiceProvider.class, ItemMappings.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
