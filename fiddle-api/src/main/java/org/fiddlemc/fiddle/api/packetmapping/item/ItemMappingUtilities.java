package org.fiddlemc.fiddle.api.packetmapping.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * Some utilities for the mapping of items.
 */
public interface ItemMappingUtilities {

    /**
     * An internal interface to get the {@link ItemMappingUtilities} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ItemMappingUtilities> {
    }

    /**
     * @return The {@link ItemMappingUtilities} instance.
     */
    static ItemMappingUtilities get() {
        return ServiceLoader.load(ItemMappingUtilities.ServiceProvider.class, ItemMappingUtilities.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    /**
     * Changes the {@link ItemType} of the given handle's {@link ItemStack},
     * while attempting to keep the client-side appearance the same in most ways.
     *
     * @param handle      The handle being mapped.
     * @param newItemType The new {@link ItemType} for the item stack.
     * @return Whether any changes were made.
     */
    boolean setItemTypeWhilePreservingRest(ItemMappingHandle handle, ItemType newItemType);

}
