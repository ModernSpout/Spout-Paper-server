package org.fiddlemc.fiddle.impl.packetmapping.component;

import io.papermc.paper.adventure.AdventureComponent;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappings;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentTarget;
import org.fiddlemc.fiddle.impl.packetmapping.WithClientViewContextSingleStepMappingPipeline;
import org.fiddlemc.fiddle.impl.packetmapping.component.translatable.ServerSideTranslationsComponentMappingsStep;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;

/**
 * A pipeline of component mappings.
 */
public final class ComponentMappingsImpl extends ComposableImpl<ComponentMappingsComposeEvent<ComponentMappingsStep>, ComponentMappingsComposeEventImpl> implements WithClientViewContextSingleStepMappingPipeline<Component, ComponentMappingFunctionContext, ComponentMappingHandleNMSImpl>, ComponentMappings<ComponentMappingsStep> {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<ComponentMappings<?>, ComponentMappingsImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(ComponentMappingsImpl.class);
        }

    }

    public static ComponentMappingsImpl get() {
        return (ComponentMappingsImpl) ComponentMappings.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_component_mappings";
    }

    /**
     * The registered steps.
     *
     * <p>
     * The mappings are organized in an array where {@link ClientView.AwarenessLevel#ordinal()}
     * is the index, and then in an array where {@link ComponentTarget#ordinal()} is the index.
     * The lowest-level array may be null, but will never be empty.
     * </p>
     */
    private final ComponentMappingsStep[][][] mappings;

    private ComponentMappingsImpl() {
        this.mappings = new ComponentMappingsStep[ClientView.AwarenessLevel.getAll().length][][];
    }

    @Override
    public ComponentMappingsStep @Nullable [] getStepsThatMayApplyTo(ComponentMappingHandleNMSImpl handle) {
        return this.mappings[handle.getContext().getClientView().getAwarenessLevel().ordinal()][ComponentTargetUtil.getMostSpecificTarget(handle.getImmutable()).ordinal()];
    }

    @Override
    public ComponentMappingHandleNMSImpl createHandle(Component data, ComponentMappingFunctionContext context) {
        return new ComponentMappingHandleNMSImpl(data, context, false);
    }

    @Override
    public ComponentMappingFunctionContext createGenericContext(final ClientView clientView) {
        return new ComponentMappingFunctionContextImpl(clientView);
    }

    @Override
    public Component apply(Component data, ComponentMappingFunctionContext context) {
        // Convert the component to a vanilla component
        Component dataToApplyTo = data instanceof AdventureComponent adventureComponent ? adventureComponent.deepConverted() : data;
        // Apply the pipeline
        return WithClientViewContextSingleStepMappingPipeline.super.apply(dataToApplyTo, context);
    }

    @Override
    protected ComponentMappingsComposeEventImpl createComposeEvent() {
        // Create the event
        ComponentMappingsComposeEventImpl event = new ComponentMappingsComposeEventImpl();
        // Register the built-in server-side translations step
        event.register(
            Arrays.asList(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideTranslatables()),
            List.of(ComponentTarget.TRANSLATABLE),
            new ServerSideTranslationsComponentMappingsStep()
        );
        // Return the event
        return event;
    }

    @Override
    protected void copyInformationFromEvent(ComponentMappingsComposeEventImpl event) {

        // Initialize the steps
        for (int i = 0; i < this.mappings.length; i++) {
            this.mappings[i] = new ComponentMappingsStep[ComponentTarget.values().length][];
        }

        // Copy steps from event
        event.copyRegisteredInvertedAndReinvertedInto(this.mappings, ComponentMappingsStep[]::new);

    }

}
