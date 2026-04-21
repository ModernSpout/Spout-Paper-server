package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

/**
 * The contents of a section, consisting of a {@link BlockStateIdPalette}
 * and a way to map each block in the section to a block state id.
 *
 * <p>
 * An instance of this class is reusable, but {@link #clear} must be called after usage.
 * </p>
 */
public interface SectionContents {

    /**
     * Prepare this instance for reuse.
     */
    void clear();

}
