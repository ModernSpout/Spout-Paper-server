package org.fiddlemc.fiddle.api.packetmapping.item.nms;

import net.minecraft.world.item.ItemStack;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.MutableMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ItemMappingBuilderNMS#to}.
 */
public interface ItemMappingHandleNMS extends WithContextMappingFunctionHandle<ItemStack, ItemMappingFunctionContext>, WithOriginalMappingFunctionHandle<ItemStack>, MutableMappingFunctionHandle<ItemStack, ItemStack> {
}
