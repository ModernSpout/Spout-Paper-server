package org.fiddlemc.fiddle.impl.packetmapping.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingHandle;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingHandleNMS;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingUtilitiesNMS;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

/**
 * The implementation for {@link ItemMappingUtilities} and {@link ItemMappingUtilitiesNMS}.
 */
public final class ItemMappingUtilitiesImpl implements ItemMappingUtilitiesNMS {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<ItemMappingUtilities, ItemMappingUtilitiesImpl> implements ItemMappingUtilities.ServiceProvider {

        public ServiceProviderImpl() {
            super(ItemMappingUtilitiesImpl.class);
        }

    }

    public static final class ServiceProviderNMSImpl extends NoArgsConstructorServiceProviderImpl<ItemMappingUtilitiesNMS, ItemMappingUtilitiesImpl> implements ItemMappingUtilitiesNMS.ServiceProvider {

        public ServiceProviderNMSImpl() {
            super(ItemMappingUtilitiesImpl.class);
        }

    }

    public static ItemMappingUtilitiesImpl get() {
        return (ItemMappingUtilitiesImpl) ItemMappingUtilitiesNMS.get();
    }

    @Override
    public boolean setItemTypeWhilePreservingRest(ItemMappingHandle handle, ItemType newItemType) {
        return this.setItemWhilePreservingRest(((ItemMappingHandleNMSImpl.BukkitHandle) handle).internal, ((CraftItemType<?>) newItemType).getHandle());
    }

    @Override
    public boolean setItemWhilePreservingRest(ItemMappingHandleNMS handle, Item newItem) {

        // Don't make changes if the item is already present
        ItemStack immutable = handle.getImmutable();
        Item originalItem = immutable.getItem();
        if (originalItem == newItem) {
            return false;
        }

        // Change the item
        ItemStack mutable = handle.getMutable();
        mutable.setItem(newItem);

        // Restore the item name
        Component originalItemName = immutable.getItemName();
        if (originalItemName.equals(CommonComponents.EMPTY)) {
            originalItemName = null;
        }
        Component newItemName = mutable.getItemName();
        if (newItemName.equals(CommonComponents.EMPTY)) {
            newItemName = null;
        }
        if (newItemName == null && originalItemName == null) {
            if (!newItem.getDescriptionId().equals(originalItem.getDescriptionId())) {
                mutable.set(DataComponents.ITEM_NAME, Component.translatable(originalItem.getDescriptionId()));
            }
        } else if (!Objects.equals(newItemName, originalItemName)) {
            mutable.set(DataComponents.ITEM_NAME, originalItemName != null ? originalItemName : Component.translatable(originalItem.getDescriptionId()));
        }

        // Restore the rarity
        Rarity originalRarity = immutable.getRarity();
        Rarity newRarity = mutable.getRarity();
        if (!newRarity.equals(originalRarity)) {
            Rarity newRarityComponentValue;
            if (mutable.isEnchanted()) {
                // Set the rarity component to one lower
                newRarityComponentValue = switch (originalRarity) {
                    case EPIC -> Rarity.RARE;
                    case RARE -> Rarity.UNCOMMON;
                    default -> Rarity.COMMON;
                };
            } else {
                newRarityComponentValue = originalRarity;
            }
            @Nullable Rarity existingRarityComponentValue = mutable.get(DataComponents.RARITY);
            if (existingRarityComponentValue == null || !existingRarityComponentValue.equals(newRarityComponentValue)) {
                mutable.set(DataComponents.RARITY, newRarityComponentValue);
            }
        }

        // Restore the max damage and damage
        int originalMaxDamage = immutable.getMaxDamage();
        int originalDamage = immutable.getDamageValue();
        int newMaxDamage = mutable.getMaxDamage();
        if (newMaxDamage != originalMaxDamage) {
            mutable.set(DataComponents.MAX_DAMAGE, originalMaxDamage);
        }
        int newDamage = mutable.getDamageValue();
        if (newDamage != originalDamage) {
            mutable.set(DataComponents.DAMAGE, originalDamage);
        }

        // We made changes
        return true;

    }


}
