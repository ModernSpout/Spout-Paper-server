package org.fiddlemc.fiddle.impl.packetmapping.item;

import java.util.function.Consumer;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingHandle;

/**
 * A {@link ItemMappingsStep} that performs a specific function.
 */
public record BukkitFunctionItemMappingsStep(Consumer<ItemMappingHandle> function) implements ItemMappingsStep {

    @Override
    public void apply(ItemMappingHandleNMSImpl handle) {
        this.function.accept(handle.bukkitHandle());
    }

}
