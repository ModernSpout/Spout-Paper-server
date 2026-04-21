package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

public class PaletteIndexedSectionContents implements SectionContents {

    /**
     * The palette of this section contents.
     */
    public final DoubleMappedBlockStateIdPalette palette;

    /**
     * A mapping (as an array of length 4096) of each block index to an index in the {@link #palette}.
     */
    private final int[] blockIndexToPaletteIndex;

    PaletteIndexedSectionContents(DoubleMappedBlockStateIdPalette palette, int[] blockIndexToPaletteIndex) {
        super();
        this.palette = palette;
        this.blockIndexToPaletteIndex = blockIndexToPaletteIndex;
    }

    /**
     * @return The palette index for the given block index.
     */
    public int getPaletteIndex(int blockIndex) {
        return this.blockIndexToPaletteIndex[blockIndex];
    }

    /**
     * Reads from an indirect or direct paletted container on the buffer.
     */
    public void readFromIndirectOrDirectPalettedContainer(ChunkPacketBlockMapperReader reader, byte bitsPerEntry) {
        if (bitsPerEntry <= 8) {
            this.readFromIndirectPalettedContainer(reader, bitsPerEntry);
        } else {
            this.readFromDirectPalettedContainer(reader, bitsPerEntry);
        }
    }

    /**
     * Reads from an indirect paletted container on the buffer.
     */
    private void readFromIndirectPalettedContainer(ChunkPacketBlockMapperReader reader, byte bitsPerEntry) {
        // Read the palette
        int paletteSize = reader.readVarInt();
        for (int i = 0; i < paletteSize; i++) {
            this.palette.add(reader.readVarInt());
        }
        // Read the palette indices
        this.readFromDataArrayOfLongsIntoPaletteIndices(reader, bitsPerEntry);
    }

    /**
     * Reads from a direct paletted container on the buffer.
     */
    private void readFromDirectPalettedContainer(ChunkPacketBlockMapperReader reader, byte bitsPerEntry) {
        // Temporarily read the block state ids into the palette indices array
        this.readFromDataArrayOfLongsIntoPaletteIndices(reader, bitsPerEntry);
        // Transform the block states array into a palette and palette indices array
        for (int blockIndex = 0; blockIndex < 4096; blockIndex++) {
            this.blockIndexToPaletteIndex[blockIndex] = this.palette.add(this.blockIndexToPaletteIndex[blockIndex]);
        }
    }

    private void readFromDataArrayOfLongsIntoPaletteIndices(ChunkPacketBlockMapperReader reader, byte bitsPerEntry) {
        // Read the values
        reader.startReadingBitStorage(bitsPerEntry);
        for (int blockIndex = 0; blockIndex < 4096; blockIndex++) {
            this.blockIndexToPaletteIndex[blockIndex] = reader.readFromBitStorage();
        }
        reader.stopReadingBitStorage();
    }

    @Override
    public void clear() {
        this.palette.clear();
    }

}
