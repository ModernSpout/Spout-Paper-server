package org.fiddlemc.fiddle.impl.packetmapping.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappings;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingsComposeEvent;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.WithClientViewContextSingleStepMappingPipeline;
import org.fiddlemc.fiddle.impl.packetmapping.item.builtin.MapDefaultItemNamesItemMappingsStep;
import org.fiddlemc.fiddle.impl.packetmapping.item.builtin.RemoveNonVanillaDebugStickStateItemMappingsStep;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.WithContextSingleStepMappingPipeline;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A pipeline of item mappings.
 */
public final class ItemMappingsImpl extends ComposableImpl<ItemMappingsComposeEvent<ItemMappingsStep>, ItemMappingsComposeEventImpl> implements WithClientViewContextSingleStepMappingPipeline<ItemStack, ItemMappingFunctionContext, ItemMappingHandleNMSImpl>, ItemMappings<ItemMappingsStep> {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<ItemMappings<?>, ItemMappingsImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(ItemMappingsImpl.class);
        }

    }

    public static ItemMappingsImpl get() {
        return (ItemMappingsImpl) ItemMappings.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_item_mappings";
    }

    /**
     * The registered mappings.
     *
     * <p>
     * The mappings are organized in an array where {@link ClientView.AwarenessLevel#ordinal()}
     * is the index, and then in an array where {@link Item#indexInItemRegistry} is the index.
     * The lowest-level array may be null, but will never be empty.
     * </p>
     */
    private final ItemMappingsStep[][][] mappings;

    private ItemMappingsImpl() {
        this.mappings = new ItemMappingsStep[ClientView.AwarenessLevel.getAll().length][][];
    }

    @Override
    public ItemMappingsStep @Nullable [] getStepsThatMayApplyTo(ItemMappingHandleNMSImpl handle) {
        return this.mappings[handle.getContext().getClientView().getAwarenessLevel().ordinal()][handle.getOriginal().getItem().indexInItemRegistry];
    }

    @Override
    public ItemMappingHandleNMSImpl createHandle(ItemStack data, ItemMappingFunctionContext context) {
        return new ItemMappingHandleNMSImpl(data, context, false);
    }

    @Override
    public ItemMappingFunctionContextImpl createGenericContext(ClientView clientView) {
        return new ItemMappingFunctionContextImpl(clientView, false, false);
    }

    @Override
    public ItemStack apply(ItemStack data, ItemMappingFunctionContext context) {
        // Skip the mapping for empty item stacks
        if (data.isEmpty() || data.getItem() == null) {
            return data;
        }
        // Apply the pipeline
        return WithClientViewContextSingleStepMappingPipeline.super.apply(data, context);
    }

    /**
     * Similar to {@link #apply}, but for a whole item type.
     *
     * @return The mapped {@link Item}, on a best-attempt basis.
     */
    public Item apply(Item data, ItemMappingFunctionContext context) {
        return apply(new ItemStack(data), context).getItem();
    }

    /**
     * Convenience function for {@link #apply(Item, ItemMappingFunctionContext)},
     * analogous to {@link WithContextSingleStepMappingPipeline#applyGenerically}.
     */
    public Item applyGenerically(Item data) {
        return this.apply(data, this.createGenericContext());
    }

    /**
     * Convenience function to call {@link #apply(Item, ItemMappingFunctionContext)}
     * for all items in the given {@link HolderSet}.
     *
     * @return A {@link HolderSet} of mapped {@link Item}s.
     * This {@link HolderSet} may be the given {@code data}, and if not, the {@link Holder}s inside
     * may be those in {@code data}.
     */
    public HolderSet<Item> apply(HolderSet<Item> data, ItemMappingFunctionContext context) {
        Int2ObjectMap<Item> fromToMap = new Int2ObjectArrayMap<>();
        List<Holder<Item>> result = new ArrayList<>(data.size());
        boolean changed = false;
        for (int i = 0; i < data.size(); i++) {
            Holder<Item> holder = data.get(i);
            Item item = holder.value();
            int id = item.indexInItemRegistry;
            Item mapped = fromToMap.computeIfAbsent(id, $ -> this.apply(item, context));
            if (mapped != item) {
                changed = true;
            }
            result.add(changed ? Holder.direct(mapped) : holder);
        }
        return changed ? HolderSet.direct(result) : data;
    }

    /**
     * Convenience function for {@link #apply(HolderSet, ItemMappingFunctionContext)},
     * analogous to {@link WithContextSingleStepMappingPipeline#applyGenerically}.
     */
    public HolderSet<Item> applyGenerically(HolderSet<Item> data) {
        return this.apply(data, this.createGenericContext());
    }

    @Override
    protected ItemMappingsComposeEventImpl createComposeEvent() {

        // Create the event
        ItemMappingsComposeEventImpl event = new ItemMappingsComposeEventImpl();

        // Register the built-in default item names mapping step
        event.register(
            Arrays.asList(ClientView.AwarenessLevel.getAll()),
            Registry.ITEM.stream().toList(),
            new MapDefaultItemNamesItemMappingsStep()
        );

        // Register the built-in non-vanilla debug stick state removal step
        event.register(
            Arrays.asList(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks()),
            List.of(ItemType.DEBUG_STICK),
            new RemoveNonVanillaDebugStickStateItemMappingsStep()
        );

        // Register data-driven mappings
        ItemRegistry.get().forEach(item -> {
            if (item.unappliedDataPackMappings != null) {
                // Apply the mappings
                item.unappliedDataPackMappings.forEach(mapping -> mapping.apply(event, item));
                // Dereference the mappings to reclaim memory
                item.unappliedDataPackMappings = null;
            }
        });

        // Return the event
        return event;

    }

    @Override
    protected void copyInformationFromEvent(ItemMappingsComposeEventImpl event) {

        // Initialize the steps
        int registrySize = ItemRegistry.get().size();
        for (int i = 0; i < this.mappings.length; i++) {
            this.mappings[i] = new ItemMappingsStep[registrySize][];
        }

        // Copy steps from event
        event.copyRegisteredInvertedAndReinvertedInto(this.mappings, ItemMappingsStep[]::new);

    }

}
