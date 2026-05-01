package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#trapdoor}.
 */
public class TrapdoorRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.TrapDoor, FromToBlockTypeRequestBuilderImpl<UsedStates.TrapDoor>> { // TODO use powered vs unpowered unused states

    public TrapdoorRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.TrapDoor> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return claimableBlocks();
    }

    @Override
    protected UsedStates.TrapDoor createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.TrapDoor(new UsedStatesInternalImpls.BlockTrapDoor<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
    }

    /**
     * The return value of {@link #claimableBlocks()},
     * or null if not initialized yet.
     *
     * <p>
     * This may contain some states that are not actually claimable.
     * </p>
     */
    private static Block @Nullable [] claimableBlocks;

    /**
     * @return The list of {@link Block}s that can be attempted to be claimed
     * by this class.
     */
    private static Block[] claimableBlocks() {
        if (claimableBlocks == null) {
            claimableBlocks = new Block[] {
                // Copper
                Blocks.COPPER_TRAPDOOR,
                Blocks.EXPOSED_COPPER_TRAPDOOR,
                Blocks.OXIDIZED_COPPER_TRAPDOOR,
                Blocks.WEATHERED_COPPER_TRAPDOOR,
                Blocks.WAXED_COPPER_TRAPDOOR,
                Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR,
                Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR,
                Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR
            };
        }
        return claimableBlocks;
    }

}
