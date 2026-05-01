package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#door}.
 */
public class DoorRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Door, FromToBlockTypeRequestBuilderImpl<UsedStates.Door>> {// TODO use powered vs unpowered states, also try to claim duplicate states of other door blocks (based on hinge and direction)

    public DoorRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.Door> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return claimableBlocks();
    }

    @Override
    protected UsedStates.Door createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.Door(new UsedStatesInternalImpls.BlockDoor<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
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
                Blocks.COPPER_DOOR,
                Blocks.EXPOSED_COPPER_DOOR,
                Blocks.OXIDIZED_COPPER_DOOR,
                Blocks.WEATHERED_COPPER_DOOR,
                Blocks.WAXED_COPPER_DOOR,
                Blocks.WAXED_EXPOSED_COPPER_DOOR,
                Blocks.WAXED_OXIDIZED_COPPER_DOOR,
                Blocks.WAXED_WEATHERED_COPPER_DOOR
            };
        }
        return claimableBlocks;
    }

}
