package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

import ca.spottedleaf.dataconverter.util.IntegerUtil;
import io.papermc.paper.antixray.ChunkPacketInfo;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.SingleValuePalette;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappings;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingFunctionContextImpl;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsStep;
import org.fiddlemc.fiddle.impl.packetmapping.block.FunctionBlockMappingsStep;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;

/**
 * Provides functionality to map the blocks in chunk packets.
 *
 * <p>
 * Each instance of this class can also be {@linkplain #applyMappings used} once.
 * </p>
 */
public final class ChunkPacketBlockMapper {

    /**
     * The instance of the {@link BlockMappingsImpl}.
     */
    private final BlockMappingsImpl pipeline = BlockMappingsImpl.get();

    /**
     * The packet currently being processed.
     */
    private final ClientboundLevelChunkWithLightPacket packet;

    /**
     * The lowest x-coordinate of a block in the chunk for which the mappings are currently being run.
     */
    private final int chunkStartX;

    /**
     * The lowest y-coordinate of a block in the chunk for which the mappings are currently being run.
     */
    private final int chunkStartY;

    /**
     * The lowest z-coordinate of a block in the chunk for which the mappings are currently being run.
     */
    private final int chunkStartZ;

    /**
     * The {@link ClientView} for which the mappings are currently being run.
     */
    private final ClientView clientView;

    /**
     * The {@link ClientView.AwarenessLevel#ordinal()} of the {@link #clientView}.
     */
    private final int clientViewAwarenessLevelI;

    /**
     * The number of bits per entry for a {@link PalettedContainer} using the {@link GlobalPalette}.
     */
    public final byte globalPaletteBitsPerEntry;

    /**
     * The {@link ChunkPacketInfo} of the {@link #packet}.
     */
    private final ChunkPacketInfo<BlockState> chunkPacketInfo;

    /**
     * The reader.
     */
    private final ChunkPacketBlockMapperReader reader;

    /**
     * The writer.
     */
    private final ChunkPacketBlockMapperWriter writer;

    public ChunkPacketBlockMapper(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk, ServerPlayer player, ChunkPacketInfo<BlockState> chunkPacketInfo) {
        this.packet = packet;
        this.chunkStartX = packet.getX() << 4;
        this.chunkStartY = chunk.getMinY();
        this.chunkStartZ = packet.getZ() << 4;
        this.clientView = player.getClientViewOrFallback();
        this.clientViewAwarenessLevelI = this.clientView.getAwarenessLevel().ordinal();
        this.globalPaletteBitsPerEntry = (byte) IntegerUtil.ceilLog2((this.clientView.understandsAllServerSideBlocks() ? BlockStateRegistry.get() : VanillaOnlyBlockStateRegistry.get()).size());
        this.chunkPacketInfo = chunkPacketInfo;
        ClientboundLevelChunkPacketData chunkData = packet.getChunkData();
        this.reader = new ChunkPacketBlockMapperReader(chunkData.buffer);
        this.writer = new ChunkPacketBlockMapperWriter(chunkData.buffer, this.reader);
    }

    private void setDone() {
        // Mark the packet as ready
        this.packet.readyMappingBlocks = true;
        // Remove the reference to this mapper to reclaim memory
        this.packet.fiddleChunkPacketBlockMapper = null;
    }

    /**
     * Apply the block mappings registered with the {@link BlockMappings} to the {@link #packet}.
     */
    public void applyMappings() {
        // Do a check over all paletted containers to see if any mappings are necessary at all
        if (!requiresMapping(this.chunkPacketInfo, this.pipeline, this.clientViewAwarenessLevelI, this.reader)) {
            // Skip mapping if there are no block states with mappings
            this.setDone();
            return;
        }
        // Process the sections one by one
        this.applyMappingsToSection(0, this.packet.getChunkData());
    }

