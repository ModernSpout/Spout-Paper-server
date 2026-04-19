package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A type of data-driven block mapping.
 */
public interface DataDrivenBlockMappingType {

    <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike);

}
