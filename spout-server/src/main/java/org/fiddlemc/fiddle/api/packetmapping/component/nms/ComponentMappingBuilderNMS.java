package org.fiddlemc.fiddle.api.packetmapping.component.nms;

import org.fiddlemc.fiddle.api.packetmapping.AwarenessLevelMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentTarget;
import org.fiddlemc.fiddle.api.util.composable.FromBuilder;
import org.fiddlemc.fiddle.api.util.composable.FunctionBuilder;

/**
 * An alternative to {@link ComponentMappingBuilder} that uses Minecraft internals.
 */
public interface ComponentMappingBuilderNMS extends AwarenessLevelMappingBuilder, FromBuilder<ComponentTarget>, FunctionBuilder<ComponentMappingHandleNMS> {
}
