package org.fiddlemc.fiddle.impl.bukkit.enuminjection;

import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamePickFunctionHandle;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SimpleMappingFunctionHandleImpl;

/**
 * The implementation of {@link BukkitEnumNamePickFunctionHandle}.
 */
public final class BukkitEnumNameMappingHandleImpl<S> extends SimpleMappingFunctionHandleImpl<String, String> implements BukkitEnumNamePickFunctionHandle<S> {

    private final S sourceValue;

    public BukkitEnumNameMappingHandleImpl(String data, S sourceValue) {
        super(data, false);
        this.sourceValue = sourceValue;
    }

    @Override
    public S getSourceValue() {
        return this.sourceValue;
    }

}
