package org.galaxystudios.dungeonCreate.LoadPlugin;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.RecipeChoice;


import java.io.File;
import java.util.*;

/**
 * Loads custom items from items.yml and registers their recipes.
 */
public class LoadItems {

    public static final Map<String, ItemStack> loadedItems = new HashMap<>();

    public static void registerCustomItem(String key, ItemStack item) {
        if (key == null || item == null) return;
        loadedItems.put(key.toLowerCase(), item.clone());
    }

    /** Retrieve a loaded item by key (case-insensitive). */
    public static ItemStack getItem(String key) {
        if (key == null) return null;
        ItemStack item = loadedItems.get(key.toLowerCase());
        return item != null ? item.clone() : null;
    }


    /** Retrieve a Material or custom item from a string key. */
    public static ItemStack resolveItem(String name) {
        if (name == null) return null;

        // Try custom item first
        ItemStack custom = getItem(name);
        if (custom != null) return custom;

        // Fallback to vanilla Material
        Material mat = Material.matchMaterial(name.toUpperCase());
        return mat != null ? new ItemStack(mat) : null;
    }

    /** Loads items.yml and registers custom recipes. */
    public static void register() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("DungeonCreate");
        if (plugin == null) {
            Bukkit.getLogger().warning("[DungeonCreate] Plugin instance not found!");
            return;
        }

        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) plugin.saveResource("items.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            Bukkit.getLogger().warning("[DungeonCreate] No items found in items.yml");
            return;
        }

        loadedItems.clear();

        for (String key : items.getKeys(false)) {
            try {
                ConfigurationSection section = items.getConfigurationSection(key);
                if (section == null) continue;

                Material material = Material.matchMaterial(section.getString("material", "STONE").toUpperCase());
                if (material == null) {
                    Bukkit.getLogger().warning("[DungeonCreate] Invalid material for item: " + key);
                    continue;
                }

                String displayName = section.getString("name", key);
                List<String> loreList = section.getStringList("lore");
                String id = section.getString("texture", "pack_model");
                ItemStack item = new ItemStack(material);
                // Set a custom model data value on this item
                item.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey("dcpack", id));
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                // Display name
                if (!displayName.isEmpty()) {
                    Component nameComp = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
                    meta.displayName(nameComp);
                }

                // Lore
                if (!loreList.isEmpty()) {
                    List<@NotNull Component> loreComp = new ArrayList<>();
                    for (String line : loreList) {
                        loreComp.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                    }
                    meta.lore(loreComp);
                }


                item.setItemMeta(meta);

                // Register custom item
                loadedItems.put(key.toLowerCase(), item);

                // Recipe registration
                ConfigurationSection recipe = section.getConfigurationSection("recipe");
                if (recipe != null) registerRecipe(plugin, key, item, recipe);

                Bukkit.getLogger().info("[DungeonCreate] Loaded custom item: " + key);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[DungeonCreate] Failed to load item '" + key + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /** Registers a recipe for the given item. Supports crafting, furnace, and smithing. */
    private static void registerRecipe(Plugin plugin, String key, ItemStack result, ConfigurationSection recipe) {
        String type = recipe.getString("type", "crafting").toLowerCase(Locale.ROOT);

        try {
            switch (type) {
                case "crafting" -> {
                    List<String> shape = recipe.getStringList("shape");
                    ConfigurationSection ingredients = recipe.getConfigurationSection("ingredients");
                    if (shape.isEmpty() || ingredients == null) return;

                    NamespacedKey recipeKey = new NamespacedKey(plugin, key.toLowerCase());
                    ShapedRecipe shaped = new ShapedRecipe(recipeKey, result);
                    shaped.shape(shape.toArray(new String[0]));

                    for (String symbol : ingredients.getKeys(false)) {
                        String itemName = ingredients.getString(symbol);
                        if (itemName == null || itemName.equalsIgnoreCase("nothing")) continue;

                        // Try custom item first
                        ItemStack customItem = getItem(itemName);
                        if (customItem != null) {
                            shaped.setIngredient(symbol.charAt(0), new RecipeChoice.ExactChoice(customItem));
                            continue;
                        }

                        // Fallback to Material
                        Material mat = Material.matchMaterial(itemName.toUpperCase());
                        if (mat != null) {
                            shaped.setIngredient(symbol.charAt(0), mat);
                        } else {
                            Bukkit.getLogger().warning("[DungeonCreate] Invalid ingredient for item " + key + ": " + itemName);
                        }
                    }

                    Bukkit.addRecipe(shaped);
                }

                case "furnace" -> {
                    String input = recipe.getString("input");
                    double exp = recipe.getDouble("experience", 0);
                    int time = recipe.getInt("time", recipe.getInt("cookingTime", 200));
                    if (input == null) return;

                    ItemStack inputItem = resolveItem(input);
                    if (inputItem == null) return;

                    NamespacedKey keyName = new NamespacedKey(plugin, key.toLowerCase());
                    FurnaceRecipe furnace = new FurnaceRecipe(
                            keyName, result, inputItem.getType(), (float) exp, time
                    );
                    Bukkit.addRecipe(furnace);
                }

                case "smithing" -> {
                    String base = recipe.getString("base");
                    String addition = recipe.getString("addition");
                    String template = recipe.getString("template");
                    if (base == null || addition == null || template == null) return;
                    ItemStack templateItem = resolveItem(template);
                    ItemStack baseItem = resolveItem(base);
                    ItemStack addItem = resolveItem(addition);
                    if (baseItem == null || addItem == null) return;

                    NamespacedKey keyName = new NamespacedKey(plugin, key.toLowerCase());
                    SmithingTransformRecipe smithing = new SmithingTransformRecipe(
                            keyName,
                            result,
                            new RecipeChoice.ExactChoice(templateItem),
                            new RecipeChoice.ExactChoice(baseItem),
                            new RecipeChoice.ExactChoice(addItem)
                    );
                    Bukkit.addRecipe(smithing);

                }

                default -> Bukkit.getLogger().warning("[DungeonCreate] Unknown recipe type for " + key + ": " + type);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[DungeonCreate] Failed to register recipe for " + key + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
