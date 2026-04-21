package org.fiddlemc.fiddle.api.packetmapping.component;

import org.fiddlemc.fiddle.api.packetmapping.AwarenessLevelMappingBuilder;
import org.fiddlemc.fiddle.api.util.composable.FromBuilder;
import org.fiddlemc.fiddle.api.util.composable.FunctionBuilder;

/**
 * A builder to define a component mapping.
 */
public interface ComponentMappingBuilder extends AwarenessLevelMappingBuilder, FromBuilder<ComponentTarget>, FunctionBuilder<ComponentMappingHandle> {
}
