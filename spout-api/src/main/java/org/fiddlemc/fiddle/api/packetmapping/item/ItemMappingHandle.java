package org.fiddlemc.fiddle.api.packetmapping.item;

import org.bukkit.inventory.ItemStack;
import org.fiddlemc.fiddle.api.util.mapping.MutableMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ItemMappingBuilder#to}.
 */
public interface ItemMappingHandle extends WithContextMappingFunctionHandle<ItemStack, ItemMappingFunctionContext>, WithOriginalMappingFunctionHandle<ItemStack>, MutableMappingFunctionHandle<ItemStack, ItemStack> {
}
