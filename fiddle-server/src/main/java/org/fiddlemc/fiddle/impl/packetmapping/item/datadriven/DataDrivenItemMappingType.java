package org.fiddlemc.fiddle.impl.packetmapping.item.datadriven;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A type of data-driven item mapping.
 */
public interface DataDrivenItemMappingType {

    <T> void apply(ItemMappingsComposeEventImpl event, @Nullable Item item, DynamicOps<T> ops, MapLike<T> mapLike);

}
