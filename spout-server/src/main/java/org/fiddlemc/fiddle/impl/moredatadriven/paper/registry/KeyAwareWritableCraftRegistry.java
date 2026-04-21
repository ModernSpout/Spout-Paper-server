package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.WritableCraftRegistry;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.entry.RegistryEntryMeta;
import net.minecraft.core.MappedRegistry;
import org.bukkit.Keyed;
import java.util.function.Consumer;

/**
 * A modified {@link WritableCraftRegistry}, where {@link #createApiWritableRegistry} returns
 * {@link KeyAwareApiWritableRegistry} instead of {@link WritableCraftRegistry.ApiWritableRegistry}.
 */
public class KeyAwareWritableCraftRegistry<M, T extends Keyed, B extends SettableKeyAwareRegistryEntryNMS & PaperRegistryBuilder<M, T>> extends WritableCraftRegistry<M, T, B> {

    public KeyAwareWritableCraftRegistry(final MappedRegistry<M> registry, final RegistryEntryMeta.Buildable<M, T, B> meta) {
        super(registry, meta);
    }

    @Override
    public KeyAwareApiWritableRegistry createApiWritableRegistry(final Conversions conversions) {
        return new KeyAwareApiWritableRegistry(conversions);
    }

    /**
     * A modified {@link WritableCraftRegistry.ApiWritableRegistry},
     * that calls {@link SettableKeyAwareRegistryEntryNMS#setKeyNMS}.
     */
    public class KeyAwareApiWritableRegistry extends ApiWritableRegistry {

        public KeyAwareApiWritableRegistry(final Conversions conversions) {
            super(conversions);
        }

        @Override
        public void registerWith(final TypedKey<T> key, final Consumer<RegistryBuilderFactory<T, B>> value) {
            super.registerWith(key, factory -> {
                RegistryBuilderFactory<T, B> factoryWithInitializedKey = new RegistryBuilderFactory<>() {

                    @Override
                    public B empty() {
                        B builder = factory.empty();
                        builder.setKeyNMS(PaperAdventure.asVanilla(key));
                        return builder;
                    }

                    @Override
                    public B copyFrom(final TypedKey<T> key) {
                        B builder = factory.empty();
                        builder.setKeyNMS(PaperAdventure.asVanilla(key));
                        return builder;
                    }

                };
                value.accept(factoryWithInitializedKey);
            });
        }

    }

}
