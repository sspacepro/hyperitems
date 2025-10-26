package org.galaxystudios.dungeonCreate.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;
import org.bukkit.event.entity.EntityPickupItemEvent;
import java.util.Objects;

public record PlayerStatUpdateListener(DungeonCreate plugin) implements Listener {

    public PlayerStatUpdateListener(DungeonCreate plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // --- Catch as many "equipment change" events as possible ---
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        updatePlayerStats(event.getPlayer());
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(player), 1L);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(player), 1L);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(player), 1L);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onEquipmentChange(EntityEquipmentChangedEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(player), 1L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Useful for dispensers equipping armor
        Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerStats(event.getPlayer()), 1L);
    }

    // --- Core stat calculation ---
    private void updatePlayerStats(Player player) {
        double totalHp = 0;
        double totalSpeed = 0;

        // Check all armor slots + main hand
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND
        }) {
            ItemStack item = player.getInventory().getItem(slot);
            if (!item.isEmpty() || !item.hasItemMeta()) continue;

            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            totalHp += data.getOrDefault(key("hp"), PersistentDataType.DOUBLE, 0.0);
            totalSpeed += data.getOrDefault(key("speed"), PersistentDataType.DOUBLE, 0.0);
        }

        // --- Apply stats ---
        double baseHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getDefaultValue();
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(baseHealth + totalHp);

        double baseSpeed = 0.1;
        player.setWalkSpeed((float) Math.max(0.0, Math.min(1.0, baseSpeed + (totalSpeed * 0.01))));

        if (player.getHealth() > Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue()) {
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
        }
    }

    private NamespacedKey key(String name) {
        return new NamespacedKey(plugin, name);
    }
}
