package org.fiddlemc.fiddle.impl.content.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A union of {@link StairBlock} and {@link TransparentBlock}.
 */
public class TransparentStairBlock extends HalfTransparentStairBlock {

    public static final MapCodec<TransparentStairBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(com.mojang.serialization.Codec.STRING.fieldOf("base_state").forGetter(stairBlock -> stairBlock.baseStateString), propertiesCodec())
            .apply(instance, TransparentStairBlock::new)
    );

    protected TransparentStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    protected TransparentStairBlock(String baseStateString, Properties properties) {
        super(baseStateString, properties);
    }

    @Override
    public MapCodec<? extends TransparentStairBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

}
