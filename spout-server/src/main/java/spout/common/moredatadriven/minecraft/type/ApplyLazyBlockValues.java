package spout.common.moredatadriven.minecraft.type;

import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.InfestedBlock;
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
            if (block instanceof BrushableBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.turnsInto = lazyValue;
                }
            }
            // Set candleBlock for CandleCakeBlock
            if (block instanceof CandleCakeBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    CandleBlock lazyValue = (CandleBlock) Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    CandleCakeBlock.BY_CANDLE.put(lazyValue, castBlock);
                    castBlock.candleBlock = lazyValue;
                }
            }
            // Set plant for ChorusFlowerBlock
            if (block instanceof ChorusFlowerBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.plant = lazyValue;
                }
            }
            // Set concrete for ConcretePowderBlock
            if (block instanceof ConcretePowderBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.concrete = lazyValue;
                }
            }
            // Set deadBlock for CoralBlock
            if (block instanceof CoralBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.deadBlock = lazyValue;
                }
            }
            // Set deadBlock for CoralFanBlock
            if (block instanceof CoralFanBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.deadBlock = lazyValue;
                }
            }
            // Set deadBlock for CoralPlantBlock
            if (block instanceof CoralPlantBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.deadBlock = lazyValue;
                }
            }
            // Set deadBlock for CoralWallFanBlock
            if (block instanceof CoralWallFanBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    castBlock.deadBlock = lazyValue;
                }
            }
            // Set potted for FlowerPotBlock
            if (block instanceof FlowerPotBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    FlowerPotBlock.POTTED_BY_CONTENT.put(lazyValue, castBlock);
                    castBlock.potted = lazyValue;
                }
            }
            // Set hostBlock for InfestedBlock
            if (block instanceof InfestedBlock castBlock) {
                Identifier temporaryValue = TemporaryValuesForLazyValues.pollBlockIdentifier(castBlock);
                if (temporaryValue != null) {
                    Block lazyValue = Objects.requireNonNull(BuiltInRegistries.BLOCK.getValue(temporaryValue));
                    InfestedBlock.BLOCK_BY_HOST_BLOCK.put(lazyValue, castBlock);
                    castBlock.hostBlock = lazyValue;
                }
            }
            // Set baseState for StairBlock
            if (block instanceof StairBlock castBlock) {
                String temporaryValue = TemporaryValuesForLazyValues.pollBlockString(castBlock);
                if (temporaryValue != null) {
                    BlockState lazyValue = BlockStateStringConversion.blockStateFromString(temporaryValue);
                    castBlock.baseState = lazyValue;
                    castBlock.base = lazyValue.getBlock();
                }
            }
        });
    }

}
