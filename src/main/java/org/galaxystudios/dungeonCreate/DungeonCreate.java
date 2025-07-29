package org.galaxystudios.dungeonCreate;

import org.mineacademy.fo.plugin.SimplePlugin;


public final class DungeonCreate extends SimplePlugin {

    @Override
    public void onPluginStart() {
        //Listeners
        getServer().getPluginManager().registerEvents(new MythicMobSpawnListener(), this);

        //commands


        //Loading classes
        LoadEntityElements.getInstance().load();

    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }

    public void onPluginReload() {
        LoadEntityElements.getInstance().load();


    }
}
