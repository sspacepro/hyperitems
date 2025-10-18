package org.galaxystudios.dungeonCreate;

import org.galaxystudios.dungeonCreate.ElementEquipment.ElementArmor;
import org.galaxystudios.dungeonCreate.Listeners.DamageDoneListener;
import org.galaxystudios.dungeonCreate.Listeners.MythicMobSpawnListener;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadEntityElements;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadElementBeatsMap;
import org.mineacademy.fo.plugin.SimplePlugin;


public final class DungeonCreate extends SimplePlugin {

    @Override
    public void onPluginStart() {
        //Listeners
        getServer().getPluginManager().registerEvents(new MythicMobSpawnListener(), this);
        getServer().getPluginManager().registerEvents(new DamageDoneListener(), this);
        //commands


        //Loading classes
        LoadEntityElements.getInstance().load();
        LoadElementBeatsMap.load();
        ElementArmor.register();



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
