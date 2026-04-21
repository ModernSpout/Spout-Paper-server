package org.fiddlemc.fiddle.impl.packetmapping.component;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingFunctionContext;
import org.fiddlemc.fiddle.impl.packetmapping.WithClientViewMappingFunctionContextImpl;

/**
 * The implementation of {@link ComponentMappingFunctionContext}.
 */
public class ComponentMappingFunctionContextImpl extends WithClientViewMappingFunctionContextImpl implements ComponentMappingFunctionContext {
    public ComponentMappingFunctionContextImpl(ClientView clientView) {
        super(clientView);
    }
}
