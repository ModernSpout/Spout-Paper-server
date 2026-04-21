package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import net.minecraft.network.VarInt;

/**
 * {@link DirectSectionContents} where each block has the same block state id.
 */
public final class SingleValuedDirectSectionContents extends DirectSectionContents {

    /**
     * The single block state id.
     */
    private int blockStateId;

    SingleValuedDirectSectionContents() {
        super();
    }

    @Override
    public int getBlockStateId(int blockIndex) {
        return this.blockStateId;
    }

    public SingleValuedDirectSectionContents setBlockStateId(int blockStateId) {
        this.blockStateId = blockStateId;
        return this;
    }

    @Override
    public short getNonEmptyBlockCount() {
        return isNonEmptyBlockStateId(this.blockStateId) ? (short) 4096 : 0;
    }

    @Override
    public byte getValidMinimalBitsPerEntry(byte globalPaletteBitsPerEntry) {
        return 0;
    }

    @Override
    public int getPalettedContainerSizeInBytes(byte bitsPerEntry) {
        return 1 + VarInt.getByteSize(this.blockStateId);
    }

    @Override
    protected void writeAsPalettedContainerInternal(final ChunkPacketBlockMapperWriter writer, final byte bitsPerEntry) {
        this.writeAsSingleValuedPalettedContainer(writer);
    }

    @Override
    public void clear() {
    }

}
