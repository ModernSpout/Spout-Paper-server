package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import io.papermc.paper.antixray.ChunkPacketInfo;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import java.util.Arrays;

public abstract class DirectSectionContents implements SectionContents {

    DirectSectionContents() {
        super();
    }

    /**
     * @return The block state id for the given block index.
     */
    public abstract int getBlockStateId(int blockIndex);

    /**
     * @return The non-empty block count for this section contents.
     */
    public abstract short getNonEmptyBlockCount();

    /**
     * @return A valid but minimal bits per entry for these contents.
     */
    public abstract byte getValidMinimalBitsPerEntry(byte globalPaletteBitsPerEntry);

    /**
     * @return The exact serialized size of the paletted container of these contents,
     * in bytes, assuming the given number of bits per entry.
     */
    public abstract int getPalettedContainerSizeInBytes(byte bitsPerEntry);

    private static final int[] AIR_BLOCK_STATE_IDS;

    static {
        int[] blockStateIds = new int[BlockStateRegistry.get().size()];
        int[] count = {0}; // Array to make effectively final
        BlockStateRegistry.get().forEach(blockState -> {
            if (blockState.isAir()) {
                blockStateIds[count[0]++] = blockState.indexInBlockStateRegistry;
            }
        });
        AIR_BLOCK_STATE_IDS = Arrays.copyOf(blockStateIds, count[0]);
    }

    protected static boolean isNonEmptyBlockStateId(int blockStateId) {
        for (int airBlockStateId : AIR_BLOCK_STATE_IDS) {
            if (blockStateId == airBlockStateId) {
                return false;
            }
        }
        return true;
    }

    protected void writeAsSingleValuedPalettedContainer(ChunkPacketBlockMapperWriter writer) {
        writer.writeByte((byte) 0);
        writer.writeVarInt(this.getBlockStateId(0));
    }

    protected abstract void writeAsPalettedContainerInternal(ChunkPacketBlockMapperWriter writer, byte bitsPerEntry);

    public void writeAsPalettedContainer(ChunkPacketBlockMapperWriter writer, int section, ChunkPacketInfo<BlockState> chunkPacketInfo, ChunkPacketBlockMapperReader reader, byte globalPaletteBitsPerEntry) {
        // Determine the bits per entry
        byte bitsPerEntry = this.getValidMinimalBitsPerEntry(globalPaletteBitsPerEntry);
        // Increase write buffer capacity if needed
        int sizeInBytes = this.getPalettedContainerSizeInBytes(bitsPerEntry);
        int chunkSectionsCount = chunkPacketInfo.palettes.length;
        int bytesToReadBeforeNextCheck = chunkPacketInfo.sectionEndIndexes[section] - reader.getIndex() + (section == chunkSectionsCount - 1 ? 0 : 2); // Biomes paletted container + non-empty block count of the next section
        writer.increaseCapacityIfNeeded(sizeInBytes + bytesToReadBeforeNextCheck, bytesToReadBeforeNextCheck); // This section contents + same as bytes to read
        // Write to the buffer
        this.writeAsPalettedContainerInternal(writer, bitsPerEntry);
    }

}
