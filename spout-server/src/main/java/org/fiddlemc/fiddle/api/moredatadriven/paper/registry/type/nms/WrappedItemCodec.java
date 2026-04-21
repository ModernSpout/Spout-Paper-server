package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;

/**
 * A wrapped codec for a specific type of item.
 */
public interface WrappedItemCodec<I extends Item> {

    MapCodec<I> getCodec();

    MapCodec<I> getExtendedCodec();

}
