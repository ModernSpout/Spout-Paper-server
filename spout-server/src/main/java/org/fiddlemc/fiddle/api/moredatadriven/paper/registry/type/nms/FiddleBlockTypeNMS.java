package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockType;

/**
 * Extension of {@link FiddleBlockType} using Minecraft internals.
 */
public interface FiddleBlockTypeNMS<B extends Block> extends FiddleBlockType {

    @Override
    WrappedBlockCodec<B> getWrappedCodec();

}
