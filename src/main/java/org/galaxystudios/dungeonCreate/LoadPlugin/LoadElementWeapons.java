package org.galaxystudios.dungeonCreate.LoadPlugin;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;
import org.galaxystudios.dungeonCreate.MythicIntegration.ItemManager;

import java.io.File;
import java.util.*;

/**
 * Loads custom element-based weapons from weapons.yml,
 * stores them in ItemManager,
 * and registers recipes using ItemManager items.
 */
public class LoadElementWeapons {

    public static void register() {
        DungeonCreate plugin = (DungeonCreate) DungeonCreate.getInstance();

        File file = new File(plugin.getDataFolder(), "weapons.yml");
        if (!file.exists()) plugin.saveResource("weapons.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection weapons = config.getConfigurationSection("weapons");
        if (weapons == null) {
            Bukkit.getLogger().warning("[DungeonCreate] No weapons found in weapons.yml");
            return;
        }

        for (String key : weapons.getKeys(false)) {
            ConfigurationSection section = weapons.getConfigurationSection(key);
            if (section == null) continue;

            try {
                Material material = Material.matchMaterial(section.getString("material", "IRON_SWORD").toUpperCase());
                if (material == null) {
                    Bukkit.getLogger().warning("[DungeonCreate] Invalid material for weapon: " + key);
                    continue;
                }

                String displayName = section.getString("name", key);
                NamedTextColor nameColor = Optional.ofNullable(
                        NamedTextColor.NAMES.value(section.getString("nameColor", "WHITE"))
                ).orElse(NamedTextColor.WHITE);

                String element = section.getString("element", "Unknown");
                NamedTextColor elementColor = Optional.ofNullable(
                        NamedTextColor.NAMES.value(section.getString("elementColor", "GRAY"))
                ).orElse(NamedTextColor.GRAY);

                String flavor = section.getString("flavor", "");

                double damage = section.getDouble("attributes.damage", 0);
                double attackSpeed = section.getDouble("attributes.attackSpeed", 0);
                int durability = (int) section.getDouble("attributes.durability", -1);

                CustomStats stats = new CustomStats(
                        section.getDouble("stats.damage", 0),
                        section.getDouble("stats.luck", 0),
                        section.getDouble("stats.speed", 0),
                        section.getDouble("stats.lifesteal", 0)
                );

                // Create weapon
                ItemStack weaponItem = createWeaponItem(
                        plugin, material, displayName, nameColor, element, elementColor,
                        flavor, damage, attackSpeed, stats, durability
                );

                // Register with ItemManager
                ItemManager.registerCustomItem(key, weaponItem);

                // Recipe
                List<String> shape = section.getStringList("recipe.shape");
                ConfigurationSection ingredients = section.getConfigurationSection("recipe.ingredients");
                if (!shape.isEmpty() && ingredients != null) {
                    registerRecipe(plugin, key, weaponItem, shape, ingredients);
                }

                Bukkit.getLogger().info("[DungeonCreate] Loaded element weapon: " + key);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[DungeonCreate] Failed to load weapon '" + key + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static ItemStack createWeaponItem(
            DungeonCreate plugin,
            Material material,
            String displayName,
            NamedTextColor displayColor,
            String elementName,
            NamedTextColor elementColor,
            String flavorText,
            double damage,
            double attackSpeed,
            CustomStats stats,
            int durability
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Display name
        Component displayNameComponent = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(displayName)
                .colorIfAbsent(displayColor)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayNameComponent);

        // Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Element: ", NamedTextColor.GRAY)
                .append(Component.text(elementName, elementColor))
                .decoration(TextDecoration.ITALIC, false));

        if (!flavorText.isEmpty()) {
            lore.add(Component.text(flavorText, NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.text(""));
        lore.add(Component.text("Stats:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("⚔ Damage: +" + stats.damage(), NamedTextColor.DARK_RED));
        lore.add(Component.text("☘ Luck: +" + stats.luck(), NamedTextColor.GREEN));
        lore.add(Component.text("✦ Speed: +" + stats.speed(), NamedTextColor.AQUA));
        lore.add(Component.text("❤ Lifesteal: +" + stats.lifesteal() + "%", NamedTextColor.LIGHT_PURPLE));
        meta.lore(lore);

        // Persistent data
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "elementType"), PersistentDataType.STRING, elementName.toLowerCase());
        data.set(new NamespacedKey(plugin, "damage"), PersistentDataType.DOUBLE, stats.damage());
        data.set(new NamespacedKey(plugin, "luck"), PersistentDataType.DOUBLE, stats.luck());
        data.set(new NamespacedKey(plugin, "speed"), PersistentDataType.DOUBLE, stats.speed());
        data.set(new NamespacedKey(plugin, "lifesteal"), PersistentDataType.DOUBLE, stats.lifesteal());

        // Attributes
        String safeKey = displayName.toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                new NamespacedKey(plugin, safeKey + "_attack_damage"),
                damage,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                new NamespacedKey(plugin, safeKey + "_attack_speed"),
                attackSpeed,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));

        item.setItemMeta(meta);
        item.setData(DataComponentTypes.MAX_DAMAGE, durability >= 0 ? durability : material.getMaxDurability());
        return item;
    }

    private static void registerRecipe(DungeonCreate plugin, String key, ItemStack result,
                                       List<String> shape, ConfigurationSection ingredients) {
        try {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key.toLowerCase()), result);
            recipe.shape(shape.toArray(new String[0]));

            for (String symbol : ingredients.getKeys(false)) {
                String ingredientName = ingredients.getString(symbol);
                if (ingredientName == null || ingredientName.equalsIgnoreCase("nothing")) continue;

                // Always use ItemManager for ingredients
                ItemStack customItem = ItemManager.getCustomItem(ingredientName);
                if (customItem != null) {
                    recipe.setIngredient(symbol.charAt(0), new RecipeChoice.ExactChoice(customItem));
                    continue;
                }

                Material mat = Material.matchMaterial(ingredientName.toUpperCase());
                if (mat != null) {
                    recipe.setIngredient(symbol.charAt(0), mat);
                } else {
                    Bukkit.getLogger().warning("[DungeonCreate] Invalid ingredient: " + ingredientName + " in " + key);
                }
            }

            Bukkit.addRecipe(recipe);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[DungeonCreate] Failed to register recipe for " + key + ": " + e.getMessage());
        }
    }

    private record CustomStats(double damage, double luck, double speed, double lifesteal) {}
}
