package org.galaxystudios.dungeonCreate.Listeners;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.dungeonCreate.DungeonCreate;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadEntityElements;

import java.util.Map;

public class MythicMobSpawnListener implements Listener {
    @EventHandler
    public void onMythicMobSpawn(MythicMobSpawnEvent event) {
        MythicMob mythicMob = event.getMob().getType();


        String mobName = mythicMob.getInternalName().toUpperCase();
        Map<String, String> elementMap = LoadEntityElements.getInstance().getMobElementMap();
        String element = elementMap.get(mobName);
        if (element == null) {
            return;
        }
        final NamespacedKey key = new NamespacedKey(DungeonCreate.getInstance(), "element");
        Entity entity = event.getMob().getEntity().getBukkitEntity();
        entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, element);

        //Setting each element mob to its own color.
        String colorCode = switch (element) {
            case "Fire" -> "§6";
            case "Water" -> "§b";
            case "Earth" -> "§2";
            case "Air" -> "§f";
            default -> null;
        };

        if (colorCode != null) {
            PlaceholderString coloredName = PlaceholderString.of(colorCode + mythicMob.getDisplayName().toString());
            mythicMob.setDisplayName(coloredName);
        }

    }
}

