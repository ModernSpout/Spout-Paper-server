package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

/**
 * A client-side enum
 * (normally at {@code net.minecraft.client.renderer.chunk.ChunkSectionLayer})
 * that determines the potential transparency of a block during rendering.
 */
public enum ChunkSectionLayer {
    SOLID,
    CUTOUT,
    TRANSLUCENT,
    TRIPWIRE
}
