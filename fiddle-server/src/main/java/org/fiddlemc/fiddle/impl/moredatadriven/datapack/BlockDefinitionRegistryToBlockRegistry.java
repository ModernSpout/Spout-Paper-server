package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.beforefreeze.LoadBeforeFreezeBuiltInRegistries;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;

/**
 * Utility class that adds a {@link Block} entry to the {@link BlockRegistry}
 * for each {@link BlockDefinition} entry in the {@link FiddleDataPackResourceRegistries#BLOCK_DEFINITION} registry.
 */
public final class BlockDefinitionRegistryToBlockRegistry {

    private BlockDefinitionRegistryToBlockRegistry() {
        throw new UnsupportedOperationException();
    }

    public static void apply() {
        LoadBeforeFreezeBuiltInRegistries.BLOCK_DEFINITION.listElements().forEach(reference -> {
            ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, reference.key().identifier());
            Block block = BlockDefinitionFromToBlock.to(blockKey, reference.value());
            Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        });
    }

}
