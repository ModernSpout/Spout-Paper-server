package org.fiddlemc.fiddle.impl.packetmapping.component;

import java.util.Collection;
import java.util.function.Consumer;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentTarget;
import org.fiddlemc.fiddle.api.packetmapping.component.nms.ComponentMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.component.nms.ComponentMappingsComposeEventNMS;
import org.fiddlemc.fiddle.impl.util.composable.AwarenessLevelPairKeyedBuilderComposeEventImpl;

/**
 * The implementation of {@link ComponentMappingsComposeEvent}.
 */
public final class ComponentMappingsComposeEventImpl extends AwarenessLevelPairKeyedBuilderComposeEventImpl<ComponentTarget, ComponentMappingsStep, ComponentMappingBuilder> implements ComponentMappingsComposeEventNMS<ComponentMappingsStep> {

    @Override
    public void register(Consumer<ComponentMappingBuilder> builderConsumer) {
        ComponentMappingBuilderImpl builder = new ComponentMappingBuilderImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    public void registerNMS(Consumer<ComponentMappingBuilderNMS> builderConsumer) {
        ComponentMappingBuilderNMSImpl builder = new ComponentMappingBuilderNMSImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    protected int keyPartToInt(ComponentTarget key) {
        return key.ordinal();
    }

    @Override
    protected ComponentTarget intToKeyPart(int internalKey) {
        return ComponentTargetUtil.getByOrdinal(internalKey);
    }

    @Override
    public void register(Collection<ClientView.AwarenessLevel> awarenessLevels, Collection<ComponentTarget> keys, ComponentMappingsStep element) {
        super.register(awarenessLevels, ComponentTargetUtil.expandTargets(keys), element);
    }
}
