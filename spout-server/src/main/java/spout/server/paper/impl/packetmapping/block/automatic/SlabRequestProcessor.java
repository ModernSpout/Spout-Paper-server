package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#slab}.
 */
public class SlabRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Slab, SlabRequestBuilderImpl> {

    public SlabRequestProcessor(SlabRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return claimableBlocks();
    }

    @Override
    protected UsedStates.Slab createUsedStates(BlockState @Nullable [] result) {
        return new UsedStates.Slab(result != null ? new UsedStatesInternalImpls.Slab<>(result[0].getBlock(), false) : new UsedStatesInternalImpls.Slab<>(this.request.fallback, true));
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
            claimableBlocks = new Block[]{
                // Copper
                Blocks.CUT_COPPER_SLAB,
                Blocks.EXPOSED_CUT_COPPER_SLAB,
                Blocks.OXIDIZED_CUT_COPPER_SLAB,
                Blocks.WEATHERED_CUT_COPPER_SLAB,
                Blocks.WAXED_CUT_COPPER_SLAB,
                Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
                Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                // Petrified oak
                Blocks.OAK_SLAB,
                Blocks.PETRIFIED_OAK_SLAB
            };
        }
        return claimableBlocks;
    }

}
