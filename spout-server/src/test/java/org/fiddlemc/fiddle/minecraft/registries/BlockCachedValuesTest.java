package org.fiddlemc.fiddle.minecraft.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.support.environment.VanillaFeature;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests that some cached values in {@link Block} and {@link BlockState}
 * are set correctly by {@link BlockRegistry#register}, {@link VanillaOnlyBlockRegistry#add},
 * {@link BlockStateRegistry#add} and {@link VanillaOnlyBlockStateRegistry#add}.
 */
@VanillaFeature
public class BlockCachedValuesTest {

    @BeforeEach
    public void beforeEach() {
        // Ensure all vanilla blocks are initialized
        var ignored = Blocks.AIR;
        assertFalse(BlockRegistry.get().isEmpty());
    }

    @Test
    public void keyInBlockRegistry() {
        BlockRegistry.get().entrySet().forEach(entry -> {
            var key = entry.getKey();
            var block = entry.getValue();
            assertEquals(key.identifier(), block.keyInBlockRegistry, "Actual key (" + key + ") does not equal keyInBlockRegistry (" + block.keyInBlockRegistry + ") for " + key.identifier());
        });
    }

    @Test
    public void indexInBlockRegistry() {
        BlockRegistry.get().entrySet().forEach(entry -> {
            var key = entry.getKey();
            var block = entry.getValue();
            var lookupResult = BlockRegistry.get().getId(block);
            assertEquals(lookupResult, block.indexInBlockRegistry, "Looked up id (" + lookupResult + ") does not equal indexInBlockRegistry (" + block.indexInBlockRegistry + ") for " + key.identifier());
            lookupResult = VanillaOnlyBlockRegistry.get().getId(block);
            assertEquals(lookupResult, block.indexInVanillaOnlyBlockRegistry, "Looked up id (" + lookupResult + ") does not equal indexInVanillaOnlyBlockRegistry (" + block.indexInVanillaOnlyBlockRegistry + ") for " + key.identifier());
        });
    }

    @Test
    public void indexInBlockStateRegistry() {
        BlockRegistry.get().listElements().forEach(holder -> {
            var block = holder.value();
            for (var blockState : block.getStateDefinition().getPossibleStates()) {
                var lookupResult = BlockStateRegistry.get().getId(blockState);
                assertEquals(lookupResult, blockState.indexInBlockStateRegistry, "Looked up id (" + lookupResult + ") does not equal indexInBlockStateRegistry (" + blockState.indexInBlockStateRegistry + ") for " + blockState);
                lookupResult = VanillaOnlyBlockStateRegistry.get().getId(blockState);
                assertEquals(lookupResult, blockState.indexInVanillaOnlyBlockStateRegistry, "Looked up id (" + lookupResult + ") does not equal indexInVanillaOnlyBlockStateRegistry (" + blockState.indexInVanillaOnlyBlockStateRegistry + ") for " + blockState);
            }
        });
    }

}
