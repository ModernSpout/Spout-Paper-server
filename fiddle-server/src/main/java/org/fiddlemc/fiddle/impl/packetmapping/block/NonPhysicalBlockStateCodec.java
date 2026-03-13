package org.fiddlemc.fiddle.impl.packetmapping.block;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;

/**
 * A holder for a wrapper for the unmapped block state codec.
 */
public final class NonPhysicalBlockStateCodec {

    public static final StreamCodec<ByteBuf, BlockState> UNMAPPED_STREAM_CODEC = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY);

    public static final StreamCodec<FriendlyByteBuf, BlockState> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public void encode(FriendlyByteBuf buffer, BlockState value) {
            BlockMappingFunctionContext context = new BlockMappingFunctionContextImpl(buffer.getClientViewOrFallback());
            BlockState mappedValue = BlockMappingsImpl.get().apply(value, context);
            UNMAPPED_STREAM_CODEC.encode(buffer, mappedValue);
        }

        @Override
        public BlockState decode(FriendlyByteBuf buffer) {
            return UNMAPPED_STREAM_CODEC.decode(buffer);
        }

    };

}
