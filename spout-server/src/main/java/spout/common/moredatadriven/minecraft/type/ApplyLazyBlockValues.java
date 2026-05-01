package spout.common.moredatadriven.minecraft.type;

import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A utility class that applies lazily applied block values,
 * such as the base state of stairs.
 */
public final class ApplyLazyBlockValues {

    private ApplyLazyBlockValues() {
        throw new UnsupportedOperationException();
    }

    public static void apply(Stream<Block> blocks) {
        blocks.forEach(block -> {
            // Set base state for stairs
            if (block instanceof StairBlock stairBlock) {
                String baseStateString = stairBlock.spout$getBaseStateString();
                if (baseStateString != null) {
                    BlockState baseState = BlockStateStringConversion.blockStateFromString(baseStateString);
                    stairBlock.baseState = baseState;
                    stairBlock.base = baseState.getBlock();
                    stairBlock.spout$setBaseStateString(null);
                }
            }
        });
    }

}
