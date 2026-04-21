package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data-centric version-specific registry entry for the {@link FiddleBlockType} type.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface FiddleBlockTypeRegistryEntry {

    Object getWrappedCodec();

    /**
     * A mutable builder for the {@link FiddleBlockTypeRegistryEntry},
     * that plugins may change in applicable registry events.
     *
     * <p>
     * Currently, this must be cast to {@code BlockTypeRegistryEntryBuilderNMS} to be used.
     * </p>
     */
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder extends FiddleBlockTypeRegistryEntry, RegistryBuilder<FiddleBlockType> {
    }

}
