package org.fiddlemc.fiddle.impl.packetmapping.item;

import java.util.Collection;
import net.minecraft.world.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingHandle;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link ItemMappingBuilder}.
 */
public class ItemMappingBuilderImpl extends AbstractItemMappingBuilderImpl<ItemType, ItemMappingHandle> implements ItemMappingBuilder {

    @Override
    public @Nullable NamespacedKey itemModel() {
        return this.itemModel == null ? null : CraftNamespacedKey.fromMinecraft(this.itemModel);
    }

    @Override
    public void itemModel(@Nullable NamespacedKey itemModel) {
        this.itemModel = itemModel == null ? null : CraftNamespacedKey.toMinecraft(itemModel);
    }

    @Override
    protected Collection<ItemType> getItemsToRegisterFor() {
        return this.from;
    }

    @Override
    protected ItemMappingsStep createFunctionStep() {
        return new BukkitFunctionItemMappingsStep(this.function);
    }

    @Override
    protected Item getSimpleTo() {
        return ((CraftItemType<?>) this.to).getHandle();
    }

}
