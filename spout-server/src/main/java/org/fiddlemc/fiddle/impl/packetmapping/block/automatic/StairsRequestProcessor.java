package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A {@link RequestProcessor} for {@link StairsRequestBuilderImpl}s.
 */
public class StairsRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.Stairs, StairsRequestBuilderImpl> {

    public StairsRequestProcessor(StairsRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return claimableBlocks();
    }

    @Override
    protected UsedStates.Stairs createUsedStates(BlockState @Nullable [] result) {
        return result != null ? new UsedStatesImpl.StairsImpl(result[0].getBlock(), false) : new UsedStatesImpl.StairsImpl(this.request.fallback, true);
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
