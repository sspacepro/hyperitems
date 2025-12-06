package org.galaxystudios.dungeonCreate.LoadPlugin;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
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
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;
import org.galaxystudios.dungeonCreate.MythicIntegration.ItemManager;

import java.io.File;
import java.util.*;

public class LoadElementArmor {

    public static void register() {
        DungeonCreate plugin = (DungeonCreate) DungeonCreate.getInstance();

        File file = new File(plugin.getDataFolder(), "armors.yml");
        if (!file.exists()) plugin.saveResource("armors.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection armors = config.getConfigurationSection("armors");
        if (armors == null) return;

        for (String key : armors.getKeys(false)) {
            ConfigurationSection section = armors.getConfigurationSection(key);
            if (section == null) continue;

            try {
                // Basic info
                Material material = Material.valueOf(section.getString("material", "LEATHER_CHESTPLATE").toUpperCase());
                String displayName = section.getString("name", key);

                NamedTextColor nameColor = Optional.ofNullable(
                        NamedTextColor.NAMES.value(section.getString("nameColor", "WHITE"))
                ).orElse(NamedTextColor.WHITE);

                String element = section.getString("element", "Unknown");
                NamedTextColor elementColor = Optional.ofNullable(
                        NamedTextColor.NAMES.value(section.getString("elementColor", "GRAY"))
                ).orElse(NamedTextColor.GRAY);

                String flavor = section.getString("flavor", "");

                Color color = Color.fromRGB(
                        section.getInt("color.r", 255),
                        section.getInt("color.g", 0),
                        section.getInt("color.b", 255)
                );

                TrimPattern trimPattern = RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.TRIM_PATTERN)
                        .get(NamespacedKey.minecraft(section.getString("trim.trimPattern", "coast").toLowerCase()));
                if (trimPattern == null) trimPattern = TrimPattern.RAISER;

                TrimMaterial trimMaterial = RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.TRIM_MATERIAL)
                        .get(NamespacedKey.minecraft(section.getString("trim.material", "amethyst").toLowerCase()));
                if (trimMaterial == null) trimMaterial = TrimMaterial.REDSTONE;

                double armor = section.getDouble("attributes.armor", 0);
                double toughness = section.getDouble("attributes.toughness", 0);
                int durability = (int) section.getDouble("attributes.durability", -1);

                CustomStats stats = new CustomStats(
                        section.getDouble("stats.hp", 0),
                        section.getDouble("stats.damage", 0),
                        section.getDouble("stats.critchance", 0),
                        section.getDouble("stats.speed", 0),
                        section.getDouble("stats.lifesteal", 0)
                );

                // Build the item
                ItemStack result = applyArmorPiece(
                        plugin, material, displayName, nameColor,
                        element, elementColor, flavor, color,
                        new ArmorTrim(trimMaterial, trimPattern),
                        armor, toughness, stats, durability
                );

                // Register in ItemManager
                ItemManager.registerCustomItem(key, result);

                // Recipe
                List<String> shape = section.getStringList("recipe.shape");
                ConfigurationSection ingredients = section.getConfigurationSection("recipe.ingredients");
                if (!shape.isEmpty() && ingredients != null) {
                    registerRecipe(plugin, key, result, shape, ingredients);
                }

                Bukkit.getLogger().info("Loaded custom armor: " + displayName);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to load armor '" + key + "': " + e.getMessage());
            }
        }
    }

    private static ItemStack applyArmorPiece(
            DungeonCreate plugin,
            Material material,
            String displayName,
            NamedTextColor displayColor,
            String elementName,
            NamedTextColor elementColor,
            String flavorText,
            Color color,
            ArmorTrim trim,
            double armor,
            double toughness,
            CustomStats stats,
            int durability
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof ColorableArmorMeta colorMeta) {
            colorMeta.setColor(color);
            meta = colorMeta;
        }

        // Display name
        Component displayNameComponent = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(displayName)
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
        lore.add(Component.text(" "));
        lore.add(Component.text("Stats:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("♥ Health: +" + stats.hp(), NamedTextColor.RED));
        lore.add(Component.text("⚔ Damage: +" + stats.damage(), NamedTextColor.DARK_RED));
        lore.add(Component.text("☘ Crit Chance: +" + stats.critchance(), NamedTextColor.GREEN));
        lore.add(Component.text("✦ Speed: +" + stats.speed(), NamedTextColor.AQUA));
        lore.add(Component.text("❤ Lifesteal: +" + stats.lifesteal() + "%", NamedTextColor.LIGHT_PURPLE));
        meta.lore(lore);

        // Persistent data
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "elementType"), PersistentDataType.STRING, elementName.toLowerCase());
        data.set(new NamespacedKey(plugin, "hp"), PersistentDataType.DOUBLE, stats.hp());
        data.set(new NamespacedKey(plugin, "damage"), PersistentDataType.DOUBLE, stats.damage());
        data.set(new NamespacedKey(plugin, "critchance"), PersistentDataType.DOUBLE, stats.critchance());
        data.set(new NamespacedKey(plugin, "speed"), PersistentDataType.DOUBLE, stats.speed());
        data.set(new NamespacedKey(plugin, "lifesteal"), PersistentDataType.DOUBLE, stats.lifesteal());

        if (meta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(trim);
        }

        // Attributes
        String safeKey = displayName.toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(
                new NamespacedKey(plugin, safeKey + "_armor"),
                armor,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.ARMOR
        ));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                new NamespacedKey(plugin, safeKey + "_toughness"),
                toughness,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.ARMOR
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

                // Use ItemManager for all custom ingredients
                ItemStack customItem = ItemManager.getCustomItem(ingredientName);
                if (customItem != null) {
                    recipe.setIngredient(symbol.charAt(0), new RecipeChoice.ExactChoice(customItem));
                    continue;
                }

                // Fallback to vanilla material
                Material mat = Material.matchMaterial(ingredientName.toUpperCase());
                if (mat != null) {
                    recipe.setIngredient(symbol.charAt(0), mat);
                } else {
                    Bukkit.getLogger().warning("[DungeonCreate] Invalid ingredient: " + ingredientName);
                }
            }

            Bukkit.addRecipe(recipe);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to register recipe for " + key + ": " + e.getMessage());
        }
    }

    private record CustomStats(double hp, double damage, double critchance, double speed, double lifesteal) {}
}
