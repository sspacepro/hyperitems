package org.galaxystudios.hyperitems.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockTrimmingLeatherListener implements Listener {

    public BlockTrimmingLeatherListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack input = event.getInventory().getInputEquipment();
        if (input == null) return;

        Material type = input.getType();

        // Check if the item is any piece of leather armor
        if (type == Material.LEATHER_HELMET
                || type == Material.LEATHER_CHESTPLATE
                || type == Material.LEATHER_LEGGINGS
                || type == Material.LEATHER_BOOTS) {

            // Prevent result from showing (block trimming)
            event.setResult(null);
        }
    }
}
