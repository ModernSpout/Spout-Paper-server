package org.fiddlemc.fiddle.impl.content.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A union of {@link StairBlock} and {@link HalfTransparentBlock}.
 */
public class HalfTransparentStairBlock extends StairBlock {

    public static final MapCodec<HalfTransparentStairBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(com.mojang.serialization.Codec.STRING.fieldOf("base_state").forGetter(stairBlock -> stairBlock.baseStateString), propertiesCodec())
            .apply(instance, HalfTransparentStairBlock::new)
    );

    protected HalfTransparentStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    protected HalfTransparentStairBlock(String baseStateString, Properties properties) {
        super(baseStateString, properties);
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
