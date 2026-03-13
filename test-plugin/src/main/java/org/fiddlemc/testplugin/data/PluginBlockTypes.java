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
    public static Supplier<BlockType> BIRCH_BOOKSHELF = blockType("quark:birch_bookshelf");
    public static Supplier<BlockType> DIORITE_BRICKS = blockType("quark:diorite_bricks");
    public static Supplier<BlockType> DIORITE_BRICKS_SLAB = blockType("quark:diorite_bricks_slab");
    public static Supplier<BlockType> DIORITE_BRICKS_STAIRS = blockType("quark:diorite_bricks_stairs");

    private static Supplier<BlockType> blockType(String key) {
        return Suppliers.memoize(() -> Registry.BLOCK.get(Key.key(key)));
    }
}
