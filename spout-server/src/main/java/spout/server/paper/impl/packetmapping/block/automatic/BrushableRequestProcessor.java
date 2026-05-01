package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#brushable}.
 */
public class BrushableRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Brushable, FromToBlockTypeRequestBuilderImpl<UsedStates.Brushable>> { // TODO instead of extending MatchingBlockStateClaimAttemptsRequestProcessor just claim 4 full block states

    public BrushableRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.Brushable> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return new Block[0];
    }

    @Override
    protected UsedStates.Brushable createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.Brushable(new UsedStatesInternalImpls.BlockSwitch<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
    }

}
