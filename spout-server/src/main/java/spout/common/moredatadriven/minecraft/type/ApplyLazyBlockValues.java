package spout.common.moredatadriven.minecraft.type;

import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
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
            // Set turnsInto for BrushableBlock
            if (block instanceof BrushableBlock brushableBlock) {
                Identifier turnsIntoIdentifier = brushableBlock.spout$getTurnsIntoIdentifier();
                if (turnsIntoIdentifier != null) {
                    Block turnsInto = BuiltInRegistries.BLOCK.getValue(turnsIntoIdentifier);
                    brushableBlock.turnsInto = turnsInto;
                    brushableBlock.spout$setTurnsIntoIdentifier(null);
                }
            }
            // Set baseState for StairBlock
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
