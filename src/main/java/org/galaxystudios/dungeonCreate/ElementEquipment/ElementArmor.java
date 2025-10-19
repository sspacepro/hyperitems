package org.galaxystudios.dungeonCreate.ElementEquipment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;

import java.util.List;
import java.util.UUID;

public class ElementArmor {

    public static void register() {

        NamespacedKey elementKey = new NamespacedKey(DungeonCreate.getInstance(), "elementType");

        // ------------------------------------------------------------
        // Test Diamond
        // ------------------------------------------------------------
        ItemStack testDiamond = new ItemStack(Material.DIAMOND);
        ItemMeta testMeta = testDiamond.getItemMeta();
        testMeta.displayName(Component.text("Test Diamond", NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false));
        testMeta.lore(List.of(
                Component.text("A Test Diamond for testing crafts.", NamedTextColor.BLUE)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        testDiamond.setItemMeta(testMeta);

        ShapelessRecipe testDiamondRecipe = new ShapelessRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "test_diamond"),
                testDiamond
        );
        testDiamondRecipe.addIngredient(Material.DIAMOND);
        Bukkit.addRecipe(testDiamondRecipe);

        // ------------------------------------------------------------
        // LAVA CHESTPLATE
        // ------------------------------------------------------------
        ItemStack lavaChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        if (lavaChestplate.getItemMeta() instanceof ColorableArmorMeta meta) {
            meta.displayName(Component.text("Blazing Coreplate", NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Element: ", NamedTextColor.GRAY)
                            .append(Component.text("Lava", NamedTextColor.RED))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Forged in molten fury.", NamedTextColor.DARK_RED)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            meta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "lava");

            meta.setColor(Color.fromRGB(255, 80, 0));
            meta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.RAISER));


            meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                    new AttributeModifier(UUID.randomUUID(), "lava_armor_points", 8.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                    new AttributeModifier(UUID.randomUUID(), "lava_toughness", 3.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));

            lavaChestplate.setItemMeta(meta);
        }

        ShapedRecipe lavaRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "lava_chestplate"),
                lavaChestplate
        );
        lavaRecipe.shape("T T", "TTT", "TTT");
        lavaRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(lavaRecipe);

        // ------------------------------------------------------------
        // WATER HELMET
        // ------------------------------------------------------------
        ItemStack waterHelmet = new ItemStack(Material.LEATHER_HELMET);
        if (waterHelmet.getItemMeta() instanceof ColorableArmorMeta meta) {
            meta.displayName(Component.text("Tidewatch Helm", NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Element: ", NamedTextColor.GRAY)
                            .append(Component.text("Water", NamedTextColor.AQUA))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Flowing with calm power.", NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            meta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "water");

            meta.setColor(Color.fromRGB(0, 128, 255));
            meta.setTrim(new ArmorTrim(TrimMaterial.LAPIS, TrimPattern.COAST));

            meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                    new AttributeModifier(UUID.randomUUID(), "water_armor_points", 5.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                    new AttributeModifier(UUID.randomUUID(), "water_toughness", 2.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));

            waterHelmet.setItemMeta(meta);
        }

        ShapedRecipe waterRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "water_helmet"),
                waterHelmet
        );
        waterRecipe.shape("TTT", "T T", "   ");
        waterRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(waterRecipe);

        // ------------------------------------------------------------
        // AIR LEGGINGS
        // ------------------------------------------------------------
        ItemStack airLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
        if (airLeggings.getItemMeta() instanceof ColorableArmorMeta meta) {
            meta.displayName(Component.text("Skystride Leggings", NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Element: ", NamedTextColor.GRAY)
                            .append(Component.text("Air", NamedTextColor.WHITE))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Light as the wind itself.", NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            meta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "air");

            meta.setColor(Color.fromRGB(220, 220, 255));
            meta.setTrim(new ArmorTrim(TrimMaterial.QUARTZ, TrimPattern.VEX));

            meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                    new AttributeModifier(UUID.randomUUID(), "air_armor_points", 6.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                    new AttributeModifier(UUID.randomUUID(), "air_toughness", 1.5,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));

            airLeggings.setItemMeta(meta);
        }

        ShapedRecipe airRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "air_leggings"),
                airLeggings
        );
        airRecipe.shape("TTT", "T T", "T T");
        airRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(airRecipe);

        // ------------------------------------------------------------
        // EARTH BOOTS
        // ------------------------------------------------------------
        ItemStack earthBoots = new ItemStack(Material.LEATHER_BOOTS);
        if (earthBoots.getItemMeta() instanceof ColorableArmorMeta meta) {
            meta.displayName(Component.text("Rootbound Greaves", NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Element: ", NamedTextColor.GRAY)
                            .append(Component.text("Earth", NamedTextColor.GREEN))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Grounded and unyielding.", NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
