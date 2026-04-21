package org.fiddlemc.fiddle.impl.moredatadriven.minecraft;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

/**
 * An implementation of {@link Registry} specially for {@link BuiltInRegistries#ITEM}.
 *
 * <p>
 * A single instance of this class shall exist: {@link #INSTANCE}.
 * </p>
 */
public final class ItemRegistry extends DefaultedMappedRegistry<Item> {

    private static @Nullable ItemRegistry INSTANCE;

    public static ItemRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new ItemRegistry();
        }
        return INSTANCE;
    }

    private ItemRegistry() {
        super("air", Registries.ITEM, Lifecycle.stable(), true);
    }

    @Override
    public Holder.Reference<Item> register(ResourceKey<Item> key, Item entry, RegistrationInfo registrationInfo) {
        // Register the entry
        var reference = super.register(key, entry, registrationInfo);
        // Initialize the cached registry key field
        entry.keyInItemRegistry = super.getKey(entry);
        // Initialize the cached registry index field
        entry.indexInItemRegistry = super.getId(entry);
        // Register the item by its blocks if applicable
        if (entry instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, entry);
        }
        // Also add the item to the vanilla-only registry if applicable
        if (entry.isVanilla()) {
            VanillaOnlyItemRegistry.get().add(entry);
        }
        // Return the reference
        return reference;
    }

    @Override
    public Identifier getKey(Item value) {
        // Use the cached value
        return value.keyInItemRegistry;
    }

    @Override
    public int getId(@Nullable Item value) {
        // Use the cached value
        return value == null ? -1 : value.indexInItemRegistry;
    }

    @Override
    public boolean isFreezingDelayed() {
        return true;
    }

}
