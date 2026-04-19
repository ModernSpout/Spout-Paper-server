package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type;

import org.bukkit.Keyed;

/**
 * An item type, implementing {@link Keyed}.
 */
public interface FiddleItemType extends Keyed {

    Object getWrappedCodec();

}
