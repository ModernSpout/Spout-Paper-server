package org.fiddlemc.fiddle.impl.packetmapping.block.chunk;

/**
 * Utilities for handling data packed into arrays of longs.
 */
public final class BitStorageUtilities {

    private BitStorageUtilities() {
        throw new UnsupportedOperationException();
    }

    /**
     * The size in bytes of the smallest array of longs that can hold the given number of values.
     *
     * @param elements     The number of elements.
     * @param bitsPerEntry The bits used per entry.
     */
    public static int getSizeInBytes(int elements, byte bitsPerEntry) {
        int entriesPerLong = 64 / bitsPerEntry; // = floor(64 / bitsPerEntry)
        int longsNeeded = (elements + entriesPerLong - 1) / entriesPerLong; // = ceil(elements / entriesPerLong)
        return longsNeeded * 8;
    }

}
