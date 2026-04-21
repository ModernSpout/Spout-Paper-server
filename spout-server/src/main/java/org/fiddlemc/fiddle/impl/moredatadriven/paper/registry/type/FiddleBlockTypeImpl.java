package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.FiddleBlockTypeNMS;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedBlockCodec;

/**
 * The implementation for {@link FiddleBlockType}.
 */
public final class FiddleBlockTypeImpl<B extends Block> extends HolderableBase<WrappedBlockCodec<B>> implements FiddleBlockTypeNMS<B> {

    public FiddleBlockTypeImpl(Holder<WrappedBlockCodec<B>> wrappedCodecHolder) {
        super(wrappedCodecHolder);
    }

    @Override
    public WrappedBlockCodec<B> getWrappedCodec() {
        return this.getHandle();
    }

    public static <B extends Block> FiddleBlockTypeImpl<B> of(Holder<WrappedBlockCodec<?>> wrappedCodecHolder) {
        return new FiddleBlockTypeImpl<>((Holder<WrappedBlockCodec<B>>) (Holder) wrappedCodecHolder);
    }

}
