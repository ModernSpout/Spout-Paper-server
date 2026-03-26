package org.fiddlemc.testplugin.data;

import com.google.common.base.Suppliers;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import java.util.function.Supplier;

/**
 * Don't call {@link Supplier#get} on a field of this class before its item type has been registered.
 */
public final class PluginItemTypes {
    public static Supplier<ItemType> LIT_PAPER_LAMP = itemType("chinese_mythology_mashup:lit_paper_lamp");
    public static Supplier<ItemType> PAPER_LAMP = itemType("chinese_mythology_mashup:paper_lamp");
    public static Supplier<ItemType> YELLOW_MAPLE_LEAVES = itemType("maple_delight:yellow_maple_leaves");
    public static Supplier<ItemType> SNOWED_STONE_BRICKS = itemType("minecraft_dungeons:snowed_stone_bricks");
    public static Supplier<ItemType> DIRT_SLAB = itemType("more_vanilla_shapes:dirt_slab");
    public static Supplier<ItemType> DIRT_STAIRS = itemType("more_vanilla_shapes:dirt_stairs");
    public static Supplier<ItemType> GLASS_SLAB = itemType("more_vanilla_shapes:glass_slab");
    public static Supplier<ItemType> GLASS_STAIRS = itemType("more_vanilla_shapes:glass_stairs");
    public static Supplier<ItemType> GRASS_SLAB = itemType("more_vanilla_shapes:grass_slab");
    public static Supplier<ItemType> STONE_BRICK_BEVEL = itemType("more_vanilla_shapes:stone_brick_bevel");
    public static Supplier<ItemType> AZALEA_PLANKS = itemType("quark:azalea_planks");
    public static Supplier<ItemType> BIRCH_BOOKSHELF = itemType("quark:birch_bookshelf");
    public static Supplier<ItemType> DIORITE_BRICK_SLAB = itemType("quark:diorite_brick_slab");
    public static Supplier<ItemType> DIORITE_BRICK_STAIRS = itemType("quark:diorite_brick_stairs");
    public static Supplier<ItemType> DIORITE_BRICKS = itemType("quark:diorite_bricks");
    public static Supplier<ItemType> GLASS_SHARD = itemType("quark:glass_shard");
    public static Supplier<ItemType> EXAMPLE_BLOCK_ENTITY = itemType("example:block_entity");

    private static Supplier<ItemType> itemType(String key) {
        return Suppliers.memoize(() -> Registry.ITEM.get(Key.key(key)));
    }
}
