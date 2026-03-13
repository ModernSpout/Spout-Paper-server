package org.fiddlemc.fiddle.impl.packetmapping.item;

import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingHandle;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingHandleNMS;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.CrossMappedWithContextMutableMappingFunctionHandleImpl;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SimpleWithContextMappingFunctionHandleImpl;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link ItemMappingHandleNMS}.
 */
public class ItemMappingHandleNMSImpl extends SimpleWithContextMappingFunctionHandleImpl<ItemStack, ItemStack, ItemMappingFunctionContext> implements ItemMappingHandleNMS {

    /**
     * Cached return value for {@link #bukkitHandle()}.
     */
    private @Nullable BukkitHandle bukkitHandle;

    public ItemMappingHandleNMSImpl(ItemStack data, ItemMappingFunctionContext context, boolean isDataMutable) {
        super(data, context, isDataMutable);
    }

    @Override
    protected ItemStack cloneMutable(ItemStack data) {
        return data.copy();
    }

    public static class BukkitHandle extends CrossMappedWithContextMutableMappingFunctionHandleImpl<org.bukkit.inventory.ItemStack, org.bukkit.inventory.ItemStack, ItemMappingFunctionContext, ItemStack, ItemStack, ItemMappingHandleNMSImpl> implements ItemMappingHandle {

        public BukkitHandle(ItemMappingHandleNMSImpl internal) {
            super(internal);
        }

        @Override
        protected ItemStack mapToInternal(org.bukkit.inventory.ItemStack data) {
            return CraftItemStack.asNMSCopy(data);
        }

        @Override
        protected org.bukkit.inventory.ItemStack mapFromInternal(ItemStack data) {
            return CraftItemStack.asCraftMirror(data);
        }

        @Override
        protected ItemStack mapToInternalMutable(org.bukkit.inventory.ItemStack data) {
            return this.mapToInternal(data);
        }

        @Override
        protected org.bukkit.inventory.ItemStack mapFromInternalMutable(ItemStack data) {
            return this.mapFromInternal(data);
        }

    }

    /**
     * @return A handle that can be passed to {@link BukkitFunctionItemMappingsStep}.
     */
    public ItemMappingHandle bukkitHandle() {
        if (this.bukkitHandle == null) {
            this.bukkitHandle = new BukkitHandle(this);
        }
        return this.bukkitHandle;
    }

}
