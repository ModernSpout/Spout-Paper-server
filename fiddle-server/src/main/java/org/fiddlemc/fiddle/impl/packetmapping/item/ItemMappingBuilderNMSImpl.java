package org.fiddlemc.fiddle.impl.packetmapping.item;

import java.util.Collection;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingHandleNMS;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link BlockMappingBuilderNMS}.
 */
public class ItemMappingBuilderNMSImpl extends AbstractItemMappingBuilderImpl<Item, ItemMappingHandleNMS> implements ItemMappingBuilderNMS {

    @Override
    public @Nullable Identifier itemModel() {
        return this.itemModel;
    }

    @Override
    protected Collection<ItemType> getItemsToRegisterFor() {
        return this.from.stream().map(item -> Registry.ITEM.get(Key.key(item.keyInItemRegistry.getNamespace(), item.keyInItemRegistry.getPath()))).toList();
    }

    @Override
    protected ItemMappingsStep createFunctionStep() {
        return new MinecraftFunctionItemMappingsStep(this.function);
    }

    @Override
    protected Item getSimpleTo() {
        return this.to;
    }

}
