package org.fiddlemc.fiddle.impl.bukkit.enuminjection;

import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamePickFunctionHandle;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNames;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamesComposeEvent;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.MappingPipelineStep;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SimpleMappingPipelineStep;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SingleStepMappingPipeline;
import org.jspecify.annotations.Nullable;

/**
 * Base implementation of {@link BukkitEnumNames}
 */
public abstract class BukkitEnumNameMappingPipelineImpl<S> extends ComposableImpl<BukkitEnumNamesComposeEvent<S>, BukkitEnumNameMappingPipelineComposeEventImpl<S>> implements SingleStepMappingPipeline<String, BukkitEnumNamePickFunctionHandle<S>>, BukkitEnumNames<S> {

    /**
     * The registered mappings,
     * or null if there are no registered mappings.
     */
    protected MappingPipelineStep<BukkitEnumNamePickFunctionHandle<S>> @Nullable [] mappings;

    @Override
    protected BukkitEnumNameMappingPipelineComposeEventImpl<S> createComposeEvent() {
        return new BukkitEnumNameMappingPipelineComposeEventImpl<>();
    }

    @Override
    protected void copyInformationFromEvent(BukkitEnumNameMappingPipelineComposeEventImpl<S> event) {
        if (event.mappings != null) {
            this.mappings = event.mappings.stream().map(mapping -> new SimpleMappingPipelineStep<>(mapping)).toArray(SimpleMappingPipelineStep[]::new);
        }
    }

    @Override
    public @Nullable MappingPipelineStep<BukkitEnumNamePickFunctionHandle<S>> @Nullable [] getStepsThatMayApplyTo(BukkitEnumNamePickFunctionHandle<S> handle) {
        return this.mappings;
    }

}
