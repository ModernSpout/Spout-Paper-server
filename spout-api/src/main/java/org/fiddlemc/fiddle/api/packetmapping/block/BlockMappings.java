package org.fiddlemc.fiddle.api.packetmapping.block;

import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service for the block mappings that Fiddle applies.
 */
public interface BlockMappings extends Composable<BlockMappingsComposeEvent> {

    /**
     * An internal interface to get the {@link BlockMappings} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<BlockMappings> {
    }

    /**
     * @return The {@link BlockMappings} instance.
     */
    static BlockMappings get() {
        return ServiceLoader.load(BlockMappings.ServiceProvider.class, BlockMappings.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
