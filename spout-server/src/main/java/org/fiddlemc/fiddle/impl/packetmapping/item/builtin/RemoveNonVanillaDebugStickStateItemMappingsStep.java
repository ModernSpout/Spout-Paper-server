package org.fiddlemc.fiddle.impl.packetmapping.item.builtin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingHandleNMSImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsStep;
import org.jspecify.annotations.Nullable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An {@link ItemMappingsStep} to be registered with {@link ItemMappingsImpl},
 * that removes non-vanilla block states from the debug stick state.
 */
public final class RemoveNonVanillaDebugStickStateItemMappingsStep implements ItemMappingsStep {

    @Override
    public void apply(final ItemMappingHandleNMSImpl handle) {
        if (handle.getContext().getClientView().understandsAllServerSideBlocks()) return;
        @Nullable DebugStickState state = handle.getImmutable().get(DataComponents.DEBUG_STICK_STATE);
        if (state == null) return;
        Map<Holder<Block>, Property<?>> properties = state.properties();
        if (properties.keySet().stream().allMatch(holder -> holder.value().isVanilla())) return;
        Map<Holder<Block>, Property<?>> filteredProperties = properties.entrySet().stream().filter(entry -> entry.getKey().value().isVanilla()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        handle.getMutable().set(DataComponents.DEBUG_STICK_STATE, new DebugStickState(filteredProperties));
    }

}
