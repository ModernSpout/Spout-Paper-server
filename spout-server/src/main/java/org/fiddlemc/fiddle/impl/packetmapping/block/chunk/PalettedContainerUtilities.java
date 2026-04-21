package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import net.minecraft.network.VarInt;

/**
 * Utilities for paletted containers.
 */
public final class PalettedContainerUtilities {

    private PalettedContainerUtilities() {
        throw new UnsupportedOperationException();
    }

    /**
     * Computes the size in bytes to store a paletted container with the given palette and number of bits per entry.
     *
     * @param isBiomes     Whether the paletted container contains biomes.
     * @param bitsPerEntry The number of bits per entry. This must be a valid amount,
     *                     e.g. 0, [4,8] or {@link ChunkPacketBlockMapper#globalPaletteBitsPerEntry} for block states.
     */
    public static int getSizeInBytes(boolean isBiomes, byte bitsPerEntry, BlockStateIdPalette palette) {
        int headerSize;
        int storageSize;
        if (bitsPerEntry == 0) {
            // Single valued
            headerSize = VarInt.getByteSize(palette.getBlockStateId(0));
        } else if (bitsPerEntry <= 3 || (!isBiomes && bitsPerEntry <= 8)) {
            // Indirect
            headerSize = palette.getIndirectHeaderSizeInBytes();
        } else {
            // Direct
            headerSize = 0;
        }
        storageSize = getBitStorageSizeInBytes(isBiomes, bitsPerEntry);
        return 1 + headerSize + storageSize;
    }

    /**
     * Computes the size in bytes of the bit storage of a paletted container with the given number of bits per entry.
     *
     * @param isBiomes     Whether the paletted container contains biomes.
     * @param bitsPerEntry The number of bits per entry. This must be a valid amount,
     *                     e.g. 0, [4,8] or {@link ChunkPacketBlockMapper#globalPaletteBitsPerEntry} for block states.
     */
    public static int getBitStorageSizeInBytes(boolean isBiomes, byte bitsPerEntry) {
        if (bitsPerEntry == 0) {
            return 0;
        }
        return BitStorageUtilities.getSizeInBytes(isBiomes ? 64 : 4096, bitsPerEntry);
    }

    /**
     * Copy the paletted container in the reader at its current index
     * over to the writer at its current index.
     *
     * <p>
     * This method must only be called when it is guaranteed that the writer has enough capacity.
     * </p>
     *
     * @param isBiomes Whether the paletted container contains biomes.
     */
    public static void copy(boolean isBiomes, ChunkPacketBlockMapperReader reader, ChunkPacketBlockMapperWriter writer) {
        boolean doWrite = !writer.sharesBufferWithReader() || reader.getIndex() != writer.getIndex(); // Only write if necessary
        byte bitsPerEntry = reader.readByte();
        if (doWrite) {
            writer.writeByte(bitsPerEntry);
        }
        if (bitsPerEntry == 0) {
            // Single valued
            int value = reader.readVarInt();
            if (doWrite) {
                writer.writeVarInt(value);
            }
        } else {
            if (bitsPerEntry <= 3 || (!isBiomes && bitsPerEntry <= 8)) {
                // Indirect
                int size = reader.readVarInt();
                if (doWrite) {
                    writer.writeVarInt(size);
                    for (int i = 0; i < size; i++) {
                        writer.writeVarInt(reader.readVarInt());
                    }
                } else {
                    for (int i = 0; i < size; i++) {
                        reader.readVarInt();
                    }
                }
            }
            // Indirect or direct
            int bytesToCopy = getBitStorageSizeInBytes(isBiomes, bitsPerEntry);
            if (doWrite) {
                writer.writeBytesFromReader(bytesToCopy);
            } else {
                reader.setIndex(reader.getIndex() + bytesToCopy);
            }
        }
        if (!doWrite) {
            // Match up the writer index to where it should be if we had actually performed the writes
            writer.setIndex(reader.getIndex());
        }
    }

}
