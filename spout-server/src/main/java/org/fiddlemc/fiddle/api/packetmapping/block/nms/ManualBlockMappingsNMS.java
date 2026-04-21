package org.fiddlemc.fiddle.api.packetmapping.block.nms;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.key.Key;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.block.ManualBlockMappings;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * An extension to {@link BlockMappingsComposeEvent} using Minecraft internals.
 */
public interface ManualBlockMappingsNMS<M> extends ManualBlockMappings<M> {

    /**
     * @see #register(Consumer)
     */
    void registerNMS(Consumer<BlockMappingBuilderNMS> builderConsumer);

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegisteredNMS(Pair<ClientView.AwarenessLevel, BlockState> key) {
        return this.getRegisteredNMS(key.left(), key.right());
    }

    /**
     * @see #getRegistered(ClientView.AwarenessLevel, BlockData)
     */
    default List<M> getRegisteredNMS(ClientView.AwarenessLevel awarenessLevel, BlockState from) {
        return this.getRegistered(awarenessLevel, from.createCraftBlockData());
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegisteredNMS(Pair<ClientView.AwarenessLevel, BlockState> key, Consumer<List<M>> listConsumer) {
        this.changeRegisteredNMS(key.left(), key.right(), listConsumer);
    }

    /**
     * @see #changeRegistered(ClientView.AwarenessLevel, BlockData, Consumer)
     */
    default void changeRegisteredNMS(ClientView.AwarenessLevel awarenessLevel, BlockState from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(awarenessLevel, from.createCraftBlockData(), listConsumer);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType) 
     */
    default void registerStateToStateNMS(ClientView.AwarenessLevel awarenessLevel, Block from, Block to) {
        this.registerStateToStateNMS(List.of(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel[], BlockType, BlockType)
     */
    default void registerStateToStateNMS(ClientView.AwarenessLevel[] awarenessLevel, Block from, Block to) {
        this.registerStateToStateNMS(Arrays.asList(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(Collection, BlockType, BlockType)
     */
    default void registerStateToStateNMS(Collection<ClientView.AwarenessLevel> awarenessLevel, Block from, Block to) {
        this.registerStateToState(awarenessLevel, Registry.BLOCK.getOrThrow(Key.key(from.keyInBlockRegistry.getNamespace(), from.keyInBlockRegistry.getPath())), Registry.BLOCK.getOrThrow(Key.key(to.keyInBlockRegistry.getNamespace(), to.keyInBlockRegistry.getPath())));
    }

    /**
     * Calls {@link #registerNMS(Consumer)} with
     * {@link BlockMappingBuilderNMS#awarenessLevel} set to {@code awarenessLevel},
     * {@link BlockMappingBuilderNMS#from} set to {@code from}
     * and {@link BlockMappingBuilderNMS#to} set to {@code to}.
     */
    default void registerNMS(ClientView.AwarenessLevel awarenessLevel, BlockState from, BlockState to) {
        this.registerNMS(builder -> {
            builder.awarenessLevel(awarenessLevel);
            builder.from(from);
            builder.to(to);
        });
    }

    /**
     * Calls {@link #registerNMS(Consumer)} with
     * {@link BlockMappingBuilderNMS#awarenessLevel} set to {@code awarenessLevels},
     * {@link BlockMappingBuilderNMS#from} set to {@code from}
     * and {@link BlockMappingBuilderNMS#to} set to {@code to}.
     */
    default void registerNMS(ClientView.AwarenessLevel[] awarenessLevels, BlockState from, BlockState to) {
        this.registerNMS(builder -> {
            builder.awarenessLevel(awarenessLevels);
            builder.from(from);
            builder.to(to);
        });
    }

    /**
     * Calls {@link #registerNMS(Consumer)} with
     * {@link BlockMappingBuilderNMS#awarenessLevel} set to {@code awarenessLevels},
     * {@link BlockMappingBuilderNMS#from} set to {@code from}
     * and {@link BlockMappingBuilderNMS#to} set to {@code to}.
     */
    default void registerNMS(Collection<ClientView.AwarenessLevel> awarenessLevels, BlockState from, BlockState to) {
        this.registerNMS(builder -> {
            builder.awarenessLevel(awarenessLevels);
            builder.from(from);
            builder.to(to);
        });
    }

}
