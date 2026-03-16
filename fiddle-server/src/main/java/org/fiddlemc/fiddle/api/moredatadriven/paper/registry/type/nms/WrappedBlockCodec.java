package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/**
 * A wrapped codec for a specific type of block.
 */
public interface WrappedBlockCodec<B extends Block>{

    MapCodec<B> getCodec();

}
