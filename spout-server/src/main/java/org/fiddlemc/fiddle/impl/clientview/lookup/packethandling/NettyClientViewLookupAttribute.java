package org.fiddlemc.fiddle.impl.clientview.lookup.packethandling;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.fiddlemc.fiddle.impl.clientview.lookup.ClientViewLookup;
import java.lang.ref.WeakReference;

/**
 * Holder for {@link #ATTRIBUTE_KEY}.
 */
public final class NettyClientViewLookupAttribute {

    private NettyClientViewLookupAttribute() {
        throw new UnsupportedOperationException();
    }

    /**
     * An {@link AttributeKey} that is used to {@linkplain Channel#attr store} the {@link ClientViewLookup}
     * in its {@link Channel}.
     */
    public static final AttributeKey<WeakReference<ClientViewLookup>> ATTRIBUTE_KEY = AttributeKey.valueOf("fiddle:client_view_lookup");

}
