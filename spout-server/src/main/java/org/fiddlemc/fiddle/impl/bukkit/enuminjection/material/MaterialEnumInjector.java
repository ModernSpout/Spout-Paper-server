package org.fiddlemc.fiddle.impl.bukkit.enuminjection.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;
import org.fiddlemc.fiddle.impl.util.java.enuminjection.EnumInjector;
import org.fiddlemc.fiddle.impl.util.java.function.ConsumerThrowsException;
import org.fiddlemc.fiddle.impl.util.java.reflect.ReflectionUtil;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An {@link EnumInjector} for {@link Material}.
 */
public class MaterialEnumInjector extends EnumInjector<Material> {

    private final Field keyField;
    private final Field idField;
    private final Field legacyField;
    private final Field ctorField;
    private final Field dataField;
    private final Field itemTypeField;
    private final Field blockTypeField;
    private final Field byNameField;

    public MaterialEnumInjector() throws Exception {
        super(Material.class);
        this.keyField = ReflectionUtil.getDeclaredField(Material.class, "key");
        this.idField = ReflectionUtil.getDeclaredField(Material.class, "id");
        this.legacyField = ReflectionUtil.getDeclaredField(Material.class, "legacy");
        this.ctorField = ReflectionUtil.getDeclaredField(Material.class, "ctor");
        this.dataField = ReflectionUtil.getDeclaredField(Material.class, "data");
        this.itemTypeField = ReflectionUtil.getDeclaredField(Material.class, "itemType");
        this.blockTypeField = ReflectionUtil.getDeclaredField(Material.class, "blockType");
        this.byNameField = ReflectionUtil.getDeclaredField(Material.class, "BY_NAME");
    }

    @Override
    protected void stage(final String enumName, @Nullable final ConsumerThrowsException<Material, Exception> onAllocate) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see EnumInjector#stage 
     */
    public void stage(String enumName, NamespacedKey key, @Nullable BlockType blockType, @Nullable ItemType itemType) {
        super.stage(enumName, material -> {
            // Set its getKey()
            keyField.set(material, key);
            // Set its getId()
            this.idField.set(material, -1);
            // Set its isLegacy()
            this.legacyField.set(material, false);
            // Set its MaterialData constructor
            this.ctorField.set(material, MaterialData.class.getConstructor(Material.class, byte.class));
            // Set its public data field
            this.dataField.set(material, MaterialData.class); // TODO set based on actual block or item
            // Set its asItemType()
            this.itemTypeField.set(material, (Supplier<ItemType>) () -> itemType);
            // Set its asBlockType()
            this.blockTypeField.set(material, (Supplier<BlockType>) () -> blockType);
            // Support it in getMaterial(..)
            ((Map<String, Material>) this.byNameField.get(null)).put(enumName, material);
            // Add to CraftMagicNumbers conversions
            if (itemType != null) {
                Item item = ((CraftItemType<?>) itemType).getHandle();
                CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
                CraftMagicNumbers.ITEM_MATERIAL.put(item, material);
            }
            if (blockType != null) {
                Block block = ((CraftBlockType<?>) blockType).getHandle();
                CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
                CraftMagicNumbers.BLOCK_MATERIAL.put(block, material);
            }
        });
    }

}
