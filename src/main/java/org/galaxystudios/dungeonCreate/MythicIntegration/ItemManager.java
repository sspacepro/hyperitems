package org.galaxystudios.dungeonCreate.MythicIntegration;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    // Stores all custom items by key (lowercase)
    private static final Map<String, ItemStack> customItems = new HashMap<>();

    /**
     * Registers a custom item with a unique key.
     * Overwrites existing items with the same key.
     */
    public static void registerCustomItem(String key, ItemStack item) {
        if (key == null || item == null) return;
        customItems.put(key.toLowerCase(), item.clone());
    }

    /**
     * Retrieves a custom item by key (case-insensitive).
     * Returns a clone to prevent external modifications.
     */
    public static ItemStack getCustomItem(String key) {
        if (key == null) return null;
        ItemStack item = customItems.get(key.toLowerCase());
        return item != null ? item.clone() : null;
    }

    /**
     * Checks if a custom item exists by key.
     */
    public static boolean hasCustomItem(String key) {
        return key != null && customItems.containsKey(key.toLowerCase());
    }

    /**
     * Returns an unmodifiable view of all registered custom items.
     */
    public static Map<String, ItemStack> getAllCustomItems() {
        return Collections.unmodifiableMap(customItems);
    }
}
