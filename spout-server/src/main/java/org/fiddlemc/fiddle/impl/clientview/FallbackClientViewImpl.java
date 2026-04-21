package org.fiddlemc.fiddle.impl.clientview;

import net.minecraft.network.Connection;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.impl.packetmapping.item.reverse.ItemMappingReverser;
import org.jspecify.annotations.Nullable;

/**
 * A fallback {@link ClientView} that is used for mappings when no view is known.
 */
public final class FallbackClientViewImpl extends ClientViewImpl {

    /**
     * A usable instance of {@link FallbackClientViewImpl}.
     */
    public static final FallbackClientViewImpl INSTANCE = new FallbackClientViewImpl();

    private FallbackClientViewImpl() {
        super();
    }

    @Override
    public AwarenessLevel getAwarenessLevel() {
        return AwarenessLevel.VANILLA;
    }

    @Override
    public @Nullable Connection getConnection() {
        return null;
    }

    @Override
    public @Nullable ItemMappingReverser getItemMappingReverser() {
        return null;
    }

}
