package org.galaxystudios.dungeonCreate;


import org.galaxystudios.dungeonCreate.Listeners.BlockTrimmingLeatherListener;
import org.galaxystudios.dungeonCreate.Listeners.DamageDoneListener;
import org.galaxystudios.dungeonCreate.Listeners.MythicMobSpawnListener;
import org.galaxystudios.dungeonCreate.Listeners.PlayerStatUpdateListener;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadElementArmor;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadElementWeapons;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadEntityElements;
import org.galaxystudios.dungeonCreate.LoadPlugin.LoadElementBeatsMap;
import org.mineacademy.fo.plugin.SimplePlugin;


public final class DungeonCreate extends SimplePlugin {

    @Override
    public void onPluginStart() {
        //Listeners
        getServer().getPluginManager().registerEvents(new MythicMobSpawnListener(), this);
        getServer().getPluginManager().registerEvents(new DamageDoneListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerStatUpdateListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockTrimmingLeatherListener(this), this);
        //commands


        //Loading classes
        LoadEntityElements.getInstance().load();
        LoadElementBeatsMap.load();
        LoadElementArmor.register();
        LoadElementWeapons.register();



        //Files
        // Make it so this will only run on the first start not every time the plugin restarts
        saveResource("EntityElements.yml", false);
        saveResource("armors.yml",false);
        saveResource("weapons.yml",false);
    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }

    public void onPluginReload() {
        LoadEntityElements.getInstance().load();
        LoadElementArmor.register();
        LoadElementWeapons.register();


    }
}
