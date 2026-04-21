package org.fiddlemc.fiddle.impl.util.mappingpipeline;

import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionHandle;
import java.util.function.Consumer;

/**
 * A simple implementation of {@link MappingPipelineStep} that defers to a {@link Consumer}.
 */
public record SimpleMappingPipelineStep<H extends MappingFunctionHandle<?>>(Consumer<H> consumer) implements MappingPipelineStep<H> {

    @Override
    public void apply(H handle) {
        this.consumer.accept(handle);
    }

}
