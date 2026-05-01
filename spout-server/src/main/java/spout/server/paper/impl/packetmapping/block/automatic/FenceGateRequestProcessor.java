package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#fenceGate}.
 */
public class FenceGateRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Gate, FromToBlockTypeRequestBuilderImpl<UsedStates.Gate>> { // TODO try to claim duplicate states of fence gates

    public FenceGateRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.Gate> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return new Block[0];
    }

    @Override
    protected UsedStates.Gate createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.Gate(new UsedStatesInternalImpls.BlockGate<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
    }

}
