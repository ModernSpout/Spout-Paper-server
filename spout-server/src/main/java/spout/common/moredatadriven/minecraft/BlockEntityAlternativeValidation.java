package spout.common.moredatadriven.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Provides alternative validation for the {@link BlockEntityType#isValid}.
 */
public final class BlockEntityAlternativeValidation {

    private BlockEntityAlternativeValidation() {
        throw new UnsupportedOperationException();
    }

    private static boolean skipValidation = false;

    private static final Map<BlockEntityType<?>, Set<Block>> alternativelyValid = new HashMap<>(1);

    public static boolean isAlternativelyValid(BlockEntityType<?> blockEntityType, BlockState state) {
        if (skipValidation) return true;
        Set<Block> blocks = alternativelyValid.get(blockEntityType);
        return blocks != null && blocks.contains(state.getBlock());
    }

    public static Collection<Block> getAlternativelyValidBlocks(BlockEntityType<?> blockEntityType) {
        return alternativelyValid.getOrDefault(blockEntityType, Collections.emptySet());
    }

    public static void clear() {
        alternativelyValid.clear();
    }

    public static void update(Stream<Block> blocks) {
        clear();
        blocks.forEach(block -> {
            if (block instanceof EntityBlock entityBlock) {
                skipValidation = true;
                BlockEntity entity = entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
                skipValidation = false;
                if (entity != null) {
                    alternativelyValid.computeIfAbsent(entity.getType(), $ -> new HashSet<>(1)).add(block);
                }
            }
        });
    }

}
