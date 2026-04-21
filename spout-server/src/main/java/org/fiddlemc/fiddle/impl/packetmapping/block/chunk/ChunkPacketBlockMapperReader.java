package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import io.netty.buffer.Unpooled;
import io.papermc.paper.antixray.BitStorageReader;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A buffer reader for {@link ChunkPacketBlockMapper}.
 */
public final class ChunkPacketBlockMapperReader {

    /**
     * The buffer currently being read from.
     */
    private final byte[] buffer;

    /**
     * A {@link FriendlyByteBuf} wrapping {@link #buffer}.
     */
    final FriendlyByteBuf friendlyByteBuf;

    /**
     * A {@link BitStorageReader} wrapping {@link #buffer}.
     */
    private final BitStorageReader bitStorageReader;

    /**
     * The last value passed to {@link BitStorageReader#setBits}.
     */
    private byte bitStorageBitsPerEntry;

    /**
     * The number of data elements read with {@link #readFromBitStorage}
     * since calling {@link #startReadingBitStorage}.
     */
    private int elementsReadFromBitStorageCount;

    ChunkPacketBlockMapperReader(byte[] buffer) {
        this.buffer = buffer;
        this.friendlyByteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
        this.friendlyByteBuf.readerIndex(0);
        this.bitStorageReader = new BitStorageReader();
        this.bitStorageReader.setBuffer(this.buffer);
    }

    public int size() {
        return this.buffer.length;
    }

    public int getIndex() {
        return this.friendlyByteBuf.readerIndex();
    }

    public void setIndex(int index) {
        this.friendlyByteBuf.readerIndex(index);
    }

    public byte readByte() {
        return this.friendlyByteBuf.readByte();
    }

    public short readShort() {
        return this.friendlyByteBuf.readShort();
    }

    public int readVarInt() {
        return this.friendlyByteBuf.readVarInt();
    }

    public void startReadingBitStorage(byte bitsPerEntry) {
        this.startReadingBitStorage(this.getIndex(), bitsPerEntry);
    }

    public void startReadingBitStorage(int index, byte bitsPerEntry) {
        this.bitStorageReader.setIndex(index);
        this.bitStorageReader.setBits(bitsPerEntry);
        this.bitStorageBitsPerEntry = bitsPerEntry;
        this.elementsReadFromBitStorageCount = 0;
    }

    public int readFromBitStorage() {
        this.elementsReadFromBitStorageCount++;
        return this.bitStorageReader.read();
    }

    public void stopReadingBitStorage() {
        // Forward the FriendlyByteBuf to the right index
        int sizeInBytes = BitStorageUtilities.getSizeInBytes(this.elementsReadFromBitStorageCount, this.bitStorageBitsPerEntry);
        this.friendlyByteBuf.readerIndex(this.friendlyByteBuf.readerIndex() + sizeInBytes);
    }

}
