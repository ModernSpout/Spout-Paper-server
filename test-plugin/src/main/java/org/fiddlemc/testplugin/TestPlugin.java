package org.fiddlemc.testplugin;

import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class TestPlugin extends JavaPlugin implements Listener {

    private Logger logger;

    @Override
    public void onEnable() {
        // Get the logger
        this.logger = this.getLogger();
        // Register as a listener
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Ops each player and gives them all custom items on join.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setOp(true);
        Registry.ITEM.stream().filter(item -> !item.isVanilla()).forEach(itemType -> {
            int hasAmount = Arrays.stream(player.getInventory().getContents())
                .filter(itemStack -> itemStack != null && itemStack.getType().asItemType() == itemType)
                .mapToInt(ItemStack::getAmount).sum();
            if (hasAmount < 64) {
                player.give(itemType.createItemStack(64 - hasAmount));
            }
        });
    }

}
