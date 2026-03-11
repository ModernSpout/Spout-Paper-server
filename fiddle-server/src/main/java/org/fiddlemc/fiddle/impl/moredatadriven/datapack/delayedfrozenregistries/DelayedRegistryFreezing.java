package org.fiddlemc.fiddle.impl.moredatadriven.datapack.delayedfrozenregistries;

import com.mojang.datafixers.util.Pair;
import io.papermc.paper.registry.PaperRegistryListenerManager;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functions to delay freezing of some registries.
 */
public final class DelayedRegistryFreezing {

    private static boolean canFreeze = false;

    private static @Nullable List<Registry<?>> toFreezeLater = new ArrayList<>();
    private static @Nullable List<Pair<ResourceKey<? extends Registry<?>>, Conversions>> toRunFreezeListenersLater = new ArrayList<>();

    public static boolean isFreezingAllowed(Registry<?> registry) {
        return canFreeze || !registry.isFreezingDelayed();
    }

    /**
     * Saves the registry to be frozen later.
     */
    public static void freezeLater(Registry<?> registry) {
        if (!toFreezeLater.contains(registry)) {
            toFreezeLater.add(registry);
        }
    }

    /**
     * Saves the registry to have its freeze listeners be run later.
     */
    public static void runFreezeListenersLater(ResourceKey<? extends Registry<?>> resourceKey, Conversions conversions) {
        if (toRunFreezeListenersLater.stream().noneMatch(entry -> entry.getFirst().equals(resourceKey))) {
            toRunFreezeListenersLater.add(Pair.of(resourceKey, conversions));
        }
    }

    /**
     * Allow freezing from this moment on, and freeze all registries that would have been frozen before.
     */
    public static void freezeDelayedRegistries() {
        canFreeze = true;
        for (Pair<ResourceKey<? extends Registry<?>>, Conversions> entry : toRunFreezeListenersLater) {
            runFreezeListenersInternal(entry.getFirst(), entry.getSecond());
        }
        toRunFreezeListenersLater = null;
        for (Registry<?> registry : toFreezeLater) {
            registry.freeze();
        }
        toFreezeLater = null;
    }

    private static <M> void runFreezeListenersInternal(ResourceKey<? extends Registry<?>> resourceKey, Conversions conversions) {
        PaperRegistryListenerManager.INSTANCE.runFreezeListeners((ResourceKey<? extends Registry<M>>) resourceKey, conversions);
    }

}
