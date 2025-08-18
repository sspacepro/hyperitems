package org.galaxystudios.dungeonCreate;

import org.galaxystudios.dungeonCreate.Listeners.MythicMobSpawnListener;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadEntityElements;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadelementBeatsMap;
import org.mineacademy.fo.plugin.SimplePlugin;


public final class DungeonCreate extends SimplePlugin {

    @Override
    public void onPluginStart() {
        //Listeners
        getServer().getPluginManager().registerEvents(new MythicMobSpawnListener(), this);

        //commands


        //Loading classes
        LoadEntityElements.getInstance().load();
        LoadelementBeatsMap.load();


        //Files
        saveResource("EntityElements.yml", false);
    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }

    public void onPluginReload() {
        LoadEntityElements.getInstance().load();


    }
}
