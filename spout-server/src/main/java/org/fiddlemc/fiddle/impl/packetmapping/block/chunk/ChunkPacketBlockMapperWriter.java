package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import io.netty.buffer.Unpooled;
import io.papermc.paper.antixray.BitStorageReader;
import io.papermc.paper.antixray.BitStorageWriter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import java.util.Arrays;

/**
 * A buffer writer for {@link ChunkPacketBlockMapper}.
 */
public final class ChunkPacketBlockMapperWriter {

    /**
     * The buffer currently being written to.
     */
    private byte[] buffer;

    /**
     * A {@link FriendlyByteBuf} wrapping {@link #buffer}.
     */
    private FriendlyByteBuf friendlyByteBuf;

    /**
     * A {@link BitStorageWriter} wrapping {@link #buffer}.
     */
    private final BitStorageWriter bitStorageWriter;

    /**
     * The last value passed to {@link BitStorageReader#setBits}.
     */
    private byte bitStorageBitsPerEntry;

    /**
     * The number of data elements read with {@link #writeToBitStorage}
     * since calling {@link #startWritingBitStorage}.
     */
    private int elementsWrittenToBitStorageCount;

    /**
     * The corresponding {@link ChunkPacketBlockMapperReader}.
     */
    private final ChunkPacketBlockMapperReader reader;

    /**
     * Whether the {@link #buffer} is the same instance
     * as that was passed to the corresponding {@link #reader}.
     */
    private boolean sharesBufferWithReader = true;

    /**
     * @param buffer The buffer to write to, which is initially the same as the buffer passed
     *               to the corresponding {@code reader}.
     */
    ChunkPacketBlockMapperWriter(byte[] buffer, ChunkPacketBlockMapperReader reader) {
        this.buffer = buffer;
        this.friendlyByteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
        this.friendlyByteBuf.ensureWritable(5, true);
        this.friendlyByteBuf.writerIndex(0);
        this.bitStorageWriter = new BitStorageWriter();
        this.bitStorageWriter.setBuffer(this.buffer);
        this.reader = reader;
    }

    public void storeIn(ClientboundLevelChunkPacketData chunkData) {
        chunkData.buffer = this.buffer;
        chunkData.bufferLength = this.getIndex();
    }

    public int getIndex() {
        return this.friendlyByteBuf.writerIndex();
    }

    public void setIndex(int index) {
        this.friendlyByteBuf.writerIndex(index);
    }

    public void increaseCapacity(int bytesLeftToWriteLowerBound) {

        // Calculate the new size
        int index = this.getIndex();
        int requiredBufferSizeLowerBound = index + bytesLeftToWriteLowerBound;
        int newBufferSize = Math.max(this.buffer.length, requiredBufferSizeLowerBound) * 3 / 2 + 100;

        // Replace the buffers and its wrappers
        this.buffer = Arrays.copyOf(this.buffer, newBufferSize);
        this.friendlyByteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
        this.friendlyByteBuf.writerIndex(index);
        this.bitStorageWriter.setBuffer(this.buffer);
        this.sharesBufferWithReader = false;

    }

    public void increaseCapacityIfNeeded(int bytesToWriteBeforeNextCheck, int bytesToReadBeforeNextCheck) {
        if ((this.sharesBufferWithReader && this.getIndex() + bytesToWriteBeforeNextCheck > reader.getIndex() + bytesToReadBeforeNextCheck) || this.getIndex() + bytesToWriteBeforeNextCheck > this.buffer.length) {
            int bytesLeftToRead = this.reader.size() - this.reader.getIndex();
            this.increaseCapacity(bytesToWriteBeforeNextCheck + bytesLeftToRead - bytesToReadBeforeNextCheck);
        }
    }

    public void writeByte(byte value) {
        this.friendlyByteBuf.writeByte(value);
    }

    public void writeShort(short value) {
        this.friendlyByteBuf.writeShort(value);
    }

    public void writeVarInt(int value) {
        this.friendlyByteBuf.writeVarInt(value);
    }

    public void writeBytesFromReader(int length) {
        this.friendlyByteBuf.writeBytes(this.reader.friendlyByteBuf, length);
    }

    public void startWritingBitStorage(byte bitsPerEntry) {
        this.startWritingBitStorage(this.getIndex(), bitsPerEntry);
    }

    public void startWritingBitStorage(int index, byte bitsPerEntry) {
        this.bitStorageWriter.setIndex(index);
        this.bitStorageWriter.setBits(bitsPerEntry);
        this.bitStorageBitsPerEntry = bitsPerEntry;
        this.elementsWrittenToBitStorageCount = 0;
    }

    public void writeToBitStorage(int value) {
        this.elementsWrittenToBitStorageCount++;
        this.bitStorageWriter.write(value);
    }

    public void stopWritingBitStorage() {
        // Flush the bit storage changes
        this.bitStorageWriter.flush();
        // Forward the FriendlyByteBuf to the right index
        int sizeInBytes = BitStorageUtilities.getSizeInBytes(this.elementsWrittenToBitStorageCount, this.bitStorageBitsPerEntry);
        this.friendlyByteBuf.writerIndex(this.friendlyByteBuf.writerIndex() + sizeInBytes);
    }

    public boolean sharesBufferWithReader() {
        return this.sharesBufferWithReader;
    }

}
