package org.fiddlemc.fiddle.impl.packetmapping;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.WithClientViewMappingFunctionContext;

/**
 * A base implementation of {@link WithClientViewMappingFunctionContext}.
 */
public class WithClientViewMappingFunctionContextImpl implements WithClientViewMappingFunctionContext {

    private final ClientView clientView;

    public WithClientViewMappingFunctionContextImpl(ClientView clientView) {
        this.clientView = clientView;
    }

    @Override
    public ClientView getClientView() {
        return this.clientView;
    }

}
