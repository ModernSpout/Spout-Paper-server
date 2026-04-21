package org.fiddlemc.fiddle.api.packetmapping.block;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.util.composable.BuilderComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.ChangeRegisteredComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.GetRegisteredComposeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides manual mapping functionality to
 * {@link BlockMappingsComposeEvent}.
 *
 * <p>
 * Manual mappings carry a risk of conflicts with other plugins,
 * so it is generally recommended you use {@linkplain BlockMappingsComposeEvent#automaticMappings() proxy mappings} instead.
 * </p>
 *
 * <p>
 * Casting this instance to {@code ManualBlockMappingsNMS} and using its methods
 * with Minecraft internals gives slightly better performance.
 * </p>
 */
public interface ManualBlockMappings<M> extends BuilderComposeEvent<BlockMappingBuilder>, GetRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, BlockData>, M>, ChangeRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, BlockData>, M> {

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegistered(ClientView.AwarenessLevel awarenessLevel, BlockData from) {
        return this.getRegistered(Pair.of(awarenessLevel, from));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegistered(ClientView.AwarenessLevel awarenessLevel, BlockData from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(Pair.of(awarenessLevel, from), listConsumer);
    }

    /**
     * A convenience function that calls {@link #register}
     * for each matching {@link BlockData} of the {@code from} and {@code to}.
     */
    default void registerStateToState(ClientView.AwarenessLevel awarenessLevel, BlockType from, BlockType to) {
        this.registerStateToState(List.of(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(ClientView.AwarenessLevel[] awarenessLevel, BlockType from, BlockType to) {
        this.registerStateToState(Arrays.asList(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(Collection<ClientView.AwarenessLevel> awarenessLevel, BlockType from, BlockType to) {
        this.registerStateToState(awarenessLevel, from.createBlockDataStates(), to);
    }

    /**
     * Calls {@link #registerStateToState} with
     * {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideBlocks()}.
     */
    default void registerStateToState(BlockType from, BlockType to) {
        this.registerStateToState(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(), from, to);
    }

    /**
     * A convenience function that calls {@link #register}
     * for each matching {@link BlockData} of the {@code from} and {@code to}.
     */
    default void registerStateToState(ClientView.AwarenessLevel awarenessLevel, Iterable<? extends BlockData> from, BlockType to) {
        this.registerStateToState(List.of(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(ClientView.AwarenessLevel[] awarenessLevel, Iterable<? extends BlockData> from, BlockType to) {
        this.registerStateToState(Arrays.asList(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(Collection<ClientView.AwarenessLevel> awarenessLevel, Iterable<? extends BlockData> from, BlockType to) {
        for (BlockData fromState : from) {
            BlockData toState = to.createBlockData();
            fromState.copyTo(toState);
            this.register(builder -> {
                builder.awarenessLevel(awarenessLevel);
                builder.from(fromState);
                builder.to(toState);
            });
        }
    }

    /**
     * Calls {@link #registerStateToState} with
     * {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideBlocks()}.
     */
    default void registerStateToState(Iterable<? extends BlockData> from, BlockType to) {
        this.registerStateToState(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(), from, to);
    }

    /**
     * Calls {@link #register(Consumer)} with
     * {@link BlockMappingBuilder#awarenessLevel} set to {@code awarenessLevel},
     * {@link BlockMappingBuilder#from} set to {@code from}
     * and {@link BlockMappingBuilder#to} set to {@code to}.
     */
    default void register(ClientView.AwarenessLevel awarenessLevel, BlockData from, BlockData to) {
        this.register(builder -> {
            builder.awarenessLevel(awarenessLevel);
            builder.from(from);
            builder.to(to);
        });
    }

    /**
     * Calls {@link #register(Consumer)} with
     * {@link BlockMappingBuilder#awarenessLevel} set to {@code awarenessLevels},
     * {@link BlockMappingBuilder#from} set to {@code from}
     * and {@link BlockMappingBuilder#to} set to {@code to}.
     */
    default void register(ClientView.AwarenessLevel[] awarenessLevels, BlockData from, BlockData to) {
        this.register(builder -> {
            builder.awarenessLevel(awarenessLevels);
            builder.from(from);
            builder.to(to);
        });
    }

    /**
     * Calls {@link #register(Consumer)} with
     * {@link BlockMappingBuilder#awarenessLevel} set to {@code awarenessLevels},
     * {@link BlockMappingBuilder#from} set to {@code from}
     * and {@link BlockMappingBuilder#to} set to {@code to}.
     */
    default void register(Collection<ClientView.AwarenessLevel> awarenessLevels, BlockData from, BlockData to) {
        this.register(builder -> {
            builder.awarenessLevel(awarenessLevels);
            builder.from(from);
            builder.to(to);
        });
    }

}
