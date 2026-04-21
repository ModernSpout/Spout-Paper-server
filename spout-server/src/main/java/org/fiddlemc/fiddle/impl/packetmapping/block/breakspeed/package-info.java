/**
 * <h1>Block mapping - Breaking speed/h1>
 *
 * <p>
 * Adjusts the block break speed of players so that the client expects the same result as the server
 * at the same time. This is done on a best-attempt basis, but with higher ping desync may still occur.
 * </p>
 *
 * <p>
 * The implementation basically sends packets to the client to change
 * {@link net.minecraft.world.entity.ai.attributes.Attributes#BLOCK_BREAK_SPEED}.
 * </p>
 */
@NullMarked
package org.fiddlemc.fiddle.impl.packetmapping.block.breakspeed;

import org.jspecify.annotations.NullMarked;
