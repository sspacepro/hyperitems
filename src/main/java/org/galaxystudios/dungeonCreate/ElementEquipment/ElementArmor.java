package org.galaxystudios.dungeonCreate.ElementEquipment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;

import java.util.List;

public class ElementArmor {

    public static void register() {

        // create a NamespacedKey for the element type

        NamespacedKey elementKey = new NamespacedKey(DungeonCreate.getInstance(), "elementType");




        // Test Diamond

        ItemStack testDiamond = new ItemStack(Material.DIAMOND);
        ItemMeta testMeta = testDiamond.getItemMeta();
        testMeta.displayName(
                Component.text("Test Diamond", NamedTextColor.LIGHT_PURPLE)
                        .decoration(TextDecoration.ITALIC, false)
        );
        testMeta.lore(List.of(
                Component.text("A Test Diamond for testing crafts.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        testDiamond.setItemMeta(testMeta);

        ShapelessRecipe testDiamondRecipe = new ShapelessRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "test_diamond"),
                testDiamond
        );
        testDiamondRecipe.addIngredient(Material.DIAMOND);
        Bukkit.addRecipe(testDiamondRecipe);


        // Lava Chestplate

        ItemStack TestlavaChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta IMeta = TestlavaChestplate.getItemMeta();
        IMeta.displayName(
                Component.text("Blazing Coreplate", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        IMeta.lore(List.of(
                Component.text("Element: ", NamedTextColor.GRAY)
                        .append(Component.text("Lava", NamedTextColor.RED))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Forged in molten fury.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        IMeta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "lava");

        TestlavaChestplate.setItemMeta(IMeta);

        ShapedRecipe lavaRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "lava_chestplate"),
                TestlavaChestplate
        );
        lavaRecipe.shape(
                "T T",
                "TTT",
                "TTT"
        );
        lavaRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(lavaRecipe);


        // Water Helmet

        ItemStack TestwaterHelmet = new ItemStack(Material.DIAMOND_HELMET);
        IMeta = TestwaterHelmet.getItemMeta();
        IMeta.displayName(
                Component.text("Tidewatch Helm", NamedTextColor.AQUA)
                        .decoration(TextDecoration.ITALIC, false)
        );
        IMeta.lore(List.of(
                Component.text("Element: ", NamedTextColor.GRAY)
                        .append(Component.text("Water", NamedTextColor.AQUA))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Flowing with calm power.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        IMeta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "water");
        TestwaterHelmet.setItemMeta(IMeta);


        ShapedRecipe waterRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "water_helmet"),
                TestwaterHelmet
        );
        waterRecipe.shape(
                "TTT",
                "T T",
                "   "
        );
        waterRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(waterRecipe);

        // Air Leggings

        ItemStack TestairLeggings = new ItemStack(Material.IRON_LEGGINGS);
        IMeta = TestairLeggings.getItemMeta();
        IMeta.displayName(
                Component.text("Skystride Leggings", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
        );
        IMeta.lore(List.of(
                Component.text("Element: ", NamedTextColor.GRAY)
                        .append(Component.text("Air", NamedTextColor.WHITE))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Light as the wind itself.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        IMeta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "air");
        TestairLeggings.setItemMeta(IMeta);

        ShapedRecipe airRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "air_leggings"),
                TestairLeggings
        );
        airRecipe.shape(
                "TTT",
                "T T",
                "T T"
        );
        airRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(airRecipe);


        //  Earth Boots

        ItemStack TestearthBoots = new ItemStack(Material.LEATHER_BOOTS);
        IMeta = TestearthBoots.getItemMeta();
        IMeta.displayName(
                Component.text("Rootbound Greaves", NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
        );
        IMeta.lore(List.of(
                Component.text("Element: ", NamedTextColor.GRAY)
                        .append(Component.text("Earth", NamedTextColor.GREEN))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Grounded and unyielding.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        IMeta.getPersistentDataContainer().set(elementKey, PersistentDataType.STRING, "earth");
        TestearthBoots.setItemMeta(IMeta);

        ShapedRecipe earthRecipe = new ShapedRecipe(
                new NamespacedKey(DungeonCreate.getInstance(), "earth_boots"),
                TestearthBoots
        );
        earthRecipe.shape(
                "   ",
                "T T",
                "T T"
        );
        earthRecipe.setIngredient('T', new RecipeChoice.ExactChoice(testDiamond));
        Bukkit.addRecipe(earthRecipe);


    }
}
