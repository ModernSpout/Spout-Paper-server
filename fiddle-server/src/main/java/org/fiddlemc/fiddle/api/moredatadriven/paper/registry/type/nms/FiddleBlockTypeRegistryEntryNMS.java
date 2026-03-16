package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockTypeRegistryEntry;

/**
 * An extension of {@link FiddleBlockTypeRegistryEntry} using Minecraft internals.
 */
public interface FiddleBlockTypeRegistryEntryNMS extends FiddleBlockTypeRegistryEntry {

    @Override
    WrappedBlockCodec<?> getWrappedCodec();

    @Override
    default MapCodec<? extends Block> getCodec() {
        return this.getWrappedCodec().getCodec();
    }

    /**
     * A {@link FiddleBlockTypeRegistryEntry.Builder}
     * that allows building a {@link FiddleBlockType} type using Minecraft internals.
     */
    interface Builder extends FiddleBlockTypeRegistryEntryNMS, FiddleBlockTypeRegistryEntry.Builder {

        void setCodec(MapCodec<? extends Block> codecForType);

    }

}
