package org.fiddlemc.fiddle.impl.packetmapping.block;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappings;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingsComposeEvent;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.claim.ResourcePackBlockStateClaimsImpl;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A pipeline of block mappings.
 */
public final class BlockMappingsImpl extends ComposableImpl<BlockMappingsComposeEvent, BlockMappingsComposeEventImpl> implements BlockMappings {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<BlockMappings, BlockMappingsImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(BlockMappingsImpl.class);
        }

    }

    public static BlockMappingsImpl get() {
        return (BlockMappingsImpl) BlockMappings.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_block_mappings";
    }

    /**
     * The registered steps that must be performed in chains:
     * this includes only the mappings for {@link BlockState}s for which there was
     * at least 1 {@link MinecraftFunctionBlockMappingsStep}.
     *
     * <p>
     * The mappings are organized in an array where {@link ClientView.AwarenessLevel#ordinal()}
     * is the index, and then in an array where {@link BlockState#indexInBlockStateRegistry} is the index.
     * The lowest-level array may be null, but will never be empty.
     * </p>
     */
    private final BlockMappingsStep[][][] chainMappings;

    /**
     * The registered mappings that can be applied directly:
     * this includes only the mappings for {@link BlockState}s for which there were only
     * {@linkplain SimpleBlockMappingsStep simple} mappings,
     * meaning the mapping always returns the same value.
     *
     * <p>
     * The mappings are organized in an array where {@link ClientView.AwarenessLevel#ordinal()}
     * is the index, and then in an array where {@link BlockState#indexInBlockStateRegistry} is the index.
     * The lowest-level value may be null.
     * </p>
     */
    private final @Nullable BlockState[][] directMappings;

    /**
     * The same as {@link #directMappings}, but contains the {@link BlockState#indexInBlockStateRegistry},
     * or -1 if the value in {@link #directMappings} would be null.
     */
    private final int[][] directMappingsAsIds;

    private BlockMappingsImpl() {
        this.chainMappings = new BlockMappingsStep[ClientView.AwarenessLevel.getAll().length][][];
        this.directMappings = new BlockState[this.chainMappings.length][];
        this.directMappingsAsIds = new int[this.chainMappings.length][];
    }

    public BlockState apply(BlockState data, BlockMappingFunctionContext context) {
        int awarenessLevelI = context.getClientView().getAwarenessLevel().ordinal();
        // If there is a direct mapping, apply it
        @Nullable BlockState directMapping = this.directMappings[awarenessLevelI][data.indexInBlockStateRegistry];
        if (directMapping != null) {
            return directMapping;
        }
        // If there is a mapping chain, apply it
        BlockMappingsStep @Nullable [] chain = this.chainMappings[awarenessLevelI][data.indexInBlockStateRegistry];
        if (chain != null) {
            return applyChain(data, context, chain);
        }
        // No mappings need to be applied
        return data;
    }

    public static BlockState applyChain(BlockState blockState, BlockMappingFunctionContext context, BlockMappingsStep[] chain) {
        BlockMappingHandleNMSImpl handle = new BlockMappingHandleNMSImpl(blockState, context, false);
        for (BlockMappingsStep mapping : chain) {
            mapping.apply(handle);
        }
        return handle.getImmutable();
    }

    public static int applyChain(int blockStateId, BlockMappingFunctionContext context, BlockMappingsStep[] chain) {
        return applyChain(BlockStateRegistry.get().byId(blockStateId), context, chain).indexInBlockStateRegistry;
    }

    /**
     * @param awarenessLevelI A {@link ClientView.AwarenessLevel#ordinal()}.
     * @param blockStateId    A {@link BlockState#indexInBlockStateRegistry}.
     * @return The {@link BlockState#indexInBlockStateRegistry} for the {@link BlockState}
     * to which the {@link BlockState} with the given {@code blockStateId} is mapped,
     * or -1 if there is no direct mapping.
     */
    public int getDirectMapping(int awarenessLevelI, int blockStateId) {
        return this.directMappingsAsIds[awarenessLevelI][blockStateId];
    }

    /**
     * @param awarenessLevelI A {@link ClientView.AwarenessLevel#ordinal()}.
     * @param blockStateId    A {@link BlockState#indexInBlockStateRegistry}.
     * @return The chain mapping for the {@link BlockState} with the given {@code blockStateId},
     * or null if there is no chain mapping.
     */
    public BlockMappingsStep @Nullable [] getChainMapping(int awarenessLevelI, int blockStateId) {
        return this.chainMappings[awarenessLevelI][blockStateId];
    }

    public boolean hasAnyMapping(int awarenessLevelI, int blockStateId) {
        return this.directMappingsAsIds[awarenessLevelI][blockStateId] != -1 || this.chainMappings[awarenessLevelI][blockStateId] != null;
    }

    public boolean hasAnyMapping(int awarenessLevelI, BlockState blockState) {
        return this.hasAnyMapping(awarenessLevelI, blockState.indexInBlockStateRegistry);
    }

    @Override
    protected BlockMappingsComposeEventImpl createComposeEvent() {

        // Create the event
        BlockMappingsComposeEventImpl event = new BlockMappingsComposeEventImpl();

        // Register data-driven mappings
        BlockRegistry.get().forEach(block -> {
            if (block.unappliedDataPackMappings != null) {
                // Apply the mappings
                block.unappliedDataPackMappings.forEach(mapping -> mapping.apply(event, block));
                // Dereference the mappings to reclaim memory
                block.unappliedDataPackMappings = null;
            }
        });

        // Register the built-in mappings
        // TODO

        // Return the event
        return event;

    }

    @Override
    protected void copyInformationFromEvent(BlockMappingsComposeEventImpl event) {

        // Process the claim requests
        ResourcePackBlockStateClaimsImpl.get().processRequests();

        // Initialize the steps
        int registrySize = BlockStateRegistry.get().size();
        for (int i = 0; i < this.chainMappings.length; i++) {
            this.chainMappings[i] = new BlockMappingsStep[registrySize][];
            this.directMappings[i] = new BlockState[registrySize];
            this.directMappingsAsIds[i] = new int[registrySize];
            Arrays.fill(this.directMappingsAsIds[i], -1);
        }

        // Invert the mappings from id -> lists of steps to list of steps -> ids, so that we only have one reference per unique list
        Map<List<BlockMappingsStep>, List<IntIntPair>> invertedChainMappings = new HashMap<>();
        List<Pair<IntIntPair, List<BlockMappingsStep>>> registered = event.getRegisteredWithInternalKey();
        for (Pair<IntIntPair, List<BlockMappingsStep>> entry : registered) {
            List<BlockMappingsStep> mappings = entry.right();
            // Do a check for if the list contains any function step
            boolean containsFunction = false;
            for (BlockMappingsStep mapping : mappings) {
                if (mapping instanceof FunctionBlockMappingsStep) {
                    containsFunction = true;
                    break;
                }
            }
            if (containsFunction) {
                // If there is a function step, add the list of steps as a chain
                // But first we can simplify it a bit by keeping only the last simple step in any contiguous subsequence of simple mappings
                List<BlockMappingsStep> optimizedMappings = new ArrayList<>(mappings.size());
                for (int i = 0; i < mappings.size(); i++) {
                    BlockMappingsStep mapping = mappings.get(i);
                    if (i == mappings.size() - 1 || mapping instanceof FunctionBlockMappingsStep || mappings.get(i + 1) instanceof FunctionBlockMappingsStep) {
                        optimizedMappings.add(mapping);
                    }
                }
                invertedChainMappings.computeIfAbsent(optimizedMappings, $ -> new ArrayList<>()).add(entry.first());
            } else {
                // If there is no complex mapping, add this to the direct mappings
                BlockState to = ((SimpleBlockMappingsStep) mappings.get(mappings.size() - 1)).to();
                this.directMappings[entry.first().firstInt()][entry.first().secondInt()] = to;
                this.directMappingsAsIds[entry.first().firstInt()][entry.first().secondInt()] = to.indexInBlockStateRegistry;
            }
        }

        // Invert back
        for (Map.Entry<List<BlockMappingsStep>, List<IntIntPair>> entry : invertedChainMappings.entrySet()) {
            for (IntIntPair target : entry.getValue()) {
                this.chainMappings[target.firstInt()][target.secondInt()] = entry.getKey().toArray(BlockMappingsStep[]::new);
            }
        }

    }

}
