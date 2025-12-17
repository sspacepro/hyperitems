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
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.hyperitems;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerStatUpdateListener implements Listener {

    private final hyperitems plugin;

    // Map to prevent multiple updates in the same tick
    private final Map<Player, Boolean> scheduledUpdates = new HashMap<>();

    public PlayerStatUpdateListener(hyperitems plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // --- Event handlers ---
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            scheduleUpdate(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            scheduleUpdate(player);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            scheduleUpdate(player);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onEquipmentChange(EntityEquipmentChangedEvent event) {
        if (event.getEntity() instanceof Player player) {
            scheduleUpdate(player);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    // --- Core stat calculation ---
    private void updatePlayerStats(Player player) {
        double totalHp = 0;
        double totalSpeed = 0;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND
        }) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item.isEmpty() || !item.hasItemMeta()) continue;

            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            totalHp += data.getOrDefault(key("hp"), PersistentDataType.DOUBLE, 0.0);
            totalSpeed += data.getOrDefault(key("speed"), PersistentDataType.DOUBLE, 0.0);
        }

        // Apply health
        double baseHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getDefaultValue();
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(baseHealth + totalHp);

        // Apply speed
        double baseSpeed = 0.2; // correct default
        player.setWalkSpeed((float) Math.max(0.0, Math.min(1.0, baseSpeed + (totalSpeed * 0.01))));

        // Clamp current health to max
        if (player.getHealth() > Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue()) {
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
        }
    }

    private NamespacedKey key(String name) {
        return new NamespacedKey(plugin, name);
    }

    // --- Schedule updates efficiently ---
    private void scheduleUpdate(Player player) {
        // Avoid scheduling multiple updates in the same tick
        if (scheduledUpdates.getOrDefault(player, false)) return;

        scheduledUpdates.put(player, true);
        Bukkit.getScheduler().runTask(plugin, () -> {
            updatePlayerStats(player);
            scheduledUpdates.put(player, false);
        });
    }
}
