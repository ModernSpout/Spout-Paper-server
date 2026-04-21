package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ProxyStatesRequestBuilder;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A processor for {@link ProxyStatesRequestBuilderImpl}s.
 *
 * <p>
 * Each processor instance is for one {@link ProxyStatesRequestBuilderImpl}.
 * </p>
 */
public abstract class RequestProcessor<R extends ProxyStatesRequestBuilderImpl<?>> {

    protected final R request;
    protected final BlockMappingsComposeEventImpl event;

    protected RequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        this.request = request;
        this.event = event;
    }

    public void process() {
        this.validateArguments();
        this.processAfterValidateArguments();
    }

    protected void validateArguments() {
        this.request.validateArguments();
    }

    protected abstract void processAfterValidateArguments();

}
