package org.fiddlemc.fiddle.impl.bukkit.enuminjection;

import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamePickFunctionHandle;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamesComposeEvent;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The implementation of {@link BukkitEnumNamesComposeEvent}.
 */
public final class BukkitEnumNameMappingPipelineComposeEventImpl<S> implements PaperLifecycleEvent, BukkitEnumNamesComposeEvent<S> {

    /**
     * The registered mappings,
     * or null if no mappings are registered.
     */
    @Nullable List<Consumer<BukkitEnumNamePickFunctionHandle<S>>> mappings;

    @Override
    public void register(Consumer<BukkitEnumNamePickFunctionHandle<S>> mapping) {
        if (this.mappings == null) {
            this.mappings = new ArrayList<>(1);
        }
        this.mappings.add(mapping);
    }

    @Override
    public void changeRegistered(Consumer<List<Consumer<BukkitEnumNamePickFunctionHandle<S>>>> consumer) {
        List<Consumer<BukkitEnumNamePickFunctionHandle<S>>> passed = this.mappings != null ? this.mappings : new ArrayList<>(1);
        consumer.accept(passed);
        if (passed.isEmpty() && this.mappings != null) {
            this.mappings = null;
        } else if (!passed.isEmpty() && this.mappings == null) {
            this.mappings = passed;
        }
    }

}
