package org.fiddlemc.fiddle.impl.packetmapping.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentTarget;
import org.jspecify.annotations.Nullable;

/**
 * A common base for {@link ComponentMappingBuilderImpl} and {@link ComponentMappingBuilderNMSImpl}.
 */
public abstract class AbstractComponentMappingBuilderImpl<H> {

    protected @Nullable ArrayList<ClientView.AwarenessLevel> awarenessLevels;
    protected @Nullable ArrayList<ComponentTarget> from;
    protected @Nullable Consumer<H> function;

    public void awarenessLevel(Collection<ClientView.AwarenessLevel> awarenessLevels) {
        this.awarenessLevels = new ArrayList<>(awarenessLevels);
    }

    public void addAwarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        if (this.awarenessLevels == null) {
            this.awarenessLevels = new ArrayList<>(1);
        }
        this.awarenessLevels.add(awarenessLevel);
    }

    public void from(Collection<? extends ComponentTarget> from) {
        this.from = new ArrayList<>(from);
    }

    public void addFrom(ComponentTarget from) {
        if (this.from == null) {
            this.from = new ArrayList<>(1);
        }
        this.from.add(from);
    }

    public void to(Consumer<H> function) {
        this.function = function;
    }

    abstract protected ComponentMappingsStep createFunctionStep();

    public void registerWith(ComponentMappingsComposeEventImpl event) {
        if (this.awarenessLevels == null) {
            throw new IllegalStateException("No awareness level(s) were specified");
        }
        if (this.from == null) {
            throw new IllegalStateException("No from was specified");
        }
        if (this.function != null) {
            event.register(this.awarenessLevels, this.from, this.createFunctionStep());
            return;
        }
        throw new IllegalStateException("No function was specified");
    }

}
