package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.Arrays;

/**
 * A {@link BlockStateClaimAttemptsRequestProcessor}
 * where each list of block states matches the {@link FromToBlockStatesRequestBuilder#fromStates()}.
 */
public abstract class MatchingBlockStateClaimAttemptsRequestProcessor<US extends UsedStates, R extends ProxyStatesRequestBuilderImpl<US> & FromToBlockStatesRequestBuilder> extends ArrayBlockStateClaimAttemptsRequestProcessor<US, R> {

    protected MatchingBlockStateClaimAttemptsRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    protected abstract Block[] createBlocksToClaim();

    @Override
    protected BlockState[][] createStatesToClaim() {
        Block[] claimableBlocks = this.createBlocksToClaim();
        BlockState[][] validBlockStates = new BlockState[claimableBlocks.length][];
        int validBlockCount = 0;
        BlockState[] fromStates = this.request.fromStates();
        findValidBlocks: for (Block candidate : claimableBlocks) {
            BlockState[] candidateStates = new BlockState[fromStates.length];
            for (int i = 0; i < fromStates.length; i++) {
                candidateStates[i] = candidate.defaultBlockState();
                BlockState fromState = fromStates[i];
                for (Property<?> property : fromState.getProperties()) {
                    if (candidateStates[i].hasProperty(property)) {
                        candidateStates[i] = candidateStates[i].setValue((Property) property, fromStates[i].getValue(property));
                    }
                }
                if (!BlockRequestProcessUtils.isValidProxyCandidate(fromState, candidateStates[i])) {
                    continue findValidBlocks;
                }
            }
            validBlockStates[validBlockCount++] = Arrays.stream(candidateStates).distinct().toArray(BlockState[]::new);
        }
        ObjectArrays.stableSort(validBlockStates, 0, validBlockCount, (states1, states2) -> BlockRequestProcessUtils.compareProxyCandidates(fromStates[0], states1[0], states2[0]));
        BlockState[][] statesToClaim = new BlockState[validBlockCount][];
        for (int i = 0; i < validBlockCount; i++) {
            statesToClaim[i] = validBlockStates[i];
        }
        return statesToClaim;
    }

    @Override
    protected int mapFromStatesIndexToProxyStatesIndex(int fromStatesIndex, BlockState[] proxyStates) {
        BlockState fromState = this.request.fromStates()[fromStatesIndex];
        findIndex: for (int i = 0; i < proxyStates.length; i++) {
            for (Property<?> property : proxyStates[i].getProperties()) {
                if (fromState.hasProperty(property)) {
                    if (!fromState.getValue(property).equals(proxyStates[i].getValue(property))) {
                        continue findIndex;
                    }
                }
            }
            return i;
        }
        throw new IllegalStateException("No proxy state in " + Arrays.toString(proxyStates) + " for from state: " + fromState);
    }

    @Override
    protected int mapProxyStatesIndexToFromStatesIndex(int proxyStatesIndex, BlockState[] proxyStates) {
        BlockState proxyState = proxyStates[proxyStatesIndex];
        BlockState[] fromStates = this.request.fromStates();
        findIndex: for (int i = 0; i < fromStates.length; i++) {
            for (Property<?> property : fromStates[i].getProperties()) {
                if (proxyState.hasProperty(property)) {
                    if (!proxyState.getValue(property).equals(fromStates[i].getValue(property))) {
                        continue findIndex;
                    }
                }
            }
            return i;
        }
        throw new IllegalStateException("No from state in " + Arrays.toString(fromStates) + " for proxy state: " + proxyState);
    }

    @Override
    protected int mapFromStatesIndexToFallbackStatesIndex(int fromStatesIndex) {
        BlockState fromState = this.request.fromStates()[fromStatesIndex];
        BlockState[] fallbackStates = this.request.fallbackStates();
        findIndex: for (int i = 0; i < fallbackStates.length; i++) {
            for (Property<?> property : fallbackStates[i].getProperties()) {
                if (fromState.hasProperty(property)) {
                    if (!fromState.getValue(property).equals(fallbackStates[i].getValue(property))) {
                        continue findIndex;
                    }
                }
            }
            return i;
        }
        throw new IllegalStateException("No fallback state in " + Arrays.toString(fallbackStates) + " for from state: " + fromState);
    }

}