    private static boolean requiresMapping(ChunkPacketInfo<BlockState> chunkPacketInfo, BlockMappingsImpl pipeline, int clientViewAwarenessLevelI, ChunkPacketBlockMapperReader reader) {
        int chunkSectionsCount = chunkPacketInfo.palettes.length;
        for (int i = 0; i < chunkSectionsCount; i++) {
            Palette<BlockState> palette = (Palette<BlockState>) chunkPacketInfo.palettes[i];
            switch (palette) {
                case SingleValuePalette<BlockState> singleValuePalette -> {
                    if (pipeline.hasAnyMapping(clientViewAwarenessLevelI, singleValuePalette.valueFor(0))) {
                        return true;
                    }
                }
                case LinearPalette<BlockState> linearPalette -> {
                    for (int j = 0; j < linearPalette.getSize(); j++) {
                        if (pipeline.hasAnyMapping(clientViewAwarenessLevelI, linearPalette.valueFor(j))) {
                            return true;
                        }
                    }
                }
                case HashMapPalette<BlockState> hashMapPalette -> {
                    for (BlockState value : hashMapPalette.getEntries()) {
                        if (pipeline.hasAnyMapping(clientViewAwarenessLevelI, value)) {
                            return true;
                        }
                    }
                }
                case null, default -> {
                    reader.startReadingBitStorage(chunkPacketInfo.getIndex(i), (byte) chunkPacketInfo.getBits(i));
                    for (int j = 0; j < 4096; j++) {
                        int value = reader.readFromBitStorage();
                        if (pipeline.hasAnyMapping(clientViewAwarenessLevelI, value)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Processes a single section, then calls this method again to continue processing the remaining sections.
     */
    private void applyMappingsToSection(int section, ClientboundLevelChunkPacketData chunkData) {

        // Stop if we have finished
        if (this.reader.getIndex() == this.reader.size()) {
            // Store the new buffer
            this.writer.storeIn(chunkData);
            // Mark this mapper as done
            this.setDone();
            return;
        }

        // Read the non-empty block count, which we re-calculate ourselves later
        this.reader.readShort();
        // Determine the new block states
        DirectSectionContents newContents = determineNewSectionContents(this.chunkStartY + (section << 4));

        // Write the new block states to the buffer
        this.writer.writeShort(newContents.getNonEmptyBlockCount());
        newContents.writeAsPalettedContainer(this.writer, section, this.chunkPacketInfo, this.reader, this.globalPaletteBitsPerEntry);
        // Clean the reusable new contents
        newContents.clear();
        // Copy the biomes
        PalettedContainerUtilities.copy(true, this.reader, this.writer);
        // Continue with the next section (implemented recursively to allow for continuing on the main thread if we switched to it)
        applyMappingsToSection(section + 1, chunkData);

    }

    /**
     * Determines the new contents for a section.
     */
    private DirectSectionContents determineNewSectionContents(int sectionStartY) {
        byte oldBitsPerEntry = this.reader.readByte();
        if (oldBitsPerEntry == 0) {
            return this.determineNewSectionContentsForSingleValuedPalette(sectionStartY);
        } else {
            return this.determineNewSectionContentsForIndirectOrDirectPalette(sectionStartY, oldBitsPerEntry);
        }
    }

    /**
     * Auxiliary function to {@link #determineNewSectionContents},
     * for when the old section contents uses a single valued palette.
     */
    private DirectSectionContents determineNewSectionContentsForSingleValuedPalette(int sectionStartY) {

        int oldBlockStateId = this.reader.readVarInt();

        int singleNewBlockStateId = this.pipeline.getDirectMapping(this.clientViewAwarenessLevelI, oldBlockStateId);
        if (singleNewBlockStateId != -1) {
            // Use the directly mapped result
            return this.reusableSingleValuedNewSectionContents().setBlockStateId(singleNewBlockStateId);
        }

        BlockMappingsStep @Nullable [] chainMapping = this.pipeline.getChainMapping(this.clientViewAwarenessLevelI, oldBlockStateId);
        if (chainMapping == null) {
            // There are no relevant mappings
            return this.reusableSingleValuedNewSectionContents().setBlockStateId(oldBlockStateId);
        }

        if (noneRequiresCoordinates(chainMapping)) {
            // Apply the chain once for all blocks in this section that have this block state
            return this.reusableSingleValuedNewSectionContents().setBlockStateId(BlockMappingsImpl.applyChain(oldBlockStateId, this.getGenericContext(), chainMapping));
        }

        // Apply the chain for each position
        MultiValuedDirectSectionContents newContents = this.reusableMultiValuedNewSectionContents();
        int blockInSection = 0;
        for (int yInSection = 0; yInSection < 16; yInSection++) {
            int y = sectionStartY + yInSection;
            for (int zInSection = 0; zInSection < 16; zInSection++) {
                int z = this.chunkStartZ + zInSection;
                for (int xInSection = 0; xInSection < 16; xInSection++) {
                    int x = this.chunkStartX + xInSection;
                    BlockMappingFunctionContext context = new BlockMappingFunctionContextImpl(this.clientView, x, y, z);
                    int mappedValue = BlockMappingsImpl.applyChain(oldBlockStateId, context, chainMapping);
                    newContents.setBlockStateId(blockInSection++, mappedValue);
                }
            }
        }
        return newContents;

    }

    /**
     * Auxiliary function to {@link #determineNewSectionContents},
     * for when the old section contents uses an indirect or direct palette.
     */
    private DirectSectionContents determineNewSectionContentsForIndirectOrDirectPalette(int sectionStartY, byte oldBitsPerEntry) {

        // First, read the old contents
        PaletteIndexedSectionContents oldContents = this.reusableOldSectionContents();
        oldContents.readFromIndirectOrDirectPalettedContainer(this.reader, oldBitsPerEntry);

        // Determine the new block states for each old block state, or store their chain mapping if we must apply it per block later
        int[] oldPaletteIndexToNewBlockStateId = this.reusableOldPaletteIndexToNewBlockStateId();
        BlockMappingsStep[][] oldPaletteIndexToChain = this.reusableOldPaletteIndexToChain();
        for (int oldPaletteIndex = 0; oldPaletteIndex < oldContents.palette.size(); oldPaletteIndex++) {
            int oldBlockStateId = oldContents.palette.getBlockStateId(oldPaletteIndex);
            int singleNewBlockStateId = this.pipeline.getDirectMapping(this.clientViewAwarenessLevelI, oldBlockStateId);
            if (singleNewBlockStateId >= 0) {
                // Use the directly mapped result
                oldPaletteIndexToNewBlockStateId[oldPaletteIndex] = singleNewBlockStateId;
            } else {
                BlockMappingsStep @Nullable [] chainMapping = this.pipeline.getChainMapping(this.clientViewAwarenessLevelI, oldBlockStateId);
                if (chainMapping == null) {
                    // There are no relevant mappings
                    oldPaletteIndexToNewBlockStateId[oldPaletteIndex] = oldBlockStateId;
                } else {
                    if (noneRequiresCoordinates(chainMapping)) {
                        // Apply the chain once for all blocks in this section that have this block state
                        oldPaletteIndexToNewBlockStateId[oldPaletteIndex] = BlockMappingsImpl.applyChain(oldBlockStateId, this.getGenericContext(), chainMapping);
                    } else {
                        // Store the chain for later application
                        oldPaletteIndexToNewBlockStateId[oldPaletteIndex] = -1;
                        oldPaletteIndexToChain[oldPaletteIndex] = chainMapping;
                    }
                }
            }
        }

        // Determine the block state per block in the section
        MultiValuedDirectSectionContents newContents = this.reusableMultiValuedNewSectionContents();
        int blockIndexInSection = 0;
        for (int yInSection = 0; yInSection < 16; yInSection++) {
            int y = sectionStartY + yInSection;
            for (int zInSection = 0; zInSection < 16; zInSection++) {
                int z = this.chunkStartZ + zInSection;
                for (int xInSection = 0; xInSection < 16; xInSection++) {
                    int x = this.chunkStartX + xInSection;
                    int oldPaletteIndex = oldContents.getPaletteIndex(blockIndexInSection);
                    int newBlockStateId = oldPaletteIndexToNewBlockStateId[oldPaletteIndex];
                    if (newBlockStateId < 0) {
                        // Apply the chain
                        BlockMappingFunctionContext context = new BlockMappingFunctionContextImpl(this.clientView, x, y, z);
                        newBlockStateId = BlockMappingsImpl.applyChain(oldContents.palette.getBlockStateId(oldPaletteIndex), context, oldPaletteIndexToChain[oldPaletteIndex]);
                    }
                    newContents.setBlockStateId(blockIndexInSection++, newBlockStateId);
                }
            }
        }

        // Clear the reusable chain array
        Arrays.fill(oldPaletteIndexToChain, 0, oldContents.palette.size(), null);
        // Clear the reusable old contents
        oldContents.clear();

        // Return the new contents
        return newContents;

    }


    private static boolean noneRequiresCoordinates(BlockMappingsStep[] chainMapping) {
        for (BlockMappingsStep mapping : chainMapping) {
            if (mapping instanceof FunctionBlockMappingsStep complexMapping && complexMapping.requiresCoordinates()) {
                return false;
            }
        }
        return true;
    }

    private static final ThreadLocal<PaletteIndexedSectionContents> OLD_SECTION_CONTENTS_THREAD_LOCAL = ThreadLocal.withInitial(() -> new PaletteIndexedSectionContents(new DoubleMappedBlockStateIdPalette(), new int[4096]));
    private static final ThreadLocal<int[]> REUSABLE_OLD_PALETTE_INDEX_TO_NEW_BLOCK_STATE_ID_THREAD_LOCAL = ThreadLocal.withInitial(() -> new int[4096]);
    private static final ThreadLocal<BlockMappingsStep[][]> REUSABLE_OLD_PALETTE_INDEX_TO_CHAIN_THREAD_LOCAL = ThreadLocal.withInitial(() -> new BlockMappingsStep[4096][]);
    private static final ThreadLocal<SingleValuedDirectSectionContents> SINGLE_VALUED_NEW_SECTION_CONTENTS_THREAD_LOCAL = ThreadLocal.withInitial(SingleValuedDirectSectionContents::new);
    private static final ThreadLocal<MultiValuedDirectSectionContents> MULTI_VALUED_NEW_SECTION_CONTENTS_THREAD_LOCAL = ThreadLocal.withInitial(() -> new MultiValuedDirectSectionContents(new DoubleMappedBlockStateIdPalette(), new int[4096]));

    private @Nullable BlockMappingFunctionContext cachedGenericContext;
    private @Nullable PaletteIndexedSectionContents cachedReusableOldSectionContents;
    private int @Nullable [] cachedReusableOldPaletteIndexToNewBlockStateId;
    private BlockMappingsStep @Nullable [] @Nullable [] cachedReusableOldPaletteIndexToChain;
    private @Nullable SingleValuedDirectSectionContents cachedReusableSingleValuedNewSectionContents;
    private @Nullable MultiValuedDirectSectionContents cachedReusableMultiValuedNewSectionContents;

    /**
     * @return A {@link BlockMappingFunctionContext} for mappings that do not require any specific information.
     */
    private BlockMappingFunctionContext getGenericContext() {
        if (this.cachedGenericContext == null) {
            this.cachedGenericContext = new BlockMappingFunctionContextImpl(this.clientView, true, 0, 0, 0);
        }
        return this.cachedGenericContext;
    }

    /**
     * @return A reusable {@link PaletteIndexedSectionContents} for the old contents of a section.
     */
    private PaletteIndexedSectionContents reusableOldSectionContents() {
        if (this.cachedReusableOldSectionContents == null) {
            this.cachedReusableOldSectionContents = OLD_SECTION_CONTENTS_THREAD_LOCAL.get();
        }
        return this.cachedReusableOldSectionContents;
    }

    /**
     * @return An int array of length 4096, serving as a map from the index (0 to 4095, inclusive) of an index
     * in {@link #reusableOldSectionContents()}{@code .}{@linkplain PaletteIndexedSectionContents#palette <code>palette</code>}
     * to the new block state id mapped from the old block state id at
     * that index in the old palette, or -1
     * {@linkplain #cachedReusableOldPaletteIndexToChain if a chain mapping is required}.
     *
     * <p>
     * It must be filled with null after usage.
     * </p>
     */
    private int[] reusableOldPaletteIndexToNewBlockStateId() {
        if (this.cachedReusableOldPaletteIndexToNewBlockStateId == null) {
            this.cachedReusableOldPaletteIndexToNewBlockStateId = REUSABLE_OLD_PALETTE_INDEX_TO_NEW_BLOCK_STATE_ID_THREAD_LOCAL.get();
        }
        return this.cachedReusableOldPaletteIndexToNewBlockStateId;
    }

    /**
     * @return A chain array of length 4096, serving as a map from the index (0 to 4095, inclusive) of an index
     * in {@link #reusableOldSectionContents()}{@code .}{@linkplain PaletteIndexedSectionContents#palette <code>palette</code>} to the chain mapping that must be applied to any block in the section
     * that has the old block state id at that index in the old palette as its block state, or null
     * {@linkplain #cachedReusableOldPaletteIndexToNewBlockStateId if no chain mapping is required}.
     */
    private BlockMappingsStep[][] reusableOldPaletteIndexToChain() {
        if (this.cachedReusableOldPaletteIndexToChain == null) {
            this.cachedReusableOldPaletteIndexToChain = REUSABLE_OLD_PALETTE_INDEX_TO_CHAIN_THREAD_LOCAL.get();
        }
        return this.cachedReusableOldPaletteIndexToChain;
    }

    /**
     * @return A reusable {@link SingleValuedDirectSectionContents} for the new contents of a section.
     */
    private SingleValuedDirectSectionContents reusableSingleValuedNewSectionContents() {
        if (this.cachedReusableSingleValuedNewSectionContents == null) {
            this.cachedReusableSingleValuedNewSectionContents = SINGLE_VALUED_NEW_SECTION_CONTENTS_THREAD_LOCAL.get();
        }
        return this.cachedReusableSingleValuedNewSectionContents;
    }

    /**
     * @return A reusable {@link MultiValuedDirectSectionContents} for the new contents of a section.
     */
    private MultiValuedDirectSectionContents reusableMultiValuedNewSectionContents() {
        if (this.cachedReusableMultiValuedNewSectionContents == null) {
            this.cachedReusableMultiValuedNewSectionContents = MULTI_VALUED_NEW_SECTION_CONTENTS_THREAD_LOCAL.get();
        }
        return this.cachedReusableMultiValuedNewSectionContents;
    }

}
