package org.fiddlemc.fiddle.impl.packetmapping.item.applytohoverevent;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.item.ItemStack;
import org.fiddlemc.fiddle.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsImpl;

import java.util.function.Function;

/**
 * Holder for {@link #CLIENT_VIEW_MAPPED_CODEC}.
 */
public final class ClientViewMappedItemStackMapCodec {

    private ClientViewMappedItemStackMapCodec() {
        throw new UnsupportedOperationException();
    }

    /**
     * A modified version of {@link ItemStack#MAP_CODEC}, which maps the item according to the
     * {@link ClientViewLookupThreadLocal#getThreadLocalClientViewOrFallback}.
     *
     * <p>
     * The usage of this instance in {@link HoverEvent.ShowItem} makes it so that
     * item stacks in hover events are mapped.
     * </p>
     */
    public static final MapCodec<ItemStack> CLIENT_VIEW_MAPPED_CODEC = ItemStack.MAP_CODEC.xmap(
        Function.identity(), // Used by io.papermc.paper.adventure.WrapperAwareSerializer#deserialize
        itemStack -> ItemMappingsImpl.get().applyGenerically(itemStack)
    );

}
