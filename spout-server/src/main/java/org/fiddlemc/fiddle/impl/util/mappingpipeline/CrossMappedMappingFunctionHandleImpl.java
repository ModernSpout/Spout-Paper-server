package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * An implementation of {@link MappingFunctionHandle},
 * that also implements {@link WithOriginalMappingFunctionHandle},
 * that passes any calls to an internal {@link MappingFunctionHandle},
 * cross-mapping its types.
 */
public abstract class CrossMappedMappingFunctionHandleImpl<T, IT, IH extends MappingFunctionHandle<IT>> implements WithOriginalMappingFunctionHandle<T> {

    public final IH internal;

    public CrossMappedMappingFunctionHandleImpl(IH internal) {
        this.internal = internal;
    }

    protected abstract IT mapToInternal(T data);

    protected abstract T mapFromInternal(IT data);

    @Override
    public T getOriginal() {
        if (this.internal instanceof WithOriginalMappingFunctionHandle<?> withOriginalInternal) {
            return this.mapFromInternal((IT) withOriginalInternal.getOriginal());
        }
        throw new UnsupportedOperationException("Internal handle does not support original");
    }

    @Override
    public T getImmutable() {
        return this.mapFromInternal(this.internal.getImmutable());
    }

    @Override
    public void set(T data) {
        this.internal.set(this.mapToInternal(data));
    }

}
