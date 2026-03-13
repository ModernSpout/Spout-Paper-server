package org.fiddlemc.fiddle.api.packetmapping.item.nms;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * An extension to {@link ItemMappingUtilities} using Minecraft internals.
 */
public interface ItemMappingUtilitiesNMS extends ItemMappingUtilities {

    /**
     * An internal interface to get the {@link ItemMappingUtilitiesNMS} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ItemMappingUtilitiesNMS> {
    }

    /**
     * @return The {@link ItemMappingUtilitiesNMS} instance.
     */
    static ItemMappingUtilitiesNMS get() {
        return ServiceLoader.load(ItemMappingUtilitiesNMS.ServiceProvider.class, ItemMappingUtilitiesNMS.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    /**
     * Changes the {@link Item} of the given handle's {@link ItemStack},
     * while attempting to keep the client-side appearance the same in most ways.
     *
     * @param handle  The handle being mapped.
     * @param newItem The new {@link Item} for the item stack.
     * @return Whether any changes were made.
     */
    boolean setItemWhilePreservingRest(ItemMappingHandleNMS handle, Item newItem);

}
