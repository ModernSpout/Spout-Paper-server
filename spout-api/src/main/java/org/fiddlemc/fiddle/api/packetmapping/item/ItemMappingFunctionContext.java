package org.fiddlemc.fiddle.api.packetmapping.item;

import org.fiddlemc.fiddle.api.packetmapping.WithClientViewMappingFunctionContext;
import org.fiddlemc.fiddle.api.util.mapping.MappingFunctionContext;

/**
 * A {@link MappingFunctionContext} for the {@link ItemMappingHandle}s.
 */
public interface ItemMappingFunctionContext extends WithClientViewMappingFunctionContext {

    /**
     * @return Whether the item stack on which this mapping is being applied
     * is an item stack in an item frame.
     */
    boolean isItemStackInItemFrame();

    /**
     * @return Whether the item stack on which this mapping is being applied
     * is the result of a stonecutter recipe.
     */
    boolean isStonecutterRecipeResult();

}
