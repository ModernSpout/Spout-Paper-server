package org.fiddlemc.fiddle.impl.packetmapping.component;

import java.util.function.Consumer;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingHandle;

/**
 * A {@link ComponentMappingsStep} that performs a specific function.
 */
public record AdventureFunctionComponentMappingsStep(Consumer<ComponentMappingHandle> function) implements ComponentMappingsStep {

    @Override
    public void apply(ComponentMappingHandleNMSImpl handle) {
        this.function.accept(handle.adventureHandle());
    }

}
