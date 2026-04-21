package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link LeavesRequestBuilderImpl}s.
 */
public class LeavesRequestProcessor extends ArrayBlockStateClaimAttemptsRequestProcessor<UsedStates.Waterlogged, LeavesRequestBuilderImpl> {

    public LeavesRequestProcessor(LeavesRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected BlockState[][] createStatesToClaim() {
        return this.request.tintedOrInfer() ? tintedClaimableStates() : untintedClaimableStates();
    }

    @Override
    protected UsedStates.Waterlogged createUsedStates(BlockState @Nullable [] result) {
        return result != null ? new UsedStatesImpl.WaterloggedImpl(result[0], result[1], false, false) : new UsedStatesImpl.WaterloggedImpl(this.request.fallbackNonWaterloggedState(), this.request.fallbackWaterloggedState(), true, true);
    }

    @Override
    protected int mapFromStatesIndexToProxyStatesIndex(int fromStatesIndex, BlockState[] proxyStates) {
        BlockState fromState = this.request.fromStates()[fromStatesIndex];
        return fromState.getValue(BlockStateProperties.WATERLOGGED) ? 1 : 0;
    }

    @Override
    protected int mapProxyStatesIndexToFromStatesIndex(int proxyStatesIndex, BlockState[] proxyStates) {
        BlockState state = this.request.from.defaultBlockState();
        if (proxyStatesIndex == 1) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        }
        return Arrays.asList(this.request.fromStates()).indexOf(state);
    }

    /**
     * The return value of {@link #untintedClaimableStates()},
     * or null if not initialized yet.
     *
     * <p>
     * Every entry consists of two states: a non-waterlogged state and its corresponding waterlogged state.
     * </p>
     */
    private static BlockState @Nullable [][] untintedClaimableStates;

    /**
     * The same as {@link #untintedClaimableStates}, but for tinted leaves.
     */
    private static BlockState @Nullable [][] tintedClaimableStates;

    /**
     * @return The list of {@link BlockState}s that can be attempted to be claimed
     * by this class, for untinted leaves.
     * @see #untintedClaimableStates
     */
    private static BlockState[][] untintedClaimableStates() {
        if (untintedClaimableStates == null) {
            untintedClaimableStates = computeClaimableStates(StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof UntintedParticleLeavesBlock));
        }
        return untintedClaimableStates;
    }

    /**
     * @return The list of {@link BlockState}s that can be attempted to be claimed
     * by this class, for tinted leaves.
     * @see #tintedClaimableStates
     */
    private static BlockState[][] tintedClaimableStates() {
        if (tintedClaimableStates == null) {
            tintedClaimableStates = computeClaimableStates(StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof TintedParticleLeavesBlock));
        }
        return tintedClaimableStates;
    }

    private static BlockState[][] computeClaimableStates(Stream<Block> blocks) {
        return blocks
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream().filter(state -> !state.getValue(BlockStateProperties.WATERLOGGED)))
            .sorted(Comparator.comparing(state -> state == state.getBlock().defaultBlockState()))
            .map(state -> new BlockState[]{state, state.setValue(BlockStateProperties.WATERLOGGED, true)})
            .toArray(BlockState[][]::new);
    }

}
