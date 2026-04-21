package org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type;

import org.bukkit.Keyed;

/**
 * A block type, implementing {@link Keyed}.
 */
public interface FiddleBlockType extends Keyed {

    Object getWrappedCodec();

}
