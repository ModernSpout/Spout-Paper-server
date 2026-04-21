package org.fiddlemc.fiddle.impl.packetmapping.component;

import org.fiddlemc.fiddle.api.packetmapping.component.nms.ComponentMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.component.nms.ComponentMappingHandleNMS;

/**
 * The implementation of {@link ComponentMappingBuilderNMS}.
 */
public class ComponentMappingBuilderNMSImpl extends AbstractComponentMappingBuilderImpl<ComponentMappingHandleNMS> implements ComponentMappingBuilderNMS {

    @Override
    protected ComponentMappingsStep createFunctionStep() {
        return new MinecraftFunctionComponentMappingsStep(this.function);
    }

}
