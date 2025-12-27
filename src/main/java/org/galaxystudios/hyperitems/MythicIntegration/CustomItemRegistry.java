package org.galaxystudios.hyperitems.MythicIntegration;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CustomItemRegistry {

    private static final Map<String, ItemStack> ITEMS = new HashMap<>();

    public static void register(String key, ItemStack item) {
        if (key == null || item == null) return;
        ITEMS.put(key.toLowerCase(), item.clone());
    }

    public static ItemStack get(String key) {
        if (key == null) return null;
        ItemStack item = ITEMS.get(key.toLowerCase());
        return item != null ? item.clone() : null;
    }

    public static boolean exists(String key) {
        return key != null && ITEMS.containsKey(key.toLowerCase());
    }

    public static Map<String, ItemStack> all() {
        return Collections.unmodifiableMap(ITEMS);
    }

    public static void clear() {
        ITEMS.clear();
    }
}
