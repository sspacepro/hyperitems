package org.galaxystudios.dungeonCreate;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.sql.rowset.spi.SyncFactoryException;

import static javax.sql.rowset.spi.SyncFactory.getLogger;

public class MythicMobSpawnListener implements Listener {
    @EventHandler
    public void onMythicMobSpawn(MythicMobSpawnEvent event) {


        try {
            getLogger().info("Adding Metadata to Mythic Mob");
        } catch (SyncFactoryException e) {
            throw new RuntimeException(e);
        }

    }
}

