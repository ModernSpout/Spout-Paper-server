package org.fiddlemc.testplugin.data;

import com.google.common.base.Suppliers;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import java.util.function.Supplier;

/**
 * Don't call {@link Supplier#get} on a field of this class before its block type has been registered.
 */
public final class PluginBlockTypes {
    public static Supplier<BlockType> ASH_BLOCK = blockType("example:ash_block");
    public static Supplier<BlockType> ASH_STAIRS = blockType("example:ash_stairs");
    public static Supplier<BlockType> MAPLE_LEAVES = blockType("example:maple_leaves");

    private static Supplier<BlockType> blockType(String key) {
        return Suppliers.memoize(() -> Registry.BLOCK.get(Key.key(key)));
    }
}
