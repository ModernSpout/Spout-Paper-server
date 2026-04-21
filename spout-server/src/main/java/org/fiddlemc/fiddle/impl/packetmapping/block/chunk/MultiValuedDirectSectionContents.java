package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

/**
 * {@link DirectSectionContents} where each block has a particular block state id.
 */
public final class MultiValuedDirectSectionContents extends DirectSectionContents {

    /**
     * The palette of this section contents.
     */
    private final DoubleMappedBlockStateIdPalette palette;

    /**
     * A mapping (as an array of length 4096) of each block index to a block state id.
     */
    private final int[] blockIndexToBlockStateId;

    MultiValuedDirectSectionContents(DoubleMappedBlockStateIdPalette palette, int[] blockIndexToBlockStateId) {
        super();
        this.palette = palette;
        this.blockIndexToBlockStateId = blockIndexToBlockStateId;
    }

    @Override
    public int getBlockStateId(int blockIndex) {
        return this.blockIndexToBlockStateId[blockIndex];
    }

    public void setBlockStateId(int blockIndexInSection, int blockStateId) {
        this.palette.add(blockStateId);
        this.blockIndexToBlockStateId[blockIndexInSection] = blockStateId;
    }

    @Override
    public short getNonEmptyBlockCount() {
        short nonEmptyBlockCount = 0;
        for (int blockIndex = 0; blockIndex < 4096; blockIndex++) {
            if (isNonEmptyBlockStateId(this.blockIndexToBlockStateId[blockIndex])) {
                nonEmptyBlockCount++;
            }
        }
        return nonEmptyBlockCount;
    }

    @Override
    public byte getValidMinimalBitsPerEntry(byte globalPaletteBitsPerEntry) {
        return this.palette.getValidMinimalBitsPerEntry(globalPaletteBitsPerEntry);
    }

    @Override
    public int getPalettedContainerSizeInBytes(byte bitsPerEntry) {
        return PalettedContainerUtilities.getSizeInBytes(false, bitsPerEntry, this.palette);
    }

    private void writeAsIndirectOrDirectPalettedContainer(ChunkPacketBlockMapperWriter writer, byte bitsPerEntry) {

        // Write the bits per entry
        writer.writeByte(bitsPerEntry);

        // Write the palette values if indirect
        boolean isIndirect = bitsPerEntry <= 8;
        if (isIndirect) {
            this.palette.writeAsIndirectPaletteWithoutBitsPerEntry(writer);
        }

        // Write the bit storage
        writer.startWritingBitStorage(bitsPerEntry);
        if (isIndirect) {
            for (int blockIndex = 0; blockIndex < 4096; blockIndex++) {
                writer.writeToBitStorage(this.palette.getIndex(this.blockIndexToBlockStateId[blockIndex]));
            }
        } else {
            for (int blockIndex = 0; blockIndex < 4096; blockIndex++) {
                writer.writeToBitStorage(this.blockIndexToBlockStateId[blockIndex]);
            }
        }
        writer.stopWritingBitStorage();

    }

    @Override
    protected void writeAsPalettedContainerInternal(final ChunkPacketBlockMapperWriter writer, final byte bitsPerEntry) {
        if (bitsPerEntry == 0) {
            // Single valued palette
            this.writeAsSingleValuedPalettedContainer(writer);
        } else {
            this.writeAsIndirectOrDirectPalettedContainer(writer, bitsPerEntry);
        }
    }

    @Override
    public void clear() {
        this.palette.clear();
    }

}
