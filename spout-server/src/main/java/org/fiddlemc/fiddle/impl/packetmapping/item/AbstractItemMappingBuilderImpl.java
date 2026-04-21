package org.fiddlemc.fiddle.impl.packetmapping.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.jspecify.annotations.Nullable;

/**
 * A common base for {@link ItemMappingBuilderImpl} and {@link ItemMappingBuilderNMSImpl}.
 */
public abstract class AbstractItemMappingBuilderImpl<T, H> {

    protected @Nullable ArrayList<ClientView.AwarenessLevel> awarenessLevels;
    protected @Nullable ArrayList<T> from;
    protected @Nullable T to;
    protected @Nullable Consumer<H> function;
    protected @Nullable Boolean overrideItemModel;
    protected @Nullable Identifier itemModel;

    public void awarenessLevel(Collection<ClientView.AwarenessLevel> awarenessLevels) {
        this.awarenessLevels = new ArrayList<>(awarenessLevels);
    }

    public void addAwarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        if (this.awarenessLevels == null) {
            this.awarenessLevels = new ArrayList<>(1);
        }
        this.awarenessLevels.add(awarenessLevel);
    }

    public void from(Collection<? extends T> from) {
        this.from = new ArrayList<>(from);
    }

    public void addFrom(T from) {
        if (this.from == null) {
            this.from = new ArrayList<>(1);
        }
        this.from.add(from);
    }

    public void to(T to) {
        this.to = to;
    }

    public void to(Consumer<H> function) {
        this.function = function;
    }

    public @Nullable Boolean overrideItemModel() {
        return this.overrideItemModel;
    }

    public void overrideItemModel(@Nullable Boolean overrideItemModel) {
        this.overrideItemModel = overrideItemModel;
    }

    public void itemModel(Identifier itemModel) {
        this.itemModel = itemModel;
    }

    abstract protected Collection<ItemType> getItemsToRegisterFor();

    abstract protected ItemMappingsStep createFunctionStep();

    abstract protected Item getSimpleTo();

    public void registerWith(ItemMappingsComposeEventImpl event) {
        List<ClientView.AwarenessLevel> awarenessLevels = this.awarenessLevels != null ? this.awarenessLevels : Arrays.asList(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems());
        if (this.from == null) {
            throw new IllegalStateException("No from was specified");
        }
        boolean registeredMapping = false;
        Collection<ItemType> itemsToRegisterFor = this.getItemsToRegisterFor();
        if (this.function != null) {
            event.register(awarenessLevels, itemsToRegisterFor, this.createFunctionStep());
            registeredMapping = true;
        } else if (this.to != null) {
            event.register(awarenessLevels, itemsToRegisterFor, new SimpleItemMappingsStep(this.getSimpleTo()));
            registeredMapping = true;
        }
        if (this.overrideItemModel == null || this.overrideItemModel) {
            // Override item model
            List<ClientView.AwarenessLevel> awarenessLevelsToOverrideItemModelFor;
            if (this.overrideItemModel == null && awarenessLevels.contains(ClientView.AwarenessLevel.VANILLA)) {
                awarenessLevelsToOverrideItemModelFor = awarenessLevels.stream().filter(level -> level != ClientView.AwarenessLevel.VANILLA).toList();
            } else {
                awarenessLevelsToOverrideItemModelFor = awarenessLevels;
            }
            if (!awarenessLevelsToOverrideItemModelFor.isEmpty()) {
                if (this.itemModel != null) {
                    Identifier itemModelToUse = this.itemModel;
                    event.register(awarenessLevelsToOverrideItemModelFor, itemsToRegisterFor, new MinecraftFunctionItemMappingsStep(handle -> {
                        handle.getMutable().set(DataComponents.ITEM_MODEL, itemModelToUse);
                    }));
                } else {
                    for (ItemType fromItem : itemsToRegisterFor) {
                        Identifier itemModelToUse = CraftItemType.bukkitToMinecraftNew(fromItem).keyInItemRegistry;
                        event.register(awarenessLevelsToOverrideItemModelFor, List.of(fromItem), new MinecraftFunctionItemMappingsStep(handle -> {
                            handle.getMutable().set(DataComponents.ITEM_MODEL, itemModelToUse);
                        }));
                    }
                }
                registeredMapping = true;
            }
        }
        if (!registeredMapping) {
            throw new IllegalStateException("Empty mapping: no to(item), to(function) was given, and no item model needed to be overridden");
        }
    }

}
