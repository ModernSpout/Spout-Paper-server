package org.fiddlemc.fiddle.minecraft.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.bukkit.support.environment.VanillaFeature;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyItemRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests that some cached values in {@link Item}
 * are set correctly by {@link ItemRegistry#register} and {@link VanillaOnlyItemRegistry#add}.
 */
@VanillaFeature
public class ItemCachedValuesTest {

    @BeforeEach
    public void beforeEach() {
        // Ensure all vanilla items are initialized
        var ignored = Items.AIR;
        assertFalse(ItemRegistry.get().isEmpty());
    }

    @Test
    public void keyInItemRegistry() {
        ItemRegistry.get().entrySet().forEach(entry -> {
            var key = entry.getKey();
            var item = entry.getValue();
            assertEquals(key.identifier(), item.keyInItemRegistry, "Actual key (" + key + ") does not equal keyInItemRegistry (" + item.keyInItemRegistry + ") for " + key.identifier());
        });
    }

    @Test
    public void indexInItemRegistry() {
        ItemRegistry.get().entrySet().forEach(entry -> {
            var key = entry.getKey();
            var item = entry.getValue();
            var lookupResult = ItemRegistry.get().getId(item);
            assertEquals(lookupResult, item.indexInItemRegistry, "Looked up id (" + lookupResult + ") does not equal indexInItemRegistry (" + item.indexInItemRegistry + ") for " + key.identifier());
            lookupResult = VanillaOnlyItemRegistry.get().getId(item);
            assertEquals(lookupResult, item.indexInVanillaOnlyItemRegistry, "Looked up id (" + lookupResult + ") does not equal indexInVanillaOnlyItemRegistry (" + item.indexInVanillaOnlyItemRegistry + ") for " + key.identifier());
        });
    }

}
