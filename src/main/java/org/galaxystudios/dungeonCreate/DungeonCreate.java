package org.galaxystudios.dungeonCreate;

import org.mineacademy.fo.plugin.SimplePlugin;


public final class DungeonCreate extends SimplePlugin {

    @Override
    public void onPluginStart() {
        getServer().getPluginManager().registerEvents(new MythicMobSpawnListener(), this);


    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }
}
