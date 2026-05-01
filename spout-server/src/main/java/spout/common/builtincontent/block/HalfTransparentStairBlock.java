package spout.common.builtincontent.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import spout.common.moredatadriven.minecraft.type.BlockCodecs;

/**
 * A union of {@link StairBlock} and {@link HalfTransparentBlock}.
 */
public class HalfTransparentStairBlock extends StairBlock {

    public static final MapCodec<HalfTransparentStairBlock> CODEC = BlockCodecs.stairCodec(HalfTransparentStairBlock::new);

    protected HalfTransparentStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    public MapCodec<? extends HalfTransparentStairBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

}
