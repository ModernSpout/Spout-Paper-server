package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import java.util.Arrays;

/**
 * A {@link BlockStateIdPalette} that is also inversely mapped.
 *
 * <p>
 * It cannot contain duplicates.
 * </p>
 */
public final class DoubleMappedBlockStateIdPalette extends BlockStateIdPalette {

    /**
     * The inverse of {@link #getBlockStateId}: which maps each block state id to its index,
     * or -1 if this palette does not contain that block state id.
     */
    private final short[] invertedBlockStateIds = new short[BlockStateRegistry.get().size()];

    DoubleMappedBlockStateIdPalette() {
        super();
        Arrays.fill(this.invertedBlockStateIds, (short) -1);
    }

    public short getIndex(int blockStateId) {
        return this.invertedBlockStateIds[blockStateId];
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.size(); i++) {
            this.invertedBlockStateIds[this.getBlockStateId(i)] = -1;
        }
        super.clear();
    }

    @Override
    public short add(int blockStateId) {
        short index = this.invertedBlockStateIds[blockStateId];
        if (index >= 0) {
            return index;
        }
        index = super.add(blockStateId);
        this.invertedBlockStateIds[blockStateId] = index;
        return index;
    }

}
