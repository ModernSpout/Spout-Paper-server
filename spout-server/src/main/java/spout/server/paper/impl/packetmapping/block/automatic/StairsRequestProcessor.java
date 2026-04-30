package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#stairs}.
 */
public class StairsRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Stairs, FromToBlockTypeRequestBuilderImpl<UsedStates.Stairs>> {

    public StairsRequestProcessor(FromToBlockTypeRequestBuilderImpl<UsedStates.Stairs> request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return claimableBlocks();
    }

    @Override
    protected UsedStates.Stairs createUsedStates(BlockState @Nullable [] result) {
        boolean isFallback = result == null;
        return new UsedStates.Stairs(new UsedStatesInternalImpls.BlockStairs<>(isFallback ? this.request.fallback : result[0].getBlock(), isFallback));
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
                Blocks.CUT_COPPER_STAIRS,
                Blocks.EXPOSED_CUT_COPPER_STAIRS,
                Blocks.OXIDIZED_CUT_COPPER_STAIRS,
                Blocks.WEATHERED_CUT_COPPER_STAIRS,
                Blocks.WAXED_CUT_COPPER_STAIRS,
                Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
                Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS
            };
        }
        return claimableBlocks;
    }

}
