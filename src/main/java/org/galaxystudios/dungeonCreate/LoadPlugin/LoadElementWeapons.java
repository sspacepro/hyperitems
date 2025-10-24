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
import java.io.File;
import java.util.*;

import static java.lang.System.getLogger;

public class LoadElementWeapons {

    public static void register() {
        DungeonCreate plugin = (DungeonCreate) DungeonCreate.getInstance();

        File file = new File(plugin.getDataFolder(), "weapons.yml");
        if (!file.exists()) {
            plugin.saveResource("weapons.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection weapons = config.getConfigurationSection("weapons");
        if (weapons == null) return;

        for (String key : weapons.getKeys(false)) {
            ConfigurationSection section = weapons.getConfigurationSection(key);
            if (section == null) continue;

            try {
                // Basic Info
                Material material = Material.valueOf(section.getString("material", "IRON_SWORD").toUpperCase());
                String displayName = section.getString("name", key);

                NamedTextColor nameColor = Optional.ofNullable(
                        NamedTextColor.NAMES.value(section.getString("nameColor", "WHITE"))
                ).orElse(NamedTextColor.WHITE);

                String element = section.getString("element", "Unknown");
                NamedTextColor elementColor = Optional.ofNullable(
                        NamedTextColor.NAMES.value(section.getString("elementColor", "GRAY"))
                ).orElse(NamedTextColor.GRAY);

                String flavor = section.getString("flavor", "");

                // Attributes
                double damage = section.getDouble("attributes.damage", 0);
                double attackSpeed = section.getDouble("attributes.attackSpeed", 0);
                int durability = (int) section.getDouble("attributes.durability", -1);

                // Custom Stats
                CustomStats stats = new CustomStats(
                        section.getDouble("stats.damage", 0),
                        section.getDouble("stats.luck", 0),
                        section.getDouble("stats.speed", 0),
                        section.getDouble("stats.lifesteal", 0)
                );

                // Recipe
                List<String> shape = section.getStringList("recipe.shape");
                ConfigurationSection ingredients = section.getConfigurationSection("recipe.ingredients");

                ItemStack result = applyWeaponItem(
                        plugin,
                        material,
                        displayName,
                        nameColor,
                        element,
                        elementColor,
                        flavor,
                        damage,
                        attackSpeed,
                        stats,
                        durability
                );

                if (!shape.isEmpty() && ingredients != null) {
                    registerRecipe(plugin, key, result, shape, ingredients);
                }

                Bukkit.getLogger().info("Loaded custom weapon: " + displayName);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to load weapon '" + key + "': " + e.getMessage());
            }
        }
    }

    private static ItemStack applyWeaponItem(
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

        // Display Name
        Component displayNameComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        meta.displayName(displayNameComponent.decoration(TextDecoration.ITALIC, false));

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
        lore.add(Component.text("⚔ Damage: +" + stats.damage(), NamedTextColor.DARK_RED));
        lore.add(Component.text("☘ Luck: +" + stats.luck(), NamedTextColor.GREEN));
        lore.add(Component.text("✦ Speed: +" + stats.speed(), NamedTextColor.AQUA));
        lore.add(Component.text("❤ Lifesteal: +" + stats.lifesteal() + "%", NamedTextColor.LIGHT_PURPLE));
        meta.lore(lore);

        // Persistent Data
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "elementType"), PersistentDataType.STRING, elementName.toLowerCase());
        data.set(new NamespacedKey(plugin, "damage"), PersistentDataType.DOUBLE, stats.damage());
        data.set(new NamespacedKey(plugin, "luck"), PersistentDataType.DOUBLE, stats.luck());
        data.set(new NamespacedKey(plugin, "speed"), PersistentDataType.DOUBLE, stats.speed());
        data.set(new NamespacedKey(plugin, "lifesteal"), PersistentDataType.DOUBLE, stats.lifesteal());

        // Attributes
        String safeKey = displayName.toLowerCase().replaceAll("[^a-z0-9_.-]", "_");

        // Damage
        NamespacedKey dmgKey = new NamespacedKey(plugin, safeKey + "_attack_damage");
        AttributeModifier dmgModifier = new AttributeModifier(
                dmgKey,
                damage,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, dmgModifier);

        // Attack Speed
        NamespacedKey speedKey = new NamespacedKey(plugin, safeKey + "_attack_speed");
        AttributeModifier speedModifier = new AttributeModifier(
                speedKey,
                attackSpeed,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, speedModifier);

        item.setItemMeta(meta);

        // Durability
        item.setData(DataComponentTypes.MAX_DAMAGE, durability >= 0 ? durability : material.getMaxDurability());

        return item;
    }

    private static void registerRecipe(DungeonCreate plugin, String key, ItemStack result,
                                       List<String> shape, ConfigurationSection ingredients) {
        try {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key.toLowerCase()), result);
            recipe.shape(shape.toArray(new String[0]));

            for (String symbol : ingredients.getKeys(false)) {
                String matName = ingredients.getString(symbol);
                if (matName == null) continue;
                Material mat = Material.matchMaterial(matName.toUpperCase());
                if (mat != null) recipe.setIngredient(symbol.charAt(0), mat);
            }

            Bukkit.addRecipe(recipe);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to register recipe for " + key + ": " + e.getMessage());
        }
    }

    private record CustomStats(double damage, double luck, double speed, double lifesteal) {}
}

