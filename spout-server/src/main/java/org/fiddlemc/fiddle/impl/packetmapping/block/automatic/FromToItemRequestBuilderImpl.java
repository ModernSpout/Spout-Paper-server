package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.FromItemRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ToItemRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.jspecify.annotations.Nullable;

/**
 * A base implementation of {@link FromToBlockStateRequestBuilderImpl}
 * and {@link FromToBlockTypeRequestBuilderImpl}.
 */
public abstract class FromToItemRequestBuilderImpl<US extends UsedStates> extends ProxyStatesRequestBuilderImpl<US> implements FromItemRequestBuilder<US>, ToItemRequestBuilder<US> {

    public @Nullable Item fromItem;
    public @Nullable Item fallbackItem;

    @Override
    public void fromItem(@Nullable ItemType fromItem) {
        this.fromItem = fromItem == null ? null : CraftItemType.bukkitToMinecraftNew(fromItem);
    }

    protected abstract @Nullable Block getBlockToInferFromItem();

    protected @Nullable Item inferFromItem() {
        return inferItem(this.getBlockToInferFromItem());
    }

    public @Nullable Item fromItemMinecraft() {
        return this.fromItem != null ? this.fromItem : this.inferFromItem();
    }

    @Override
    public @Nullable ItemType fromItem() {
        Item item = this.fromItemMinecraft();
        return item == null ? null : CraftItemType.minecraftToBukkitNew(item);
    }

    protected abstract @Nullable Block getBlockToInferFallbackItem();

    protected @Nullable Item inferFallbackItem() {
        return inferItem(this.getBlockToInferFallbackItem());
    }

    @Override
    public void fallbackItem(@Nullable ItemType fallbackItem) {
        this.fallbackItem = fallbackItem == null ? null : CraftItemType.bukkitToMinecraftNew(fallbackItem);
    }

    public @Nullable Item fallbackItemMinecraft() {
        return this.fallbackItem != null ? this.fallbackItem : this.inferFallbackItem();
    }

    @Override
    public @Nullable ItemType fallbackItem() {
        Item item = this.fallbackItemMinecraft();
        return item == null ? null : CraftItemType.minecraftToBukkitNew(item);
    }

    public @Nullable Identifier getFromItemModel() { // TODO make customizable
        @Nullable Item item = this.fromItemMinecraft();
        return item == null ? null : item.getDefaultInstance().getOrDefault(DataComponents.ITEM_MODEL, item.keyInItemRegistry);
    }

    public static @Nullable Item inferItem(@Nullable Block block) {
        if (block == null) return null;
        Item item = block.asItem();
        if (item == Items.AIR) return null;
        return item;
    }

}
