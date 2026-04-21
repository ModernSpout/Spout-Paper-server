package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry.type;

import com.mojang.serialization.MapCodec;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockTypeRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.FiddleBlockTypeRegistryEntryNMS;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedBlockCodec;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type.WrappedBlockCodecImpl;
import org.jspecify.annotations.Nullable;

/**
 * Implementation for {@link FiddleBlockTypeRegistryEntry}.
 */
public class FiddleBlockTypeRegistryEntryImpl implements FiddleBlockTypeRegistryEntryNMS {

    protected @Nullable WrappedBlockCodec<?> wrappedCodec;

    public FiddleBlockTypeRegistryEntryImpl(Conversions conversions, @Nullable WrappedBlockCodec<?> internal) {
        this.wrappedCodec = internal;
    }

    @Override
    public WrappedBlockCodec<?> getWrappedCodec() {
        return this.wrappedCodec;
    }

    public static class BuilderImpl extends FiddleBlockTypeRegistryEntryImpl implements FiddleBlockTypeRegistryEntryNMS.Builder, PaperRegistryBuilder<WrappedBlockCodec<?>, FiddleBlockType> {

        public BuilderImpl(Conversions conversions, @Nullable WrappedBlockCodec<?> internal) {
            super(conversions, internal);
        }

        @Override
        public void setCodec(MapCodec<? extends Block> codecForType) {
            this.wrappedCodec = WrappedBlockCodecImpl.wrap(codecForType);
        }

        @Override
        public WrappedBlockCodec<?> build() {
            return this.wrappedCodec;
        }

    }

}
