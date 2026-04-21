package org.fiddlemc.fiddle.api.bukkit.enuminjection.material;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNames;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import org.jspecify.annotations.Nullable;
import java.util.ServiceLoader;

/**
 * The {@link BukkitEnumNames} for {@link Material}.
 */
public interface MaterialEnumNames extends BukkitEnumNames<Triple<NamespacedKey, @Nullable BlockType, @Nullable ItemType>> {

    /**
     * An internal interface to get the {@link MaterialEnumNames} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<MaterialEnumNames> {
    }

    /**
     * @return The {@link MaterialEnumNames} instance.
     */
    static MaterialEnumNames get() {
        return ServiceLoader.load(MaterialEnumNames.ServiceProvider.class, MaterialEnumNames.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
