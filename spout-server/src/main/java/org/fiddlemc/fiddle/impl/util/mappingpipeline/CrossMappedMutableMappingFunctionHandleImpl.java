package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.MutableMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * An implementation of {@link MappingFunctionHandle},
 * that also implements {@link WithOriginalMappingFunctionHandle} and {@link MutableMappingFunctionHandle},
 * that passes any calls to an internal {@link MappingFunctionHandle},
 * cross-mapping its types.
 */
public abstract class CrossMappedMutableMappingFunctionHandleImpl<T, MT extends T, IT, IMT extends IT, IH extends MutableMappingFunctionHandle<IT, IMT>> extends CrossMappedMappingFunctionHandleImpl<T, IT, IH> implements MutableMappingFunctionHandle<T, MT> {

    public CrossMappedMutableMappingFunctionHandleImpl(IH internal) {
        super(internal);
    }

    protected abstract IMT mapToInternalMutable(MT data);

    protected abstract MT mapFromInternalMutable(IMT data);

    @Override
    public MT getMutable() {
        return this.mapFromInternalMutable(this.internal.getMutable());
    }

    @Override
    public void setMutable(MT data) {
        this.internal.setMutable(this.mapToInternalMutable(data));
    }

}
