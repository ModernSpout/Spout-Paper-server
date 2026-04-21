package org.fiddlemc.fiddle.api.packetmapping.component.nms;

import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingsComposeEvent;
import java.util.function.Consumer;

/**
 * An extension to {@link ComponentMappingsComposeEvent} using Minecraft internals.
 */
public interface ComponentMappingsComposeEventNMS<M> extends ComponentMappingsComposeEvent<M> {

    /**
     * @see #register(Consumer)
     */
    void registerNMS(Consumer<ComponentMappingBuilderNMS> builderConsumer);

}
