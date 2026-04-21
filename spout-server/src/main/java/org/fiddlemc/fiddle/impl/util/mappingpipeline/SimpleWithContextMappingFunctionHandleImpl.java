package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;

/**
 * A base implementation of {@link WithContextMappingFunctionHandle},
 * that builds upon {@link SimpleMappingFunctionHandleImpl}.
 */
public class SimpleWithContextMappingFunctionHandleImpl<T, MT extends T, C extends MappingFunctionContext> extends SimpleMappingFunctionHandleImpl<T, MT> implements WithContextMappingFunctionHandle<T, C> {

    /**
     * The context.
     */
    protected final C context;

    public SimpleWithContextMappingFunctionHandleImpl(T data, C context, boolean isDataMutable) {
        super(data, isDataMutable);
        this.context = context;
    }

    @Override
    public C getContext() {
        return this.context;
    }

}
