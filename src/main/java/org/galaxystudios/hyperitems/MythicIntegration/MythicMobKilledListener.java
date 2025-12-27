package org.galaxystudios.hyperitems.MythicIntegration;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


import java.io.File;
import java.util.*;

public class MythicMobKilledListener implements Listener {

    private final FileConfiguration dropConfig;

    public MythicMobKilledListener(Plugin plugin) {

        File dropFile = new File(plugin.getDataFolder(), "drops.yml");
        if (!dropFile.exists()) plugin.saveResource("drops.yml", false);
        this.dropConfig = YamlConfiguration.loadConfiguration(dropFile);
    }

    @EventHandler
    public void onMythicMobKilled(MythicMobDeathEvent event) {
        LivingEntity killerEntity = event.getKiller();
        if (!(killerEntity instanceof Player player)) {
            return;
        }

        String mobName = event.getMob().getType().getInternalName();
        ConfigurationSection mobSection = dropConfig.getConfigurationSection("drops." + mobName);
        if (mobSection == null) return;

        Random random = new Random();

        for (String dropKey : mobSection.getKeys(false)) {
            ConfigurationSection dropData = mobSection.getConfigurationSection(dropKey);
            if (dropData == null) continue;

            String itemKey = dropData.getString("item");
            double chance = dropData.getDouble("chance", 0.0) / 100.0; // supports % values
            int amount = dropData.getInt("amount", 1);

            if (itemKey == null || itemKey.equalsIgnoreCase("NOTHING")) continue;

            if (random.nextDouble() <= chance) {
                ItemStack baseItem = CustomItemRegistry.get(itemKey);
                if (baseItem == null) {
                    Bukkit.getLogger().warning("Unknown item '" + itemKey + "' for mob " + mobName);
                    continue;
                }

                baseItem.setAmount(amount);
                Location dropLoc = event.getEntity().getLocation();

                // Try adding to inventory first
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(baseItem);
                if (!leftovers.isEmpty()) {
                    dropLoc.getWorld().dropItemNaturally(dropLoc, baseItem);
                    player.sendMessage(ChatColor.RED + "Your inventory is full!");
                }
            }
        }
    }
}
