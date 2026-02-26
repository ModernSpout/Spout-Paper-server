/**
 * <h1>Client view - Lookup - During packet handling</h1>
 *
 * <p>
 * Allows for looking up the client view for which a packet is being sent or received, during its handling.
 * The view is tracked along (and can therefore be looked up from):
 * <ul>
 *     <li>
 *         An {@linkplain org.fiddlemc.fiddle.impl.clientview.lookup.packethandling.NettyClientViewLookupAttribute attribute}
 *         on the Netty channel
 *     </li>
 *     <li>
 *         A {@linkplain org.fiddlemc.fiddle.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal thread-local variable},
 *         on the thread on which a packet is being handled
 *     </li>
 *     <li>
 *         The {@linkplain net.minecraft.network.FriendlyByteBuf write buffer} that the packet is being serialized to
 *     </li>
 * </ul>
 * </p>
 */
@NullMarked
package org.fiddlemc.fiddle.impl.clientview.lookup.packethandling;

import org.jspecify.annotations.NullMarked;
