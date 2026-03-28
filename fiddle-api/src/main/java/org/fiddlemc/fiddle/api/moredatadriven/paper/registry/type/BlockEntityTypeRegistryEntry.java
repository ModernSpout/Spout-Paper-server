package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.RegistryBuilder;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data-centric version-specific registry entry for the {@link BlockEntityType} type.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface BlockEntityTypeRegistryEntry {

    /**
     * A mutable builder for the {@link BlockEntityTypeRegistryEntry},
     * that plugins may change in applicable registry events.
      * <p>
     * Currently, this must be cast to {@code BlockEntityTypeRegistryEntryBuilderNMS} to be used.
     * </p>
     * <p>
     * It is mandatory to assign a factory. (Currently only possible using NMS)
     * </p>
     */
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder extends RegistryBuilder<BlockEntityType> {
        /**
         * Add block types that are valid for this block entity type.
         *
         * <p>
         * For example, the shulker box block entity type would be valid for all the
         * different colors of shulker box blocks.
         * </p>
         *
         * @param blocks The block types.
         * @return This builder, for chaining.
         */
        Builder validBlocks(BlockType... blocks);
    }
}
