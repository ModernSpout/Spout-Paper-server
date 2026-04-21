package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A {@link BlockStateClaimAttemptsRequestProcessor}
 * where the states to claim are stored in an array in advance.
 */
public abstract class ArrayBlockStateClaimAttemptsRequestProcessor<US extends UsedStates, R extends ProxyStatesRequestBuilderImpl<US> & FromToBlockStatesRequestBuilder> extends BlockStateClaimAttemptsRequestProcessor<US, R> {

    private BlockState @Nullable [][] statesToClaim;
    private int index;

    protected ArrayBlockStateClaimAttemptsRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    protected abstract BlockState[][] createStatesToClaim();

    private BlockState[][] getStatesToClaim() {
        if (this.statesToClaim == null) {
            this.statesToClaim = this.createStatesToClaim();
        }
        return this.statesToClaim;
    }

    @Override
    protected BlockState[] nextCandidates() {
        return this.getStatesToClaim()[this.index++];
    }

    @Override
    protected boolean hasMoreAttempts() {
        return this.index < this.getStatesToClaim().length;
    }

}
