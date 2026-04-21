package org.fiddlemc.fiddle.api.packetmapping.block;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.packetmapping.AwarenessLevelMappingBuilder;
import org.fiddlemc.fiddle.api.util.composable.FromBuilder;
import org.fiddlemc.fiddle.api.util.composable.FunctionBuilder;
import org.fiddlemc.fiddle.api.util.composable.ToBuilder;

/**
 * A builder to define a block mapping.
 */
public interface BlockMappingBuilder extends AwarenessLevelMappingBuilder, FromBuilder<BlockData>, ToBuilder<BlockData> {

    /**
     * Sets the target {@link BlockType} to which this mapping will be applied.
     * It will be applied to every block state of that type.
     *
     * <p>
     * This replaces any previous value set with {@link #from} or {@link #fromEveryStateOf}.
     * </p>
     */
    default void fromEveryStateOf(BlockType from) {
        this.fromEveryStateOf(List.of(from));
    }

    /**
     * @see #fromEveryStateOf(BlockType)
     */
    default void fromEveryStateOf(BlockType[] from) {
        this.fromEveryStateOf(Arrays.asList(from));
    }

    /**
     * @see #fromEveryStateOf(BlockType)
     */
    default void fromEveryStateOf(Collection<BlockType> from) {
        this.from(from.stream().flatMap(value -> value.createBlockDataStates().stream().map(state -> (BlockData) state)).toList());
    }

    /**
     * Adds a {@link BlockType} to which this mapping will be applied.
     *
     * @see #fromEveryStateOf(BlockType)
     */
    default void addFromEveryStateOf(BlockType from) {
        for (BlockData value : from.createBlockDataStates()) {
            this.addFrom(value);
        }
    }

    /**
     * @see #addFromEveryStateOf(BlockType)
     */
    default void addFromEveryStateOf(BlockType[] from) {
        for (BlockType value : from) {
            this.addFromEveryStateOf(value);
        }
    }

    /**
     * @see #addFromEveryStateOf(BlockType)
     */
    default void addFromEveryStateOf(Collection<BlockType> from) {
        for (BlockType value : from) {
            this.addFromEveryStateOf(value);
        }
    }

    /**
     * Calls {@link #to} with the {@linkplain BlockType#createBlockData() default block state}
     * of the given {@link BlockType}.
     */
    default void toDefaultStateOf(BlockType to) {
        this.to(to.createBlockData());
    }

    /**
     * @param requiresCoordinates Whether this mapping requires the coordinates
     *                            ({@link BlockMappingFunctionContext#getPhysicalBlockX()} and so on).
     * @see FunctionBuilder#to
     */
    void to(Consumer<BlockMappingHandle> function, boolean requiresCoordinates);

}
