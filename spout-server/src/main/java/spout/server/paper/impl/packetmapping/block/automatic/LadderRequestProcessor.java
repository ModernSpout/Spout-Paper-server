package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#ladder}.
 */
public class LadderRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Ladder, FromToBlockTypeRequestBuilderImpl<UsedStates.Ladder>> {

    public LadderRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.Ladder> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return new Block[0];
    }

    @Override
    protected UsedStates.Ladder createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.Ladder(new UsedStatesInternalImpls.BlockLadder<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
    }

}
