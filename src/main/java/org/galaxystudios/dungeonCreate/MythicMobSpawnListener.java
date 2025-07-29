package org.galaxystudios.dungeonCreate;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class MythicMobSpawnListener implements Listener {
    @EventHandler
    public void onMythicMobSpawn(MythicMobSpawnEvent event) {

        MythicMob mythicMob = event.getMob().getType();
        String mobName = toUpperCase(mythicMob.getInternalName());
        Map<String, String> elementMap = LoadEntityElements.getInstance().getMobElementMap();
        String element = elementMap.get(mobName);
        final NamespacedKey key = new NamespacedKey(DungeonCreate.getInstance(), "element");
        Entity entity = event.getMob().getEntity().getBukkitEntity();
        if (element != null) {
            entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, element);
        }
        //for testing let replace with element
        String elementTEMPORARY = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if ("Fire".equals(elementTEMPORARY)) {
            entity.customName(Component.text(currentName).color(NamedTextColor.GOLD));
        }


    }
}

