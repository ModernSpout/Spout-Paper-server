/**
 * <h1>Bukkit enum injection - Implementation part</h1>
 *
 * <p>
 * Bukkit enums are based on other values.
 * This means that Bukkit enum injections are typically based on such other values,
 * and for such purpose, this package provides
 * {@link org.fiddlemc.fiddle.impl.bukkit.enuminjection.BukkitEnumSynchronizer} and its subclass
 * {@link org.fiddlemc.fiddle.impl.bukkit.enuminjection.KeyedSourceBukkitEnumSynchronizer}
 * for the easy injection of Bukkit enum values based on some source values.
 * </p>
 */
@NullMarked
package org.fiddlemc.fiddle.impl.bukkit.enuminjection;

import org.jspecify.annotations.NullMarked;
