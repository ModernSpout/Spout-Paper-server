package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemType;

/**
 * Extension of {@link FiddleItemType} using Minecraft internals.
 */
public interface FiddleItemTypeNMS<I extends Item> extends FiddleItemType {

    @Override
    WrappedItemCodec<I> getWrappedCodec();

}
