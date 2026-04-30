package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#button}.
 */
public class ButtonRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Switch, FromToBlockTypeRequestBuilderImpl<UsedStates.Switch>> {

    public ButtonRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.Switch> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return new Block[0];
    }

    @Override
    protected UsedStates.Switch createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.Switch(new UsedStatesInternalImpls.BlockSwitch<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
    }

}
