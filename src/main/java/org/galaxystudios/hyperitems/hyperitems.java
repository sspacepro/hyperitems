package org.galaxystudios.hyperitems;




import org.galaxystudios.hyperitems.Listeners.BlockTrimmingLeatherListener;
import org.galaxystudios.hyperitems.Listeners.DamageDoneListener;
import org.galaxystudios.hyperitems.LoadPlugin.*;


import org.galaxystudios.hyperitems.MythicIntegration.ItemManager;
import org.galaxystudios.hyperitems.MythicIntegration.MythicMobKilledListener;
import org.galaxystudios.hyperitems.MythicIntegration.MythicMobSpawnListener;
import org.galaxystudios.hyperitems.Listeners.PlayerStatUpdateListener;
import org.mineacademy.fo.plugin.SimplePlugin;

import static org.galaxystudios.hyperitems.LoadPlugin.LoadItems.loadedItems;


public final class hyperitems extends SimplePlugin {
// TEST
    @Override
    public void onPluginStart() {
        //Has to be first
        LoadItems.register();
        for (String key : loadedItems.keySet()) {
            ItemManager.registerCustomItem(key, loadedItems.get(key));
        }


        //Listeners
        getServer().getPluginManager().registerEvents(new MythicMobSpawnListener(), this);
        getServer().getPluginManager().registerEvents(new DamageDoneListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerStatUpdateListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockTrimmingLeatherListener(this), this);
        getServer().getPluginManager().registerEvents(new MythicMobKilledListener(this), this);
        //commands


        //Loading classes
        LoadEntityElements.getInstance().load();
        LoadElementBeatsMap.load();
        LoadElementArmor.register();
        LoadElementWeapons.register();





        //Files
        // Only for development: ensures default config files are present
        saveResource("EntityElements.yml", true);
        saveResource("armors.yml",true);
        saveResource("weapons.yml",true);
        saveResource("items.yml", true);
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
