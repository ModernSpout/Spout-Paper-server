package org.fiddlemc.fiddle.impl.resourcepack.construct;

import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackPath;

/**
 * The implementation for {@link FiddleResourcePackPath}.
 */
public record FiddleResourcePackPathImpl(FiddleResourcePackConstructEventImpl event, String path) implements FiddleResourcePackPath {
}
