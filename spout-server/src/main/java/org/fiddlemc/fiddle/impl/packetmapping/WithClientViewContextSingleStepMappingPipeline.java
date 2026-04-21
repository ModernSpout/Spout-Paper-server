package org.fiddlemc.fiddle.impl.packetmapping;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.WithClientViewMappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.WithContextSingleStepMappingPipeline;

/**
 * A {@link WithContextSingleStepMappingPipeline} for contexts of type {@link WithClientViewMappingFunctionContext}.
 */
public interface WithClientViewContextSingleStepMappingPipeline<T, C extends WithClientViewMappingFunctionContext, H extends WithContextMappingFunctionHandle<T, C>> extends WithContextSingleStepMappingPipeline<T, C, H> {

    /**
     * Creates a generic {@linkplain C context} for {@link #applyGenerically},
     * using the given {@link ClientView}.
     *
     * <p>
     * While this method is allowed to return a very generic context,
     * it also has the freedom to attempt to make the returned context as accurate as possible,
     * with the information available.
     * </p>
     */
    C createGenericContext(ClientView clientView);

    @Override
    default C createGenericContext() {
        return this.createGenericContext(ClientViewLookupThreadLocal.getThreadLocalClientViewOrFallback());
    }

    /**
     * A simple implementation of {@link WithClientViewContextSingleStepMappingPipeline}
     * where the context is no more complex than {@link WithClientViewMappingFunctionContext}.
     */
    interface Simple<T, H extends WithContextMappingFunctionHandle<T, WithClientViewMappingFunctionContext>> extends WithClientViewContextSingleStepMappingPipeline<T, WithClientViewMappingFunctionContext, H> {

        @Override
        default WithClientViewMappingFunctionContextImpl createGenericContext(ClientView clientView) {
            return new WithClientViewMappingFunctionContextImpl(clientView);
        }

    }

}
