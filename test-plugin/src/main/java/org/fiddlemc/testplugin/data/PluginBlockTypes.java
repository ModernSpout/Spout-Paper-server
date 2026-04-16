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
    public static Supplier<BlockType> LIT_PAPER_LAMP = blockType("chinese_mythology_mashup:lit_paper_lamp");
    public static Supplier<BlockType> PAPER_LAMP = blockType("chinese_mythology_mashup:paper_lamp");
    public static Supplier<BlockType> YELLOW_MAPLE_LEAVES = blockType("maple_delight:yellow_maple_leaves");
    public static Supplier<BlockType> SNOWED_STONE_BRICKS = blockType("minecraft_dungeons:snowed_stone_bricks");
    public static Supplier<BlockType> DIRT_SLAB = blockType("more_vanilla_shapes:dirt_slab");
    public static Supplier<BlockType> DIRT_STAIRS = blockType("more_vanilla_shapes:dirt_stairs");
    public static Supplier<BlockType> GLASS_SLAB = blockType("more_vanilla_shapes:glass_slab");
    public static Supplier<BlockType> GLASS_STAIRS = blockType("more_vanilla_shapes:glass_stairs");
    public static Supplier<BlockType> STONE_BRICK_BEVEL = blockType("more_vanilla_shapes:stone_brick_bevel");
    public static Supplier<BlockType> AZALEA_PLANKS = blockType("quark:azalea_planks");
    public static Supplier<BlockType> BIRCH_BOOKSHELF = blockType("quark:birch_bookshelf");
    public static Supplier<BlockType> DIORITE_BRICK_SLAB = blockType("quark:diorite_brick_slab");
    public static Supplier<BlockType> DIORITE_BRICK_STAIRS = blockType("quark:diorite_brick_stairs");
    public static Supplier<BlockType> DIORITE_BRICKS = blockType("quark:diorite_bricks");

    private static Supplier<BlockType> blockType(String key) {
        return Suppliers.memoize(() -> Registry.BLOCK.get(Key.key(key)));
    }
}
