package org.fiddlemc.fiddle.impl.packetmapping.item;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingFunctionContext;
import org.fiddlemc.fiddle.impl.packetmapping.WithClientViewMappingFunctionContextImpl;

/**
 * The implementation of {@link ItemMappingFunctionContext}.
 */
public class ItemMappingFunctionContextImpl extends WithClientViewMappingFunctionContextImpl implements ItemMappingFunctionContext {

    private final boolean isItemStackInItemFrame;
    private final boolean isStonecutterRecipeResult;

    public ItemMappingFunctionContextImpl(ClientView clientView, boolean isItemStackInItemFrame, boolean isStonecutterRecipeResult) {
        super(clientView);
        this.isItemStackInItemFrame = isItemStackInItemFrame;
        this.isStonecutterRecipeResult = isStonecutterRecipeResult;
    }

    @Override
    public boolean isItemStackInItemFrame() {
        return this.isItemStackInItemFrame;
    }

    @Override
    public boolean isStonecutterRecipeResult() {
        return this.isStonecutterRecipeResult;
    }

}
