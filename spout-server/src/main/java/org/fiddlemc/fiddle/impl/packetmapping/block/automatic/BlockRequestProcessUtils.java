package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.fiddlemc.fiddle.impl.packetmapping.block.claim.VisualDuplicatesImpl;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Some utilities for the processing of block requests.
 */
public final class BlockRequestProcessUtils {

    private BlockRequestProcessUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return True if the given {@code proxyCandidate} is potentially, but not certainly, a valid proxy candidate
     * for the given {@code from}, or false if it is definitely not.
     */
    public static boolean isValidProxyCandidate(BlockState from, BlockState proxyCandidate) {
        // Check air
        if (from.isAir() != proxyCandidate.isAir()) {
            return false;
        }
        // Check liquid
        if (from.liquid() != proxyCandidate.liquid()) {
            return false;
        }
        // Check light
        if (from.getLightEmission() != proxyCandidate.getLightEmission()) {
            return false;
        }
        // Cannot use instabreak proxy for non-instabreak from
        if (proxyCandidate.destroySpeed == 0 && from.destroySpeed != 0) {
            return false;
        }
        // Currently no other strict checks
        return true;
    }

    /**
     * @return A measure of logarithmic similarity: 0 is the most similar, higher means less similar.
     */
    private static double getLogarithmicSimilarity(double a, double b) {
        return Math.abs(Math.log(b / a));
    }

    private static final List<TagKey<Block>> TOOL_TAGS = List.of(
        BlockTags.MINEABLE_WITH_AXE,
        BlockTags.MINEABLE_WITH_HOE,
        BlockTags.MINEABLE_WITH_PICKAXE,
        BlockTags.MINEABLE_WITH_SHOVEL
    );

    private static List<TagKey<Block>> getToolTags(BlockState state) {
        if (!state.requiresCorrectToolForDrops()) {
            return Collections.emptyList();
        }
        return state.getTags().filter(TOOL_TAGS::contains).toList();
    }

    public static int compareProxyCandidates(BlockState from, BlockState proxyCandidate1, BlockState proxyCandidate2) {
        // Filter validity
        int validityCompare = -Boolean.compare(
            isValidProxyCandidate(from, proxyCandidate1),
            isValidProxyCandidate(from, proxyCandidate2)
        );
        if (validityCompare != 0) {
            return validityCompare;
        }
        // Commonality
        int commonalityCompare = COMMONALITY_COMPARATOR.compare(proxyCandidate1, proxyCandidate2);
        if (commonalityCompare != 0) {
            return commonalityCompare;
        }
        // Don't choose a double slab unless we really have to
        int doubleSlabCompare = DOUBLE_SLAB_COMPARATOR.compare(proxyCandidate1, proxyCandidate2);
        if (doubleSlabCompare != 0) {
            return doubleSlabCompare;
        }
        // Break speed
        if (from.destroySpeed == 0) {
            // From is instabreak, so choose the lowest destroy time
            return Double.compare(proxyCandidate1.destroySpeed, proxyCandidate2.destroySpeed);
        } else if (Math.abs(proxyCandidate1.destroySpeed - proxyCandidate2.destroySpeed) >= 0.001) {
            // Choose the most similar destroy time
            double similarity1 = getLogarithmicSimilarity(from.destroySpeed, proxyCandidate1.destroySpeed);
            double similarity2 = getLogarithmicSimilarity(from.destroySpeed, proxyCandidate2.destroySpeed);
            if (Math.abs(similarity1 - similarity2) >= 0.001) {
                return Double.compare(similarity1, similarity2);
            }
        }
        // Tool
        List<TagKey<Block>> fromToolTags = getToolTags(from);
        int toolCompare = -Boolean.compare(
            getToolTags(proxyCandidate1).equals(fromToolTags),
            getToolTags(proxyCandidate2).equals(fromToolTags)
        );
        if (toolCompare != 0) {
            return toolCompare;
        }
        // Replaceability
        int replaceableCompare = -Boolean.compare(
            from.canBeReplaced() == proxyCandidate1.canBeReplaced(),
            from.canBeReplaced() == proxyCandidate2.canBeReplaced()
        );
        if (replaceableCompare != 0) {
            return replaceableCompare;
        }
        // Sound
        int soundCompare = -Boolean.compare(
            from.getSoundType() == proxyCandidate1.getSoundType(),
            from.getSoundType() == proxyCandidate2.getSoundType()
        );
        if (soundCompare != 0) {
            return soundCompare;
        }
        // Note block instrument
        int instrumentCompare = -Boolean.compare(
            from.instrument() == proxyCandidate1.instrument(),
            from.instrument() == proxyCandidate2.instrument()
        );
        if (instrumentCompare != 0) {
            return instrumentCompare;
        }
        // No difference
        return 0;
    }

    public static final Comparator<BlockState> COMMONALITY_COMPARATOR = Comparator
            .comparing(state -> VisualDuplicatesImpl.VisualDuplicateGroupImpl.getMoreCommonBlocks().contains(state.getBlock().indexInVanillaOnlyBlockRegistry));

    public static final Comparator<BlockState> DOUBLE_SLAB_COMPARATOR = Comparator
            .comparing(state -> (boolean) state.getOptionalValue(BlockStateProperties.SLAB_TYPE).map(value -> value == SlabType.DOUBLE).orElse(false));

}
