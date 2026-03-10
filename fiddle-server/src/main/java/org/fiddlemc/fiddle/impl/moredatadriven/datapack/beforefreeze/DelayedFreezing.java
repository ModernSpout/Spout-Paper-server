package org.fiddlemc.fiddle.impl.moredatadriven.datapack.beforefreeze;

import com.mojang.datafixers.util.Pair;
import io.papermc.paper.registry.PaperRegistryListenerManager;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functions to delay freezing registries.
 */
public final class DelayedFreezing {

    private static boolean canFreeze = false;

    private static @Nullable List<Registry<?>> toFreezeLater = new ArrayList<>();
    private static @Nullable List<Pair<ResourceKey<? extends Registry<?>>, Conversions>> toRunFreezeListenersLater = new ArrayList<>();

    /**
     * @return Whether the registry can be frozen now.
     * If false, saves the registry to be frozen later.
     */
    public static boolean freeze(Registry<?> registry) {
        if (canFreeze) {
            return true;
        }
        if (!toFreezeLater.contains(registry)) {
            toFreezeLater.add(registry);
        }
        return false;
    }

    /**
     * @return Whether the registry freeze listeners can be run now.
     * If false, saves the registry to have its freeze listeners be run later.
     */
    public static boolean runFreezeListeners(ResourceKey<? extends Registry<?>> resourceKey, Conversions conversions) {
        if (canFreeze) {
            return true;
        }
        if (toRunFreezeListenersLater.stream().noneMatch(entry -> entry.getFirst().equals(resourceKey))) {
            toRunFreezeListenersLater.add(Pair.of(resourceKey, conversions));
        }
        return false;
    }

    /**
     * Allow freezing from this moment on, and freeze all registries that would have been frozen before.
     */
    public static void allowFreezing() {
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
