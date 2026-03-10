/**
 * <h1>More data-driven - Data pack</h1>
 *
 * <p>
 * Allows vanilla blocks and items to be added using data packs.
 * </p>
 *
 * <p>
 * Specifically, this adds {@link org.fiddlemc.fiddle.impl.moredatadriven.datapack.BlockDefinition}
 * and {@link org.fiddlemc.fiddle.impl.moredatadriven.datapack.ItemDefinition},
 * which are resources loaded from data packs.
 * Those definitions are then added as {@link net.minecraft.world.level.block.Block}
 * and {@link net.minecraft.world.item.Item} instances in their respective registries.
 * </p>
 */
@NullMarked
package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import org.jspecify.annotations.NullMarked;
