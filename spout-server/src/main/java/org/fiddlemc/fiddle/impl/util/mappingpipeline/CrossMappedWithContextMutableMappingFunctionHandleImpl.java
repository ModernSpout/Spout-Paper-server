package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.MutableMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;

/**
 * A {@link CrossMappedMutableMappingFunctionHandleImpl}
 * that also implements {@link WithContextMappingFunctionHandle}.
 */
public abstract class CrossMappedWithContextMutableMappingFunctionHandleImpl<T, MT extends T, C extends MappingFunctionContext, IT, IMT extends IT, IH extends WithContextMappingFunctionHandle<IT, C> & MutableMappingFunctionHandle<IT, IMT>> extends CrossMappedMutableMappingFunctionHandleImpl<T, MT, IT, IMT, IH> implements WithContextMappingFunctionHandle<T, C> {

    public CrossMappedWithContextMutableMappingFunctionHandleImpl(IH internal) {
        super(internal);
    }

    @Override
    public C getContext() {
        return this.internal.getContext();
    }

}
