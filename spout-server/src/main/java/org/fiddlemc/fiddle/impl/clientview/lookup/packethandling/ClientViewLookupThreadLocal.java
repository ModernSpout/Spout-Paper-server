package org.fiddlemc.fiddle.impl.clientview.lookup.packethandling;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.impl.clientview.FallbackClientViewImpl;
import org.fiddlemc.fiddle.impl.clientview.lookup.ClientViewLookup;
import org.jspecify.annotations.Nullable;
import java.lang.ref.WeakReference;

/**
 * Holder for {@link #THREAD_LOCAL}.
 */
public final class ClientViewLookupThreadLocal {

    private ClientViewLookupThreadLocal() {
        throw new UnsupportedOperationException();
    }

    /**
     * A {@link ThreadLocal} holding the {@link ClientViewLookup}
     * for which packets are being encoded on the current thread.
     */
    public static final ThreadLocal<@Nullable WeakReference<ClientViewLookup>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Utility function to get the {@link ClientView} from the {@link ClientViewLookup} in {@link #THREAD_LOCAL},
     * or null if it doesn't exist.
     */
    public static @Nullable ClientView getThreadLocalClientView() {
        @Nullable WeakReference<ClientViewLookup> clientViewProviderReference = THREAD_LOCAL.get();
        @Nullable ClientViewLookup clientViewLookup = clientViewProviderReference == null ? null : clientViewProviderReference.get();
        return clientViewLookup == null ? null : clientViewLookup.getClientView();
    }

    /**
     * Utility function, the same as {@link #getThreadLocalClientView}, but will return
     * {@link FallbackClientViewImpl#INSTANCE} instead of null.
     */
    public static ClientView getThreadLocalClientViewOrFallback() {
        @Nullable ClientView clientView = getThreadLocalClientView();
        return clientView != null ? clientView : FallbackClientViewImpl.INSTANCE;
    }

}
