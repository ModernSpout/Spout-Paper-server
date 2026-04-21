package org.fiddlemc.fiddle.impl.packetmapping.item.builtin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import org.fiddlemc.fiddle.impl.packetmapping.component.ComponentMappingsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingHandleNMSImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsStep;

/**
 * An {@link ItemMappingsStep} to be registered with {@link ItemMappingsImpl},
 * that maps default item name components.
 */
public final class MapDefaultItemNamesItemMappingsStep implements ItemMappingsStep {

    @Override
    public void apply(ItemMappingHandleNMSImpl handle) {
        Component itemName = handle.getImmutable().getItemName().copy();
        Component mappedItemName = ComponentMappingsImpl.get().apply(itemName, ComponentMappingsImpl.get().createGenericContext(handle.getContext().getClientView()));
        if (!mappedItemName.equals(itemName)) {
            handle.getMutable().set(DataComponents.ITEM_NAME, mappedItemName);
        }
    }

}
