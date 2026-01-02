package org.galaxystudios.hyperitems.LoadPlugin;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.hyperitems.MythicIntegration.CustomItemRegistry;
import org.galaxystudios.hyperitems.hyperitems;
import java.util.ArrayList;
import java.util.List;


import java.io.File;
import java.util.*;

public final class UnifiedItemLoader {

    public static void registerAll() {
        CustomItemRegistry.clear();
        loadFile("items.yml", "items");
        loadFile("weapons.yml", "weapons");
        loadFile("armors.yml", "armors");
    }

    private static void loadFile(String fileName, String root) {
        hyperitems plugin = (hyperitems) hyperitems.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) plugin.saveResource(fileName, false);

        var config = YamlConfiguration.loadConfiguration(file);
        var section = config.getConfigurationSection(root);
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s == null) continue;

            try {
                ItemStack item = buildItem(plugin, s);
                CustomItemRegistry.register(key, item);

                if (s.contains("recipe")) {
                    registerRecipe(plugin, key, item, Objects.requireNonNull(s.getConfigurationSection("recipe")));
                }

                Bukkit.getLogger().info("[HyperItems] Loaded: " + key);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to load " + key + ": " + e.getMessage());
            }
        }
    }

    private static ItemStack buildItem(hyperitems plugin, ConfigurationSection s) {
        Material mat = Material.matchMaterial(s.getString("material", "STONE").toUpperCase());
        assert mat != null;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        // Texture
        if (s.contains("texture")) {
            meta.setItemModel(new NamespacedKey("dcpack", Objects.requireNonNull(s.getString("texture"))));
        }

        // Name
        if (s.contains("name")) {
            meta.displayName(
                    LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(Objects.requireNonNull(s.getString("name")))
                            .decoration(TextDecoration.ITALIC, false)
            );
        }

        //Add YAML-defined lore if present
        if (s.contains("lore")) {
            for (String line : s.getStringList("lore")) {
                lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
            }
        }

        // Add stats inline
        if (s.contains("stats.hp")) {
            lore.add(Component.text("§c♥ Health: +" + s.getInt("stats.hp")));
        }
        if (s.contains("stats.damage")) {
            lore.add(Component.text("§e⚔ Damage: +" + s.getDouble("stats.damage")));
        }
        if (s.contains("stats.critchance")) {
            lore.add(Component.text("§6✦ Crit Chance: " + s.getDouble("stats.critchance") + "%"));
        }
        if (s.contains("stats.speed")) {
            lore.add(Component.text("§b✦ Speed: +" + s.getDouble("stats.speed")));
        }
        if (s.contains("stats.lifesteal")) {
            lore.add(Component.text("§d❤ Lifesteal: " + s.getDouble("stats.lifesteal") + "%"));
        }

        // Apply the lore to the ItemMeta
        meta.lore(lore);

        // Element metadata
        if (s.contains("element")) {
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "elementType"),
                    PersistentDataType.STRING,
                    Objects.requireNonNull(s.getString("element")).toLowerCase()
            );
        }


        if (s.contains("stats")) {
            ConfigurationSection stats = s.getConfigurationSection("stats");
            assert stats != null;
            for (String stat : stats.getKeys(false)) {
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, stat),
                        PersistentDataType.DOUBLE,
                        stats.getDouble(stat)
                );
            }
        }

        // Armor coloring
        if (meta instanceof ColorableArmorMeta cam && s.contains("color")) {
            cam.setColor(Color.fromRGB(
                    s.getInt("color.r"),
                    s.getInt("color.g"),
                    s.getInt("color.b")
            ));
        }

        // Armor trims
        if (meta instanceof ArmorMeta am && s.contains("trim")) {
            TrimPattern pattern = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.TRIM_PATTERN)
                    .get(NamespacedKey.minecraft(Objects.requireNonNull(s.getString("trim.pattern")).toLowerCase()));

            TrimMaterial material = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.TRIM_MATERIAL)
                    .get(NamespacedKey.minecraft(Objects.requireNonNull(s.getString("trim.material")).toLowerCase()));

            assert material != null;
            assert pattern != null;
            am.setTrim(new ArmorTrim(material, pattern));
        }

        // Attributes
        if (s.contains("attributes")) {
            ConfigurationSection a = s.getConfigurationSection("attributes");

            applyAttribute(meta, plugin, a, "armor", Attribute.ARMOR, EquipmentSlotGroup.ARMOR);
            applyAttribute(meta, plugin, a, "toughness", Attribute.ARMOR_TOUGHNESS, EquipmentSlotGroup.ARMOR);
            applyAttribute(meta, plugin, a, "damage", Attribute.ATTACK_DAMAGE, EquipmentSlotGroup.MAINHAND);
            applyAttribute(meta, plugin, a, "attackSpeed", Attribute.ATTACK_SPEED, EquipmentSlotGroup.MAINHAND);

            if (a.contains("durability")) {
                item.setData(
                        DataComponentTypes.MAX_DAMAGE,
                        a.getInt("durability")
                );
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private static void applyAttribute(ItemMeta meta, hyperitems plugin,
                                       ConfigurationSection sec, String key,
                                       Attribute attr, EquipmentSlotGroup slot) {
        if (!sec.contains(key)) return;
        meta.addAttributeModifier(attr, new AttributeModifier(
                new NamespacedKey(plugin, key),
                sec.getDouble(key),
                AttributeModifier.Operation.ADD_NUMBER,
                slot
        ));
    }

    private static void registerRecipe(hyperitems plugin, String key, ItemStack result,
                                       ConfigurationSection recipe) {
        String type = recipe.getString("type", "crafting").toLowerCase();

        switch (type) {
            case "crafting" -> {
                ShapedRecipe r = new ShapedRecipe(new NamespacedKey(plugin, key), result);
                r.shape(recipe.getStringList("shape").toArray(String[]::new));

                ConfigurationSection ingredients = recipe.getConfigurationSection("ingredients");
                if (ingredients == null) return;

                for (String c : ingredients.getKeys(false)) {
                    String name = ingredients.getString(c);
                    if (name == null) continue;

                    ItemStack custom = CustomItemRegistry.get(name);
                    if (custom != null) {
                        r.setIngredient(c.charAt(0), new RecipeChoice.ExactChoice(custom));
                    } else {
                        Material mat = Material.matchMaterial(name.toUpperCase());
                        if (mat != null) r.setIngredient(c.charAt(0), mat);
                        else Bukkit.getLogger().warning("Unknown ingredient '" + name + "' for recipe " + key);
                    }
                }

                Bukkit.addRecipe(r);
            }

            case "furnace" -> {
                Material inputMat = Material.matchMaterial(Objects.requireNonNull(recipe.getString("input")));
                if (inputMat == null) {
                    Bukkit.getLogger().warning("Furnace recipe input must be a vanilla Material for " + key);
                    return;
                }

                Bukkit.addRecipe(new FurnaceRecipe(
                        new NamespacedKey(plugin, key),
                        result,
                        inputMat,
                        (float) recipe.getDouble("experience"),
                        recipe.getInt("time", 200)
                ));
            }

            case "smithing" -> {
                ItemStack templateItem = resolveCustomOrVanilla(recipe.getString("template"));
                ItemStack baseItem = resolveCustomOrVanilla(recipe.getString("base"));
                ItemStack additionItem = resolveCustomOrVanilla(recipe.getString("addition"));

                if (templateItem == null || baseItem == null || additionItem == null) {
                    Bukkit.getLogger().warning("Smithing recipe for " + key + " has invalid items.");
                    return;
                }

                Bukkit.addRecipe(new SmithingTransformRecipe(
                        new NamespacedKey(plugin, key),
                        result,
                        new RecipeChoice.ExactChoice(templateItem),
                        new RecipeChoice.ExactChoice(baseItem),
                        new RecipeChoice.ExactChoice(additionItem)
                ));
            }
        }
    }

    // Helper method to get either a custom item or a vanilla material as an ItemStack
    private static ItemStack resolveCustomOrVanilla(String name) {
        if (name == null) return null;
        ItemStack custom = CustomItemRegistry.get(name);
        if (custom != null) return custom;

        Material mat = Material.matchMaterial(name.toUpperCase());
        if (mat != null) return new ItemStack(mat);

        return null;
    }

}

