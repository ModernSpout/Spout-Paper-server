package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data-centric version-specific registry entry for the {@link FiddleItemType} type.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface FiddleItemTypeRegistryEntry {

    Object getWrappedCodec();

    Object getCodec();

    /**
     * A mutable builder for the {@link FiddleItemTypeRegistryEntry},
     * that plugins may change in applicable registry events.
     *
     * <p>
     * Currently, this must be cast to {@code FiddleItemTypeRegistryEntryBuilderNMS} to be used.
     * </p>
     */
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder extends FiddleItemTypeRegistryEntry, RegistryBuilder<FiddleItemType> {
    }

}
