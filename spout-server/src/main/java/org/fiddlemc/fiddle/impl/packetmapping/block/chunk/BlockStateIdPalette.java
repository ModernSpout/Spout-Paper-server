package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import ca.spottedleaf.dataconverter.util.IntegerUtil;
import net.minecraft.network.VarInt;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import java.util.Arrays;

/**
 * A palette of block state ids.
 *
 * <p>
 * It may contain duplicates.
 * </p>
 *
 * <p>
 * An instance of this class is reusable, but {@link #clear} must be called after usage.
 * </p>
 */
public sealed class BlockStateIdPalette permits DoubleMappedBlockStateIdPalette {

    /**
     * The block state ids.
     */
    private final int[] blockStateIds = new int[4096];

    /**
     * The number of block state ids present.
     */
    private short size;

    BlockStateIdPalette() {
    }

    public byte getRawMinimalBitsPerEntry() {
        return (byte) IntegerUtil.ceilLog2(this.size);
    }

    public byte getValidMinimalBitsPerEntry(byte globalPaletteBitsPerEntry) {
        byte rawBitsPerEntry = this.getRawMinimalBitsPerEntry();
        if (rawBitsPerEntry == 0) {
            return 0;
        } else if (rawBitsPerEntry <= 4) {
            return 4;
        } else if (rawBitsPerEntry <= 8) {
            return rawBitsPerEntry;
        }
        return globalPaletteBitsPerEntry;
    }

    public short size() {
        return this.size;
    }

    public int getBlockStateId(int index) {
        return this.blockStateIds[index];
    }

    public void clear() {
        this.size = 0;
    }

    public short add(int blockStateId) {
        short index = this.size++;
        this.blockStateIds[index] = blockStateId;
        return index;
    }

    public void writeAsIndirectPaletteWithoutBitsPerEntry(ChunkPacketBlockMapperWriter writer) {
        writer.writeVarInt(this.size);
        for (int i = 0; i < this.size; i++) {
            writer.writeVarInt(this.blockStateIds[i]);
        }
    }

    public int getIndirectHeaderSizeInBytes() {
        int headerSize = VarInt.getByteSize(this.size);
        for (int i = 0; i < this.size; i++) {
            headerSize += VarInt.getByteSize(this.blockStateIds[i]);
        }
        return headerSize;
    }

    @Override
    public String toString() {
        int[] presentIds = Arrays.copyOf(this.blockStateIds, this.size);
        BlockState[] presentBlockStates = Arrays.stream(presentIds).mapToObj(id -> BlockStateRegistry.get().byId(id)).toArray(BlockState[]::new);
        return Arrays.toString(presentIds) + " -> " + Arrays.toString(presentBlockStates);
    }
}
