package org.galaxystudios.dungeonCreate.ElementEquipment;

/*

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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;

import java.util.ArrayList;
import java.util.List;

public class ElementArmor {

    public static void register() {
        DungeonCreate plugin = (DungeonCreate) DungeonCreate.getInstance();

        NamespacedKey elementKey = new NamespacedKey(plugin, "elementType");

        // ------------------------------------------------------------
        // TEST DIAMOND
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
                new NamespacedKey(plugin, "test_diamond"),
                testDiamond
        );
        testDiamondRecipe.addIngredient(Material.DIAMOND);
        Bukkit.addRecipe(testDiamondRecipe);

        // ------------------------------------------------------------
        // ELEMENT ARMORS
        // ------------------------------------------------------------
        applyArmorPiece(
                plugin,
                Material.LEATHER_CHESTPLATE,
                "Blazing Coreplate",
                NamedTextColor.RED,
                "Lava",
                NamedTextColor.RED,
                "Forged in molten fury.",
                Color.fromRGB(255, 80, 0),
                new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.RAISER),
                8.0, 3.0,
                new CustomStats(6, 2, 0, 0, 1)
        );

        applyArmorPiece(
                plugin,
                Material.LEATHER_HELMET,
                "Tidewatch Helm",
                NamedTextColor.AQUA,
                "Water",
                NamedTextColor.AQUA,
                "Flowing with calm power.",
                Color.fromRGB(0, 128, 255),
                new ArmorTrim(TrimMaterial.LAPIS, TrimPattern.COAST),
                5.0, 2.0,
                new CustomStats(4, 0, 2, 1, 0)
        );

        applyArmorPiece(
                plugin,
                Material.LEATHER_LEGGINGS,
                "Skystride Leggings",
                NamedTextColor.WHITE,
                "Air",
                NamedTextColor.WHITE,
                "Light as the wind itself.",
                Color.fromRGB(220, 220, 255),
                new ArmorTrim(TrimMaterial.QUARTZ, TrimPattern.VEX),
                6.0, 1.5,
                new CustomStats(3, 0, 1, 3, 0)
        );

        applyArmorPiece(
                plugin,
                Material.LEATHER_BOOTS,
                "Rootbound Greaves",
                NamedTextColor.GREEN,
                "Earth",
                NamedTextColor.GREEN,
                "Grounded and unyielding.",
                Color.fromRGB(50, 160, 50),
                new ArmorTrim(TrimMaterial.EMERALD, TrimPattern.WARD),
                4.0, 1.0,
                new CustomStats(5, 1, 1, 0, 0)
        );
    }

    private static void applyArmorPiece(
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
            CustomStats stats
    ) {
        ItemStack item = new ItemStack(material);
        if (!(item.getItemMeta() instanceof ColorableArmorMeta meta)) return;

        // Display name & base lore
        meta.displayName(Component.text(displayName, displayColor).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Element: ", NamedTextColor.GRAY)
                .append(Component.text(elementName, elementColor))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(flavorText, NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(" "));

        // Stat Lore
        lore.add(Component.text("Stats:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("♥ Health: +" + stats.hp(), NamedTextColor.RED));
        lore.add(Component.text("⚔ Damage: +" + stats.damage(), NamedTextColor.DARK_RED));
        lore.add(Component.text("☘ Luck: +" + stats.luck(), NamedTextColor.GREEN));
        lore.add(Component.text("✦ Speed: +" + stats.speed(), NamedTextColor.AQUA));
        lore.add(Component.text("❤ Lifesteal: +" + stats.lifesteal() + "%", NamedTextColor.LIGHT_PURPLE));

        meta.lore(lore);

        // Persistent data
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "elementType"), PersistentDataType.STRING, elementName.toLowerCase());
        data.set(new NamespacedKey(plugin, "hp"), PersistentDataType.DOUBLE, stats.hp());
        data.set(new NamespacedKey(plugin, "damage"), PersistentDataType.DOUBLE, stats.damage());
        data.set(new NamespacedKey(plugin, "luck"), PersistentDataType.DOUBLE, stats.luck());
        data.set(new NamespacedKey(plugin, "speed"), PersistentDataType.DOUBLE, stats.speed());
        data.set(new NamespacedKey(plugin, "lifesteal"), PersistentDataType.DOUBLE, stats.lifesteal());

        // Armor color & trim
        meta.setColor(color);
        meta.setTrim(trim);

        // Base armor attributes
        meta.addAttributeModifier(Attribute.ARMOR,
                new AttributeModifier(new NamespacedKey(plugin, displayName.toLowerCase() + "_armor"),
                        armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                new AttributeModifier(new NamespacedKey(plugin, displayName.toLowerCase() + "_toughness"),
                        toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));

        item.setItemMeta(meta);

        // Register recipe
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, displayName.toLowerCase().replace(" ", "_")), item);
        recipe.shape("TTT", "TTT", "TTT");
        recipe.setIngredient('T', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
    }

    // ------------------------------------------------------------
    // Custom stat container
    // ------------------------------------------------------------
    private record CustomStats(double hp, double damage, double luck, double speed, double lifesteal) {}
}

*/
