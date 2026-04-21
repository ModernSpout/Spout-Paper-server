package org.fiddlemc.fiddle.impl.packetmapping.component;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingHandle;
import org.fiddlemc.fiddle.api.packetmapping.component.nms.ComponentMappingHandleNMS;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.CrossMappedWithContextMappingFunctionHandleImpl;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SimpleWithContextMappingFunctionHandleImpl;
import org.jspecify.annotations.Nullable;

/**
 * The handle passed to {@link ComponentMappingsStep}s.
 */
public class ComponentMappingHandleNMSImpl extends SimpleWithContextMappingFunctionHandleImpl<Component, MutableComponent, ComponentMappingFunctionContext> implements ComponentMappingHandleNMS {

    /**
     * Cached return value for {@link #adventureHandle()}.
     */
    private @Nullable AdventureHandle adventureHandle;

    public ComponentMappingHandleNMSImpl(Component data, ComponentMappingFunctionContext context, boolean isDataMutable) {
        super(data, context, isDataMutable);
    }

    @Override
    protected MutableComponent cloneMutable(Component data) {
        return data.copy();
    }

    private static class AdventureHandle extends CrossMappedWithContextMappingFunctionHandleImpl<net.kyori.adventure.text.Component, ComponentMappingFunctionContext, Component, ComponentMappingHandleNMSImpl> implements ComponentMappingHandle {

        public AdventureHandle(ComponentMappingHandleNMSImpl internal) {
            super(internal);
        }

        @Override
        protected Component mapToInternal(net.kyori.adventure.text.Component data) {
            return PaperAdventure.asVanilla(data);
        }

        @Override
        protected net.kyori.adventure.text.Component mapFromInternal(Component data) {
            return PaperAdventure.asAdventure(data);
        }

    }

    /**
     * @return A handle that can be passed to {@link AdventureFunctionComponentMappingsStep}.
     */
    public ComponentMappingHandle adventureHandle() {
        if (this.adventureHandle == null) {
            this.adventureHandle = new AdventureHandle(this);
        }
        return this.adventureHandle;
    }

}
