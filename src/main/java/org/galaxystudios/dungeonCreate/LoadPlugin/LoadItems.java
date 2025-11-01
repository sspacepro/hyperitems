package org.galaxystudios.dungeonCreate.LoadPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Loads custom items from items.yml and registers their recipes.
 */
public class LoadItems {

    private static final Map<String, ItemStack> loadedItems = new HashMap<>();

    /** Retrieve a loaded item by key (case-insensitive). */
    public static ItemStack getItem(String key) {
        if (key == null) return null;
        ItemStack item = loadedItems.get(key.toLowerCase());
        return item != null ? item.clone() : null;
    }

    /** Retrieve a Material or custom item from a string key. */
    public static ItemStack resolveItem(String name) {
        if (name == null) return null;

        // Try custom item
        ItemStack custom = getItem(name);
        if (custom != null) return custom;

        // Fallback to vanilla material
        Material mat = Material.matchMaterial(name.toUpperCase());
        return mat != null ? new ItemStack(mat) : null;
    }

    /** Loads items.yml and registers custom recipes. */
    public static void register() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("DungeonCreate");
        if (plugin == null) {
            Bukkit.getLogger().warning("DungeonCreate plugin instance not found!");
            return;
        }

        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) plugin.saveResource("items.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            Bukkit.getLogger().warning("No items found in items.yml");
            return;
        }

        for (String key : items.getKeys(false)) {
            try {
                ConfigurationSection section = items.getConfigurationSection(key);
                if (section == null) continue;

                Material material = Material.matchMaterial(
                        section.getString("material", "STONE").toUpperCase()
                );
                if (material == null) {
                    Bukkit.getLogger().warning("Invalid material for item: " + key);
                    continue;
                }

                String displayName = section.getString("name", key);
                List<String> loreList = section.getStringList("lore");

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                // Name
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
                loadedItems.put(key.toLowerCase(), item);

                // Recipe
                ConfigurationSection recipe = section.getConfigurationSection("recipe");
                if (recipe != null) registerRecipe(plugin, key, item, recipe);

                Bukkit.getLogger().info("Loaded custom item: " + key);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to load item '" + key + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /** Registers a recipe for the given item. Supports crafting, furnace, smithing. */
    private static void registerRecipe(Plugin plugin, String key, ItemStack result, ConfigurationSection recipe) {
        String type = recipe.getString("type", "crafting").toLowerCase();

        try {
            switch (type) {
                case "crafting" -> {
                    List<String> shape = recipe.getStringList("shape");
                    ConfigurationSection ingredients = recipe.getConfigurationSection("ingredients");
                    if (shape.isEmpty() || ingredients == null) return;

                    ShapedRecipe shaped = new ShapedRecipe(new NamespacedKey(plugin, key.toLowerCase()), result);
                    shaped.shape(shape.toArray(new String[0]));

                    for (String symbol : ingredients.getKeys(false)) {
                        String matName = ingredients.getString(symbol);
                        if (matName == null) continue;
                        Material mat = Material.matchMaterial(matName.toUpperCase());
                        if (mat != null) shaped.setIngredient(symbol.charAt(0), mat);
                    }

                    Bukkit.addRecipe(shaped);
                }

                case "furnace" -> {
                    String input = recipe.getString("input");
                    double exp = recipe.getDouble("experience", 0);
                    int time = recipe.getInt("time", recipe.getInt("cookingTime", 200));
                    if (input == null) return;

                    Material inputMat = Material.matchMaterial(input.toUpperCase());
                    if (inputMat == null) return;

                    FurnaceRecipe furnace = new FurnaceRecipe(
                            new NamespacedKey(plugin, key.toLowerCase()),
                            result,
                            inputMat,
                            (float) exp,
                            time
                    );
                    Bukkit.addRecipe(furnace);
                }

                case "smithing" -> {
                    String base = recipe.getString("base");
                    String addition = recipe.getString("addition");
                    if (base == null || addition == null) return;

                    Material baseMat = Material.matchMaterial(base.toUpperCase());
                    Material addMat = Material.matchMaterial(addition.toUpperCase());
                    if (baseMat == null || addMat == null) return;

                    SmithingTransformRecipe smithing = new SmithingTransformRecipe(
                            new NamespacedKey(plugin, key.toLowerCase()),
                            result,
                            new RecipeChoice.MaterialChoice(baseMat),
                            new RecipeChoice.MaterialChoice(addMat),
                            new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                    );
                    Bukkit.addRecipe(smithing);
                }

                default -> Bukkit.getLogger().warning("Unknown recipe type for " + key + ": " + type);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to register recipe for " + key + ": " + e.getMessage());
        }
    }
}
