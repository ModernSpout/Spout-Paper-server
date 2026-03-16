package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.FiddleItemTypeNMS;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;

/**
 * The implementation for {@link FiddleItemType}.
 */
public final class FiddleItemTypeImpl<I extends Item> extends HolderableBase<WrappedItemCodec<I>> implements FiddleItemTypeNMS<I> {

    public FiddleItemTypeImpl(Holder<WrappedItemCodec<I>> wrappedCodecHolder) {
        super(wrappedCodecHolder);
    }

    @Override
    public WrappedItemCodec<I> getWrappedCodec() {
        return this.getHandle();
    }

    public static <I extends Item> FiddleItemTypeImpl<I> of(Holder<WrappedItemCodec<?>> wrappedCodecHolder) {
        return new FiddleItemTypeImpl<>((Holder<WrappedItemCodec<I>>) (Holder) wrappedCodecHolder);
    }

}
