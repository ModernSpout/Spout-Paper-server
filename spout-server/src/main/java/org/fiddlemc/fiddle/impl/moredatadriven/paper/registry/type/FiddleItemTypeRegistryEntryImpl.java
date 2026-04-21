package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry.type;

import com.mojang.serialization.MapCodec;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemTypeRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.FiddleItemTypeRegistryEntryNMS;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type.WrappedItemCodecImpl;
import org.jspecify.annotations.Nullable;

/**
 * Implementation for {@link FiddleItemTypeRegistryEntry}.
 */
public class FiddleItemTypeRegistryEntryImpl implements FiddleItemTypeRegistryEntryNMS {

    protected @Nullable WrappedItemCodec<?> wrappedCodec;

    public FiddleItemTypeRegistryEntryImpl(Conversions conversions, @Nullable WrappedItemCodec<?> internal) {
        this.wrappedCodec = internal;
    }

    @Override
    public WrappedItemCodec<?> getWrappedCodec() {
        return this.wrappedCodec;
    }

    public static class BuilderImpl extends FiddleItemTypeRegistryEntryImpl implements FiddleItemTypeRegistryEntryNMS.Builder, PaperRegistryBuilder<WrappedItemCodec<?>, FiddleItemType> {

        public BuilderImpl(Conversions conversions, @Nullable WrappedItemCodec<?> internal) {
            super(conversions, internal);
        }

        @Override
        public void setCodec(MapCodec<? extends Item> codecForType) {
            this.wrappedCodec = WrappedItemCodecImpl.wrap(codecForType);
        }

        @Override
        public WrappedItemCodec<?> build() {
            return this.wrappedCodec;
        }

    }

}
