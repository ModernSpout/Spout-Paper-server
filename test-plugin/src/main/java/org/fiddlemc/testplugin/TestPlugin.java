package org.fiddlemc.testplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class TestPlugin extends JavaPlugin implements Listener {

    private Logger logger;

    @Override
    public void onEnable() {

        // Get the logger
        this.logger = this.getLogger();

        // Print non-vanilla block types
        this.logger.info("All non-vanilla block types:");
        Registry.BLOCK.stream()
            .filter(type -> !type.getKey().namespace().equals(NamespacedKey.MINECRAFT_NAMESPACE))
            .forEach(type -> this.logger.info("* " + type.getKey()));

        // Print non-vanilla item types
        this.logger.info("All non-vanilla item types:");
        Registry.ITEM.stream()
            .filter(type -> !type.getKey().namespace().equals(NamespacedKey.MINECRAFT_NAMESPACE))
            .forEach(type -> this.logger.info("* " + type.getKey()));

        // Print non-vanilla Material instances
        this.logger.info("All non-vanilla Material instances:");
        Arrays.stream(Material.values())
            .filter(type -> !type.isLegacy() && !type.getKey().namespace().equals(NamespacedKey.MINECRAFT_NAMESPACE))
            .forEach(type -> this.logger.info("* " + type.name() + " = " + type.getKey()));

        // Register as a listener
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Plugins can use the custom blocks and items as a Material
            Material type = event.getItem().getItemStack().getType();
            if (!type.key().namespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
                player.sendMessage(Component.text("You picked up a custom item: ").append(Component.translatable(type)));
            }
        }
    }

}
