package org.fiddlemc.fiddle.api.packetmapping.block.nms;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.packetmapping.AwarenessLevelMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;
import org.fiddlemc.fiddle.api.util.composable.FromBuilder;
import org.fiddlemc.fiddle.api.util.composable.FunctionBuilder;
import org.fiddlemc.fiddle.api.util.composable.ToBuilder;

/**
 * An alternative to {@link BlockMappingBuilder} that uses Minecraft internals.
 */
public interface BlockMappingBuilderNMS extends AwarenessLevelMappingBuilder, FromBuilder<BlockState>, ToBuilder<BlockState> {

    /**
     * Sets the target {@link BlockState} to which this mapping will be applied.
     * It will be applied to every block state of that type.
     *
     * <p>
     * This replaces any previous value set with {@link #from} or {@link #fromEveryStateOf}.
     * </p>
     */
    default void fromEveryStateOf(Block from) {
        this.fromEveryStateOf(List.of(from));
    }

    /**
     * @see #fromEveryStateOf(Block)
     */
    default void fromEveryStateOf(Block[] from) {
        this.fromEveryStateOf(Arrays.asList(from));
    }

    /**
     * @see #fromEveryStateOf(Block)
     */
    default void fromEveryStateOf(Collection<Block> from) {
        this.from(from.stream().flatMap(value -> value.getStateDefinition().getPossibleStates().stream()).toList());
    }

    /**
     * Adds a {@link Block} to which this mapping will be applied.
     *
     * @see #fromEveryStateOf(Block)
     */
    default void addFromEveryStateOf(Block from) {
        for (BlockState value : from.getStateDefinition().getPossibleStates()) {
            this.addFrom(value);
        }
    }

    /**
     * @see #addFromEveryStateOf(Block)
     */
    default void addFromEveryStateOf(Block[] from) {
        for (Block value : from) {
            this.addFromEveryStateOf(value);
        }
    }

    /**
     * @see #addFromEveryStateOf(Block)
     */
    default void addFromEveryStateOf(Collection<Block> from) {
        for (Block value : from) {
            this.addFromEveryStateOf(value);
        }
    }

    /**
     * Calls {@link #to} with the {@linkplain Block#defaultBlockState() default block state}
     * of the given {@link Block}.
     */
    default void toDefaultStateOf(Block to) {
        this.to(to.defaultBlockState());
    }

    /**
     * @param requiresCoordinates Whether this mapping requires the coordinates
     *                            ({@link BlockMappingFunctionContext#getPhysicalBlockX()} and so on).
     * @see FunctionBuilder#to
     */
    void to(Consumer<BlockMappingHandleNMS> function, boolean requiresCoordinates);

}
