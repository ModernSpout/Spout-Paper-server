package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.fiddlemc.fiddle.impl.packetmapping.block.claim.VisualDuplicatesImpl;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RequestProcessor} for {@link FullBlockRequestBuilderImpl}s.
 */
public class FullBlockRequestProcessor extends ArrayBlockStateClaimAttemptsRequestProcessor<UsedStates.Single, FullBlockRequestBuilderImpl> {

    public FullBlockRequestProcessor(FullBlockRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected BlockState[][] createStatesToClaim() {
        BlockState[] claimableStates = claimableStates();
        BlockState[] validStates = new BlockState[claimableStates.length];
        int validStateCount = 0;
        for (BlockState candidate : claimableStates) {
            if (BlockRequestProcessUtils.isValidProxyCandidate(this.request.from, candidate)) {
                validStates[validStateCount++] = candidate;
            }
        }
        ObjectArrays.stableSort(validStates, 0, validStateCount, (state1, state2) -> BlockRequestProcessUtils.compareProxyCandidates(this.request.from, state1, state2));
        BlockState[][] statesToClaim = new BlockState[validStateCount][];
        for (int i = 0; i < validStateCount; i++) {
            statesToClaim[i] = new BlockState[]{validStates[i]};
        }
        return statesToClaim;
    }

    @Override
    protected UsedStates.Single createUsedStates(BlockState @Nullable [] result) {
        return result != null ? new UsedStatesImpl.SingleImpl(result[0], false) : new UsedStatesImpl.SingleImpl(this.request.fallback, true);
    }

    /**
     * The return value of {@link #claimableStates()},
     * or null if not initialized yet.
     *
     * <p>
     * This may contain some states that are not actually claimable.
     * </p>
     */
    private static BlockState @Nullable [] claimableStates;

    /**
     * @return The list of {@link BlockState}s that can be attempted to be claimed
     * by this class.
     */
    private static BlockState[] claimableStates() {
        if (claimableStates == null) {
            // Build the claimable states
            List<BlockState> claimableStates = new ArrayList<>();
            // Blocks for which every block state is a full block
            List.of(
                // Beehive and bee nest
                Blocks.BEEHIVE,
                Blocks.BEE_NEST,
                // Copper
                Blocks.CHISELED_COPPER,
                Blocks.COPPER_BLOCK,
                Blocks.COPPER_GRATE,
                Blocks.CUT_COPPER,
                Blocks.EXPOSED_CHISELED_COPPER,
                Blocks.EXPOSED_COPPER,
                Blocks.EXPOSED_COPPER_GRATE,
                Blocks.EXPOSED_CUT_COPPER,
                Blocks.OXIDIZED_CHISELED_COPPER,
                Blocks.OXIDIZED_COPPER,
                Blocks.OXIDIZED_COPPER_GRATE,
                Blocks.OXIDIZED_CUT_COPPER,
                Blocks.WEATHERED_CHISELED_COPPER,
                Blocks.WEATHERED_COPPER,
                Blocks.WEATHERED_COPPER_GRATE,
                Blocks.WEATHERED_CUT_COPPER,
                Blocks.WAXED_CHISELED_COPPER,
                Blocks.WAXED_COPPER_BLOCK,
                Blocks.WAXED_COPPER_GRATE,
                Blocks.WAXED_CUT_COPPER,
                Blocks.WAXED_EXPOSED_CHISELED_COPPER,
                Blocks.WAXED_EXPOSED_COPPER,
                Blocks.WAXED_EXPOSED_COPPER_GRATE,
                Blocks.WAXED_EXPOSED_CUT_COPPER,
                Blocks.WAXED_OXIDIZED_CHISELED_COPPER,
                Blocks.WAXED_OXIDIZED_COPPER,
                Blocks.WAXED_OXIDIZED_COPPER_GRATE,
                Blocks.WAXED_OXIDIZED_CUT_COPPER,
                Blocks.WAXED_WEATHERED_CHISELED_COPPER,
                Blocks.WAXED_WEATHERED_COPPER,
                Blocks.WAXED_WEATHERED_COPPER_GRATE,
                Blocks.WAXED_WEATHERED_CUT_COPPER,
                // Copper bulb
                Blocks.COPPER_BULB,
                Blocks.EXPOSED_COPPER_BULB,
                Blocks.OXIDIZED_COPPER_BULB,
                Blocks.WEATHERED_COPPER_BULB,
                Blocks.WAXED_COPPER_BULB,
                Blocks.WAXED_EXPOSED_COPPER_BULB,
                Blocks.WAXED_OXIDIZED_COPPER_BULB,
                Blocks.WAXED_WEATHERED_COPPER_BULB,
                // Creaking heart
                Blocks.CREAKING_HEART,
                // Dispenser and dropper
                Blocks.DISPENSER,
                Blocks.DROPPER,
                // Every state is the same
                Blocks.JUKEBOX,
                Blocks.NOTE_BLOCK,
                Blocks.TARGET,
                Blocks.TNT,
                // Infested
                Blocks.CHISELED_STONE_BRICKS,
                Blocks.COBBLESTONE,
                Blocks.CRACKED_STONE_BRICKS,
                Blocks.DEEPSLATE,
                Blocks.MOSSY_STONE_BRICKS,
                Blocks.STONE,
                Blocks.STONE_BRICKS,
                Blocks.INFESTED_CHISELED_STONE_BRICKS,
                Blocks.INFESTED_COBBLESTONE,
                Blocks.INFESTED_CRACKED_STONE_BRICKS,
                Blocks.INFESTED_DEEPSLATE,
                Blocks.INFESTED_MOSSY_STONE_BRICKS,
                Blocks.INFESTED_STONE,
                Blocks.INFESTED_STONE_BRICKS,
                // Snowy mycelium and podzol
                Blocks.GRASS_BLOCK,
                Blocks.MYCELIUM,
                Blocks.PODZOL
            ).forEach(block -> claimableStates.addAll(block.getStateDefinition().getPossibleStates()));
            // Slabs
            List.of(
                Blocks.ACACIA_SLAB,
                Blocks.ANDESITE_SLAB,
                Blocks.BAMBOO_MOSAIC_SLAB,
                Blocks.BAMBOO_SLAB,
                Blocks.BIRCH_SLAB,
                Blocks.BLACKSTONE_SLAB,
                Blocks.BRICK_SLAB,
                Blocks.CHERRY_SLAB,
                Blocks.COBBLED_DEEPSLATE_SLAB,
                Blocks.COBBLESTONE_SLAB,
                Blocks.CRIMSON_SLAB,
                Blocks.CUT_COPPER_SLAB,
                Blocks.CUT_RED_SANDSTONE_SLAB,
                Blocks.CUT_SANDSTONE_SLAB,
                Blocks.DARK_OAK_SLAB,
                Blocks.DARK_PRISMARINE_SLAB,
                Blocks.DEEPSLATE_BRICK_SLAB,
                Blocks.DEEPSLATE_TILE_SLAB,
                Blocks.DIORITE_SLAB,
                Blocks.END_STONE_BRICK_SLAB,
                Blocks.EXPOSED_CUT_COPPER_SLAB,
                Blocks.GRANITE_SLAB,
                Blocks.JUNGLE_SLAB,
                Blocks.MANGROVE_SLAB,
                Blocks.MOSSY_COBBLESTONE_SLAB,
                Blocks.MOSSY_STONE_BRICK_SLAB,
                Blocks.MUD_BRICK_SLAB,
                Blocks.NETHER_BRICK_SLAB,
                Blocks.OAK_SLAB,
                Blocks.OXIDIZED_CUT_COPPER_SLAB,
                Blocks.PALE_OAK_SLAB,
                Blocks.PETRIFIED_OAK_SLAB,
                Blocks.POLISHED_ANDESITE_SLAB,
                Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
                Blocks.POLISHED_BLACKSTONE_SLAB,
                Blocks.POLISHED_DEEPSLATE_SLAB,
                Blocks.POLISHED_DIORITE_SLAB,
                Blocks.POLISHED_GRANITE_SLAB,
                Blocks.POLISHED_TUFF_SLAB,
                Blocks.PRISMARINE_BRICK_SLAB,
                Blocks.PRISMARINE_SLAB,
                Blocks.PURPUR_SLAB,
                Blocks.QUARTZ_SLAB,
                Blocks.RED_NETHER_BRICK_SLAB,
                Blocks.RED_SANDSTONE_SLAB,
                Blocks.RESIN_BRICK_SLAB,
                Blocks.SANDSTONE_SLAB,
                Blocks.SMOOTH_QUARTZ_SLAB,
                Blocks.SMOOTH_RED_SANDSTONE_SLAB,
                Blocks.SMOOTH_SANDSTONE_SLAB,
                Blocks.SPRUCE_SLAB,
                Blocks.STONE_BRICK_SLAB,
                Blocks.STONE_SLAB,
                Blocks.TUFF_BRICK_SLAB,
                Blocks.TUFF_SLAB,
                Blocks.WARPED_SLAB,
                Blocks.WAXED_CUT_COPPER_SLAB,
                Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
                Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                Blocks.WEATHERED_CUT_COPPER_SLAB
            ).forEach(block -> claimableStates.add(block.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE)));
            FullBlockRequestProcessor.claimableStates = claimableStates.toArray(BlockState[]::new);
            ObjectArrays.stableSort(FullBlockRequestProcessor.claimableStates, VisualDuplicatesImpl.VisualDuplicateGroupImpl.STATE_COMPARATOR);
        }
        return claimableStates;
    }

}
