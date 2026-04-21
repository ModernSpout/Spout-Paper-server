package org.fiddlemc.fiddle.impl.packetmapping.component;

import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingHandle;

/**
 * The implementation of {@link ComponentMappingBuilder}.
 */
public class ComponentMappingBuilderImpl extends AbstractComponentMappingBuilderImpl<ComponentMappingHandle> implements ComponentMappingBuilder {

    @Override
    protected ComponentMappingsStep createFunctionStep() {
        return new AdventureFunctionComponentMappingsStep(this.function);
    }

}
