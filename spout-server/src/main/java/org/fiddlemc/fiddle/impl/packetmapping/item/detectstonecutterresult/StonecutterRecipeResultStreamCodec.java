package org.fiddlemc.fiddle.impl.packetmapping.item.detectstonecutterresult;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * Holder for {@link #STREAM_CODEC}.
 */
public final class StonecutterRecipeResultStreamCodec {

    private StonecutterRecipeResultStreamCodec() {
        throw new UnsupportedOperationException();
    }

    /**
     * A {@link StreamCodec} based on {@link ItemStack#STREAM_CODEC} that
     * sets {@link FriendlyByteBuf#isEncodingStonecutterRecipeResult} to true while encoding.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public ItemStack decode(RegistryFriendlyByteBuf buffer) {
            throw new UnsupportedOperationException("This codec must only be used to encode");
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ItemStack value) {
            buffer.isEncodingStonecutterRecipeResult = true;
            ItemStack.STREAM_CODEC.encode(buffer, value);
            buffer.isEncodingStonecutterRecipeResult = false;
        }

    };

}
