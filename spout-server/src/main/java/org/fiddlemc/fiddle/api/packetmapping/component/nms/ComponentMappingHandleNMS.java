package org.fiddlemc.fiddle.api.packetmapping.component.nms;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.MutableMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithContextMappingFunctionHandle;
import org.fiddlemc.fiddle.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ComponentMappingBuilderNMS#to}.
 */
public interface ComponentMappingHandleNMS extends WithContextMappingFunctionHandle<Component, ComponentMappingFunctionContext>, WithOriginalMappingFunctionHandle<Component>, MutableMappingFunctionHandle<Component, MutableComponent> {
}
