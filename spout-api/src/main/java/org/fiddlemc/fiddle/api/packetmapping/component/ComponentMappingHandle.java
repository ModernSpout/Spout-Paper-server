package org.fiddlemc.fiddle.api.packetmapping.component;

import net.kyori.adventure.text.Component;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ComponentMappingBuilder#to}.
 */
public interface ComponentMappingHandle extends WithContextMappingFunctionHandle<Component, ComponentMappingFunctionContext>, WithOriginalMappingFunctionHandle<Component> {
}
