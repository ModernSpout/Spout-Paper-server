package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemTypeRegistryEntry;

/**
 * An extension of {@link FiddleItemTypeRegistryEntry} using Minecraft internals.
 */
public interface FiddleItemTypeRegistryEntryNMS extends FiddleItemTypeRegistryEntry {

    @Override
    WrappedItemCodec<?> getWrappedCodec();

    @Override
    default MapCodec<? extends Item> getCodec() {
        return this.getWrappedCodec().getCodec();
    }

    /**
     * A {@link FiddleItemTypeRegistryEntry.Builder}
     * that allows building an {@link FiddleItemType} type using Minecraft internals.
     */
    interface Builder extends FiddleItemTypeRegistryEntryNMS, FiddleItemTypeRegistryEntry.Builder {

        void setCodec(MapCodec<? extends Item> codecForType);

    }

}
